(ns main.modelo.patologia
  (:require #?@(:clj [[com.fulcrologic.fulcro.data-fetch :as df]
                      [main.backend.db.conexion :refer [obtener-conexion]]
                      [main.backend.db.config :as c]
                      [com.brunobonacci.mulog :as µ]]
                :cljs [[com.fulcrologic.fulcro.mutations :refer [defmutation]]])
            [tick.core :as t]
            [com.wsscode.pathom3.connect.operation :as pco]
            [clojure.string :as string]))

#?(:clj (import java.sql.SQLException))

#?(:clj
   (pco/defresolver todas-las-patologias
     [_ _]
     {::pco/output [{:todas-las-patologias [:tbc_patologia/pat_codi :tbc_patologia/pat_descrip]}]} 
     {:todas-las-patologias (try 
                              (with-open [c (obtener-conexion :maestros)]
                                (some->> (c/obtener-patologias c)
                                        (mapv #(update % :tbc_patologia/pat_descrip string/trim))
                                        (sort-by :tbc_patologia/pat_descrip)))
                              (catch SQLException e (let [msj (ex-message e)] 
                                                      (µ/log ::excepcion-al-obtener-todas-las-patologias :fecha (t/date-time) :excepcion msj)
                                                      (throw (ex-info "Hubo un problema al obtener todas las patologias" {:excepcion msj})))))}))

#?(:clj
   (def resolvers [todas-las-patologias]))

(comment
  
  (c/obtener-patologias (obtener-conexion :maestros))
  (todas-las-patologias)

  (with-open [c (obtener-conexion :maestros)]
    (some->> (c/obtener-patologias c)
             (mapv #(update % :tbc_patologia/pat_descrip string/trim))
             (sort-by :tbc_patologia/pat_descrip)))
  )