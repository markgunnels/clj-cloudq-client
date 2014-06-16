(ns clj-cloudq-client.worker
  (:require [clj-http.client :as http])
  (:use [clojure.data.json :only (json-str
                                  write-json
                                  read-json)]
        [slingshot.slingshot :only [throw+ try+]]
        clj-cloudq-client.core))

(defn retrieve-job-from-cloudq
  [url username password]
  (try+
   (get-job url username password)
   (catch Object o
     (println o))))

(defn run
  "This is a generic worker. It accepts:
   - the URL of the cloudq,
   - the queue to pull the job from,
   - username of the cloudq,
   - password of the cloudq,
   - the function to apply to the job,
   - the function to run if the job fails,
   - how long to wait if the queue is
     empty to poll again."
  [cloudq-url queue username password
   attempt-job-fn on-failure-fn empty-delay-ms]
  (loop []
    (let [url (str cloudq-url "/" queue)
          job (retrieve-job-from-cloudq url username password)]
      (try+
       (println job)
       (if (queue-not-empty? job)
         (do
           (attempt-job-fn job)
           (delete-job url
                       (:id job)
                       username
                       password))
         (Thread/sleep empty-delay-ms))
       (catch Object o
         (on-failure-fn job o))))
    (recur)))
