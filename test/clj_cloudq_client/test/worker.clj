(ns clj-cloudq-client.test.worker
  (:use [clj-cloudq-client.core]
        [clj-cloudq-client.worker]
        [clj-cloudq-client.test.config]
        [clojure.data.json :only (read-json)]
        [slingshot.slingshot :only [throw+ try+]]
        [clojure.test]))

(def queue "clj-test")
(def props (load-props "cloudq.properties"))

(defn make-counter [init-val]
  (let [c (atom init-val)] #(swap! c inc)))

(defn counter-run
  [c job]
  (let [args (:args job)
        count (c)]
    (println args)
    (if (not= "exit" args)
      (println count ":"  args)
      (throw (Exception. "whatup?")))))

(deftest test-run ;; FIXME: write
  (let [queue-url (str (:url props) queue)
        c (make-counter 0)
        success-fn (partial counter-run c)]
    (dotimes [n 2]
      (post-job queue-url
                "greeting"
                "hello"
                (:username props)
                (:password props)))
    (post-job queue-url
              "greeting"
              "exit"
              (:username props)
              (:password props))
    (try+
     (run (:url props)
         queue
         (:username props)
         (:password props)
         success-fn
         (fn [j o]
           (println j ":" o)
           (throw (Exception. "bye")))
         10000)
     (catch Object o))
    (is (<= 3
           (c)))))
