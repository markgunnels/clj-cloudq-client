(ns clj-cloudq-client.test.config
  (:require [clojure.java.io :as io]))

(defn load-props
  [file-name]
  (with-open [^java.io.Reader reader (io/reader
                                      (io/resource file-name)) ]
    (let [props (java.util.Properties.)]
       (.load props reader)
       (into {} (for [[k v] props] [(keyword k) (read-string v)])))))


