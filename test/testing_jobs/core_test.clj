(ns testing-jobs.core-test
  (:require [clojure.test :refer :all]
            [testing-jobs.core :refer :all]))

(deftest prioritize-test
  (testing "prioritize jobs based on urgency"
    (is
     (prioritize [{:id "f26e890b-df8e-422e-a39c-7762aa0bac36" :type "rewards-question" :urgent false}
                  {:id "690de6bc-163c-4345-bf6f-25dd0c58e864" :type "bills-questions" :urgent false}
                  {:id "c0033410-981c-428a-954a-35dec05ef1d2" :type "bills-questions" :urgent true}])

     [{:id "c0033410-981c-428a-954a-35dec05ef1d2" :type "bills-questions" :urgent true}
      {:id "f26e890b-df8e-422e-a39c-7762aa0bac36" :type "rewards-question" :urgent false}
      {:id "690de6bc-163c-4345-bf6f-25dd0c58e864" :type "bills-questions" :urgent false}])

    (is
     (prioritize [{:id "f26e890b-df8e-422e-a39c-7762aa0bac36" :type "rewards-question" :urgent false}
                  {:id "690de6bc-163c-4345-bf6f-25dd0c58e864" :type "bills-questions" :urgent false}
                  {:id "c0033410-981c-428a-954a-35dec05ef1d2" :type "bills-questions" :urgent false}])

     [{:id "f26e890b-df8e-422e-a39c-7762aa0bac36" :type "rewards-question" :urgent false}
      {:id "690de6bc-163c-4345-bf6f-25dd0c58e864" :type "bills-questions" :urgent false}
      {:id "c0033410-981c-428a-954a-35dec05ef1d2" :type "bills-questions" :urgent false}])))
