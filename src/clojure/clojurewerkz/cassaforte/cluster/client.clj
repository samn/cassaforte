(ns clojurewerkz.cassaforte.cluster.client
  (:require [clojurewerkz.cassaforte.conversion :as conv]
            [clojurewerkz.cassaforte.cluster.conversion])
  (:import [com.datastax.driver.core Cluster Session PreparedStatement Query]))

(def ^{:dynamic true :tag Session}
  *client*)

(defn build-cluster
  [contact-points]
  (let [builder (Cluster/builder)]
    (doseq [contact-point contact-points]
      (.addContactPoint builder contact-point))
    (.build builder)))

(defn ^Session connect
  "Connect to a Cassandra cluster"
  [hostnames]
  (.connect (build-cluster hostnames)))

;;
;; DB Ops
;;
(defn ^clojure.lang.IPersistentMap execute-raw
  [client ^String query]
  (-> (.execute client query)
                conv/to-map))

;; TODO: separate prepare command from execute-prepared

(defn execute-prepared
  [client [^String query ^java.util.List values]]
  (let [^Query prepared-statement (.bind (.prepare client query) (to-array values))]
    (-> (.execute client prepared-statement)
        conv/to-map)))
