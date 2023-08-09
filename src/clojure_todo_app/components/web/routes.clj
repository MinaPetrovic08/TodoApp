(ns clojure-todo-app.components.web.routes
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [clojure-todo-app.components.web.index :as index]
            [io.pedestal.http.route :as route]))

(def common-interceptors [(body-params/body-params) http/html-body])

(def routes
  #{["/" :get (conj common-interceptors `index/home-page) :route-name :index]
    ["/task/add" :post (conj common-interceptors `index/add-task) :route-name :task-add]
    ["/task/toggle" :post (conj common-interceptors `index/toggle-task) :route-name :task-toggle]
    ["/greet" :get index/respond-hello :route-name :greet]})


(def url-for (route/url-for-routes
              (route/expand-routes routes)))