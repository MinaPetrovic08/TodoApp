(ns clojure-todo-app.system
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [clojure-todo-app.components.web.pedestal :as pedestal]
            [clojure-todo-app.components.web.routes :as routes]
            [clojure-todo-app.components.db.postgres :as postgres]))

(defn- build-system-map [env]
  {:env env
   ::http/routes routes/routes
   ::http/type :jetty
   ::http/port 8082
   ::http/resource-path "/public"
   ::http/join? false})

(def db-config
  {:db       "clojure"
   :user     "clojure"
   :password "clojure"})

(defn system [env]
  (component/system-map
   :system-map (build-system-map env)
   :db-config db-config

   :db
   (component/using
    (postgres/new-database)
    [:db-config])

   :web
   (component/using
    (pedestal/new-pedestal)
    [:db :system-map])))

(defn -main [& args]
  (component/start (system {})))

(comment
  (def mysystem (component/start (system {})))
  (component/stop mysystem)
  mysystem)
