(ns gpt-vunds.http-parallel
  (:require [clojure.core.async :refer [go go-loop <!! <! >! chan close! thread alts! alts!! timeout]]))



;;
;; call http api in parallel
;;

(defn api-fake
  "API call that sleeps 200 to 400ms.
   10% of calls fail...
   90% of calls return a vector of doubles."
  [text]
  ;(println "calling api-fake: " text)
  (Thread/sleep (+ 200 (rand-int 200)))
  (let [fail (< (rand) 0.1)]
    (if fail
      (throw (ex-info "api-emb-fake failed" {:text text}))
      (let [nums   (map int text)
            num    (count nums)
            max    (apply max nums)
            normed (map #(double (/ % max)) nums)]
        (->> (repeat (inc (quot 100 num)) normed)
             flatten
             (take 5)
             vec)))))

(defn fn-in-chan
  "Run a fn-call in a go-block and return a channel with the result."
  [fn-call]
  (let [result-chan (chan)]
    (go (>! result-chan
            (try (let [_ (print "S")
                       _ (flush)
                       result (fn-call)
                       _ (print "E")
                       _ (flush)]
                   result)
                 (catch Exception e {:exception (ex-message e)
                                     :data (ex-data e)}))))
    result-chan))

(defn fns-n
  "Creates n fns that can be called without parameters."
  [n] (mapv #(fn [] (api-fake (str %"-text"))) (range n)))

(comment
  (fns-n 50))

(defn chans-of-fns-n
  "Creates n channels with the results of the fns."
  [fns]
  (mapv #(fn-in-chan %) fns))

(comment
  (chans-of-fns-n (fns-n 50)))

(defn results-of-chans
  "Returns a vector of the results of the channels.
   Every "
  [chans]
  (let [size (count chans)]
    (loop [i 0
           results []]
      (if (= i size)
        results
        (let [[result ch] (alts!! chans)]
          (print ".")(flush)
          (recur (inc i)
                 (conj results result)))))))

(defn results-of-fns
  "Calls a vector of fns in parallel. Depending on the
   number of threads in the threadpool, this can be
   MUCH faster than calling the fns sequentially.
   Thread-Pool default size = processors + 2
   Setting it higher, up to 500...
   (System/setProperty \"clojure.core.async.pool-size\" \"50\")"
  [fns-vec]
  (-> fns-vec ;; vector of (fn [] (api-call par1 par2 ...)
      chans-of-fns-n ;; vector of channels, each with a result of an api-call
      results-of-chans)) ;; vector of vals of channels

(comment
  (System/setProperty "clojure.core.async.pool-size" "50")
  (System/setProperty "clojure.core.async.pool-size" "500")
  ;; Threadpool to 50. Then:
  ;; 4 seconds for 550 calls that take between 200 and 400 ms each
  ;; sequentially, it would be...
  ;; try it. Set it to 1, reload jvm. Then run the code below.
  ;; (System/setProperty "clojure.core.async.pool-size" "1")
  ;; 166 seconds!
  (-> (fns-n 550) ;; vector of (fn [] (api-call par1 par2 ...)
      chans-of-fns-n ;; vector of channels, each with a result of an api-call
      results-of-chans ;; vector of vals of channels
      first
      time)

  (time (results-of-fns (fns-n 550)))

  nil)





