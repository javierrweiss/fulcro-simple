(ns main.frontend.client
  (:require [com.fulcrologic.fulcro.application :as app] 
            [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.networking.http-remote :as http]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            ;;["react-dom/client" :as dom-client]
            [main.frontend.root :refer [Root]]
            [main.frontend.seleccion-pacientes :refer [PacienteList]]))

(defonce APP (-> (app/fulcro-app {:remotes {:remote (http/fulcro-http-remote {})}}) (with-react18)))

(defn ^:export init []
  (js/console.log "Cargando aplicación...")
  (app/set-root! APP Root {:initialize-state? true})
  (dr/initialize! APP) 
  (js/console.log "Cargando datos...") 
  (df/load! APP :todos-los-pacientes PacienteList {:marker :carga-paciente})
  (dr/change-route! APP ["lista_pacientes"])
  (js/console.log "Montando aplicación...")
  (app/mount! APP Root "app" {:initialize-state? false}))

(defn ^:export refrescar [] 
  (app/mount! APP Root "app")
  (comp/refresh-dynamic-queries! APP)
  (js/console.log "Recargado..."))

(comment 
  (refrescar)
  (require '[main.frontend.formulariocarga :as f])
  
  (df/load! APP :todas-las-patologias f/FormularioCarga {:post-mutation 'dr/target-ready
                                                         :post-mutation-params {:target [:paciente-seleccionado 123]}})

  (df/load! APP :todas-las-patologias f/Encabezado {:target [:component/id ::Encabezado :todas-las-patologias]})
  (df/load! APP :todas-las-patologias f/Encabezado)
  (refrescar) 
  (dr/active-routes APP)
  
 (dr/change-route! APP ["carga" #uuid "5efd0237-f8fb-4b12-b49e-4bd09c6678d5"])
  (dr/change-route APP ["carga" "1"])

(js/alert "Holaa Chrome!!")

(defn consolita 
  [a b c]
  (eduction (comp (map inc) (filter odd?)) (vector a b c)))

  (defn crear-titulo
    [titulo]
    (dom/h1 titulo))
  (* 16 1024)
  (consolita 1 34 13)
  
  (keys APP)
  
  (::app/state-atom APP)
  (app/current-state APP)
  (df/load! APP :main.modelo.paciente/id FichaAnestesica) 
)