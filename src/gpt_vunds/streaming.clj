(ns gpt-vunds.streaming
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.string :as str])
  (:import [java.io BufferedReader InputStreamReader]))

(def openai-api-key (System/getenv "OPENAI_API_KEY")) ; Replace with your actual OpenAI API key
(def openai-url "https://api.openai.com/v1/chat/completions")

(defn stream-api [payload callback-when-chunk callback-when-done]
  (let [done? (fn [chunk] (= "data: [DONE]" (str/trim chunk)))
        data? (fn [chunk] (and
                            (not= "" (str/trim chunk))
                            (not (str/includes? chunk "\"finish_reason\":\"stop\""))
                            (not= "data: [DONE]" (str/trim chunk))))
        chunk->data (fn [chunk] (json/read-str (subs chunk 6) :key-fn keyword))
        extract-chunk-content (fn [data] (-> data :choices (get 0) :delta :content))

        headers {"Authorization" (str "Bearer " openai-api-key)
                 "Content-Type"  "application/json"}
        payload (assoc payload :stream true)
        response (client/post openai-url
                              {:headers headers
                               :body (json/write-str payload)
                               :content-type :json
                               :as :stream})]
    (doseq [chunk (line-seq (BufferedReader. (InputStreamReader. (:body response))))]
      ;(prn chunk)
      (if (data? chunk)
        (let [data (chunk->data chunk)
              content (extract-chunk-content data)]
          (callback-when-chunk content))
        (when (and (done? chunk)
                   callback-when-done)
          (callback-when-done))))))


(comment

  (stream-api {:model "gpt-3.5-turbo"
               :temperature 0.8
               #_:stream #_true ; not needed!
               :messages [{:role "system" :content "You are an technology obsessed scientist. You cant stop talking from physics and chemistry."}
                          {:role "user" :content "Please write a very, very short poem about cyberpunks. In German language."}]}
              (fn [chunk] (print chunk) (flush))
              (fn [] (println "\n\nDONE! Next?"))))