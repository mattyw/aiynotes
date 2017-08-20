(ns aiynotes.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :as hiccup]
            [ring.util.response :refer [redirect]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def store "notes.txt")

(def notes (atom '()))

(defn load-notes [atm file]
  (let [data (clojure.string/split-lines (slurp file))]
  (reset! atm data)))

(defn update-notes [atm new-note]
  (swap! atm conj new-note)
  (spit store (clojure.string/join "\n" @atm)))

(defn handle-note [note]
  (update-notes notes note))

(defn handle-home [notes]
  (hiccup/html
    (for [note notes] 
      [:p note]
      )))

(defroutes app-routes
  (GET "/" [] (handle-home @notes))
  ;; Add note with http --form POST http://localhost:3000/note note="foobar"
  (POST "/note" [note] 
        (handle-note note)
        (redirect "/"))
  (route/not-found "Not Found"))

(load-notes notes store)

(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))
