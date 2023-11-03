(ns gpt-vunds.core
  (:require
    [clojure.string :as str]
    [wkok.openai-clojure.api :as api]
    [puget.printer :refer [cprint]])
  (:gen-class))

;; 1. by credits on https://platform.openai.com/account/billing/overview
;;    otherwise, you get an error: 429
;; 2. put your API-KEY in shell variable OPENAI_API_KEY:
;;    export OPENAI_API_KEY=sd-1dx4K6dO....x4K6dOx4K6dO
;;    e.g. in .zshrc or .bashrc
;; 3. use a lib to access the http endpoints of the openapi
;;    https://github.com/wkok/openai-clojure
;;    lein: [net.clojars.wkok/openai-clojure "0.11.0"]
;;          (:require [wkok.openai-clojure.api :as api])
;; 4. use prompt engineering to solve real tasks
;; https://clojureverse.org/t/scicloj-llm-meetup-2-prompt-engineering-managing-embeddings-summary-recording/10141
;; https://realpython.com/practical-prompt-engineering/
;; git: https://github.com/realpython/materials/tree/master/prompt-engineering/
;; https://www.thedataschool.com.au/daniel-lawson/using-chatgpt-to-parse-unstructured-text/
;; Extract Address data: https://www.thedataschool.com.au/daniel-lawson/using-chatgpt-to-parse-unstructured-text/


(comment

  ;; check if key is there

  (System/getenv "USER") ; user is always set!
  (System/getenv "OPENAI_API_KEY") ; is the API key set?

  :end)

;; 2. use API-KEY
(defn get-openai-api-key []
  (let [oak (System/getenv "OPENAI_API_KEY")]
    (if oak
      (do
        (println "OPENAI_API_KEY: ")
        (println oak)
        oak)
      (do
        (println "NOT SET: OPENAI_API_KEY = nil")
        (println "LINUX: export OPENAI_API_KEY=... to your .bashrc or .zshrc)")
        (println "WIN: see https://phoenixnap.com/kb/windows-set-environment-variable")))))

(get-openai-api-key)



(comment

  ;; 3. simple usage of API

  (api/create-chat-completion {:model "gpt-3.5-turbo"
                               :messages [{:role "system" :content "You are a helpful assistant."}
                                          {:role "user" :content "Schreib mir eine Liste von 5 Restaurants, wo ich in Stuttgart heute zu normalen Preisen essen gehen kann. Füge den Internet-Link und eine kurze Beschreibung der Küche an."}]})



  (api/create-chat-completion {:model "gpt-3.5-turbo"
                               :messages [{:role "system" :content "You are a helpful assistant."}
                                          {:role "user" :content "Who won the world series in 2020?"}
                                          {:role "assistant" :content "The Los Angeles Dodgers won the World Series in 2020."}
                                          {:role "user" :content "Where was it played?"}]})

  (api/create-chat-completion {:model "gpt-3.5-turbo"
                               :messages [{:role "system" :content "You are a helpful assistant."}
                                          {:role "user" :content "Who was Alan Touring? What did he invent?"}
                                          {:role "assistant" :content "He invented the basics modern computer and much more."}
                                          {:role "user" :content "What was his contribution to the second world war?"}
                                          {:role "assistant" :content "Alan Turing's most notable contribution to the Second World War was his work at Bletchley Park, the British codebreaking center. Turing played a pivotal role in breaking the German Enigma machine's codes, which helped the Allies decipher Nazi military communications. His work shortened the war, helped save countless lives, and has had a long-lasting impact on cryptography and computer science."}
                                          {:role "user" :content "When did he die? How? Why?"}
                                          {:role "assistant",
                                           :content "Alan Turing died on June 7, 1954. He was found dead in his home, and the cause of death was cyanide poisoning. The exact circumstances surrounding his death have been debated, but it is widely believed that he died by suicide. It is believed that societal pressure, homophobia, and the consequences of his conviction for homosexuality, which led to chemical castration as an alternative to imprisonment, contributed to his mental state and ultimately his tragic death. Turing's contributions to science and his tragic fate have since been recognized and he is widely celebrated as a pioneer in the field of computer science and for his role in codebreaking during World War II."}
                                          {:role "user" :content "What meant Christopher to him?"}
                                          {:role "assistant",
                                           :content "Christopher was the name of Alan Turing's close friend and romantic partner, Christopher Morcom. They became friends while attending Sherborne School and developed a deep bond. Christopher's sudden death in 1930, due to complications from bovine tuberculosis, had a profound impact on Turing. He mourned Christopher's death for many years and often referred to him as his first love. Turing's experiences with loss and grief influenced his perspectives on life, consciousness, and the nature of the mind, which also had an impact on his scientific and philosophical pursuits."}
                                          {:role "user" :content "can you summarize our discussion in 5 short sentences?"}
                                          {:role "assistant",
                                           :content "In our discussion, we explored the life and contributions of Alan Turing. He was a renowned mathematician and computer scientist. Turing played a crucial role in breaking the Enigma code during World War II, which helped the Allies gain an advantage. Unfortunately, he tragically died in 1954, reportedly due to cyanide poisoning in what is believed to be a case of suicide. Turing's relationship with his close friend Christopher Morcom also had a profound impact on his life and work."},
                                          {:role "user" :content "tell me 5 other people with most influence on computer science."}
                                          {:role "assistant",
                                           :content "Five other individuals who have had significant influence on computer science are:

                                1. Ada Lovelace: Considered the world's first computer programmer, Lovelace wrote the first algorithm designed to be processed by a machine. Her work on Charles Babbage's Analytical Engine laid the foundation for modern computer programming.

                                2. Grace Hopper: Known for her work on the concept of machine-independent programming languages, Hopper developed the first compiler and helped popularize high-level programming languages like COBOL.

                                3. Bill Gates: Co-founder of Microsoft, Gates played a pivotal role in the personal computer revolution. His leadership and entrepreneurship contributed to the development of MS-DOS and the widespread adoption of Windows operating system.

                                4. Tim Berners-Lee: Credited with inventing the World Wide Web, Berners-Lee developed the HTTP and HTML protocols, revolutionizing information sharing and enabling the global network of interconnected documents we use today.

                                5. John McCarthy: McCarthy coined the term \"artificial intelligence\" (AI) and made significant contributions in the field. He developed the Lisp programming language, which became influential in AI research, and his ideas helped shape the field's foundations."}
                                          {:role "user"
                                           :content "Did he ever wrote a poem?"}]})

  (api/create-chat-completion {:model "gpt-4"
                               :messages [{:role "system" :content "Du übersetzt keine Witze aus dem Englischen."}
                                          {:role "user" :content "Erzähl mir einen guten Witz über Berater."}]})



  :end)

;;
;; have ONE state of the chat
;;

(def prompt-config {:model "gpt-3.5-turbo"
                    :temperature 0.0
                    :max_tokens 2100
                    :messages [{:role "system" :content "You are are using not much words. You speak without using phrases that contain please or thank you. You never apollogize. You don't invent facts or stories."}]})
(def history (atom prompt-config))

(defn ask-ai [message]
  (let [question (update-in @history [:messages] conj {:role "user" :content message})
        _ (reset! history question)
        response-data (api/create-chat-completion question)
        answer (-> response-data :choices (get-in [0]) :message)]
    (swap! history update-in [:messages] conj answer)
    (println (:content answer))))

(defn reset-ai []
  (reset! history (atom prompt-config)))

(comment

  (ask-ai "who is Miss Piggy?")
  (ask-ai "does she have a green friend?")
  (ask-ai "If Kermit would be able to fly, what would he do?")
  history
  (reset-ai))



;;
;; 4. prompt engineering
;; have a prompt to solve a Problem
;;
(def examples
 "
=== Example Input: ===
[support_tom] 2023-07-24T10:02:23+00:00 : What can I help you with?
[johndoe] 2023-07-24T10:03:15+00:00 : I CAN'T CONNECT TO MY BLASTED ACCOUNT
[support_tom] 2023-07-24T10:03:30+00:00 : Alex, are you sure it's not your caps lock?
[johndoe] 2023-07-24T10:04:03+00:00 : Blast! You're right!

[support_ben] 2023-10-11T09:44:22+00:00: Good morning! How may I assist you?
[susan_p] 2023-10-11T09:44:55+00:00: Hello, I'd like to know the status of my order.
[support_ben] 2023-10-11T09:45:15+00:00: Of course, Susan. Could you please provide me with the order number?
[susan_p] 2023-10-11T09:45:40+00:00: It's 717171.


=== Example Output: ===
🔥
[Agent] 2023-07-24 : What can I help you with?
[Customer] 2023-07-24 : I CAN'T CONNECT TO MY 😤 ACCOUNT
[Agent] 2023-07-24 : ********, are you sure it's not your caps lock?
[Customer] 2023-07-24 : 😤! You're right!

✅
[Agent] 2023-10-11: Good morning! How may I assist you?
[Customer] 2023-10-11: Hello, I'd like to know the status of my order.
[Agent] 2023-10-11: Of course, ********. Could you please provide me with the order number?
[Customer] 2023-10-11: It's ********.")


(def data
  "
[support_johnny] 2023-07-15T14:40:37+00:00: Hello! What can I help you with today?
[becky_h] 2023-07-15T14:41:05+00:00: Hey, my promo code isn't applying the discount in my cart.
[support_johnny] 2023-07-15T14:41:30+00:00: My apologies for the trouble, Becky. Could you tell me the promo code you're trying to use?
[becky_h] 2023-07-15T14:41:55+00:00: It's \"SAVE20\".

[support_peter] 2023-07-24T10:56:43+00:00: Good day! How can I help you?
[lucy_g] 2023-07-24T10:57:12+00:00: Hi \"Peter\", I can't update my darn credit card information. Do you want my darn money or not?
[support_peter] 2023-07-24T10:57:38+00:00: I'm sorry for the inconvenience, Lucy. Can you please confirm your account's email?
[lucy_g] 2023-07-24T10:58:06+00:00: Sure, you have all my darn data already anyways. It's lucy.g@email.com.

[support_luke] 2023-08-13T11:34:02+00:00: Good morning! How may I assist you?
[anna_s] 2023-08-13T11:34:30+00:00: Hello, I'm having a problem with my mobile app, it keeps crashing.
[support_luke] 2023-08-13T11:34:58+00:00: I'm sorry to hear that, Anna. Could you tell me what device you're using?
[anna_s] 2023-08-13T11:35:22+00:00: I have an iPhone 11.

[support_lisa] 2023-08-30T20:38:00+00:00: Good evening! How may I assist you today?
[steve_b] 2023-08-30T20:38:30+00:00: Hi Lisa, I've forgotten my friggin password and I can't login into my account.
[support_lisa] 2023-08-30T20:38:55+00:00: I'm sorry for the trouble, Steve. Could you confirm your email address so we can reset your password?
[steve_b] 2023-08-30T20:39:22+00:00: Definitely, it's steve.b@email.com.

[support_william] 2023-09-01T08:22:40+00:00: Hello! How can I assist you this morning?
[emma_t] 2023-09-01T08:23:05+00:00: Hi, I'm trying to make a purchase but it's not going through.
[support_william] 2023-09-01T08:23:33+00:00: I'm sorry to hear that, Emma. Can you tell me what error message you're receiving?
[emma_t] 2023-09-01T08:24:00+00:00: It's saying \"Payment method not valid\".

[support_ben] 2023-10-11T09:44:22+00:00: Good morning! How may I assist you?
[susan_p] 2023-10-11T09:44:55+00:00: Hello, I'd like to know the status of my order.
[support_ben] 2023-10-11T09:45:15+00:00: Of course, Susan. Could you please provide me with the order number?
[susan_p] 2023-10-11T09:45:40+00:00: It's 717171.

[support_ricky] 2023-10-19T17:38:45+00:00: Welcome! How can I assist you right now?
[linda_a] 2023-10-19T17:39:10+00:00: Fudge! There's no option to change my profile picture. What kind of crikey joint are you running?
[support_ricky] 2023-10-19T17:39:32+00:00: Let me help you with this, Linda. Are you trying to update it from the mobile app or the website?
[linda_a] 2023-10-19T17:39:57+00:00: I'm using the darn website

[support_tony] 2023-10-29T16:00:32+00:00: Hello! What can I help you with today?
[mark_s] 2023-10-29T16:01:00+00:00: Hi Tony, I was charged twice for my last order.
[support_tony] 2023-10-29T16:01:22+00:00: I'm sorry to hear that, Mark. Could you share your order number so I can look into this for you?
[mark_s] 2023-10-29T16:01:46+00:00: Sure, it's 333666.

[support_emily] 2023-11-08T14:34:12+00:00: How can I help you today?
[nina_z] 2023-11-08T14:34:36+00:00: Hi, I made an order last week but I need to change the sizing.
[support_emily] 2023-11-08T14:34:58+00:00: Certainly, Nina. Could you provide me the order number?
[nina_z] 2023-11-08T14:35:26+00:00: Yes, it's 444888. Thanks!

[support_tom] 2023-07-24T10:02:23+00:00 : What can I help you with?
[johndoe] 2023-07-24T10:03:15+00:00 : I CAN'T CONNECT TO MY BLASTED ACCOUNT
[support_tom] 2023-07-24T10:03:30+00:00 : Are you sure it's not your caps lock?
[johndoe] 2023-07-24T10:04:03+00:00 : Blast! You're right!

[support_amy] 2023-06-15T14:45:35+00:00 : Hello! How can I assist you today?
[greg_stone] 2023-06-15T14:46:20+00:00 : I can't seem to find the download link for my purchased software.
[support_amy] 2023-06-15T14:47:01+00:00 : No problem, Greg. Let me find that for you. Can you please provide your order number?
[greg_stone] 2023-06-15T14:47:38+00:00 : It's 1245789. Thanks for helping me out!

[support_louis] 2023-05-05T09:22:12+00:00 : Hi, how can I help you today?
[karen_w] 2023-05-05T09:23:47+00:00 : MY BLASTED ORDER STILL HASN'T ARRIVED AND IT'S BEEN A WEEK!!!
[support_louis] 2023-05-05T09:24:15+00:00 : I'm sorry to hear that, Karen. Let's look into this issue.
[support_louis] 2023-05-05T09:25:35+00:00: Can you please provide your order number so I can check the status for you?
[karen_w] 2023-05-05T09:26:12+00:00: Fine, it's 9876543.
[support_louis] 2023-05-05T09:26:45+00:00: Thank you, Karen. I see there was a delay in shipping. Your order will arrive within the next 2 days.

[support_jenny] 2023-06-18T17:35:28+00:00: Hello! How can I help you today?
[alex_harper] 2023-06-18T17:36:05+00:00: I accidentally placed an order twice, can you help me cancel one?
[support_jenny] 2023-06-18T17:36:25+00:00: Sure, Alex. Can you give me the order number you'd like to cancel?
[alex_harper] 2023-06-18T17:36:55+00:00: Yes, it's 1122334. Thank you!
[support_jenny] 2023-06-18T17:37:32+00:00: I've successfully canceled order number 1122334. You will receive a confirmation email shortly.

[support_ben] 2023-06-29T11:51:45+00:00: Good morning, what can I assist you with today?
[lisa_beck] 2023-06-29T11:52:20+00:00: Hi there, I received a damaged item in my order. Can you help me return it?
[support_ben] 2023-06-29T11:52:45+00:00: I'm sorry to hear that, Lisa. Can you provide your order number and specify the damaged item?
[lisa_beck] 2023-06-29T11:53:22+00:00: Sure, order number is 5566778 and the damaged item is a coffee mug.

[support_rachel] 2023-05-04T08:16:37+00:00: How can I help you today?
[mike_t] 2023-05-04T08:17:15+00:00: My coupon code isn't working at checkout. Can you help?
[support_rachel] 2023-05-04T08:17:38+00:00: Of course, Mike. Please provide the coupon code you're trying to use.
[mike_t] 2023-05-04T08:18:02+00:00: It's \"HELLO10\".
[support_rachel] 2023-05-04T08:18:37+00:00: I've checked the code, and it seems to have expired. I apologize for the inconvenience. Here's a new code for you to use: \"WELCOME15\".

[support_vincent] 2023-06-15T20:43:55+00:00: Good evening! How may I assist you?
[sara_winters] 2023-06-15T20:44:30+00:00: Hi there, I'm having trouble logging into my account. I've tried resetting my password, but it's not working.
[support_vincent] 2023-06-15T20:44:52+00:00: I'm sorry to hear that, Sara. Let me help you. Can you please confirm your email address?
[sara_winters] 2023-06-15T20:45:25+00:00: Sure, it's sara.winters@email.com.

[support_david] 2023-06-24T16:28:43+00:00: Welcome! What can I do for you today?
[jane_d] 2023-06-24T16:29:16+00:00: Hi, I need to change my delivery address for my recent order.
[support_david] 2023-06-24T16:29:43+00:00: Alright, Jane. Please provide your order number.
[jane_d] 2023-06-24T16:30:11+00:00: It's 3344556. Thanks for your help!")

(def instruction
  "
Transform the given data.
1. Remove all personal information like order numbers, names, nicknames, emails, etc. and replace them by ********
2. Replace full timestamps by ISO-dates
3. remove all swear words like fucking or blast etc. and replace them with the emoji '😤'
4. finally, classify the sentiment of each conversation in the data with '🔥' for negative and '✅' for positive
")

(def prompt-config-2 {:model "gpt-4" ;"gpt-3.5-turbo"
                      :temperature 0.0
                      ;:max_tokens 2100
                      :messages [{:role "system"
                                  :content "You diligently complete tasks as instructed.\nYou never make up any information that isn't there."} ; You don't invent things.
                                 {:role "user"
                                  :content (str instruction
                                                "\n\n============ EXAMPLES ============\n"
                                                examples
                                                "\n\n============ DATA TO TRANSFORM ============\n"
                                                data)}]})


(comment
  (try (api/create-chat-completion prompt-config-2)
       (catch Exception e (println (ex-message e)))))

;;
;; extract adress
;;

(def examples-adress
  "
 === Example Input 1: ===

Produktionsprozesse müssen heute als Gesamtprozess betrachtet und Remanufacturing- bzw. Recycling-Prozesse von Anfang an bereits in den frühen Phasen der Produktentwicklung mitgedacht werden, damit wertvolle Rohstoffe auch zukünftig für produzierende Unternehmen verfügbar bleiben. Recycling- bzw. Remanufacturing-Prozesse müssen zur Wertschöpfung im Unternehmen beitragen. Insbesondere in den Branchen Maschinenbau und Industrial Automation wird dies zu einem wichtigen Wettbewerbsvorteil werden. In unserer Veranstaltung »Wertschöpfung durch Wiederverwendung« diskutieren wir mit namhaften Vertretern aus Wirtschaft und Forschung darüber, wie Produktion als Gesamtprozess betrachtet werden kann. Ein Prozess, der von der intelligenten Planung und lückenlosen Dokumentation über den gesamten Lebenszyklus eines Produktes bis hin zum Remanufacturing bzw. Recycling bis auf die Rohstoffebene des Produktes reicht.\n \nWir freuen uns auf Ihre Teilnahme.\n \nMit freundlichen Grüßen\n \nDipl.-Wirtsch.-Ing. Gerald Pörschmann\nGeschäftsführender Vorstand\n \nZukunftsallianz Maschinenbau e.V.\nDeutsche Messe Technology Academy\nMessegelände / Pavillon 36\n30521 Hannover\nTel:      +49 511 / 89 35 407\nFax:     +49 511 / 89 35 411\nMobil:   +49 170 / 89 28 454\nE-Mail: f.poerschmann@outlook.de\nwww.zukunftsallianz-maschinenbau.de\n \nGeschäftsführender Vorstand:\nDr.-Ing. Volker Franke, Vorstandssprecher\nDipl.-Wirtsch.-Ing. Gerald Pörschmann, Netzwerkmanager\n

=== Example Output 1: ===

Name:     Gerald Pörschmann
Funktion: Geschäftsführender Vorstand
Telefon:  +49 511 - 89 35 407
Mobil:    +49 170 - 89 28 454
E-Mail:   f.poerschmann@outlook.de

Zukunftsallianz Maschinenbau e.V.
Deutsche Messe Technology Academy
Messegelände / Pavillon 36
30521 Hannover
www.zukunftsallianz-maschinenbau.de
Umsatz: keine Daten
Mitarbeiter: keine Daten


 === Example Input 2: ===\n

Von: Claudia HERMANUTZ-GAISSMAIER <Claudia.Hermanutz@diehl-defence.com>\nGesendet: Montag, 30. Oktober 2023 09:08\nAn: 'gerhard.reiner@hotmail.de' <gerhard.reiner@hotmail.de>\nBetreff: WG: Gestern in Schwenningen\n \nSehr geehrter Herr Reiner,\nanbei erhalten Sie zwei Terminvorschläge mit den HH Thum und Ring.\n \n01.12.2023 von 15:00 Uhr bis 15:45 Uhr per SKYPE\n05.12.2023 von 16:00 Uhr bis 16:45 Uhr per SKYPE\n \nMit freundlichen Grüßen\n \nClaudia Hermanutz\nBetriebwirtschaft\nDiehl Defence GmbH & Co. KG\nAlte Nußdorfer Straße 13\n88662 Überlingen\nTel:  07551/89-2832\nFax:  07551/89-4077\nmailto:claudia.hermanutz@diehl-defence.com\n \n

=== Example Output 2: ===\n

Name:     Claudia Hermanutz
Funktion: Betriebwirtschaft
Telefon:  07551-89-2832
E-Mail:   claudia.hermanutz@diehl-defence.com

Diehl Defence GmbH & Co. KG
Alte Nußdorfer Straße 13
88662 Überlingen
Umsatz: 700 Mio EUR - 1 Mrd EUR
Mitarbeiter: 2500 - 3500

")


(def data-adress
  "
  Hallo zusammen,\n \nanbei die Reservierungsbestätigungen für euer Hotel zu eurer Info.\n \nViele Grüße und schönes Wochenende\nSteffen\n \n\nMit freundlichen Grüßen - Best regards\n\nSteffen Besserer\nCorporate Culture\nTel: +49 9431 7143-9529\nMobil: +49 151 44051743\nE-Mail: steffen.besserer@horsch.com \n\nHORSCH Maschinen GmbH\nSitzenhof 1 - DE-92421 Schwandorf\n\nPflichtangaben / Compulsory details | Web\nDatenschutz Information / Privacy Information\n\nVon: Schuster Madlen <Madlen.Schuster@horsch.com> \nGesendet: Freitag, 27. Oktober 2023 07:35\nAn: Besserer Steffen <steffen.besserer@horsch.com>\nBetreff: WG: Reservierungsbestätigung aus Wolfringmühle
  ")





(def instruction-adress
  "
Transform the given data.

1. find the company and persons contact information in the data.
   Search for those fields for a person:
     full name
     position and / or position
     email
     mobil
     phone

   Search for those fields for a company:
     company name
     postal code and town
     street and number
     domain

2. if there are only parts of the requested fields available, provide those, eg:
   person:
     email
     mobile
3. separate the fields, if neccesary. E.g. if street, postal code and city are in the same line:
   - separate street and number from postal code and city in different lines.
4. provide all persons found, every information found line by line.
5. provide the company data line by line.
6. search for
    - the approximate, roughly estimated number of employees and
    - the approximate, roughly estimated turnover of each company
   Provide a range, e.g. 100 Mio - 500 Mio EUR, for the turnover estimation.
   Provide a range, e.g. 700 - 2000, for the emplyees estimation.
   Provide even rough estimations instead of no data.
7. Place the turnover and employee estimation range at the end of the company data.
   ")

(def prompt-config-adress {:model "gpt-4" ;"gpt-3.5-turbo"
                           :temperature 0.0
                           ;:max_tokens 2100
                           :messages [{:role "system"
                                       :content "You diligently complete tasks as instructed.\nYou never make up any information that isn't there."} ; You don't invent things.
                                      {:role "user"
                                       :content (str instruction-adress
                                                     "\n\n============ EXAMPLES ============\n"
                                                     examples-adress
                                                     "\n\n============ DATA TO TRANSFORM ============\n"
                                                     data-adress)}]})

(comment
  (try (api/create-chat-completion prompt-config-adress)
       (catch Exception e (println (ex-message e)))))


;;
;; find company data
;;

(def examples-company-data
  "
 === Example Input 1: ===

Diehl Defence GmbH & Co. KG

=== Example Output 1: ===

Diehl Defence GmbH & Co. KG
Umsatz: 700 Mio EUR - 1 Mrd EUR
Mitarbeiter: 2500 - 3500

 === Example Input 2: ===

HORSCH Maschinen GmbH
Sitzenhof 1
DE-92421 Schwandorf

=== Example Output 2: ===
HORSCH Maschinen GmbH
Mitarbeiter: 500 - 1.500
Umsatz: 200 Mio - 500 Mio Euro
")

(def data-company-data
  #_"RAFI GmbH & Co. KG\nRavensburger Straße 128-134\n88276 Berg\n"
  "mimatic GmbH\nWestendstraße 3\n87488 Betzigau (Germany)
  ruhlamat GmbH\nSonnenacker 2, D-99819 Marksuhl"
  #_"HORSCH Maschinen GmbH")

(def instruction-company-data
  "
Find data of the given company:

1. Please don't access real-time data, but try to find
    - the approximate, roughly estimated number of employees and
    - the approximate, roughly estimated turnover of each company
   Provide a range, e.g. 100 Mio - 500 Mio EUR, for the turnover estimation.
   Provide a range, e.g. 700 - 2000, for the emplyees estimation.
   Provide even rough estimations instead of no data.
2. Provide only turnover and employee data in the result.

   ")

(def prompt-config-company-data {:model "gpt-4" ;"gpt-3.5-turbo"
                                 :temperature 0.0
                                 ;:max_tokens 2100
                                 :messages [{:role "system"
                                             :content "You diligently complete tasks as instructed.\nYou never make up any information that isn't there."} ; You don't invent things.
                                            {:role "user"
                                             :content (str instruction-company-data
                                                           "\n\n============ EXAMPLES ============\n"
                                                           examples-company-data
                                                           "\n\n============ DATA TO TRANSFORM ============\n"
                                                           data-company-data)}]})

(comment
  (try (api/create-chat-completion prompt-config-company-data)
       (catch Exception e (println (ex-message e)))))



;;
;; find clojure solution
;;

(def examples-clj-data
  "
=== EXAMPLE 1 START for a pure function with tests ===

(require '[hyperfiddle.rcf :refer [tests]]

(defn square
  \"Takes a number x and returns the square of it.\"
  [x]
  (* x x) ; multiply

(tests
  (square 5) := 25
  (square -5) := 25
  (square 1) := 1
  (square 0) := 0
  :end-tests)

=== EXAMPLE 1 END for a pure function with tests ===\n(


=== EXAMPLE 2 START for some pure functions with tests and comments ===

(require '[hyperfiddle.rcf :refer [tests]]

   (defn square
     \"Takes a number x and returns the square of it.\"
     [x]
     (* x x) ; multiply

   (tests
     (square 5) := 25
     (square -5) := 25
     (square 1) := 1
     (square 0) := 0
     :end-tests)

=== EXAMPLE 2 END  for some pure functions with tests and comments ===
")






(def data-clj-data
  "
  1. Create a hiccup text field that lets the user input dates and validates the input.
  2. Use :on-change to update a local atom
  3. write a validation function to check for ISO date format.
  4. If :on-change got an Enter-key pressed event or :on-blur was called, start validating at every key press.
  5. Use :on-blur call a callback function, given to the component, if successfully validated.
  6. if clicking the calendar-icon on the left side of the input-field, open a date-picker.
  "

  #_"

  Create a function to calc the pow function, e.g
  (tests (pow 2 3) := 8
   :end-tests)

   ")



(def instruction-clj-data
  "
Help me do programming in clojure.

1. use only those libraries:
   [hyperfiddle.rcf :refer [tests]]
   [clojure.string :as str]
   [malli.core :as m]

2. create as much pure functions as you can.

3. comment any complicated line by a single line comment.
   e.g. :on-change #(reset! date (-> % .-target .-value)) ; read current value of input field from event data and set

4. write any comments and explanations as comments like this.
   ;; This code uses library xyz.
   ;; For more info see: https://github.com/wkok/openai-clojure/blob/main/doc/01-usage-openai.md
   ;; It is not necessary to specify the endpoint url if using the default OpenAI service.

5. create tests for corner cases.

6. Don't use markdown in your result. AVOID Blocks in ```clojure  ```
   Return pure clojure code.
   Write all text that is not clojure code as comments:
   E.g.:
   ;; Here is a simple implementation of your task. Please note that
   ;; this is a basic implementation and does not include all the features you requested.
   (ns my-namespace.core
     (:require [clojure.string :as str])

   ")

(def prompt-config-clj-data {:model "gpt-4" ;"gpt-3.5-turbo"
                             :temperature 0.0
                             :max_tokens 2100
                             :messages [{:role "system"
                                         :content "You diligently complete tasks as instructed.\nYou never make up any information that isn't there."} ; You don't invent things.
                                        {:role "user"
                                         :content (str instruction-clj-data
                                                       "\n\n============ EXAMPLES ============\n"
                                                       examples-clj-data
                                                       "\n\n============ TASK FOR YOU TO PERFORM ============\n"
                                                       data-clj-data)}]})


(defn cut-clojure [all-str]
  (let [intro-rest (str/split all-str #"```clojure")
        code (str/split (get intro-rest 1) #"```")]
    (get code 0)))

(comment
  (def result (try (api/create-chat-completion prompt-config-clj-data)
                   (catch Exception e (println (ex-message e)))))

  (def clj-str (cut-clojure (-> result :choices (get 0) :message :content)))

  ;; print it as ONE element
  (def one-arr-str (str "[" clj-str"]"))
  (def data (read-string one-arr-str))
  (cprint data)
  ;; compile it
  (load-string clj-str)


  nil)


