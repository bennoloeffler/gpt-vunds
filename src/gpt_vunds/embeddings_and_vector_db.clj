(ns gpt-vunds.embeddings-and-vector-db
  (:require     [wkok.openai-clojure.api :as api]
                [hyperfiddle.rcf :refer [tests]]))

;; Enabling the tests
(hyperfiddle.rcf/enable! true)

;; https://towardsdatascience.com/getting-started-with-weaviate-a-beginners-guide-to-search-with-vector-databases-14bbb9285839
;; https://leanpub.com/clojureai
;; free online: https://leanpub.com/clojureai/read#leanpub-auto-question-answering-using-openai-apis-and-a-local-embeddings-vector-database

(defn emb->vec [emb]
  (-> emb :data (get 0) :embedding))

(defn dot-product [a b]
  (reduce + (map * a b)))

(defn api-emb [text]
  (emb->vec (api/create-embedding {:input text
                                   :model "text-embedding-ada-002"
                                   :encoding-format "float" #_"base64"})))
;;
;; Distance between embeddings
;;

(comment
  (def emb-fuesse (api-emb "Füsse am Körper"))
  (def emb-arme (api-emb "Arme und Hände"))
  (def emb-fluesse (api-emb "Flüsse sind z.B. Donau und Rhein"))
  (def emb-meer (api-emb "Das Meer besteht aus Salzwasser"))

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
;; -------- VERY simple vector db -------------
;;

#_(defn string-to-floats [s]
    (map
      #(Float/parseFloat %)
      (clojure.string/split s #" ")))

#_(defn truncate-string [s max-length]
    (if (< (count s) max-length)
      s
      (subs s 0 max-length)))


(defn break-into-chunks
  "Takes a string s and a chunk size, and breaks the string into chunks of the specified size."
  [s chunk-size]
  (let [chunks (partition-all chunk-size s)] ; partition the string into chunks
    (map #(apply str %) chunks))) ; convert each chunk into a string

;; Writing tests for the function
(tests
  ;; Testing with a string of length 10 and chunk size 2
  (break-into-chunks "abcdefghij" 2) := ["ab" "cd" "ef" "gh" "ij"]
  ;; Testing with a string of length 10 and chunk size 3
  (break-into-chunks "abcdefghij" 3) := ["abc" "def" "ghi" "j"]
  ;; Testing with a string of length 10 and chunk size 1
  (break-into-chunks "abcdefghij" 1) := ["a" "b" "c" "d" "e" "f" "g" "h" "i" "j"]
  ;; Testing with a string of length 10 and chunk size 5
  (break-into-chunks "abcdefghij" 5) := ["abcde" "fghij"]
  ;; Testing with a string of length 10 and chunk size 10
  (break-into-chunks "abcdefghij" 10) := ["abcdefghij"]
  ;; Testing with a string of length 10 and chunk size 11
  (break-into-chunks "abcdefghij" 11) := ["abcdefghij"]
  ;; Testing with an empty string and chunk size 2
  (break-into-chunks "" 2) := []
  ;; Testing with a string of length 1 and chunk size 2
  (break-into-chunks "a" 2) := ["a"]
  :end-tests)

(def directory-path "vec-orig-docs")

(defn read-all-files-in
  "Takes a path and returns a vector of maps with file names and their content."
  [path]
  (let [files (file-seq (clojure.java.io/file path))] ; get all files in the directory
    (->> files
         (filter #(.endsWith (.getName %) ".md")) ; filter only text files
         (map (fn [f] {:file-name (.getName f) :text (slurp f)})) ; read file content and create a map
         (into [])))) ; convert to vector

(comment
  (read-all-files-in directory-path))

(defn chunks-from-text [file-vec]
  (map (fn [{:keys [text] :as e}]
         (assoc e :chunks (break-into-chunks text 200))) file-vec))

(comment
  (chunks-from-text (read-all-files-in directory-path)))

#_(defn document-texts-from_dir [dir-path]
     (map
       #(slurp %)
       (-> (clojure.java.io/file dir-path)
           (file-seq)
           (rest))))

#_(comment
    (document-texts-from_dir "vec-orig-docs"))

#_(defn document-texts-to-chunks [strings]
     (flatten (map #(break-into-chunks % 200) strings)))


#_(def doc-strings (document-texts-from_dir directory-path))

#_(def doc-chunks
     (filter
       #(> (count %) 40)
       (document-texts-to-chunks doc-strings)))

(defn embeddings-for-one-file [{:keys [file-name chunks]}]
  (vec (pmap (fn [chunk] {:file-name file-name :embedding (api-emb chunk) :chunk chunk}) chunks)))

(defn chunk-embeddings []
  (flatten (vec (pmap embeddings-for-one-file
                      (chunks-from-text (read-all-files-in directory-path))))))

(comment (flatten [ [ {:a 1} {:b 1}] [{:c 1}{:c 1}]]))
;;
;; THIS IS the db, it maps the embedding vector to the text chunk
;;
(def embeddings-db (atom nil))

(defn get-embeddings-db []
  (if @embeddings-db
    @embeddings-db
    (reset! embeddings-db
            (chunk-embeddings))))

(comment
  (get-embeddings-db))
;;
;; Compare similarity of ALL embeddings based on dot-product
;; This would be done by a vector-db in "real" applications
;;
(defn best-vector-match [query]
  (let [query-emb (api-emb query)
        similarity (mapv (fn [{:keys [embedding] :as emb}]
                           (let [sim (dot-product query-emb embedding)]
                             {:similarity sim
                              :emb emb}))
                         (get-embeddings-db))]
    (reverse (sort-by :similarity similarity))))

(comment
  (set! *print-length* 10)

  ;; initialize
  (get-embeddings-db)

  ;; use it
  (clojure.pprint/pprint
    (first (get-embeddings-db)))

  (best-vector-match "Is there a project that is a SPA? That means: a fullstack web app in clojure!"))
