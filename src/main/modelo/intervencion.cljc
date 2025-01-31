(ns main.modelo.intervencion
  (:require #?@(:clj [[main.backend.db.conexion :refer [obtener-conexion]]
                      [main.backend.db.config :as c]
                      [com.brunobonacci.mulog :as µ]]
                :cljs [])
            [tick.core :as t]
            [com.wsscode.pathom3.connect.operation :as pco]))

#?(:clj (import java.sql.SQLException))
 
#?(:clj 
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
                        (catch SQLException e (let [msj (ex-message e)]
                                                (µ/log ::excepcion-al-obtener-intervencion :fecha (t/date-time) :excepcion msj)
                                                (throw (ex-info "Hubo un problema al obtener las intervenciones" {:excepcion msj})))))}))

#?(:clj
   (pco/defresolver obtener-intervenciones-resumido
     []
     {::pco/output [{:todas-las-intervenciones [:tbc_interven/itv_codi 
                                                :tbc_interven/itv_descripcion]}]}
     {:intervenciones (try
                        (with-open [c (obtener-conexion :maestros)]
                          (c/obtener-intervenciones-corto c))
                        (catch SQLException e (let [msj (ex-message e)]
                                                (µ/log ::excepcion-al-obtener-intervencion :fecha (t/date-time) :excepcion msj)
                                                (throw (ex-info "Hubo un problema al obtener las intervenciones" {:excepcion msj})))))}))


#?(:clj
   (pco/defresolver obtener-intervencion-por-id
     [_ {:tbc_interven/keys [itv_codi]}]
     {::pco/input [:tbc_interven/itv_codi]
      ::pco/output [:intervencion]} 
     {:intervencion (some-> 
                     (try
                       (with-open [c (obtener-conexion :maestros)] 
                         (c/obtener-descr-intervencion-por-id c {:itv_codi itv_codi}))
                       (catch SQLException e (let [msj (ex-message e)] 
                                               (µ/log ::excepcion-al-obtener-intervencion-por-id :fecha (t/date-time) :excepcion msj)
                                               (throw (ex-info "Hubo un problema al obtener la intervención por id" {:excepcion msj})))))
                     :tbc_interven/itv_descripcion)}))


#?(:clj 
   (def resolvers [obtener-intervencion-por-id obtener-intervenciones obtener-intervenciones-resumido]))

(comment 
  
  (obtener-intervencion-por-id nil {:tbc_interven/itv_codi 1014})
  (obtener-intervenciones) 
  (obtener-intervenciones-resumido)
  
  
  :rcf)