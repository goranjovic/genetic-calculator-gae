(ns gennum.gae-ds
  (:use [clojure.pprint :only [pprint]]
        [clojure.contrib.json :only [json-str]])
  (:require [appengine-magic.core :as ae]
            [appengine-magic.services.datastore :as ds]
            [appengine-magic.services.mail :as mail]))

(ds/defentity Post [^:key link title author date tags body active commno])
(ds/defentity Comment [^:key date author body email web number active post])

(defn add-post
  [link title author date tags body active commno]
  (ds/save! (Post. link title author date tags body active 0)))
  
(defn add-comment
  [post-name date author body email web number active]
  (let [post (ds/retrieve Post post-name)
        d-comment (ds/new* Comment [date author body email web number active post] :parent post)
        info-msg (mail/make-message :from "no-reply@genetic-calculator.appspot.com"
                                    :to ["nevenavv@gmail.com" "goranjovic@gmail.com"]
                                    :subject "[Genetic Calculator] New comment!"
                                    :text-body (str "[Comment]\n" author "\n" body))]
    (ds/with-transaction
            (ds/save! (assoc post :commno (inc (:commno post))))
            (ds/save! d-comment))
    (mail/send info-msg)))

(defn get-posts
  []
  (let [p (ds/query :kind Post)]
    (str (with-out-str (map pprint p)))))

(defn get-comm-no
  [postname]
  (str (ds/query :kind Comment :filter [(= :active true) (= :post (ds/retrieve Post postname))] :count-only? true)))

(defn get-post-comments
  [post-name]
  (json-str (map #(dissoc % :active :post) (ds/query :kind Comment :filter [(= :active true) (= :post (ds/retrieve Post post-name))]))))
