(ns gpt-vunds.doc-to-txt
  (:require [clojure.string :as str])
  (:import [org.apache.poi.xwpf.usermodel XWPFDocument]
           [java.io FileInputStream]))

(defn doc-to-txt [path-to-word-doc]
  ;(println path-to-word-doc)
  (with-open [doc-stream (FileInputStream. path-to-word-doc)]
    (let [doc (XWPFDocument. doc-stream)
          paragraphs (.getParagraphs doc)]
      (->> paragraphs
           (map #(.getText %))
           (str/join "\n")))))

(defn create-txt-from-docx [path-to-word-doc]
  (spit (str path-to-word-doc ".md") (doc-to-txt path-to-word-doc)))

(comment
  (doc-to-txt "vec-orig-docs/ExperimentimSchutzraum.docx")
  (create-txt-from-docx "vec-orig-docs/ExperimentimSchutzraum.docx"))

(defn convert-all-docs-to-md
  [path]
  (let [files (file-seq (clojure.java.io/file path))] ; get all files in the directory
    (->> files
         (filter #(and (.endsWith (.getName %) ".docx")
                       (not (.startsWith (.getName %) "~"))))
         (mapv #(create-txt-from-docx (.getAbsolutePath %))))))

(comment
  (convert-all-docs-to-md "vec-orig-docs"))