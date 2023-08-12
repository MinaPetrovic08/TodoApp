(ns clojure-todo-app.system-test
  (:require
   [com.stuartsierra.component :as component]
   [clojure.test :refer :all]
   [clojure-todo-app.system :as my-system]
   [clojure-todo-app.components.web.tasks :as tasks]))

(def mock-db
  {:query-all (fn [] [{:id 1 :name "task1"} {:id 2 :name "task2"}])
   :add! (fn [task] (println "Adding task:" task))})

(deftest add-task-test
  (testing "Add task increments total tasks"
    (let [test-system (component/system-map
                       :db mock-db
                       :web (component/start (my-system/system :web)))
          task-count (count (tasks/query-all))]
      (tasks/add! "foo")
      (is (= (inc task-count) (count (tasks/query-all)))))))

(deftest delete-task-test
  (testing "Delete task removes the task"
    (let [test-system (component/system-map
                       :db mock-db
                       :web (component/start (my-system/system :web)))
          initial-tasks (tasks/query-all)
          deleted-task-id 1]
      (tasks/delete! deleted-task-id)
      (is (empty? (filter #(= (:id %) deleted-task-id) initial-tasks)))
      (is (= (count initial-tasks) (count (tasks/query-all)))))))

