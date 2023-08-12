(ns clojure-todo-app.components.web.index
  (:require [ring.util.response :as ring-resp]
            [hiccup.page :as hp]
            [clojure-todo-app.components.web.tasks :as tasks]))

(defn- toggle-task-index [task]
  [:form#toggle-task {:action "/task/toggle" :method :POST}
   [:input {:name "id" :value (:id task) :hidden true}]
   [:input {:class "btn btn-sm btn-dark"
            :type :submit
            :value "Toggle"}]])

(defn- delete-task-index [task]
  [:form#delete-task {:action (str "/task/delete?id=" (:id task)) :method :POST}
   [:input {:name "id" :value (:id task) :hidden true}]
   [:input {:class "btn btn-sm btn-dark" :type :submit :value "Delete"}]])

(defn- tasks->rows []
  (for [task (sort-by :title (tasks/query-all))]
    [:tr
     [:td (:title task)]
     [:td (if (:done task) "✓" "🗴")]
     [:td (toggle-task-index task)]
     [:td (delete-task-index task)]]))

(defn base-template [& body]
  (ring-resp/response
   (hp/html5
    [:head
     [:title "Todo-Application"]
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible"
             :content "IE=edge"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (hp/include-css "css/bootstrap.min.css")]
    [:body
     [:div.container {:style "padding-top: 3rem"}
      body]])))

(defn home-page [request]
  (base-template
   [:h1 "Todo-Application"]
   [:form#add-task {:action "/task/add" :method :POST}
    [:div.form-group
     [:label#title "New Task"]
     [:input.form-control {:name "title" :required true}]]
    [:input {:class "btn btn-dark"
             :type :submit
             :value "Add"}]]

   [:hr {:style "margin: 3rem 0"}]

   [:h4 "Tasks"]
   [:table.table.table-striped
    [:thead
     [:tr
      [:th {:width "90%"} "Title"]
      [:th "Done?"]
      [:th]]]
    (vec (conj (tasks->rows) :tbody))]))

(defn add-task [{:keys [form-params]}]
  (tasks/add! (:title form-params))
  (ring-resp/redirect "/"))

(defn toggle-task [{:keys [form-params]}]
  (tasks/toggle! (:id form-params))
  (ring-resp/redirect "/"))

(defn delete-task [{:keys [form-params]}]
  (tasks/delete! (:id form-params))
  (ring-resp/redirect "/"))

(defn respond-hello [request]
  {:status 200 :body "Hello, world!"})