(ns main.frontend.seleccion-pacientes
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr] 
            [main.frontend.formulariocarga :as formulariocarga]
            [main.modelo.paciente :as paciente]
            [main.modelo.ficha-anestesica :as ficha-anestesica]
            [main.frontend.routing :refer [route-to]]
            [com.fulcrologic.fulcro.dom :as dom :refer [button
                                                        div
                                                        h2 
                                                        p 
                                                        table tbody tr th td]]
            [clojure.string :as string]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]))

(defn loading [current-load component-to-show] 
  (cond 
    (df/loading? current-load) (p "Cargando...")
    (df/failed? current-load) (p "¡Lo sentimos! ¡Hubo un problema al cargar los datos!")
    :else component-to-show))

(defn gatillar-transicion-formulario-carga
  [comp-ref patient-map patient-id]
  (comp/transact! comp-ref [(ficha-anestesica/inicializar-ficha-anestesica patient-map)] {:parallel? true})
  (df/load! comp-ref :patologias-e-intervenciones nil {:target [:component/id ::formulariocarga/FormularioCarga :datos-encabezado]})
  (df/load! comp-ref :todos-los-profesionales nil {:target [:component/id ::formulariocarga/FormularioCarga :datos-profesionales]})
  (comp/transact! comp-ref [(route-to {:path (dr/path-to formulariocarga/FormularioCarga patient-id)})]))

(defsc PacienteAmbulatorio [this {:keys [tbc_guardia/id 
                                         tbc_guardia/guar_apenom 
                                         tbc_guardia/guar_histclinica 
                                         tbc_guardia/guar_estado 
                                         tbc_guardia/guar_fechaingreso 
                                         tbc_guardia/guar_horaingreso
                                         intervencion]
                                  {:keys [obra tbc_hist_cab_new/histcabsexo tbc_hist_cab_new/histcabfechanac]} :paciente-ambulatorio-histcab
                                  :as props}]
  {:use-hooks? true
   :route-segment ["lista_pacientes"]
   :ident :tbc_guardia/id
   :query [:tbc_guardia/id
           :tbc_guardia/guar_apenom
           :tbc_guardia/guar_histclinica
           :tbc_guardia/guar_estado
           :intervencion
           {:paciente-ambulatorio-histcab [:obra
                                           :tbc_hist_cab_new/histcabsexo
                                           :tbc_hist_cab_new/histcabfechanac]}
           :tbc_guardia/guar_fechaingreso
           :tbc_guardia/guar_horaingreso]} 
  (when props
    (tr {:onClick #(gatillar-transicion-formulario-carga
                    this
                    {:id id
                     :nombre guar_apenom
                     :obra_social obra
                     :hc 0
                     :hcu guar_histclinica
                     :sexo histcabsexo
                     :edad histcabfechanac}
                    id)
         :classes ["border-2"
                   "border-cyan-900"
                   "odd:bg-cyan-300"
                   "even:bg-cyan-400"
                   "hover:bg-cyan-700"
                   "text-center"]}
        (td guar_histclinica)
        (td guar_apenom)
        (td guar_estado)
        (td (string/trim intervencion))
        (td guar_fechaingreso)
        (td guar_horaingreso))))

(def ui-paciente-ambulatorio (comp/factory PacienteAmbulatorio {:keyfn :tbc_guardia/id}))

(defsc PacienteInternado [this {:keys [tbc_admision_scroll/id
                                       tbc_admision_scroll/adm_histclin
                                       tbc_admision_scroll/adm_histclinuni
                                       tbc_admision_scroll/adm_apelnom
                                       tbc_admision_scroll/adm_habita
                                       tbc_admision_scroll/adm_cama
                                       tbc_admision_scroll/adm_fecing
                                       tbc_admision_scroll/adm_horing
                                       tbc_admision_scroll/adm_sexo
                                       tbc_admision_scroll/adm_obrsoc
                                       tbc_admision_scroll/adm_fecnac] :as props}]
  {:use-hooks? true
   :route-segment ["lista_pacientes"]
   :query [:tbc_admision_scroll/id
           :tbc_admision_scroll/adm_histclin
           :tbc_admision_scroll/adm_histclinuni
           :tbc_admision_scroll/adm_apelnom
           :tbc_admision_scroll/adm_habita
           :tbc_admision_scroll/adm_cama
           :tbc_admision_scroll/adm_fecing
           :tbc_admision_scroll/adm_horing
           :tbc_admision_scroll/adm_sexo
           :tbc_admision_scroll/adm_obrsoc
           :tbc_admision_scroll/adm_fecnac]
   :ident :tbc_admision_scroll/id}
  (when props
    (tr {:onClick #(gatillar-transicion-formulario-carga
                    this
                    {:id id
                     :nombre adm_apelnom
                     :hc adm_histclin
                     :hcu adm_histclinuni
                     :obra_social adm_obrsoc
                     :sexo adm_sexo
                     :edad adm_fecnac}
                    id)
         :classes ["border-2" 
                   "border-cyan-900" 
                   "odd:bg-cyan-300" 
                   "even:bg-cyan-400" 
                   "hover:bg-cyan-700"
                   "text-center"]}
        (td adm_histclin)
        (td adm_histclinuni)
        (td adm_apelnom)
        (td adm_habita)
        (td adm_cama)
        (td adm_fecing)
        (td adm_horing))))

(def ui-paciente-internado (comp/factory PacienteInternado {:keyfn :tbc_admision_scroll/id}))

(defsc PacienteTable [_ {:keys [pacientes-ambulatorios pacientes-internados ui/tipo-paciente] :as props}]
  {:use-hooks? true
   :query  [{:pacientes-ambulatorios (comp/get-query PacienteAmbulatorio)}
            {:pacientes-internados (comp/get-query PacienteInternado)}
            :ui/tipo-paciente]
   :initial-state {:pacientes-ambulatorios {}
                   :pacientes-internados {}}}
  (if (= tipo-paciente :ambulatorio)
    (table :.justify-self-center.table-auto.border-2.border-cyan-900.m-4.p-4
           (tbody
            (tr :.bg-cyan-200 (th "Historia Clínica") (th "Nombre") (th "Estado") (th "Diagnóstico") (th "Fecha ingreso") (th "Hora ingreso"))
            (map ui-paciente-ambulatorio pacientes-ambulatorios)))
    (table :.justify-self-center.table-auto.border.border-cyan-900.m-4.p-4
           (tbody
            (tr :.bg-cyan-200 (th "Historia Clínica") (th "Historia Clínica Única") (th "Nombre") (th "Habitación") (th "Cama") (th "Fecha ingreso") (th "Hora ingreso"))
            (map ui-paciente-internado pacientes-internados)))))

(def ui-pacientetable (comp/factory PacienteTable))

(defsc PacienteList [this {:keys [todos-los-pacientes ui/tipo-paciente ui/error] :as props}]
  {:use-hooks? true
   :query  [{:todos-los-pacientes (comp/get-query PacienteTable)}
            :ui/carga-paciente
            :ui/tipo-paciente
            :ui/error
            [df/marker-table :carga-paciente]]
   :initial-state (fn [_]
                    {:todos-los-pacientes (comp/get-initial-state PacienteTable)
                     :ui/tipo-paciente :ambulatorio
                     :ui/error nil})
   :ident (fn [] [:component/id ::PacienteList])
   :route-segment ["lista_pacientes"]}
  (div :.p-4.size-full
       (h2 :.text-center.text-3xl.font-black.p-2 "Selección de Paciente")
       (button :.border-solid.border-2.border-stone-300.ring-4.rounded.m-4.p-3
               {:onClick #(comp/transact! this [(paciente/toggle-tipo-paciente props)])}
               (str "Ver lista de pacientes " (if (= tipo-paciente :ambulatorio) "internados" "ambulatorios")))
       (let [datos (first todos-los-pacientes)
             marker (get props [df/marker-table :carga-paciente])]
         (cond
           (df/loading? marker) (p :.size-full.border-box.text-center.text-2xl.font-black.p-6 "Cargando...")
           error (p :.size-full.border-box.text-center.text-2xl.font-black.p-6 "¡Lo sentimos! ¡Hubo un problema al cargar los datos!")
           :else (ui-pacientetable {:pacientes-ambulatorios (:pacientes-ambulatorios datos)
                                    :pacientes-internados (:pacientes-internados datos)
                                    :ui/tipo-paciente tipo-paciente})))))


(comment
  
  (comp/get-query PacienteList)
  (comp/get-query PacienteAmbulatorio)


  (let [etiqueta "Diagnóstico operatorio"]
    (-> etiqueta (string/replace #"(?i)\W+" "_") string/lower-case))
  
  (fs/entity->pristine* {} [:id #uuid "6cfd71d7-2715-45a0-b9c6-dc1ec63eaacf"])
  )  