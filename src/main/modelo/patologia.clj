(ns main.modelo.patologia
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [com.fulcrologic.fulcro.data-fetch :as df] 
            [main.backend.db.conexion :refer [obtener-conexion]]
            [main.backend.db.config :as c])
  (:import java.sql.SQLException))

(pco/defresolver todas-las-patologias
  [_ _]
  {::pco/output [{:todas-las-patologias [:pat_codi :pat_descrip]}]} 
  {:todas-las-patologias (try 
                           (with-open [c (obtener-conexion :maestros)]
                             (c/obtener-patologias c))
                           (catch SQLException e (throw (ex-message e))))})


(def resolvers [todas-las-patologias])

(comment
  
  (c/obtener-patologias (obtener-conexion :maestros))
  
  )