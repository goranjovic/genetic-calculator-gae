(ns gennum.core
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use [gennum.web]
        [ring.util.servlet :only [defservice]]))

(defservice (api webservice))