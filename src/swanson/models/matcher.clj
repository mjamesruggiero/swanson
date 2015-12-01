(ns swanson.models.matcher)

(defn match-description
  "match a description to a value"
  [description]
  (cond
    (re-find #"AMZN" "CHECK CRD AMZN.COM") "amazon"
    :else nil))
