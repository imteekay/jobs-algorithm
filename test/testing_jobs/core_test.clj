(ns testing-jobs.core-test
  (:require [clojure.test :refer :all]
            [testing-jobs.core :refer :all]))


(deftest prioritize-test
  (testing "prioritize jobs based on urgency"
    (is
     (=
      (prioritize [{:id "1" :type "rewards-question" :urgent false}
                   {:id "2" :type "bills-questions" :urgent false}
                   {:id "3" :type "bills-questions" :urgent true}])

      [{:id "3" :type "bills-questions" :urgent true}
       {:id "1" :type "rewards-question" :urgent false}
       {:id "2" :type "bills-questions" :urgent false}]))

    (is
     (=
      (prioritize [{:id "1" :type "rewards-question" :urgent false}
                   {:id "2" :type "bills-questions" :urgent false}
                   {:id "3" :type "bills-questions" :urgent false}])

      [{:id "1" :type "rewards-question" :urgent false}
       {:id "2" :type "bills-questions" :urgent false}
       {:id "3" :type "bills-questions" :urgent false}]))

    (is
     (=
      (prioritize [{:id "1" :type "rewards-question" :urgent false}])
      [{:id "1" :type "rewards-question" :urgent false}]))

    (is
     (=
      (prioritize [])
      []))))


(deftest filter-by-skillset-test
  (testing "filter agents by job skillset"
    (is
     (=
      (filter-by-skillset [] {:id "1" :type "rewards-question" :urgent false})
      []))

    (is
     (=
      (filter-by-skillset [{:id "1"
                            :name "Son Goku"
                            :primary_skillset ["bills-questions"]
                            :secondary_skillset []}]
                          {:id "1" :type "rewards-question" :urgent false})

      []))

    (is
     (=
      (filter-by-skillset [{:id "1"
                            :name "Son Goku"
                            :primary_skillset ["bills-questions"]
                            :secondary_skillset []}]
                          {:id "2" :type "bills-questions" :urgent false})

      [{:id "1"
        :name "Son Goku"
        :primary_skillset ["bills-questions"]
        :secondary_skillset []}]))

    (is
     (=
      (filter-by-skillset [{:id "1"
                            :name "Son Goku"
                            :primary_skillset ["bills-questions"]
                            :secondary_skillset []}
                           {:id "2"
                            :name "Harry Potter"
                            :primary_skillset ["rewards-question"]
                            :secondary_skillset ["bills-questions"]}]
                          {:id "2" :type "bills-questions" :urgent false})

      [{:id "1"
        :name "Son Goku"
        :primary_skillset ["bills-questions"]
        :secondary_skillset []}
       {:id "2"
        :name "Harry Potter"
        :primary_skillset ["rewards-question"]
        :secondary_skillset ["bills-questions"]}]))))


(deftest agent-has-job-type-as-primary-skillset-test
  (testing "Job type as primary skillset"
    (testing "return 0 if agent has the job type as primary skillset"
      (is
       (=
        (agent-has-job-type-as-primary-skillset "rewards-question"
                                                {:id "1"
                                                 :name "Son Goku"
                                                 :primary_skillset ["rewards-question"]
                                                 :secondary_skillset []})
        0)))

    (testing "return 1 if agent doesn't have the job type as primary skillset"
      (is
       (=
        (agent-has-job-type-as-primary-skillset "rewards-question"
                                                {:id "1"
                                                 :name "Son Goku"
                                                 :primary_skillset ["bills-questions"]
                                                 :secondary_skillset ["rewards-question"]})
        1))

      (is
       (=
        (agent-has-job-type-as-primary-skillset "rewards-question"
                                                {:id "1"
                                                 :name "Son Goku"
                                                 :primary_skillset ["bills-questions"]
                                                 :secondary_skillset []})
        1)))))

(deftest add-contains-primary-skillset-test
  (testing "Add contains primary skillset"
    (testing "when agent has job type as primary skillset"
      (is
       (=
        (add-contains-primary-skillset "rewards-question"
                                       {:id "1"
                                        :name "Son Goku"
                                        :primary_skillset ["rewards-question"]
                                        :secondary_skillset []})
        {:id "1"
         :name "Son Goku"
         :primary_skillset ["rewards-question"]
         :secondary_skillset []
         :contains-primary-skillset? 0})))

    (testing "when agent has no job type as primary skillset"
      (is
       (=
        (add-contains-primary-skillset "bills-questions"
                                       {:id "1"
                                        :name "Son Goku"
                                        :primary_skillset ["rewards-question"]
                                        :secondary_skillset []})
        {:id "1"
         :name "Son Goku"
         :primary_skillset ["rewards-question"]
         :secondary_skillset []
         :contains-primary-skillset? 1})))))
