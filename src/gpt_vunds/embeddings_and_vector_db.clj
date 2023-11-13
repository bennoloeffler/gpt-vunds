(ns gpt-vunds.embeddings-and-vector-db
  (:require     [wkok.openai-clojure.api :as api]
                [hyperfiddle.rcf :refer [tests]]
                [gpt-vunds.doc-to-txt :as doc-to-txt]
                [clojure.edn :as edn]
                [clojure.java.io :as io]
                [gpt-vunds.streaming :as streaming]
                [puget.printer :refer [cprint]]
                [clojure.string :as str])
  (:import [java.text SimpleDateFormat]
           [java.util Date]))


;; Enabling the tests
(hyperfiddle.rcf/enable! true)


;; https://towardsdatascience.com/getting-started-with-weaviate-a-beginners-guide-to-search-with-vector-databases-14bbb9285839
;; https://leanpub.com/clojureai
;; free online: https://leanpub.com/clojureai/read#leanpub-auto-question-answering-using-openai-apis-and-a-local-embeddings-vector-database
;; https://www.featureform.com/post/the-definitive-guide-to-embeddings
;; https://supabase.com/blog/openai-embeddings-postgres-vector
;; https://youtu.be/yfHHvmaMkcA?si=GCvq8JKp9ZBoYjf0


(defn emb->vec
  "Extract the embedding data vector from the api answer."
  [emb]
  (-> emb :data (get 0) :embedding))

(defn dot-product
  "The dot product of two vectors a and b.
  Will be interpreted as semantical similarity.
  The bigger the more similar."
  [a b]
  (reduce + (map * a b)))

(defn api-emb
  "Call openai api to create an embedding for text."
  [text]
  (-> (api/create-embedding {:input           text
                             :model           "text-embedding-ada-002"
                             :encoding-format "float" #_"base64"})
      emb->vec))
;;
;; distance between embeddings
;;

(comment
  (set! *print-length* nil)
  (set! *print-length* 10)

  (def emb-fuesse (api-emb "Füsse am Körper sind dazu da, den Mensch zu bewegen."))
  (def emb-arme (api-emb "Arme und Hände erlauben es einem Primaten, Werkzeuge zu nutzen."))
  (def emb-fluesse (api-emb "Flüsse sind z.B. Donau und Rhein. Sie fließen primär :-)"))
  (def emb-meer (api-emb "Das Meer besteht aus Salzwasser. Ströme aus Süsswasser ergießen sich."))

  (def fuesse->arme (dot-product emb-fuesse emb-arme))
  (def arme->fuesse (dot-product emb-arme emb-fuesse))
  (def arme->fluesse (dot-product emb-arme emb-fluesse))
  (def meer->fluesse (dot-product emb-meer emb-fluesse))

  (println "fuesse->arme" fuesse->arme)
  (println "arme->fuesse" arme->fuesse)
  (println "arme->fluesse" arme->fluesse)
  (println "meer->fluesse" meer->fluesse)

  nil)

;;
;; -------- very, VERY simple vector db -------------
;;
(def data-store-path "vec-orig-docs")
(def max-chunk-size 200)

(defn break-into-chunks
  "Takes a string s and a chunk size, and breaks the string into chunks of the specified size."
  [s chunk-size]
  (let [chunks (partition-all chunk-size s)] ; partition the string into chunks
    (map #(apply str %) chunks))) ; convert each chunk into a string

(defn read-all-files-in
  "Takes a path and returns a vector of maps with all .md file names and their content.
  [{:file-name \"abd/eded.md\"
    :text \"text\"}
   {...}]"
  [path]
  (let [files (file-seq (clojure.java.io/file path))]
    (->> files
         (filter #(.endsWith (.getName %) ".md"))
         (map (fn [f] {:file-name (.getName f) :text (slurp f)}))
         (into []))))

(comment
  (read-all-files-in data-store-path))

(defn chunks-from-text
  "Assuming file-vec looks like this:
  [{:file-name \"abd/eded.md\"
    :text \"text\"}
   {...}]
   cut the :text to chunks and save it in :chunks
   resulting in
   [{:file-name \"abd/eded.md\"
     :text \"text\"
     :chunks (\"te..\" \"..xt\")}
    {...}]"
  [file-vec]
  (map (fn [{:keys [text] :as e}]
         (assoc e :chunks (break-into-chunks text max-chunk-size))) file-vec))

(comment
  (chunks-from-text (read-all-files-in data-store-path)))

(defn embeddings-for-one-file
  "Delivers embeddings for all chunks of a file.
   Uses openai embeddings api!
   Provide something like
   {:file-name \"abd/eded.md\"
    :chunks (\"te..\" \"..xt\")}
   and get a vector of data maps like:
   [{:file-name \"abd/eded.md\"\n
     :embedding  [8.67866 -0.67544 ...]
     :chunk \"te..\"}
    {...}]
   "
  [{:keys [file-name chunks]}]
  (let [result (mapv (fn [chunk] (do
                                   (print ".") (flush)
                                   {:file-name file-name :embedding (api-emb chunk) :chunk chunk})) chunks)]
    (println file-name)
    result))

(comment
  (embeddings-for-one-file {:file-name "abd/eded.md"
                            :text "text"
                            :chunks '("te.." "..xt")}))

(defn chunk-embeddings
  "Converts all .docx to .md and returns embeddings for all files in one huge flat data structure:
  ({:file-name \"abc.docx.md\"
    :embedding [0.7866 -8.9847 ...]
    :chunk \"some text from the fi\"}
   {...}
   {...}
   ...)"
  []
  (doc-to-txt/convert-all-docs-to-md data-store-path)
  (flatten (vec (map embeddings-for-one-file
                      (chunks-from-text (read-all-files-in data-store-path))))))

;;
;; embeddings-db: it maps the embedding vector to the text chunk and file-name.
;; There is a file-cache in order to not create the embeddings to memory every time.
;;
(def emb-db (atom nil))
(def emb-db-path (str data-store-path "/embeddings-db.edn"))

(defn file-exists? [path]
  (.exists (io/file path)))

(defn write-edn-to-file [data path]
  (binding [*print-length* nil]
    (spit path (pr-str data))))

(defn read-edn-from-file [path]
  (with-open [rdr (io/reader path)]
    (edn/read (java.io.PushbackReader. rdr))))

(defn del-embeddings-db []
  (if (file-exists? emb-db-path)
    (io/delete-file emb-db-path)
    (println "embeddings file did not exist: " emb-db-path))
  (if @emb-db
    (reset! emb-db nil)
    (println "embeddings db in memory did not exist.")))

(defn get-embeddings-db
  "embeddings-db is a sequence of embeddings, that
   have their origin chunk and file-name with them
   in a map:
    ({...}
     {:file-name \"xyz\"
      :embedding [5.275254E-4 -0.01249235 ...]
      :chunk \"Leihkräfte da (150%).\"}
      {...}
      {...})
   If embeddings-db is in memory, return it.
   If embeddings-db-file-name exists: slurp, set embeddings-db and return it.
   If no cache exists in memory or file: create it and return it."
  []
  (if @emb-db
    @emb-db
    (if (file-exists? emb-db-path)
      (do
        (println (str "read CACHED embeddings from file: " emb-db-path))
        (reset! emb-db (read-edn-from-file emb-db-path))
        (println "done.\n\n")
        @emb-db)
      (do
        (println "CREATING NEW embeddings with api...")
        (reset! emb-db
                (chunk-embeddings))
        (println (str "WRITING to file: " emb-db-path))
        (write-edn-to-file @emb-db emb-db-path)
        (println "done.\n\n")
        @emb-db))))

;;
;; part of the vector-db: comparing stored embeddings to a given one
;;
;; Compare similarity of ALL embeddings based on dot-product
;; This would be done by a "real" vector-db in "real" applications
;; because scaling and speed are a major problem to solve.
;;
(defn sorted-matches
  "returns all entries found in embeddings-db enriched by the
   similarity to the query-str, sorted by similarity.
   "
  [query-str]
  (let [query-emb  (api-emb query-str)
        similarity (mapv (fn [{:keys [embedding] :as emb}]
                           (let [sim (dot-product query-emb embedding)]
                             {:similarity sim
                              :emb        emb}))
                         (get-embeddings-db))]
    (reverse (sort-by :similarity similarity))))

(defn best-matches-filtered
  "Returns a maximum of n matches in a vector.
   Data of a match is a map:
   {:similarity 0.89
    :file-name  \"path/filename.docx.md\"
    :chunk      \"ere is the data, that is trun\"}"
  [query n]
  (mapv (fn [{:keys [similarity emb] :as data}]
          {:similarity similarity
           :file-name  (:file-name emb)
           :chunk      (:chunk emb)})
        (take n (sorted-matches query))))


;;
;; RAG - what is that?
;; 0. put data in a vector-db, together with embeddings for similarity search
;; 1. RETRIEVE data in vector-db, based on distance to embeddings.
;; 2. AUGMENT prompt with this data
;; 3. GENERATE an answer, based on that augmented prompt, that contains "my domain knowledge".
;; R_etrieval-A_ugmented G_eneration
;;

(comment
  (set! *print-length* nil)
  (set! *print-length* 100)

  ;; ATTENTION: delete file and memory cache
  #_(del-embeddings-db)

  ;; 0. initialize
  (get-embeddings-db)

  ;; use it
  (clojure.pprint/pprint
    (first (get-embeddings-db)))

  ;; this is the prompt the user writes
  (def my-question "Was ist ein Schutzraum? Welche Regeln gibt es? Wozu ist er gut?")

  ;; 1. calc similarity and get data matches out of vector-db
  (def matches (best-matches-filtered my-question 3))

  ;; 2. augment the prompt with the my data found locally
  (def messages [{:role "system" :content (str "Du hilfst gerne.\n1. Gib in Deinen Antworten die Datei/Quelle mit an.\n"
                                               "2. Format the text to a page width of about 60 characters. End the lines with '\\n'\n"
                                               "Beispiel für Formatierung:\n---\n"
                                               "  Remember that binding only affects the current thread by default.\n"
                                               "  If the code within binding is multi-threaded, you'll need to\n"
                                               "  ensure that the Var is appropriately conveyed to other threads,\n"
                                               "  for example, by using bound-fn or future-call with binding-conveyor-fn."
                                               "\n---\n")}
                 {:role "user"
                  :content (str "Hier sind einige hilfreiche Informationen von mir, die Du bitte mit Deinen Informationen verknüpfst:\n"
                                (apply str (mapv (fn [{:keys [file-name chunk]}]
                                                   (str "===  Aus der Datei/Quelle '" file-name "' ist folgende Information:\n" chunk "\n\n")) matches)))}
                 {:role "assistant" :content "Ok! Was ist Deine Frage?"}
                 {:role "user" :content my-question}])

  ;;
  ;; 3. GENERATE an answer, that uses the very special knowledge in form of chunks and file-names added to prompt
  ;;
  ;; without streaming
  ;;
  (cprint messages)
  (api/create-chat-completion {:model "gpt-4"
                               :messages messages})


  ;;
  ;; 3. GENERATE
  ;;
  ;; with streaming
  ;;
  (streaming/stream-api {:model "gpt-4"
                         #_:temperature #_0
                         #_:stream #_true ; not needed!
                         :messages messages}
              (fn data [chunk] (print chunk) (flush))
              (fn done [] (println) (println "------------ gpt: DONE ------------")))


  nil)


;;
;; DIALOG with history!
;; Now make it a simple api, that may be able to do it in a dialogue
;;

;; this is the store for the dialog
(def dialog-messages (atom nil))

(defn init-matches [matches my-message]
  [{:role "system" :content (str "Du hilfst gerne.\n"
                                 "1. Gib in Deinen Antworten die Datei/Quelle mit an.\n"
                                 "2. Format the text to a page width of about 60 characters. End the lines with '\\n'\n"
                                 "Beispiel für Formatierung:\n---\n"
                                 "  Remember that binding only affects the current thread by default.\n"
                                 "  If the code within binding is multi-threaded, you'll need to\n"
                                 "  ensure that the Var is appropriately conveyed to other threads,\n"
                                 "  for example, by using bound-fn or future-call with binding-conveyor-fn."
                                 "\n---\n")}
   {:role    "user"
    :content (str "Hier sind einige hilfreiche Informationen von mir, die Du bitte mit Deinen Informationen verknüpfst:\n"
                  (apply str (mapv (fn [{:keys [file-name chunk]}]
                                     (str "===  Aus der Datei/Quelle '" file-name "' ist folgende Information:\n" chunk "\n\n")) matches)))}
   {:role "assistant" :content "Ok! Was ist Deine Frage?"}
   {:role "user" :content my-message}])

(defn append-matches [matches my-message]
  (swap! dialog-messages conj
         {:role    "user"
          :content (str "Hier sind einige hilfreiche Informationen von mir, die Du bitte mit Deinen Informationen verknüpfst:\n"
                        (apply str (mapv (fn [{:keys [file-name chunk]}]
                                           (str "===  Aus der Datei/Quelle '" file-name "' ist folgende Information:\n" chunk "\n\n")) matches)))})
  (swap! dialog-messages conj
         {:role "assistant" :content "Ok! Was ist Deine Frage?"})
  (swap! dialog-messages conj
         {:role "user" :content my-message}))

(def current-message (atom ""))

(defn talk [my-message]
  ;; 1. calc similarity and get data matches out of vector-db
  (let [matches (best-matches-filtered my-message 10)]

    ;; 2. augment the prompt with the my data found locally
    (if @dialog-messages
      (reset! dialog-messages (append-matches matches my-message))
      (reset! dialog-messages (init-matches matches my-message)))
    (streaming/stream-api {:model    "gpt-4"
                           #_:temperature #_0
                           #_:stream #_true ; not needed!
                           :messages @dialog-messages}
                          (fn data [chunk]
                            (print chunk)
                            (flush)
                            (swap! current-message #(str % chunk)))
                          (fn done []
                            (println)
                            (println "------------ gpt: DONE ------------")
                            (swap! dialog-messages conj
                                   {:role "assistant" :content @current-message})
                            (reset! current-message "")))))


(defn formatted-timestamp []
  (let [sdf (new SimpleDateFormat "yyyy-MM-dd'_'HH'h'mm'm'ss's'")]
    (.format sdf (Date.))))

(comment
  (formatted-timestamp))

(defn dialog-file-name [file-name-comment]
  (str data-store-path "/saved-dialog_" (formatted-timestamp) "__" file-name-comment ".edn"))

(comment
  (dialog-file-name "abd-die-Katze-liegt"))


(defn reset-dialog []
  (reset! current-message "")
  (reset! dialog-messages nil))

(defn save-and-reset [file-name-comment]
  (write-edn-to-file @dialog-messages (dialog-file-name file-name-comment))
  (reset-dialog))

(defn load-dialog [file-name]
  (if-not @dialog-messages
    (let [path  (str data-store-path "/" file-name)
          new-m (read-edn-from-file path)]
      (reset! dialog-messages new-m)
      (println "loaded:" path))
    (println "THERE IS AN UNSAFED DIALOG! \nEither (reset-dialog)or (save-and-reset \"hint-for-filename\")")))

(comment

  ;; delete file and memory cache
  #_(del-embeddings-db)

  ;; NOT NEEDED: initialize
  (do (get-embeddings-db)
      nil)

  (reset-dialog)

  (talk "bitte hilf mir ein Angebot zu einer Potenzialanalyse für die Firma IBM zu schreiben. Es geht dabei um die Verbesserung des Auftragsdurchlaufes. Zähle auf, was wir alles tun. Biete optional auch einen Open-Space an.")
  (talk "viel ausführlicher")
  (talk "beschreibe Open-Space als eigenes Arbeitspaket mit 2. viel ausführlicher. Lass Definition von Handlungsfreiräumen weg.")
  (talk "und jetzt beschreibe die Arbeitspakete mit nummerierten Schritten und Abschliessend einem Ergebnis.")
  (reset-dialog)

  (talk "was ist ein Schutzraum?")
  (talk "wer tut das?")
  (talk "was tut man, um einen Schutzraum zu stiften?")
  (talk "hat der Schutzraum etwas mit Jahreszeiten zu tun?")
  (save-and-reset "schutzraum")

  (talk "was kannst Du mir über Kapaplanung sagen? vi-pipe?")
  (talk "abgesehen von den Informationen in meinen Beispielen: Wie funktioniert Kapazitätsplanung?")
  (save-and-reset "kapa-planung")


  (load-dialog "saved-dialog_2023-11-05_17h58m25s__schutzraum.edn")
  (talk "lass uns ausschließlich über Schutzräume sprechen...")

  ;; TODO
  ;;   1. find semantic good splitting for chunks
  ;;   2. try pgvector
  ;;   3. run creation of embeddings via api in parallel

  nil)









