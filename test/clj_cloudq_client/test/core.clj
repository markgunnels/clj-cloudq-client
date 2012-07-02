(ns clj-cloudq-client.test.core
  (:use [clj-cloudq-client.core]
        [clj-cloudq-client.test.config]
        [clojure.test]))

(def queue "clj-test")
(def props (load-props "cloudq.properties"))

(deftest test-post-job ;; FIXME: write
  (let [queue-url (str (:url props) queue)]
    (post-job queue-url
            "greeting"
            "hello"
            (:username props)
            (:password props))
  (is true
      (queue-not-empty? (get-job queue-url
                                 (:username props)
                                 (:password props))) )))
