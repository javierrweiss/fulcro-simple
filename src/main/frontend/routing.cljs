(ns main.frontend.routing
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [edn-query-language.core :as eql]
            [pushy.core :as pushy] 
            [clojure.string :as string]
            [main.frontend.application :refer [APP]]))

(defn url->path
  [url]
  (-> url
      (string/split "?")
      first
      (string/split "/")
      rest 
      vec))

(defn path->url
  [path]
  (->> path
       (interleave (repeat "/"))
       string/join))

(defn existe-ruta?
  [app ruta]
  (let [state-map (app/current-state app)
        root-class (app/root-class app)
        root-query (comp/get-query root-class state-map)
        ast (eql/query->ast root-query)]
    (some? (dr/ast-node-for-route ast ruta)))) 
 
 (def default-route ["lista_pacientes"])

(defonce historia (pushy/pushy
                   (fn [path]
                     (dr/change-route APP path))
                   (fn [url]
                     (let [path (url->path url)]
                       (if (existe-ruta? APP path)
                         path
                         default-route)))))

(defn iniciar!
  []
  (dr/initialize! APP)
  (pushy/start! historia))
 
 (defn route-to!
   [path]
   (pushy/set-token! historia (path->url path)))
 
 (defmutation route-to
   [{:keys [path]}]
   (action [_]
           (route-to! path)))

(comment
  
  (url->path "/abd/ruta/hdi?ww=122")
(path->url ["adb" "dsd" "1230"])
  )