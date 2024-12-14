(ns main.modelo.obra-social
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [main.backend.db.conexion :refer [conexiones conectar-maestros]]
            [main.backend.db.config :as c]))

(pco/defresolver obtener-obras-sociales
  [_ _]
  {::pco/output [{:obras-sociales [:tbc_obras/obr_codigo :tbc_obras/obr_razonsoc]}]} 
  (when-not (:maestros @conexiones) (conectar-maestros))
  {:obras-sociales (c/obtener-obras (:maestros @conexiones))})

(pco/defresolver obtener-obra-social-por-id
  [_ {:tbc_obras/keys [obr_codigo]}]
  {::pco/input [:tbc_obras/obr_codigo]
   ::pco/output [:obra]}
  (when-not (:maestros @conexiones) (conectar-maestros))
  {:obra (-> (c/obtener-obra-por-id (:maestros @conexiones) {:obr_codigo obr_codigo})
              :tbc_obras/obr_razonsoc)})

(def resolvers [obtener-obra-social-por-id obtener-obras-sociales])

(comment
  
  (obtener-obras-sociales nil nil)
  (obtener-obra-social-por-id nil {:tbc_obras/obr_codigo 1820})
  )
