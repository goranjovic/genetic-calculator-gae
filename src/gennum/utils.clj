(ns gennum.utils
  (:import [java.util Date]
	         [java.text SimpleDateFormat]))

(defn div [x y]
  (if (zero? y) (Double/NaN) (/ x y)))

(defn l [x y] x)

(defn r [x y] y)


(defn re-replace [string pattern replacement]
    (.replaceAll string (str pattern) (str replacement)))

(def operators-visible {+ "+", - "-", * "*", div "/", r "r", l "l"})

(defn print-rel [e1 rel e2]
          (if (= rel l) e1
          (if (= rel r) e2
              (str "(" e1 " " (operators-visible rel) " " e2 ")" ))))

(defn equation-pretty-print [[numbers operators] value]
      (let [[a b c d e f] numbers
            [o p q r s] operators
            -- (fn [a] (if (neg? a) (str "(" a ")") a))]
         (str value " = "
              (print-rel (print-rel (print-rel e q (-- d)) p (-- a))
                o (print-rel b r (print-rel c s (-- f)))))))

(defn #^Date now
  "The current date."
  [] (Date.))
 
(defn #^String format-date
  "Uses SimpleDateFormat to format the date d (default: now) using the format fm.
   See java.text.SimpleDateFormat for pattern information.
   e.g.: (format-date (date 400734000000) \"yyyy-MM-dd HH:mm:ss\")
   returns \"1982-09-13 00:00:00\"."
  ([fm] (format-date (now) fm))
  ([d fm]
     (.format (SimpleDateFormat. fm) d)))
