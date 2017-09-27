(ns claravis.clara-test
  (:require [clara.rules.accumulators :as acc]
            [clara.rules :refer :all]
            [clojure.test :refer [deftest testing is are]]))

;; feladat
;;
;; - given a list of orders calculate a total price
;; - given some discounts calculate discounted price
;; - when customer is vip -> add 20% extra discount
;; - when customer is ...

(defrecord Purchase  [cost item])
(defrecord Discount [reason percent])


(defrecord OrderTotal [original-price discounted-price])

(defrule total-discount-percent
  [?cost <- (acc/sum :cost) :from [Purchase]]
  [?pct <- (acc/sum :percent) :from [Discount]]
  =>
  (let [rate (/ (- 100 ?pct) 100)]
    (insert! (->OrderTotal ?cost (* rate ?cost)))))

(defquery query-total [] [?total <- OrderTotal])1

(defsession my-session 'claravis.clara-test)

(deftest test-sum
  (-> my-session
      (insert (->Purchase 100 :food)
              (->Purchase 100 :clothing))
      (fire-rules)
      (query query-total)
      ((fn [result]
         (is (= 1 (count result)))

         result))
      #_(println)))

(deftest test-total
  (-> my-session
      (insert (->Purchase 100 :food)
              (->Purchase 200 :clothing)
              (->Purchase 150 :drink)
              (->Discount "Being nice" 5)
              (->Discount "Whatever reason" 5))
      (fire-rules)
      (query query-total)
      ((fn [result]
         (is (= 1 (count result)))
         (is (= 405N (:discounted-price (:?total (first result)))))

         result))
      #_(println)))
