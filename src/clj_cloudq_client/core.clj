(ns clj-cloudq-client.core
  (:require [clj-http.client :as http])
  (:use [clojure.data.json :only (json-str
                                  write-json
                                  read-json)]))

(defn queue-not-empty?
  [job]
  (not (= (:status job) "empty")))

;;POST /:queue
;;When a client requests a POST for a queue name the server needs to
;;first make sure that JOB is valid, if the job is not valid then
;;return a response message. For our ruby and node implementations we
;;use MongoDb and create a collection called jobs and the documents in
;;the jobs collection contain an attribute called :queue
(defn post-job
  [queue-url klass args username password]
  (http/post queue-url
             {:basic-auth [username password]
              :content-type :json
              :accept :json
              :body (json-str {:job {:klass klass
                                     :args args}})}))

;;GET /:queue
;;When a client performs a get request, they are requesting an items
;;from the :queue, the server should find a JOB with a status of :new
;;and update that item to a status of :reserved then respond to the
;;client with the JOB. If there are no JOBS in a new status for that
;;queue then the server should return an empty response.
(defn get-job
  [queue-url username password]
  (let [result (http/get queue-url 
                         {:basic-auth [username password]
                          :conn-timeout 1000
                          :socket-timeout 1000
                          :insecure? true})
        body (:body result)
        info (read-json body)]
    info))

;;DELETE /:queue/:id
;;When a client requests a DELETE JOB request, the server needs to
;;locate the JOB and modify the status of the job to :deleted, and
;;return a success response, if it can't find the job then return an
;;error response or empty response.
(defn delete-job
  [queue-url job-id username password]
  (http/delete (str queue-url "/" job-id)
               {:basic-auth [username password]
                :insecure? true}))
