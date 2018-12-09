(ns testing-jobs.core
  (:use clojure.pprint))

;; -- sorting by urgency --
(defn prioritize
  [jobs]
  (sort-by (complement :urgent) jobs))
;; -- end of sorting by urgency --


;; -- filtering by skillsets --
(defn by-skillset
  [job agent]
  (or
   (some #(= (:type job) %) (:primary_skillset agent))
   (some #(= (:type job) %) (:secondary_skillset agent))))

(defn filter-by-skillset
  [agents job]
  (filter (partial by-skillset job) agents))
;; -- end of filtering by skillsets --


;; -- sorting by job type --
(defn agent-has-job-type-as-primary-skillset
  [job-type agent]
  (if (some #{job-type} (:primary_skillset agent))
    0
    1))

(defn add-contains-primary-skillset
  [job-type agent]
  (assoc
   agent
   :contains-primary-skillset?
   (agent-has-job-type-as-primary-skillset job-type agent)))

(defn remove-contains-primary-skillset
  [agent]
  (dissoc agent :contains-primary-skillset?))

(defn sort-agents-by-primary-skillset
  [agents job]
  (->> agents
       (map (partial add-contains-primary-skillset (:type job)))
       (sort-by :contains-primary-skillset?)
       (map remove-contains-primary-skillset)))

(defn agents-to-be-assigned
  [agents job]
  (-> agents
      (filter-by-skillset job)
      (sort-agents-by-primary-skillset job)))

;;; -------------------

(defn working-agents-ids
  [assigned-jobs]
  (map
   #(get-in % [:job_assigned :agent_id])
   assigned-jobs))

(defn assigned?
  [assigned-jobs agent]
  (some
   #(= (:id agent) %)
   (working-agents-ids assigned-jobs)))

(defn not-assigned?
  [assigned-jobs agent]
  ((complement assigned?) assigned-jobs agent))

(defn make-agent-job-assignment
  [job agent]
  {:job_assigned {:job_id (:id job)
                  :agent_id (:id agent)}})

(defn assignments
  [assigned-jobs job agent-to-be-assigned]
  (if (nil? agent-to-be-assigned)
    assigned-jobs
    (conj
     assigned-jobs
     (make-agent-job-assignment
      job
      agent-to-be-assigned))))

(defn assign-agent
  [agents job assigned-jobs]
  (->> (agents-to-be-assigned agents job)
       (filter (partial not-assigned? assigned-jobs))
       (first)
       (assignments assigned-jobs job)))


;; ----- Testing sorted agents -----
(defn run-jobs-assignment
  [agents jobs assigned-jobs]
  (loop
   [agents         agents
    jobs           (prioritize jobs)
    assigned-jobs  assigned-jobs]

    (if (not-empty jobs)
      (recur agents
             (rest jobs)
             (assign-agent agents (first jobs) assigned-jobs))
      assigned-jobs)))

;; -- jobs and agents definition --
(def jobs
  [{:id "f26e890b-df8e-422e-a39c-7762aa0bac36" :type "rewards-question" :urgent false}
   {:id "690de6bc-163c-4345-bf6f-25dd0c58e864" :type "bills-questions" :urgent false}
   {:id "c0033410-981c-428a-954a-35dec05ef1d2" :type "bills-questions" :urgent true}])

(def agents
  [{:id "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
    :name "BoJack Horseman"
    :primary_skillset ["bills-questions"]
    :secondary_skillset []}
   {:id "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"
    :name "Mr. Peanut Butter"
    :primary_skillset ["rewards-question"]
    :secondary_skillset ["bills-questions"]}])
;; -- end of jobs and agents definition --

(defn -main [& args]
  (pprint (run-jobs-assignment agents jobs [])))
;; ----- end of Testing sorted agents -----
