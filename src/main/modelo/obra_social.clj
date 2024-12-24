(ns main.modelo.obra-social
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [main.backend.db.conexion :refer [obtener-conexion]]
            [main.backend.db.config :as c])
  (:import java.sql.SQLException))

(pco/defresolver obtener-obras-sociales
  [_ _]
  {::pco/output [{:obras-sociales [:tbc_obras/obr_codigo :tbc_obras/obr_razonsoc]}]} 
  {:obras-sociales 
   (try
     (with-open [c (obtener-conexion :maestros)]
       (c/obtener-obras c))
     (catch SQLException e (throw (ex-message e))))})

(pco/defresolver obtener-obra-social-por-id
  [_ {:tbc_obras/keys [obr_codigo]}]
  {::pco/input [:tbc_obras/obr_codigo]
   ::pco/output [:obra]} 
  {:obra (some-> 
          (try
            (with-open [c (obtener-conexion :maestros)]
              (c/obtener-obra-por-id c {:obr_codigo obr_codigo})) 
            (catch SQLException e (throw (ex-message e))))
              :tbc_obras/obr_razonsoc)})

(def resolvers [obtener-obra-social-por-id obtener-obras-sociales])

(comment
  
  (obtener-obras-sociales nil nil)
  (obtener-obra-social-por-id nil {:tbc_obras/obr_codigo 1820})
  )
