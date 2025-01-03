(ns main.modelo.intervencion
  (:require [com.wsscode.pathom3.connect.operation :as pco] 
            [main.backend.db.conexion :refer [obtener-conexion]]
            [main.backend.db.config :as c])
  (:import java.sql.SQLException))

(pco/defresolver obtener-intervenciones
  []
  {::pco/output [{:intervenciones [:tbc_interven/itv_codi
                                   :tbc_interven/itv_codiesp
                                   :tbc_interven/itv_dadores
                                   :tbc_interven/itv_estad
                                   :tbc_interven/itv_nivelcomplejidad
                                   :tbc_interven/itv_tipestudio
                                   :tbc_interven/itv_vigila
                                   :tbc_interven/itv_abdomen
                                   :tbc_interven/itv_descripcion]}]} 
  {:intervenciones (try
                     (with-open [c (obtener-conexion :maestros)]
                       (c/obtener-intervenciones c)) 
                     (catch SQLException e (throw (ex-message e))))})

(pco/defresolver obtener-intervencion-por-id
  [_ {:tbc_interven/keys [itv_codi]}]
  {::pco/input [:tbc_interven/itv_codi]
   ::pco/output [:intervencion]} 
  {:intervencion (some-> 
                  (try
                    (with-open [c (obtener-conexion :maestros)] 
                      (c/obtener-descr-intervencion-por-id c {:itv_codi itv_codi}))
                    (catch SQLException e (throw (ex-message e))))
                  :tbc_interven/itv_descripcion)})


(def resolvers [obtener-intervencion-por-id obtener-intervenciones])

(comment 
  
  (obtener-intervencion-por-id nil {:tbc_interven/itv_codi 1014})
  (obtener-intervenciones)
  

  :rcf)