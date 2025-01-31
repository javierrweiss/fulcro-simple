(ns main.modelo.intervenciones-patologia
  (:require [main.modelo.intervencion :refer [obtener-intervenciones-resumido]]
            [main.modelo.patologia :refer [todas-las-patologias]]
            [com.wsscode.pathom3.connect.operation :as pco]))

#?(:clj
   (pco/defresolver obtener-patologias-e-intervenciones
     []
     {::pco/output [{:patologias-e-intervenciones [{:todas-las-patologias [:pat_codi
                                                                           :pat_descrip]}
                                                   {:intervenciones [:tbc_interven/itv_codi
                                                                     :tbc_interven/itv_descripcion]}]}]}
     {:patologias-e-intervenciones [(todas-las-patologias)
                                    (obtener-intervenciones-resumido)]}))

#?(:clj (def resolvers [obtener-patologias-e-intervenciones])) 

(comment
  (obtener-patologias-e-intervenciones)
  )