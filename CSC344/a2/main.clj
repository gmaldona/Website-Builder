(ns main
  (:import (clojure.lang PersistentList LazySeq)))

(defn andexp [exp1 exp2] (list 'and exp1 exp2))
(defn orexp  [e1 e2] (list 'or e1 e2))
(defn notexp [e1] (list 'not e1))

(defn drop-nth

  [n new-coll c]                                                                  
  (let [coll (into (list) (into (list) c))]
  (cond
    (= n 0) (drop-nth (- n 1) new-coll (pop coll))
    (< n 0) (cond
              (= (count coll) 0) (apply list new-coll)
              (> (count coll) 0) (drop-nth n (seq (conj (vec new-coll) (first coll))) (pop coll)))

    (> n 0) (drop-nth (- n 1) (seq (conj (vec new-coll) (first coll))) (pop coll))))
  )

(defn and-simplify
  [exp]

  (cond
    (= (count exp) 2) (last exp)

    (= (count exp) 3) (let [variable-one-type (type (nth exp 1))]
                        (let [variable-two-type (type (nth exp 2))]

                          (cond
                            (and (= variable-one-type Boolean) (not= variable-two-type Boolean)) (cond
                                                                                                                       
                                                                                                                       (= (nth exp 1) true) (nth exp 2)
                                                                                                                       
                                                                                                                       (= (nth exp 1) false) false
                                                                                                                       )

                            (and (= variable-two-type Boolean) (not= variable-one-type Boolean)) (cond
                                                                                                                       
                                                                                                                       (= (nth exp 2) true) (nth exp 1)
                                                                                                                      
                                                                                                                       (= (nth exp 2) false) false
                                                                                                                       )
                            (and (and (= (type (first (pop exp))) PersistentList) (= (type (last exp)) PersistentList)) (= (first (first (pop exp)) ) (first (last exp)) ))  (concat (first (pop (exp))) (drop-nth 0 '() (last exp)))
                          
                            (and (= (type (first (pop exp))) PersistentList) (= (first exp) (first (pop exp)))) (concat (first (pop exp)) (list (last exp)))
                            
                            (and (= (type (last exp)) PersistentList) (= (first exp) (first (last exp)))) (concat (last exp) (list (first (pop exp))))

                            (and (= variable-one-type Boolean) (= variable-two-type Boolean)) (and (nth exp 1) (nth exp 2))

                            (and (not= variable-one-type Boolean) (not= variable-two-type Boolean)) (list "and" (nth exp 1) (nth exp 2)))))

    (> (count exp) 3) (and-simplify (list "and" (and-simplify (list "and" (nth exp 1) (nth exp 2))) (and-simplify (drop-nth 1 '() (drop-nth 1 '() exp)))))
    ))

(defn or-simplify [exp]

  (cond

    (.contains exp 'true) true

    (= (count exp) 2) (last exp)

    (= (count exp) 3) (let [variable-one-type (type (first (pop exp)))]
                        (let [variable-two-type (type (last exp))]

                          (cond

                        
                            (and (= (type (first (pop exp))) PersistentList) (= (type (last exp)) PersistentList)) (concat (first (pop (exp))) (drop-nth 0 '() (last exp)))
                          
                            (= (type (first (pop exp))) PersistentList) (concat (first (pop exp)) (list (last exp)))
                
                            (= (type (last exp)) PersistentList) (concat (last exp) (list (first (pop exp))))

                            (and (= variable-one-type Boolean) (= variable-two-type Boolean)) (or (first (pop exp)) (last exp))

                            (and (= variable-one-type Boolean) (not= variable-two-type Boolean)) (cond
                                                                                                                       
                                                                                                                       (= (first (pop exp)) false) (last exp)
                                                                                                                     
                                                                                                                       (= (first (pop exp)) true) true
                                                                                                                       )
                            ; If the second type is boolean but not the first
                            (and (= variable-two-type Boolean) (not= variable-one-type Boolean)) (cond
                                                                                                               
                                                                                                                       (= (last exp) false) (first (pop exp))
                                                                                                                    
                                                                                                                       (= (last exp) true) true
                                                                                                                       )
                          
                            (and (not= variable-one-type Boolean) (not= variable-two-type Boolean)) exp

                            )))
    
    (> (count exp) 3) (or-simplify (list "or" (or-simplify (list "or" (nth exp 1) (nth exp 2))) (or-simplify (drop-nth 1 '() (drop-nth 1 '() exp)))))

    ))


(defn demorgans

  [n new-coll coll]

  (cond
 
    (= n 0) new-coll
    
    (> n 0) (demorgans (- n 1) (concat new-coll (list (list 'not (first coll)) ) ) (pop coll) )))

(defn not-simplify
  [exp]
  (let [exp-type (type (last exp))]

    (cond
      (= exp-type Boolean) (cond
  
                                         (= (last exp) true) false
                                         (= (last exp) false) true
                                         )

      (= exp-type PersistentList) (let [boolean-operation (first (last exp))]
                                                 (cond
                                                 
                                                   (= boolean-operation (symbol "not")) (last (last exp))
                                                 
                                                   (= boolean-operation (symbol "and")) (demorgans (count (pop (last exp))) '(or) (pop (last exp)))
                                                   
                                                   (= boolean-operation (symbol "or")) (demorgans (count (pop (last exp))) '(and) (pop (last exp)))
                                                   )
                                                 )

      (and (not= exp-type Boolean) (not= exp-type PersistentList)) exp
      )))


(defn simplify-exp [exp]
  (let [boolean-operator (first exp)]


    (cond
      (= boolean-operator (symbol 'and)) (let [arg1 (first (pop exp))]
                                           (let [arg2 (last exp)]
                                             (cond
                                               (and (= (type arg1) PersistentList) (= (type arg2) PersistentList)) (and-simplify (list "and" (simplify-exp arg1) (simplify-exp arg2)))
                                               (= (type arg1) PersistentList) (and-simplify (list "and" (simplify-exp arg1) arg2))
                                               (= (type arg2) PersistentList)  (and-simplify (list "and" arg1 (simplify-exp arg2)))
                                               (and (not= (type arg1) PersistentList) (not= (type arg2) PersistentList)) (and-simplify exp)
                                               )
                                             ))

      (= boolean-operator (symbol 'or)) (let [arg1 (first (pop exp))]
                                          (let [arg2 (last exp)]
                                            (cond
                                              (and (= (type arg1) PersistentList) (= (type arg2) PersistentList)) (or-simplify (list "or" (simplify-exp arg1) (simplify-exp arg2)))
                                              (= (type arg1) PersistentList) (or-simplify (list "or" (simplify-exp arg1) arg2))
                                              (= (type arg2) PersistentList) (or-simplify (list "or" arg1 (simplify-exp arg2)))
                                              (and (not= (type arg1) PersistentList) (not= (type arg2) PersistentList)) (or-simplify exp)
                                              )

                                            ))

      (= boolean-operator (symbol 'not)) (let [arg (last exp)]
                                           (cond
                                             (= (type arg) PersistentList) (not-simplify (list "not" (simplify-exp arg)))
                                             (not= (type arg) PersistentList) (not-simplify exp)
                                             )
                                           )
      )))
(defn turn-to-list [exp]
    (let [boolean-operator (first exp)]
      (cond
        (or (= boolean-operator (symbol 'and)) (= boolean-operator (symbol 'or)))
                                            (let [arg1 (first (drop-nth 0 '() exp)) ]
                                             (let [arg2 (last exp)]
                                               (cond
                                                 (and (= (type arg1) LazySeq) (= (type arg2) LazySeq)) (list boolean-operator (turn-to-list arg1) (turn-to-list arg2))
                                                 (= (type arg1) LazySeq) (list boolean-operator (turn-to-list arg1) arg2)
                                                 (= (type arg2) LazySeq) (list boolean-operator arg1 (turn-to-list arg2))
                                                 (and (not= (type arg1) LazySeq) (not= (type arg2) LazySeq)) (list boolean-operator arg1 arg2)
                                                 )
                                               ))

        (= boolean-operator (symbol 'not)) (let [arg (last exp)]
                                             (cond
                                               (= (type arg) LazySeq) (list boolean-operator (turn-to-list arg))
                                               (not= (type arg) LazySeq) (list boolean-operator arg)
                                               )
                                             )

        )
      )
  )
(defn lookup [i m]

  (get m i i))

(defn substitute [l m]

  (map (fn [i]
         (if (seq? i)
           (substitute i m)
           (lookup i m)))
       l))

(defn evalexp [l m]

  (let [exp (substitute l m)]
      (simplify-exp (turn-to-list exp))
      ))



