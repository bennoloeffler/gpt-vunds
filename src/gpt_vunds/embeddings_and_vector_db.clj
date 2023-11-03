(ns gpt-vunds.embeddings-and-vector-db
  (:require     [wkok.openai-clojure.api :as api]))


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

(defn break-into-chunks [s chunk-size]
  (let [chunks (partition-all chunk-size s)]
    (map #(apply str %) chunks)))

(defn document-texts-from_dir [dir-path]
   (map
     #(slurp %)
     (-> (clojure.java.io/file dir-path)
         (file-seq)
         (rest))))

(defn document-texts-to-chunks [strings]
   (flatten (map #(break-into-chunks % 200) strings)))

(def directory-path "vec-orig-docs")

(def doc-strings (document-texts-from_dir directory-path))

(def doc-chunks
   (filter
     #(> (count %) 40)
     (document-texts-to-chunks doc-strings)))

(def chunk-embeddings
  (pmap #(api-emb %) doc-chunks))

;;
;; THIS IS the db, it maps the embedding vector to the text chunk
;;
(def embeddings-with-chunk-texts (atom nil))

(defn get-embeddings-db []
  (if @embeddings-with-chunk-texts
    @embeddings-with-chunk-texts
    (reset! embeddings-with-chunk-texts
            (mapv vector chunk-embeddings doc-chunks))))

(defn best-vector-match [query]
  (let [query-emb (api-emb query)
        similarity (mapv (fn [[emb txt]]
                           (let [sim (dot-product query-emb emb)]
                             {:similarity sim
                              :emb emb
                              :txt txt}))
                         (get-embeddings-db))]
    (reverse (sort-by :similarity similarity))))

(comment
  (set! *print-length* 10)

  ;; initialize
  (get-embeddings-db)

  ;; use it
  (clojure.pprint/pprint
    (first (get-embeddings-db)))

  (best-vector-match "is there a project that is a SPA?"))
