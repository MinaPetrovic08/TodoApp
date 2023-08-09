(ns clojure-todo-app.components.web.pedestal
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]))

(defn test? [system-map]
  (= :test (:env system-map)))

(defrecord Pedestal [system-map system]
  component/Lifecycle
  (start [this]
    (if system
      this
      (cond-> system-map
        true                      (http/create-server)  ;; always create http server
        (not (test? system-map)) (http/start)  ;; start http server if not in testing environment
        true                      ((partial assoc this :system)))))

  (stop [this]
    (when (and system (not (test? system-map)))
      (http/stop system))
    (assoc this :system nil)))

(defn new-pedestal []
  (map->Pedestal {}))
