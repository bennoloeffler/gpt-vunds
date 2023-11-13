(ns gpt-vunds.tutorial
  (:require [wkok.openai-clojure.api :as api]
            [hyperfiddle.rcf :refer [tests]]))

(hyperfiddle.rcf/enable! true)

;;---------------------------------------------------------------
;; 1. setup TECHNOLOGY
;;---------------------------------------------------------------
;; API key and basic interaction
;; see core.clj
;; see project.clj

;;---------------------------------------------------------------
;; 2. UNDERSTANDING "DIALOG CONCEPTS": how does a llm work?
;;---------------------------------------------------------------
;;
;; a) tokens: what is that?
;; -------------------------------------------------------------
;; The LLM is trained with books and available "internet data"
;; like twitter chats to predict "the most likely next token(s)"
;; A token is "a short sequence of characters". E.g. the word
;; "tokenization" is decomposed into two tokens:
;;    token ization
;; One token is about 4 charakters long (well: about...)
;; That's in average 0.75 english words.
;;
;;
;; b) finding meaningful answers: how?
;; -------------------------------------------------------------
;; You provide a sequence of words
;; that are split internally into tokens - but for the moment,
;; let's forget about that:
;;   "We all live in a"
;; Then, the model, based on the training data, produces
;; "one next very likely token".
;; That may be: "yellow" or "shared" or "digital"
;;
;; Then, the model takes one of those "highly probable ones".
;;   "We all live in a yellow"
;; Then, this extended sequence is feed in the model again.
;; Obviously, the one of the next good tokens is "submarine".
;;   "We all live in a yellow submarine"
;; And so on, and so on...
;; Finally, the model delivers a short or long sequence of tokens
;; back to the user.
;;
;;
;; c) the LLM is not deterministic: "temperature"
;; -------------------------------------------------------------
;; "we all live in a yellow..."
;; But it could also be "house" instead of "submarine".
;; Or taxi, dress, ribbon, sunflower, etc.
;; In order to produce that "next token", the LLM will produce a
;; number of different tokens with a decreasing probability
;; of their fit:
;; submarine 0.99
;; house 0.89
;; taxi 0.86
;; dress 0.8
;; ...
;; roastbeef 0.0001
;; Then, it chooses "one of the more probable" solutions.
;; There is a parameter, called temperature.
;; If the temperature is near 0, the model chooses
;; very likely a word of very highest probability.
;; The higher you set the temperature, the more likely words
;; of lesser probability will be chosen from the list of token candidates.
;; That means:
;; Temperature = 0.9 => very different answers to the same question.
;; Temperature = 0 => almost deterministic, always almost same answers.
;;
;;
;; d. shorter or longer answers?
;; -------------------------------------------------------------
;; You cannot send a complete book as question (called: prompt).
;; And the LLM does not reply with a complete book (called: reply).
;; On the other hand, one-word-questions and one-word-answers
;; do also not work for conversation.
;; So what is the right length?
;; For practical reasons of restricted computing power,
;; the total length of tokens per "question and reply" is limited.
;; For gpt-4 it's 8192 tokens - that's about 0.75 * 8192 = 6144 words.
;; You are able to set that maximum value manually and force a short reply.
;; But typically, the model chooses longer or shorter answers itself.
;; the pricing of openai api is based on consumption of sent and
;; received tokens.
;;
;;
;; e. different models for different purposes
;; -------------------------------------------------------------
;; gpt-3.5-turbo  faster - but less elaborate.
;; gpt-4          much bigger training data set.
;; There are more - see doc of openai
;; Models are changing rapidly!
;; see: https://platform.openai.com/docs/models
;; We start with gpt-3.5-turbo
;; then we explore the difference to gpt-4
;;
;;
;; f. roles: system, user and assistent
;; -------------------------------------------------------------
;; You may tell the model, how it should act.
;; There are roles, that you may "feed" with information.
;;   system: the role the llm should play, how it should process your input.
;;   user: your input
;;   assistent: answer of the llm
;; Example:
;; {:role    "system"
;;  :content "you speak like a rapper, e.g. Eminem. Replace all swear words by the emoji 🤬"}
;;

;;---------------------------------------------------------------
;; 3. Let's to it...
;;---------------------------------------------------------------

(comment

  ;; this is the deterministic one
  (api/create-chat-completion
    {:model       "gpt-3.5-turbo"
     :temperature 0.0 ; very deterministic
     :messages    [;; you provide a hint, how the llm should behave.
                   {:role "system" :content "You are an assistant that completes sentences. Two or three words maximum."}
                   ;; then you ask your question
                   {:role "user" :content "We all live in a  ...  ..."}]})

  ;; this is the creative one, producing very different sentences
  (api/create-chat-completion
    {:model       "gpt-3.5-turbo"
     :temperature 1.0 ; very different, every time
     :messages    [{:role "system" :content "You are an assistant that completes sentences. "}
                   {:role "user" :content "We all live in a..."}]})


  ;; this is the creative one, that provides very long answers.
  (api/create-chat-completion
    {:model       "gpt-3.5-turbo"
     :temperature 1.0 ; very different, every time
     :messages    [{:role "system" :content "You are an assistant that completes sentences with several sentences. Please comment on your choices."}
                   {:role "user" :content "We all live in a"}]})


  ;; very short answers, forced by :max_tokens
  (api/create-chat-completion
    {:model      "gpt-3.5-turbo"
     :max_tokens 6 ; try 3
     :messages   [{:role "system" :content "You are an assistant that completes sentences. Please comment on your completition."}
                  {:role "user" :content "We all live in a"}]})


  ;; feeding roles infos in order to get a task done
  (api/create-chat-completion
    {:model    "gpt-3.5-turbo"
     :messages [{:role    "system"
                 :content (str "You extract and format all address information according to the following rules:\n"
                               "1. full name of person\n"
                               "2. street number\n"
                               "3. zip-code city"
                               "4. email"
                               "5. mobile"
                               "answer with the raw adress information. Leave out all descriptive text like 'mobile' etc.")}
                {:role "user" :content "Some intro... Benno Löffler lives in Stuttgart. Zip-Code is 70199 and his email is benno.loeffler@gmx.de - his number seems to be 0171 62 35 378. He lives in Adlerstraße 46. Other numbers 0711 9234344"}]})


  ;; role info could be very different.
  ;; gpt is not good with cynics, swear words etc.
  ;; almost all that type of words is filtered out.
  (api/create-chat-completion
    {:model    "gpt-3.5-turbo"
     :messages [{:role "system" :content "you speak like a rapper, e.g. Eminem. Use many, many, many swear words (like 5h!t, bu11sh!t or fu**in) and street language."}
                {:role "user" :content "describe New York City in a poem with about 10 lines."}]})

  ;; let's try the same with gpt-4
  (api/create-chat-completion
    {:model    "gpt-4"
     :messages [{:role "system" :content "you speak like a rapper, e.g. Eminem. Use many, many, many swear words (like 5h!t, bu11sh!t or fu**in) and street language."}
                {:role "user" :content "describe New York City in a poem with about 10 lines."}]})


  ;; you could provide a whole dialog, to explain
  ;; your intentions to the model.
  ;; basically, you feed in the answer of the llm again!
  (api/create-chat-completion
    {:model    "gpt-3.5-turbo"
     :messages [{:role    "system"
                 :content "you anser with a numbered list, whenever possible."}
                {:role "user" :content "provide me with a concept for a wedding: what to do in order to prepare the day?"}

                ;;
                ;; this may be an answer, that is feed in again
                ;;

                {:role    "assistant",
                 :content "To prepare for the wedding day, here is a concept divided into a numbered list:

                                  1. Create a Budget:
                                     - Determine the overall budget for the wedding.
                                     - Allocate funds for various aspects, such as venue, catering, attire, decor, and entertainment.

                                  2. Set a Date and Choose a Venue:
                                     - Decide on a wedding date that suits both you and your partner.
                                     - Research and visit potential venues that fit your budget and desired wedding style.

                                  3. Plan the Guest List:
                                     - Determine the number of guests you want to invite.
                                     - Create a guest list with names and addresses for sending invitations later.

                                  4. Select a Theme and Wedding Style:
                                     - Decide on a theme or style that represents you as a couple.
                                     - Consider colors, decor, and overall ambiance to match the chosen theme.

                                  5. Book Vendors and Services:
                                     - Research and book important vendors such as the caterer, photographer, videographer, florist, and entertainment.
                                     - Ensure the vendors have availability on your chosen date and meet your requirements.

                                  6. Choose Attire and Accessories:
                                     - Shop for wedding attire, including dresses for the bride and bridesmaids, as well as suits/tuxedos for the groom and groomsmen.
                                     - Select accessories like shoes, jewelry, and veils or other headpieces.

                                  7. Plan the Ceremony and Reception:
                                     - Decide on the details for the ceremony, such as readings, music, and officiant.
                                     - Explore reception options, including seating arrangements, menu planning, and cake design.

                                  8. Create and Send Invitations:
                                     - Design and create wedding invitations.
                                     - Determine the RSVP deadline and include necessary details for guests to respond.

                                  9. Organize Transportation and Accommodation:
                                     - Arrange transportation for the wedding party and guests, if necessary.
                                     - Book accommodations for out-of-town guests and ensure transport between the venue and accommodation is arranged.

                                  10. Create a Timeline and Plan for Day-Of Coordination:
                                      - Create a detailed timeline of the wedding day, including the ceremony and reception schedule.
                                      - Consider hiring a day-of coordinator to ensure everything runs smoothly.

                                  11. Plan Pre-Wedding Events:
                                      - Organize events such as engagement parties, bridal showers, or bachelor/bachelorette parties.

                                  12. Arrange Marriage License and Legalities:
                                      - Check the requirements for obtaining a marriage license in your jurisdiction.
                                      - Complete any necessary paperwork and schedule appointments accordingly.

                                  13. Plan and Attend Rehearsal:
                                      - Schedule and plan a rehearsal, usually held the day before the wedding.
                                      - Ensure all participants (e.g., bridal party, family members, officiant) are aware of the schedule.

                                  14. Finalize Details and Confirmations:
                                      - Reach out to all vendors to confirm arrangements and communicate any last-minute changes.
                                      - Finalize details such as menu choices, seating charts, and other logistics.

                                  15. Take Care of Personal Health and Well-being:
                                      - Focus on self-care and stress management during the wedding planning period.
                                      - Prioritize getting enough sleep, eating well, and finding time for relaxation.

                                  Remember, this is just an outline, and you can personalize and adjust these steps based on your preferences and specific wedding details."}
                ;;
                ;; ask to improve the answer
                ;;

                {:role "user" :content "for every numbered item in the list, please tell me, if there are tools, tricks or just more helpful hints, how to be successful."}]})


  nil)


;;---------------------------------------------------------------
;; 4. how to create a prompt, that may be easily configured
;;---------------------------------------------------------------
;; see from_file.clj


;;---------------------------------------------------------------
;; 5. how to work with "meaning"
;;---------------------------------------------------------------


;; a) meaning = semantics = embeddings
;; how about the semantic and phonetic similarity of:
;;   1 "shoes swimming"
;;   2 "she is slimming"
;;   3 "boots floating"
;;   4 "goats fasting"
;;
;; If we compare those sentences by
;; "naive similarity of letters or words"
;; we don't find "similarity of meaning".
;; This is,  where embeddings shine:
;;
;; A metaphor:
;; Imagine a person in a team, leaving the team.
;; Now, we would like to find something to replace her.
;; The most important aspekts/dimensions for the team are:
;; 1. She has a good expertise on her field. (0.6 on a scale from 0 to 1)
;; 2. She is extrovert. This is important - because all others are introverts. (0.9)
;; 3. she is absolutely co-operative, integrative, and she drives joint problem-solving. (0.8)
;; Imagine, the team likes to compare two candidates:
;;
;; She, who leaves, lets call her S:
;; 1. 0.6 expertise
;; 2. 0.9 extrovert
;; 3. 0.8 joint problem-solving
;;
;; candidate A:
;; 1. 0.9 expertise
;; 2. 0.3 extrovert
;; 3. 0.3 joint problem-solving
;;
;; candidate B:
;; 1. 0.4 expertise
;; 2. 0.7 extrovert
;; 3. 0.9 joint problem-solving
;;
;; There is a simple idea of comparing them:
;; Multiply them dimension by dimension and then sum up those products.
;;
;; So the similarity of S and A is:
;; = 0.6 * 0.9 + 0.9 * 0.3 + 0.8 * 0.3
;; = 1.05
;;
;; similarity S-to-B
;; = 0.6 * 0.4 + 0.9 * 0.7 + 0.8 * 0.9
;; = 1.59
;;
;; So B is more similar to S than A.
;; This is called the "vector-dot-product".
;; There is also something called "cosine similarity":
;; See: https://developers.google.com/machine-learning/clustering/similarity/measuring-similarity
;;
;; Now back to our four sentences:
;;   1 "shoes swimming"
;;   2 "she is slimming"
;;   3 "boots floating"
;;   4 "goats fasting"
;; How do we get the "dimensions of meaning" of those very short four sentences,
;; analogous to the "dimensions of personality" of the persons in the metaphor?
;; This process is called "create an embedding"!
;; An embedding is nothing else as the sequence of aspects expressed as numbers:
;; [0.6  0.9  0.8] in our example.
;;
;; A LLM uses about 1500 numbers to express meaning of text (independent of the length of the text!)
;; This is how you get an embedding:

(comment
  ;; get the embedding and store it
  (def an-embedding-raw
    (api/create-embedding {:input           "one line of text with a tower on a hill"
                           :model           "text-embedding-ada-002"
                           :encoding-format "float"}))
  ;; extract data
  (def embedding (-> an-embedding-raw :data (get 0) :embedding))

  ;; create a function in order to make it easier to use it
  (defn emb
    "Call openai api to create an embedding for text."
    [text]
    (-> (api/create-embedding {:input           text
                               :model           "text-embedding-ada-002"
                               :encoding-format "float"})
        (get-in [:data 0 :embedding])))

  ; Where do we expect high similarity?
  (do (def emb-shoe (emb "shoes swimming in the river")) ;              A
      (def emb-she (emb "she is slimming and eating less and less")) ;  |      B
      (def emb-boots (emb "boots floating in the ocean")) ;              A     |
      (def emb-goats (emb "goats fasting because no food is there"));          B
      (def emb-radius (emb "radius is a measure in math."))
      (def emb-force (emb "force is a concept in physics.")))

  ;; now - how do we compare those embeddings?
  ;; well, we calculate the dot-product, which expresses
  ;; a measure of "semantic similarity":
  (defn dp
    "dot product of two vectors"
    [a b]
    (reduce + (map * a b)))

  (tests
    (dp [1 2 3] [4 5 6]) := 32)

  (defn similarity
    "Return the similarity of two embeddings as
    a data structure that looks like:
    [\"name-of-emb-1 --> name-of-emb-2\" 0.123]
    based on input:
    {:name :shoe :val emb-shoe}
    "
    [emb-1 emb-2]
    [(str (:name emb-1) " --> " (:name emb-2))
     (dp (:val emb-1) (:val emb-2))])

  (tests
    (similarity {:name :shoe :val [1 2 3]}
                {:name :she :val [4 5 6]})
    := [":shoe --> :she" 32])

  (defn compare-all [all-embeddings]
    (for [a all-embeddings b all-embeddings]
      (similarity a b)))

  (tests
    (compare-all [{:name :shoe :val [1 2 3]}
                  {:name :she :val [4 5 6]}])
    := [[":shoe --> :shoe" 14]
        [":shoe --> :she" 32]
        [":she --> :shoe" 32]
        [":she --> :she" 77]])

  (defn round-to
    "Round the double n to digits digits.
     (round-to 0.129456789 2) ; => 0.13"
    [n digits]
    (let [multiplier (Math/pow 10 digits)]
      (/ (Math/round (* n multiplier)) multiplier)))

  (tests
    (round-to 0.129456789 2) := 0.13)

  ;; what should be nearest?
  ;; boots and shoe
  ;; goats and she
  ;; radius and force
  ;; The four first should be most distant to emb-radius and emb-force.
  (->> (compare-all [{:name :shoe :val emb-shoe}
                     {:name :she :val emb-she}
                     {:name :boots :val emb-boots}
                     {:name :goats :val emb-goats}
                     {:name :radius :val emb-radius}
                     {:name :force :val emb-force}])
       (sort-by second)
       reverse
       (map (fn [[name num]] [name (round-to num 3)])))

  nil)


;;---------------------------------------------------------------
;; 6. How to use the "meaning" in our documents
;; how to get the "meaning" of documents into gtp prompt?
;;---------------------------------------------------------------
;; see file embeddings_and_vector_db.clj



