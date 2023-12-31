(ns gpt-vunds.from-file
  (:require
    [clojure.string :as str]
    [wkok.openai-clojure.api :as api]
    [puget.printer :refer [cprint]]
    [hyperfiddle.rcf :refer [tests]]
    [gpt-vunds.streaming :as streaming])
  (:gen-class))




(defn read-files-to-prompt [base-name arg-prompt]
  (try (let [_ (println "trying to read 3 files:")
             instr (str base-name "--instructions.txt")
             _ (println "reading file:" instr)
             instr-data (slurp instr)
             examp (str base-name "--examples.txt")
             _ (println "reading file:" examp)
             examp-data (slurp examp)
             promp (str base-name "--prompt.txt")
             _ (when-not arg-prompt (println "reading file:" promp))
             promp-data (or arg-prompt (slurp promp))
             complete-prompt (str "==== GENERAL INSTRUCIONS ====\n"
                                  instr-data
                                  "\n\n==== EXAMPLES ====\n"
                                  examp-data
                                  "\n\n==== YOUR TASK ====\n"
                                  promp-data)]
         {:data complete-prompt})
       (catch Exception e {:error (str "ERROR: " (ex-message e))})))


(defn cut-clojure [all-str]
  (let [intro+rest (str/split all-str #"```clojure")
        code+rest (second intro+rest)]
    (if code+rest
      (first (str/split code+rest #"```"))
      nil)))

(comment
  (str/split "sdf sdfsdf " #"```clojure")
  (cut-clojure "asöldfj öklajsd fölkjas ```clojure dföljk```sdfsdf")
  (cut-clojure "asöldfj öklajsd fölkjas ```clojure dföljk")
  (cut-clojure "asöldfj öklajsd fölkjas sdfsdf")

  (def clj-str (cut-clojure (-> result :choices (get 0) :message :content)))

  ;; print it as ONE element
  (def one-arr-str (str "[" clj-str"]"))
  (def data (read-string one-arr-str))
  (cprint data)
  ;; compile it
  (load-string clj-str)


  nil)

(defn print-help []
  (println "Wrong arguments!")
  (println "Please provide ONE OR TWO OPTIONS!")
  (println "first: base-name for config-files")
  (println "second: your prompt (optional)")
  (println "Example 1")
  (println "gpt-vunds books \"suggest me a list of  ")
  (println "---- only ONE parameter")
  (println "Parameter 'employee-names' will result in reading the files:")
  (println "'employee-names--instructions.txt' contains STABLE, general instructions")
  (println "'employee-names--examples.txt'     contains EXAMPLES for INPUT and expected OUTPUT")
  (println "'employee-names--user-prompt.txt'  contains YOUR SPECIFIC INPUT for the request.")
  (println "")
  (println "---- TWO parameters")
  (println "Parameter 'employee-names' will result in reading the files:")
  (println "'employee-names--instructions.txt' contains STABLE, general instructions")
  (println "'employee-names--examples.txt'     contains EXAMPLES for INPUT and expected OUTPUT")
  (println "'employee-names--user-prompt.txt'  WILL BE IGNORED.")
  (println "instead, the second parameter will be used as prompt."))


(defn -main [& args]
  (if (<= 1 (count args) 2)
    (let [base-name (first args)
          result (read-files-to-prompt base-name (second args))
          error (:error result)
          prompt (:data result)]
      (if prompt
        (let [prompt-config {:model "gpt-4" ;"gpt-3.5-turbo"
                             :temperature 0.0
                             ;:max_tokens 2100
                             :messages [{:role "system"
                                         :content "You diligently complete tasks as instructed.\nYou never make up any information that isn't there."} ; You don't invent things.
                                        {:role "user"
                                         :content prompt}]}
              _           (println (str "PROMPT:\n" prompt))
              result (try (streaming/stream-api prompt-config
                                                (fn [chunk] (print chunk) (flush))
                                                (fn [] (println "\n\nDONE! Next?")))
                          (catch Exception e (println (ex-message e))))])


        (println "there was an issue assembling the prompt:\n" error)))
    (print-help)))

(comment
  (-main "books" "provide famous autobiographies of german people in business")
  (-main "clj" "show me how :on-key-pressed can catch return key."))

(comment
  ; this could be the 'main loop' for interactive use
  (loop []
    (println  "Enter a number: ")
    (let [b (read-line)]
      (println "your val:" b)
      (recur))))




(defn old-main [& args]
  (if (<= 1 (count args) 2)
    (let [base-name (first args)
          result (read-files-to-prompt base-name (second args))
          error (:error result)
          prompt (:data result)]
      (if prompt
        (let [prompt-config {:model "gpt-4" ;"gpt-3.5-turbo"
                             :temperature 0.0
                             :messages [{:role "system"
                                         :content "You diligently complete tasks as instructed.\nYou never make up any information that isn't there."} ; You don't invent things.
                                        {:role "user"
                                         :content prompt}]}
              _           (println (str "PROMPT:\n" prompt "\n\n"))
              result (try (api/create-chat-completion prompt-config)
                          (catch Exception e (println (ex-message e))))
              content (-> result :choices (get 0) :message :content)
              clj-str (cut-clojure content)
              data (if clj-str
                     (read-string (str "[" clj-str"]"))
                     content)]
          (println content)
          (when clj-str
            (println "\n\nPURE CLJ:")
            (println clj-str)))
        ;(cprint data)
        ;(println "\n\nEXECUTING clojure:")
        ;(load-string clj-str)

        (println "there was an issue assembling the prompt:\n" error)))
    (print-help)))


