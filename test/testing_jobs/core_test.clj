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


(deftest remove-contains-primary-skillset-test
  (testing "Remove contains primary skillset"
    (testing "with agent property"
      (is
       (=
        (remove-contains-primary-skillset {:id "1"
                                           :name "Son Goku"
                                           :primary_skillset ["rewards-question"]
                                           :secondary_skillset []
                                           :contains-primary-skillset? 0})
        {:id "1"
         :name "Son Goku"
         :primary_skillset ["rewards-question"]
         :secondary_skillset []})))

    (testing "without agent property"
      (is
       (=
        (remove-contains-primary-skillset {:id "1"
                                           :name "Son Goku"
                                           :primary_skillset ["rewards-question"]
                                           :secondary_skillset []})
        {:id "1"
         :name "Son Goku"
         :primary_skillset ["rewards-question"]
         :secondary_skillset []})))))


(deftest sort-agents-by-primary-skillset-test
  (testing "Sort by primary skillset"
    (is
     (=
      (sort-agents-by-primary-skillset [{:id "1"
                                         :name "Harry Potter"
                                         :primary_skillset ["bills-question"]
                                         :secondary_skillset ["rewards-questions"]}
                                        {:id "2"
                                         :name "Son Goku"
                                         :primary_skillset ["rewards-question"]
                                         :secondary_skillset []}]
                                       {:id "1"
                                        :type "rewards-question"
                                        :urgent false})
      [{:id "2"
        :name "Son Goku"
        :primary_skillset ["rewards-question"]
        :secondary_skillset []}
       {:id "1"
        :name "Harry Potter"
        :primary_skillset ["bills-question"]
        :secondary_skillset ["rewards-questions"]}]))

    (is
     (=
      (sort-agents-by-primary-skillset [{:id "1"
                                         :name "Harry Potter"
                                         :primary_skillset ["bills-question"]
                                         :secondary_skillset ["rewards-questions"]}
                                        {:id "2"
                                         :name "Son Goku"
                                         :primary_skillset ["rewards-question"]
                                         :secondary_skillset []}]
                                       {:id "1"
                                        :type "other-question"
                                        :urgent false})
      [{:id "1"
        :name "Harry Potter"
        :primary_skillset ["bills-question"]
        :secondary_skillset ["rewards-questions"]}
       {:id "2"
        :name "Son Goku"
        :primary_skillset ["rewards-question"]
        :secondary_skillset []}]))))


(deftest agents-to-be-assigned-test
  (testing "Get all agents who can be assigned"
    (is
     (=
      (agents-to-be-assigned [{:id "1"
                               :name "Harry Potter"
                               :primary_skillset ["bills-question"]
                               :secondary_skillset ["rewards-question"]}
                              {:id "2"
                               :name "Son Goku"
                               :primary_skillset ["rewards-question"]
                               :secondary_skillset []}
                              {:id "3"
                               :name "Ruffy"
                               :primary_skillset ["bills-question"]
                               :secondary_skillset ["other-stuff"]}]
                             {:id "1"
                              :type "rewards-question"
                              :urgent false})
      [{:id "2"
        :name "Son Goku"
        :primary_skillset ["rewards-question"]
        :secondary_skillset []}
       {:id "1"
        :name "Harry Potter"
        :primary_skillset ["bills-question"]
        :secondary_skillset ["rewards-question"]}]))))


(deftest working-agents-ids-test
  (testing "Get all ids from assigned agents"
    (let [job {:id "1" :type "rewards-question" :urgent false}
          agent {:id "1"
                 :name "Harry Potter"
                 :primary_skillset ["bills-question"]
                 :secondary_skillset ["rewards-question"]}]
      (is
       (=
        (working-agents-ids [{:job_assigned {:job_id (:id job)
                                     :agent_id (:id agent)}}])
        ["1"])))

    (let [assigned-agents [{:job_assigned {:job_id (:id {:id "1" :type "rewards-question" :urgent false})
                                           :agent_id (:id {:id "1"
                                                           :name "Harry Potter"
                                                           :primary_skillset ["bills-question"]
                                                           :secondary_skillset ["rewards-question"]})}}
                           {:job_assigned {:job_id (:id {:id "2" :type "rewards-question" :urgent false})
                                           :agent_id (:id {:id "2"
                                                           :name "Son Goku"
                                                           :primary_skillset ["rewards-question"]
                                                           :secondary_skillset []})}}]]
      (is
       (=
        (working-agents-ids assigned-agents)
        ["1" "2"])))
    
    (is
     (=
      (working-agents-ids [])
      []))))


(deftest assigned?-test
  (testing "Find agent id in assigned agents ids list"
    (is
     (=
      (assigned? [{:job_assigned {:job_id (:id {:id "1" :type "rewards-question" :urgent false})
                                       :agent_id (:id {:id "1"
                                                       :name "Harry Potter"
                                                       :primary_skillset ["bills-question"]
                                                       :secondary_skillset ["rewards-question"]})}}]
                      {:id "1"
                       :name "Harry Potter"
                       :primary_skillset ["bills-question"]
                       :secondary_skillset ["rewards-question"]})
      true))

    (is
     (=
      (assigned? [{:job_assigned {:job_id (:id {:id "1" :type "rewards-question" :urgent false})
                                       :agent_id (:id {:id "1"
                                                       :name "Harry Potter"
                                                       :primary_skillset ["bills-question"]
                                                       :secondary_skillset ["rewards-question"]})}}]
                      {:id "2"
                       :name "Son Goku"
                       :primary_skillset ["rewards-question"]
                       :secondary_skillset []})
      nil))))


(deftest not-assigned?-test
  (testing "Complement of the assigned? function"
    (is
     (=
      (not-assigned? [{:job_assigned {:job_id (:id {:id "1" :type "rewards-question" :urgent false})
                                  :agent_id (:id {:id "1"
                                                  :name "Harry Potter"
                                                  :primary_skillset ["bills-question"]
                                                  :secondary_skillset ["rewards-question"]})}}]
                 {:id "1"
                  :name "Harry Potter"
                  :primary_skillset ["bills-question"]
                  :secondary_skillset ["rewards-question"]})
      false))

    (is
     (=
      (not-assigned? [{:job_assigned {:job_id (:id {:id "1" :type "rewards-question" :urgent false})
                                  :agent_id (:id {:id "1"
                                                  :name "Harry Potter"
                                                  :primary_skillset ["bills-question"]
                                                  :secondary_skillset ["rewards-question"]})}}]
                 {:id "2"
                  :name "Son Goku"
                  :primary_skillset ["rewards-question"]
                  :secondary_skillset []})
      true))))