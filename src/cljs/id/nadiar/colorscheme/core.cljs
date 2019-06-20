(ns id.nadiar.colorscheme.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [id.nadiar.colorscheme.events :as events]
   [id.nadiar.colorscheme.views :as views]
   [id.nadiar.colorscheme.config :as config]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
