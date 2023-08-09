(ns clojure-todo-app.components.db.postgres
  (:require [com.stuartsierra.component :as component]
            [korma.db :as kdb]
            [clojure-todo-app.components.web.tasks :as tasks]))

(defrecord Postgres [db-config database]
  component/Lifecycle
  (start [this]
    (let [db (kdb/create-db (kdb/postgres db-config))]
      (kdb/default-connection db)
      (tasks/create-table!)
      (assoc this :database db)))
  (stop [this]
    (kdb/default-connection nil)
    (assoc this :database nil)))

(defn new-database []
  (map->Postgres {}))