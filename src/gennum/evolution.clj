;; Copyright (c) Goran Jovic, 2010. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns gennum.evolution)
(use 'clojure.contrib.math)
(use 'gennum.utils)

(defn random-operator []
	( [ + - * div r l ](rand-int 6)))

(defn change-operator [operators index]
	(let [operator (operators index)]
		(replace {operator (random-operator)} operators)))

(defn insert-into-vector [vect position element]
	(into [] (concat (subvec vect 0 position) [element] (subvec vect (inc position)))))

(defn swap-elements [elements index1 index2]
   (insert-into-vector 
     (insert-into-vector elements index1 (elements index2)) index2 (elements index1)))

(defn create-rand-operators [size]
        (take size (repeatedly random-operator)))

(defn create-rand-equation [numbers]
	[(vec (shuffle numbers)) (vec (create-rand-operators (dec (count numbers))))])

(defn mutate [[numbers operators]]
        (let [toss-coin (rand-int 2)]
                (if (= toss-coin 0) 
                        [numbers (change-operator operators (rand-int 5))]
                        [(swap-elements numbers (rand-int 6) (rand-int 6)) operators])))

(defn evaluate [[numbers operators]]
        (let [[a b c d e f] numbers [o p q r s] operators]
        (o (p (q e d) a) (r b (s c f))) ))


(defn create-initial-population [population-size numbers]
	(take population-size (repeatedly (partial create-rand-equation numbers))))

(defn calculate-fitness [goal-value [numbers operators]]
	(let [difference (abs (- goal-value (evaluate [numbers operators])))]
		(if (or (ratio? difference)(Double/isNaN difference))
				 (Double/POSITIVE_INFINITY) difference)))

(defn sort-by-fitness [goal-value population]
	(sort-by (partial calculate-fitness (int goal-value)) population))

(defn select-survivors [population]
		(take (/ (count population) 2) population))

(defn next-generation [survivors]
		(interleave (map mutate survivors) survivors))


(defn evolution [goal-value generation options  old-population]
	(let [sorted-population (sort-by-fitness goal-value  old-population )
	      champion (first (take 1 sorted-population))
              champ-value (evaluate champion)
              hit? (= champ-value goal-value)
              max-gen-reached? (= generation (int (options :max-gen)))]
		(if (or max-gen-reached? hit?)
			[champion hit? generation]
			(recur goal-value (inc generation) options
				(next-generation (select-survivors sorted-population))))))

(def default-options {:population-size 50 :max-gen 2000})

(defn solve-trivially [goal-value numbers] 
  (if (some #(= % goal-value) numbers) 
    [[[goal-value 0 0 0 0 0][l r + + +]] true 0]))

(defn solve-genetic [goal-value numbers user-options]
	(let [options (merge-with (fn [v1 v2] (if v2 v2 v1)) default-options user-options)]
     	(evolution goal-value 0 options 
  	(create-initial-population (options :population-size) numbers) )))

(defn structure-result [[champion hit? generation]]
  [(equation-pretty-print champion (evaluate champion)) hit? generation])

(defn solve 
  ([goal-value numbers]
   (solve goal-value numbers default-options))
  ([goal-value numbers options]
   (structure-result 
     (or (solve-trivially goal-value numbers)
         (solve-genetic goal-value numbers options)))))


