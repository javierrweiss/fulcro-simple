(ns main.modelo.intervencion
  (:require [com.wsscode.pathom3.connect.operation :as pco] 
            [main.backend.db.conexion :refer [conexiones conectar-maestros]]
            [main.backend.db.config :as c]))

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
  (when-not (:maestros @conexiones) (conectar-maestros))
  {:intervenciones (c/obtener-intervenciones (:maestros @conexiones))})

(pco/defresolver obtener-intervencion-por-id
  [_ {:tbc_interven/keys [itv_codi]}]
  {::pco/input [:tbc_interven/itv_codi]
   ::pco/output [:intervencion]}
  (when-not (:maestros @conexiones) (conectar-maestros)) 
  {:intervencion (-> (c/obtener-descr-intervencion-por-id (:maestros @conexiones) {:itv_codi itv_codi})
                     :tbc_interven/itv_descripcion)})


(def resolvers [obtener-intervencion-por-id obtener-intervenciones])

(comment 
  
  (obtener-intervencion-por-id nil {:tbc_interven/itv_codi 1014})
  (obtener-intervenciones)
  

  :rcf)