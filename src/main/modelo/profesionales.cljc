(ns main.modelo.profesionales
  (:require #?@(:clj [[com.fulcrologic.fulcro.data-fetch :as df]
                      [main.backend.db.conexion :refer [obtener-conexion]]
                      [main.backend.db.config :as c]
                      [com.brunobonacci.mulog :as µ]]
                :cljs [])
            [tick.core :as t]
            [com.wsscode.pathom3.connect.operation :as pco]
            [clojure.string :as string]))

#?(:clj (import java.sql.SQLException))

#?(:clj 
   (pco/defresolver obtener-todos-los-profesionales
     []
     {::pco/output [{:todos-los-profesionales {:lista-profesionales [:tbc_medicos_personal/medpercod
                                                                     :tbc_medicos_personal/medperapeynom]}}]}
     {:todos-los-profesionales {:lista-profesionales (try
                                                       (with-open [c (obtener-conexion :maestros)]
                                                         (some->> (c/obtener-todos-los-profesionales c)
                                                                 (map #(update % :tbc_medicos_personal/medperapeynom string/trim))
                                                                  (sort-by :tbc_medicos_personal/medperapeynom)))
                                                       (catch SQLException e (let [msj (ex-message e)]
                                                                               (µ/log ::excepcion-al-obtener-todos-los-profesionales :fecha (t/date-time) :excepcion msj)
                                                                               (throw (ex-info "Hubo un problema al obtener todos los profesionales" {:excepcion msj})))))}}))


#?(:clj
   (pco/defresolver obtener-todos-profesional-por-id
     [{:keys [medpercod]}]
     {::pco/output [{:profesional [:tbc_medicos_personal/medperemp
                                   :tbc_medicos_personal/medpercod
                                   :tbc_medicos_personal/medperapeynom
                                   :tbc_medicos_personal/medperesp
                                   :tbc_medicos_personal/medpertipoie
                                   :tbc_medicos_personal/medperfechaacredita
                                   :tbc_medicos_personal/medperfechafcontrato
                                   :tbc_medicos_personal/medperfechabaja
                                   :tbc_medicos_personal/medpertipocontrato
                                   :tbc_medicos_personal/medpermatricula
                                   :tbc_medicos_personal/medperestado
                                   :tbc_medicos_personal/medpermatricun
                                   :tbc_medicos_personal/medpercuit]}]}
     {:profesional (some->
                    (try
                      (with-open [c (obtener-conexion :maestros)]
                        (c/obtener-profesional-por-id c {:medpercod medpercod}))
                      (catch SQLException e (let [msj (ex-message e)]
                                              (µ/log ::excepcion-al-obtener-profesional-por-id :fecha (t/date-time) :excepcion msj)
                                              (throw (ex-info "Hubo un problema al obtener el profesional por id" {:excepcion msj})))))
                    first)}))

#?(:clj (def resolvers [obtener-todos-los-profesionales obtener-todos-profesional-por-id]))

(comment
  (obtener-todos-los-profesionales) 
  (obtener-todos-profesional-por-id {:medpercod 14500})
  )