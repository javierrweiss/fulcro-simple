(ns main.modelo.obra-social
  (:require #?@(:clj [[main.backend.db.conexion :refer [obtener-conexion]]
                      [main.backend.db.config :as c]
                      [com.brunobonacci.mulog :as µ]]
                :cljs [])
            [tick.core :as t]
            [com.wsscode.pathom3.connect.operation :as pco]))

#?(:clj (import java.sql.SQLException))

#?(:clj
   (pco/defresolver obtener-obras-sociales
     [_ _]
     {::pco/output [{:obras-sociales [:tbc_obras/obr_codigo :tbc_obras/obr_razonsoc]}]} 
     {:obras-sociales 
      (try
        (with-open [c (obtener-conexion :maestros)]
          (c/obtener-obras c))
        (catch SQLException e (let [msj (ex-message e)] 
                                (µ/log ::excepcion-al-obtener-obras-sociales :fecha (t/date-time) :excepcion msj)
                                (throw (ex-info "Hubo un problema al obtener la obra social" {:excepcion msj})))))}))

#?(:clj
   (pco/defresolver obtener-obra-social-por-id
     [_ {:tbc_obras/keys [obr_codigo]}]
     {::pco/input [:tbc_obras/obr_codigo]
      ::pco/output [:obra]} 
     {:obra (some-> 
             (try
               (with-open [c (obtener-conexion :maestros)]
                 (c/obtener-obra-por-id c {:obr_codigo obr_codigo})) 
               (catch SQLException e (let [msj (ex-message e)] 
                                       (µ/log ::excepcion-al-obtener-obra-social-por-id :fecha (t/date-time) :excepcion msj)
                                       (throw (ex-info "Hubo un problema al obtener la obra social por id" {:excepcion msj})))))
             :tbc_obras/obr_razonsoc)}))

#?(:clj
   (def resolvers [obtener-obra-social-por-id obtener-obras-sociales]))

(comment
  
  (obtener-obras-sociales nil nil)
  (obtener-obra-social-por-id nil {:tbc_obras/obr_codigo 1820})
  )
