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
      {:id "c0033410-981c-428a-954a-35dec05ef1d2" :type "bills-questions" :urgent false}])

    (is
     (prioritize [{:id "f26e890b-df8e-422e-a39c-7762aa0bac36" :type "rewards-question" :urgent false}])
     [{:id "f26e890b-df8e-422e-a39c-7762aa0bac36" :type "rewards-question" :urgent false}])

    (is
     (prioritize [])
     [])))

(deftest filter-by-skillset-test
  (testing "filter agents by job skillset"
    (is
     (filter-by-skillset [] {:id "f26e890b-df8e-422e-a39c-7762aa0bac36" :type "rewards-question" :urgent false})
     [])

    (is
     (filter-by-skillset [{:id "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
                           :name "BoJack Horseman"
                           :primary_skillset ["bills-questions"]
                           :secondary_skillset []}]
                         {:id "f26e890b-df8e-422e-a39c-7762aa0bac36" :type "rewards-question" :urgent false})

     [])

    (is
     (filter-by-skillset [{:id "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
                           :name "BoJack Horseman"
                           :primary_skillset ["bills-questions"]
                           :secondary_skillset []}]
                         {:id "690de6bc-163c-4345-bf6f-25dd0c58e864" :type "bills-questions" :urgent false})

     [{:id "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
       :name "BoJack Horseman"
       :primary_skillset ["bills-questions"]
       :secondary_skillset []}]))

  (is
   (filter-by-skillset [{:id "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
                         :name "BoJack Horseman"
                         :primary_skillset ["bills-questions"]
                         :secondary_skillset []}
                        {:id "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"
                         :name "Mr. Peanut Butter"
                         :primary_skillset ["rewards-question"]
                         :secondary_skillset ["bills-questions"]}]
                       {:id "690de6bc-163c-4345-bf6f-25dd0c58e864" :type "bills-questions" :urgent false})

   [{:id "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
     :name "BoJack Horseman"
     :primary_skillset ["bills-questions"]
     :secondary_skillset []}
    {:id "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"
     :name "Mr. Peanut Butter"
     :primary_skillset ["rewards-question"]
     :secondary_skillset ["bills-questions"]}]))
