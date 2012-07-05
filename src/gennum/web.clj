;; Copyright (c) Goran Jovic, 2010. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns gennum.web
(:use gennum.evolution)
(:use gennum.gae-ds)
(:use gennum.utils)
(:use [compojure.core :only [defroutes GET POST wrap!]])
(:use hiccup.core)
(:use hiccup.page-helpers)
(:use hiccup.form-helpers)
(:use clojure.contrib.java-utils)
(:use [clojure.stacktrace :only [print-stack-trace]])
(:use [ring.middleware params keyword-params nested-params])
(:use [ring.util.response :only [redirect]])
(:use [clojure.contrib.duck-streams :exclude [spit]]) 
(:require [compojure.route :as route]))

(defn api
  "Create a handler suitable for a web API. This adds the following
  middleware to your routes:
    - wrap-params
    - wrap-nested-params
    - wrap-keyword-params"
  [routes]
  (-> routes
      wrap-keyword-params
      wrap-nested-params
      wrap-params))

(defn html-doc 
  [title & body] 
  (html 
    (doctype :html4) 
    [:html 
      [:head 
        [:title title]
	(include-css "style.css")
        (include-js "ga.js")]
      [:body  
       [:div {:class "title"}
	[:h2 
	 [:p title]]]
        body]]))

(def web-root-path "")

(def code-link "http://code.google.com/p/genetic-my-number/")

(def look-ma-link "http://look-ma.appspot.com/")

(def locale (read-properties (str web-root-path "locale.properties")))

(def index-file-name "/flex/gencalcfx.html")

(defn localize [kwd]
	(. locale getProperty (name kwd) (name kwd)))


(defn form-element [oldvalues elem]
	[:div {:class (str (name elem) " fieldcontainer")} 
                 (label (name elem) (localize elem))
	(text-field {:size 3 :class "field"} elem (oldvalues elem))])


(defn form-structure [kwds oldvalues] 
  [:div {:class "form"}
   (map (partial form-element oldvalues) kwds)
    [:div {:class "options"}
     (map (partial form-element {}) [:max-gen :population-size])]])

(defn description-text []
  [:div {:class "description"} 
   [:ul [:li (localize :description)]
        [:li (localize :details) " " 
         [:a {:href code-link :target "_blank"} (localize :code)]]]])

(defn legal-text []
  [:div {:class "footer"} 
   [:div {:class "legal"}(localize :legal)]
   [:div {:class "legal"}(localize :acknowledgement) 
    [:a {:href look-ma-link :target "_blank"} "nevenavv"]]])

(defn sum-form [oldvalues result]
  (html-doc (localize :title) 
    (form-to [:post "/noflash"]
      (form-structure [:goal :a :b :c :d :e :f] oldvalues)
      (text-area  {:class "result" :readonly "true"} :result result) 
      (submit-button { :class "solve"} (localize :solve))
      (description-text)
      (legal-text))))

(defn parse-int 
      ([raw default]
	(if (or (nil? raw) (= raw ""))
		default (Integer/parseInt raw)))
      ([raw](parse-int raw nil)))

(defn result 
  [params]
  (let [x (parse-int (params :goal) 0)
	a (parse-int (params :a) 0)
        b (parse-int (params :b) 0)
        c (parse-int (params :c) 0)
        d (parse-int (params :d) 0)
        e (parse-int (params :e) 0) 
        f (parse-int (params :f) 0)
	
	max-gen (parse-int (params :max-gen))
	population-size (parse-int (params :population-size))
	] 
      (solve x [a b c d e f] {:max-gen max-gen :population-size population-size}))) 

(def *date-format* "MM-dd-yyyy HH:mm")

(defn create-post [link title author date tags body active]
  "Stores a new post in the datastore and issues an HTTP Redirect to the main page."
  (add-post link title author date tags body active 0)
  (redirect "/admin/yes"))

(defn create-comment [postname date author body email web commno active]
  (add-comment postname date author body email web commno active)
  (redirect (str "/comments?postname=" postname)))

(defn service-output [params]
  (apply str (interpose ";" (result params))))

(defn web-repl [source]
  (try (str (load-string (str "(in-ns 'gennum.web)" source)))
  (catch Exception e
   (with-out-str (print-stack-trace e)))))

(defroutes webservice
  (GET "/service" {params :params} (service-output params))
  (POST "/admin/repl" [source] (web-repl source))
  (GET "/noflash" {params :params} (sum-form params nil)) 
  (POST "/noflash" {params :params}
    ;(println params)
    (sum-form params (first (result params))))
  (POST "/newpost" [link title author tags body active] (create-post link title author (format-date *date-format*) tags body active))
  (GET "/posts" [] (get-posts))
  (GET "/commno" [postname] (get-comm-no postname))
  (POST "/addcomm" [postname author body email web] (create-comment postname (format-date *date-format*) author body email web 0 true))
  (GET "/comments" [postname] (get-post-comments postname))
  (route/resources "/")
  (route/not-found "Not Found!"))

