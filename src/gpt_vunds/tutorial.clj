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
;; 3. Let's do it...
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
;; 6. How to use own documents, how to find the "meaning"
;;    in words, phrases and parts of documents and
;;    how to use own documents without uploading them?
;;---------------------------------------------------------------
;; see file embeddings_and_vector_db.clj

;;---------------------------------------------------------------
;; 7. How to use assistants?
;;---------------------------------------------------------------

(comment
  ; gpt-4-1106-preview

  (api/list-assistants {:limit 3})
  #_(def assistant (api/create-assistant {:model "gpt-4-1106-preview"
                                          :name "bels-assistant-01"
                                          :instructions "you are searching your PDFs and provide consise answers to the questions of the user."}))
  ; :id "asst_x8JQA9JaVa2l9edWuCJAiEpS"

  ;; retrieve the assistant again
  (def the-ass (api/retrieve-assistant {:assistant_id "asst_x8JQA9JaVa2l9edWuCJAiEpS"}))

  ;;{:description nil,
  ;; :file_ids [],
  ;; :name "bels-assistant-01",
  ;; :tools [],
  ;; :instructions "you are searching your PDFs and provide consise answers to the questions of the user.",
  ;; :id "asst_x8JQA9JaVa2l9edWuCJAiEpS",
  ;; :metadata {},
  ;; :object "assistant",
  ;; :created_at 1701174542,
  ;; :model "gpt-4-1106-preview"}



  #_(def the-pdf-file (api/create-file {:purpose "assistants"
                                        :file (clojure.java.io/file "/Users/benno/projects/gpt-vunds/SAUGUTE ZUSAMMENARBEIT eBook.pdf")}))
  ;; {:object "file",
  ;; :id "file-pqqfFSOYyeV5tmpXKIGmJm52",
  ;; :purpose "assistants",
  ;; :filename "SAUGUTE ZUSAMMENARBEIT eBook.pdf",
  ;; :bytes 5196957,
  ;; :created_at 1701175552,
  ;; :status "processed",
  ;; :status_details nil}

  #_(def new-ass (api/modify-assistant {:assistant_id "asst_x8JQA9JaVa2l9edWuCJAiEpS"
                                        :tools [{:type "retrieval"}]
                                        :file_ids ["file-pqqfFSOYyeV5tmpXKIGmJm52"]}))
  ;; {:description nil,
  ;; :file_ids ["file-pqqfFSOYyeV5tmpXKIGmJm52"],
  ;; :name "bels-assistant-01",
  ;; :tools [{:type "retrieval"}],
  ;; :instructions "you are searching your PDFs and provide consise answers to the questions of the user.",
  ;; :id "asst_x8JQA9JaVa2l9edWuCJAiEpS",
  ;; :metadata {},
  ;; :object "assistant",
  ;; :created_at 1701174542,
  ;; :model "gpt-4-1106-preview"}

  (def thread (api/create-thread))
  ;; {:id "thread_MZhfqdCFKojpWK2FUcyP1N9y", :object "thread", :created_at 1701176450, :metadata {}}

  (def message (api/create-message {:thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y"
                                    :role      "user"
                                    :content   "Was ist der Unterschied zwischen Führung und Steuerung"}))
  ;; {:role "user",
  ;; :file_ids [],
  ;; :content [{:type "text", :text {:value "Was ist der Unterschied zwischen Führung und Steuerung", :annotations []}}],
  ;; :run_id nil,
  ;; :assistant_id nil,
  ;; :thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y",
  ;; :id "msg_WxksbvIxJeHFM4yWZBaUJLtU",
  ;; :metadata {},
  ;; :object "thread.message",
  ;; :created_at 1701176568}

  (def the-run (api/create-run {:assistant_id "asst_x8JQA9JaVa2l9edWuCJAiEpS"
                                :thread_id    "thread_MZhfqdCFKojpWK2FUcyP1N9y"}))
  ;; {:expires_at 1701177426,
  ;; :file_ids ["file-pqqfFSOYyeV5tmpXKIGmJm52"],
  ;; :started_at nil,
  ;; :completed_at nil,
  ;; :tools [{:type "retrieval"}],
  ;; :instructions "you are searching your PDFs and provide consise answers to the questions of the user.",
  ;; :assistant_id "asst_x8JQA9JaVa2l9edWuCJAiEpS",
  ;; :last_error nil,
  ;; :thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y",
  ;; :failed_at nil,
  ;; :status "queued",
  ;; :id "run_z22D9PxdWArSUWXjqg35UpBi",
  ;; :cancelled_at nil,
  ;; :metadata {},
  ;; :object "thread.run",
  ;; :created_at 1701176826,
  ;; :model "gpt-4-1106-preview"}

  (def completed (api/retrieve-run {:run_id "run_z22D9PxdWArSUWXjqg35UpBi"
                                    :thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y"}))
  ;; {:expires_at nil,
  ;; :file_ids ["file-pqqfFSOYyeV5tmpXKIGmJm52"],
  ;; :started_at 1701176826,
  ;; :completed_at 1701176841,
  ;; :tools [{:type "retrieval"}],
  ;; :instructions "you are searching your PDFs and provide consise answers to the questions of the user.",
  ;; :assistant_id "asst_x8JQA9JaVa2l9edWuCJAiEpS",
  ;; :last_error nil,
  ;; :thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y",
  ;; :failed_at nil,
  ;; :status "completed",
  ;; :id "run_z22D9PxdWArSUWXjqg35UpBi",
  ;; :cancelled_at nil,
  ;; :metadata {},
  ;; :object "thread.run",
  ;; :created_at 1701176826,
  ;; :model "gpt-4-1106-preview"}
  ;;
  (api/list-messages {:thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y"})

  (def message-2 (api/create-message {:thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y"
                                      :role      "user"
                                      :content   "Was genau zeichnet Steuerung aus?"}))

  (def the-run-2 (api/create-run {:assistant_id "asst_x8JQA9JaVa2l9edWuCJAiEpS"
                                  :thread_id    "thread_MZhfqdCFKojpWK2FUcyP1N9y"}))

  (api/list-messages {:thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y"})

  (def message-3 (api/create-message {:thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y"
                                      :role      "user"
                                      :content   "Suche im Dokument weiter und dann im Internet"}))

  (def the-run-3 (api/create-run {:assistant_id "asst_x8JQA9JaVa2l9edWuCJAiEpS"}
                                :thread_id    "thread_MZhfqdCFKojpWK2FUcyP1N9y"))


  (api/list-messages {:thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y"})

  (do
    (api/create-message {:thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y"
                         :role      "user"
                         :content   "in welchen Zusammenhängen ist Macht relevant?"})
    (api/create-run {:assistant_id "asst_x8JQA9JaVa2l9edWuCJAiEpS"
                     :thread_id    "thread_MZhfqdCFKojpWK2FUcyP1N9y"})
    (Thread/sleep 1000)
    (api/list-messages {:thread_id "thread_MZhfqdCFKojpWK2FUcyP1N9y"}))



  nil)

;;---------------------------------------------------------------
;; 8. pictures
;;---------------------------------------------------------------

(comment
  (def image (api/create-image {:prompt "create a heavy metal band playing in front of a huge crowd of headbanging business people"
                                :n 5
                                :size "1024x1024"
                                :response_format "url"}))
  {:created 1701212226,
   :data [{:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-Xow4grKIlcN5ZtT3RhrEc1KO.png?st=2023-11-28T21%3A55%3A03Z&se=2023-11-28T23%3A55%3A03Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T13%3A34%3A33Z&ske=2023-11-29T13%3A34%3A33Z&sks=b&skv=2021-08-06&sig=%2BDKd81vy3aGBLe/xtyFKmqksLF06TipQm2ebJHAPlro%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-fASv8YTSaYWHcjitGAfA2ezA.png?st=2023-11-28T21%3A57%3A06Z&se=2023-11-28T23%3A57%3A06Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T07%3A41%3A13Z&ske=2023-11-29T07%3A41%3A13Z&sks=b&skv=2021-08-06&sig=%2BCYWOlga5eZRUnq6lJuRAwK7H5gixz/yV3bK6xdSdSM%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-K5A9Fygifdqdkl6GsdekZPsD.png?st=2023-11-28T21%3A57%3A06Z&se=2023-11-28T23%3A57%3A06Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T07%3A41%3A13Z&ske=2023-11-29T07%3A41%3A13Z&sks=b&skv=2021-08-06&sig=JA3Eaeaj8CdXa9bgzb10pdMGUV5McLKS7UODt17B2Xs%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-pnnRVD9J7sgmXW9w9sG3EZbG.png?st=2023-11-28T21%3A57%3A06Z&se=2023-11-28T23%3A57%3A06Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T07%3A41%3A13Z&ske=2023-11-29T07%3A41%3A13Z&sks=b&skv=2021-08-06&sig=WF4UY9K8%2BPPtTHhs/21KV9xYQXdZBPs5oKgfO6KfgR0%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-BmBKE09XiTx81L4rrjAvWxY0.png?st=2023-11-28T21%3A57%3A06Z&se=2023-11-28T23%3A57%3A06Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T07%3A41%3A13Z&ske=2023-11-29T07%3A41%3A13Z&sks=b&skv=2021-08-06&sig=Zb2xKUHwqZ1wTFJU3NynU0MmjbMR8d4ZE7WlSiOdtEg%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-LrRcwKuwYYDrcDdorpWAMRkz.png?st=2023-11-28T21%3A57%3A06Z&se=2023-11-28T23%3A57%3A06Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T07%3A41%3A13Z&ske=2023-11-29T07%3A41%3A13Z&sks=b&skv=2021-08-06&sig=7pbOFDs0690uaBfYbfmGi6Mikb3VFxkeeEmXd4S23oQ%3D"}]}

  (def image (api/create-image {:prompt "create a manager, that looks like a machine gun"
                                :n 5
                                :size "1024x1024"
                                :response_format "url"}))
  {:created 1701212504,
   :data [{:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-I9dagny9LDy0ROxWo7UiUyFn.png?st=2023-11-28T22%3A01%3A43Z&se=2023-11-29T00%3A01%3A43Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A00%3A04Z&ske=2023-11-29T20%3A00%3A04Z&sks=b&skv=2021-08-06&sig=lSqbuRnB0Ihg4MK%2BIx9C2eyEZ5iBxK8sUEqj%2BmHedvM%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-cZac0nNdvqlmN4ans44kBmnf.png?st=2023-11-28T22%3A01%3A44Z&se=2023-11-29T00%3A01%3A44Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A00%3A04Z&ske=2023-11-29T20%3A00%3A04Z&sks=b&skv=2021-08-06&sig=PWSfkEAUYpTWPlsjzVXRbZHeEuzVHNpOrGOqpK55%2Br8%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-I2g7wKB6agGJ3Js76JAtZpex.png?st=2023-11-28T22%3A01%3A43Z&se=2023-11-29T00%3A01%3A43Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A00%3A04Z&ske=2023-11-29T20%3A00%3A04Z&sks=b&skv=2021-08-06&sig=1jGmfCdn2G8P6pEfBk5/aE1mvgBMAs3Vw9wL%2Bv4TVc8%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-poaxAsqJ8vCozcA5rhsbTPaB.png?st=2023-11-28T22%3A01%3A43Z&se=2023-11-29T00%3A01%3A43Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A00%3A04Z&ske=2023-11-29T20%3A00%3A04Z&sks=b&skv=2021-08-06&sig=zGFqLDc3xlkyzWtmWhcRV2x2iTcsrvthr4On5JNPPGk%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-Noad89DbcXjwkj7PgAzt0eEO.png?st=2023-11-28T22%3A01%3A43Z&se=2023-11-29T00%3A01%3A43Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A00%3A04Z&ske=2023-11-29T20%3A00%3A04Z&sks=b&skv=2021-08-06&sig=0oGpykmJltU9WqGSHT8ccrZyopYpSBGo7OKRInDmmDg%3D"}]}

  (def image (api/create-image {:prompt "a child that fires with a machine gun"
                                :n 5
                                :size "1024x1024"
                                :response_format "url"}))

  {:created 1701212661,
   :data [{:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-voTgbzx3OeycpzQvMUZ3mk7y.png?st=2023-11-28T22%3A04%3A21Z&se=2023-11-29T00%3A04%3A21Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A50%3A16Z&ske=2023-11-29T20%3A50%3A16Z&sks=b&skv=2021-08-06&sig=m44lDfhEgA1E4PP0/pbtF8Xk6uvNZspp/QdGH1ugO2I%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-KsgwRrJD2ZvnBzbpSYJ7Lm8M.png?st=2023-11-28T22%3A04%3A21Z&se=2023-11-29T00%3A04%3A21Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A50%3A16Z&ske=2023-11-29T20%3A50%3A16Z&sks=b&skv=2021-08-06&sig=gBmHrbDgOEj/sQBgWcSgTawhjYREhxCNDJmCjgcAoyY%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-4g2dtHA3AuwwBPzyHZDJX04U.png?st=2023-11-28T22%3A04%3A20Z&se=2023-11-29T00%3A04%3A20Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A50%3A16Z&ske=2023-11-29T20%3A50%3A16Z&sks=b&skv=2021-08-06&sig=ImtxbNzsW1KYmrkiggBlOXj9cbpyQG9%2BSa5fd4RY03o%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-ReFDml70nMcTwWW0DRRpskdo.png?st=2023-11-28T22%3A04%3A21Z&se=2023-11-29T00%3A04%3A21Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A50%3A16Z&ske=2023-11-29T20%3A50%3A16Z&sks=b&skv=2021-08-06&sig=N1j7DBuy2i23hB5oWc4m4FW81fYB8lAqPwor6qnwcOo%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-LWYyIb8EXoh4cBGFRKcdzXIk.png?st=2023-11-28T22%3A04%3A20Z&se=2023-11-29T00%3A04%3A20Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A50%3A16Z&ske=2023-11-29T20%3A50%3A16Z&sks=b&skv=2021-08-06&sig=8tVi8CGQtf25ygNwcKAqlC6huQWyyU5X0sCcB/adDs8%3D"}]}


  (def image (api/create-image {:prompt "a thinking, experimenting, active, productive Team in a big glass cube and some people watching from outside."
                                :model "dall-e-3"
                                :n 1
                                :size "1024x1024"
                                :response_format "url"}))

  ;; dall-e-2
  {:created 1701213064,
   :data [{:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-gfxSL4onzOgT7iYkOmPMrBOw.png?st=2023-11-28T22%3A11%3A04Z&se=2023-11-29T00%3A11%3A04Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T23%3A05%3A17Z&ske=2023-11-29T23%3A05%3A17Z&sks=b&skv=2021-08-06&sig=PsHYsUNskUX7T7DSQBxTHjhzvLAa/2wmhTcsRh7RhIo%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-boYURpt32B51mpuBoXeyqQnn.png?st=2023-11-28T22%3A11%3A03Z&se=2023-11-29T00%3A11%3A03Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T23%3A05%3A17Z&ske=2023-11-29T23%3A05%3A17Z&sks=b&skv=2021-08-06&sig=xrbatWzbo9YJIJ77QsNxu2ZsBc9MAWL3ODcUmwPDBuc%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-Sh7QHPE3r90K0oc2IGqAc7yk.png?st=2023-11-28T22%3A11%3A04Z&se=2023-11-29T00%3A11%3A04Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T23%3A05%3A17Z&ske=2023-11-29T23%3A05%3A17Z&sks=b&skv=2021-08-06&sig=LOX7dz9xfNs7wY4J5PsO8BErOC8GI438XGzNjGdsiRc%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-HexXgqmw6ayi7OLGk6NRNwMI.png?st=2023-11-28T22%3A11%3A03Z&se=2023-11-29T00%3A11%3A03Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T23%3A05%3A17Z&ske=2023-11-29T23%3A05%3A17Z&sks=b&skv=2021-08-06&sig=fgsyDFUUZWrG/OxUE3RWMe4L4ftUTaj24gesWzkjWiQ%3D"}
          {:url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-vx6C97GiUl1GZBa1LfAGS0hs.png?st=2023-11-28T22%3A11%3A04Z&se=2023-11-29T00%3A11%3A04Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T23%3A05%3A17Z&ske=2023-11-29T23%3A05%3A17Z&sks=b&skv=2021-08-06&sig=IThekLnC5jGLxZiwnhocHa4qQMmnoX2A%2B3IxtOfkuYw%3D"}]}

  ;; dall-e-3
  {:created 1701213446,
   :data [{:revised_prompt "View the creative process at work as an active, brainstorming team works inside a massive cube made entirely of glass. They appear to be deep in thought, their faces lit up with ideas as they strategize, hypothesize, scribble notes, and discuss their plans. The team is diverse, including a Black male graphic designer, a South Asian female software engineer, a Caucasian male project manager, and a Middle-Eastern female marketing specialist. On the outside, spectators of varying genders and descents watch with interest and curiosity, struck by the industrious and dynamic atmosphere encased in this unusual workspace.",
           :url "https://oaidalleapiprodscus.blob.core.windows.net/private/org-ItSnvWJ2Chsz7n5HSiqfcJ6h/user-b2OipQDnVVljlE2r0nNc8xW0/img-QedGkgda5M7ajOUNe7QsQiqr.png?st=2023-11-28T22%3A17%3A26Z&se=2023-11-29T00%3A17%3A26Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-11-28T20%3A09%3A14Z&ske=2023-11-29T20%3A09%3A14Z&sks=b&skv=2021-08-06&sig=CFBWwX%2BpvNVgOW0dXFjsj%2BIT9KdGAwWRFtdQwiDTeAs%3D"}]}

  ;; does not work???
  (def image (api/create-image-edit {:image (clojure.java.io/file "/Users/benno/projects/gpt-vunds/heavy_metal_hands.jpg")
                                     ;:mask (clojure.java.io/file \"path/to/mask.png\")
                                     :prompt "put a running gorilla into the dark area"
                                     :n 10
                                     :size "1024x1024"
                                     :response_format "url"})))




