(ns clojure-todo-app.system-test
  (:require [io.pedestal.http :as http]
            [io.pedestal.test :refer [response-for]]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [deftest is are testing]]
            [clojure-todo-app.system :as system]
            [clojure-todo-app.components.web.routes :as routes]
            [korma.db :as kdb]
            [clojure-todo-app.components.web.todos :as todos]))

(defrecord TestDB [db-config database]
  component/Lifecycle
  (start [this]
    (let [db (kdb/create-db (kdb/sqlite3 {:db "testing.sqlite3"}))]
      (kdb/default-connection db)
      (todos/drop-table!)
      (todos/create-table!)
      (assoc this :database db)))
  (stop [this]
    (todos/drop-table!)
    (kdb/default-connection nil)
    (assoc this :database nil)))

(defn- new-test-db []
  (map->TestDB {}))

  ;; -----------------------------------------------------------------------------

(def test-system
  (assoc (system/system :test)
         :db (new-test-db)))  ;; Inject TestDB for testing

(defmacro with-system
  [[bound-var test-system] & body]
  (deftest greeting-test
    (testing "Greeting route should print typical hello world."
      (with-system [sut (system/system :test)]
        (with-system [sut test-system]
          (let [service (service-fn sut)
                {:keys [status body]} (response-for service
                                                    :get
                                                    (routes/url-for :greet))]
            (is (= 200 status))
            (is (= "Hello, world!" body))))))

    (deftest add-todo-test
      (testing "Greeting route should print typical hello world."
        (with-system [sut test-system]
          (let [sum-total-todos (count (todos/query-all))]
            (todos/add! "foo")
            (is (= (inc sum-total-todos) (count (todos/query-all))))))))

    (comment
      (def mytestsystem (component/start test-system))
      mytestsystem)))