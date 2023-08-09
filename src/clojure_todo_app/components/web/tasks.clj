(ns clojure-todo-app.components.web.tasks
  (:require [korma.core :as kc]))

(kc/defentity task
  (kc/table :tasks)
  (kc/entity-fields :id :title :done))

(defn create-table! []
  (kc/exec-raw "CREATE TABLE IF NOT EXISTS tasks(id SERIAL, title TEXT NOT NULL, done BOOLEAN DEFAULT FALSE NOT NULL)"))

(defn drop-table! []
  (kc/exec-raw "DROP TABLE IF EXISTS tasks"))

(defn clear-table! []
  (kc/delete task))

(defn add! [title]
  (kc/insert task (kc/values {:title title})))

(defn delete! [id]
  (kc/delete task (kc/where {:id id})))

(defn toggle! [id]
  (kc/exec-raw (format "UPDATE tasks SET done = NOT done WHERE id = %s" id)))

(defn query-all []
  (kc/select task))

(comment
  (create-table!)
  (add! "deleteme")
  (delete! 10)
  (clear-table!))