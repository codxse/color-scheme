(ns id.nadiar.colorscheme.events
  (:require
   [re-frame.core :as re-frame]
   [id.nadiar.colorscheme.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
  :register-data
  (fn [db [_ path value]]
    (if (keyword? path)
      (assoc db path value)
      (assoc-in db (vec path) value))))
