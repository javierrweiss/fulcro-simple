(ns main.modelo.patologia
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [com.fulcrologic.fulcro.data-fetch :as df] 
            [main.backend.db.conexion :refer [conexiones conectar-maestros]]
            [main.backend.db.config :as c]))

(pco/defresolver todas-las-patologias
  [_ _]
  {::pco/output [{:todas-las-patologias [:pat_codi :pat_descrip]}]}
  (when-not (:maestros @conexiones) (conectar-maestros))
  {:todas-las-patologias (c/obtener-patologias (:maestros @conexiones))})


(def resolvers [todas-las-patologias])

(comment
  
  (c/obtener-patologias (:maestros @conexiones))
  
  )