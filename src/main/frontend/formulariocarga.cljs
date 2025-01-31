(ns main.frontend.formulariocarga
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [main.frontend.generic-components :as generic]
            [main.modelo.paciente :as paciente]
            [com.fulcrologic.fulcro.dom :as dom :refer [form
                                                        button
                                                        div
                                                        h1 h2 h3 h4 h5
                                                        nav
                                                        main
                                                        section
                                                        select
                                                        article
                                                        header
                                                        a
                                                        p
                                                        input
                                                        img
                                                        ul ol li
                                                        label
                                                        span
                                                        table tbody tr th td]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.mutations :as m]
            [com.fulcrologic.fulcro.react.hooks :as hooks]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [main.frontend.generic-components :refer [ui-renglon ui-opcion ui-renglon-seleccion]]
            [clojure.string :as string]
            [main.frontend.utils.utils :as u]))

(m/defmutation actualizar-hora [{:keys [current-time]}]
  (action [{:keys [state]}]
          (swap! state assoc-in [:component/id ::HoraActual :current-time] current-time)))

(defsc HoraActual [this {:keys [current-time] :as props}]
  {:use-hooks? true
   :query [:current-time]
   :ident (fn [] [:component/id ::HoraActual])
   :initial-state (fn [_] {:current-time (js/Date)})}
  (hooks/use-effect
   (fn []
     (let [id (js/setInterval
               (fn []
                 (comp/transact! this [(actualizar-hora {:current-time (js/Date)})] {:refresh [::HoraActual]}))
               1000)]
       (fn [] (js/clearInterval id)))))
  (h3 :.p-2.txt-2xl.font-bold (str current-time)))

(def ui-hora-actual (comp/factory HoraActual))

(defsc Cabecera [this props]
  {:use-hooks? true 
   :ident (fn [] [:component/id ::Cabecera])
   :initial-state {:ui/current-time (comp/get-initial-state HoraActual)}} 
  (div :#cabecera.flex.flex-row.gap-3.p-4.m-4.bg-cyan-400.ring-4
       (div {:classes ["basis-1/4"]}
            (img :.p-2 {:src "/img/Logo_Sanatorio_Colegiales_-_Horizontal-350x156.png" :alt "imagen_sanatorio"}))
       (div {:classes ["basis-1/2"]}
            (h2 :.p-2.text-xl.font-bold.text-center.italic "Carga de datos"))
       (div {:classes ["basis-1/4"]} 
             #_(ui-hora-actual props))))

(def ui-cabecera (comp/factory Cabecera))

(defsc DatosPaciente [this {:keys [id nombre hc hcu sexo edad obra_social] :as props}]
  {:query [:id :nombre :hc :hcu :sexo :edad :obra_social]
   :initial-state (fn [params]
                    {:id (:id params)
                     :nombre (:nombre params)
                     :hc (:hc params)
                     :hcu (:hcu params)
                     :sexo (:sexo params)
                     :edad (:edad params)
                     :obra_social (:obra_social params)})
   :ident :id} 
  (div :#datospaciente
       (div :.grid.grid-cols-4.gap-2
        (div :.flex-1.gap-2
         (label :.p-1.font-bold "Paciente: ")
         (span nombre))
        (div :.flex-1.gap-2
         (label :.p-1.font-bold "Obra social: ")
         (span obra_social))
        (div :.flex-1.gap-2
         (label :.p-1.font-bold "Historia Clínica: ")
         (span hc))
        (div :.flex-1.gap-2
         (label :.p-1.font-bold "Historia Clínica Única: ")
         (span (or hcu 0)))
        (div :.flex-1.gap-2
         (label :.p-1.font-bold "Sexo: ")
         (span (u/obtener-sexo sexo)))
        (div :.flex-1.gap-2
         (label :.p-1.font-bold "Edad: ")
         (span (u/obtener-edad edad)))
        (div :.flex-1.gap-2
         (label :.p-1.font-bold "Fecha de inicio: ")
         (span (js/Date))))))

(def ui-datos-paciente (comp/factory DatosPaciente))

(defsc Patologias [this {:keys [todas-las-patologias
                                intervenciones
                                diagnostico
                                diagnostico_operatorio
                                oper_propuesta
                                oper_realizada
                                id] :as props}] 
  {:query [:todas-las-patologias
           :intervenciones
           :diagnostico
           :diagnostico_operatorio
           :oper_propuesta
           :oper_realizada
           :id
           fs/form-config-join]
   :ident :id
   :form-fields #{:diagnostico
                  :diagnostico_operatorio
                  :oper_propuesta
                  :oper_realizada}} 
  (let [patologias (->>  todas-las-patologias 
                        (sort-by :tbc_patologia/pat_descrip))
        intervenciones (->> intervenciones
                            (sort-by :tbc_interven/itv_descripcion))]
    (div :#patologias
         (ui-renglon-seleccion {:etiqueta "Diagnóstico"
                                :opciones patologias
                                :llave-valor-real :tbc_patologia/pat_codi
                                :llave-valor-mostrado :tbc_patologia/pat_descrip
                                :onChange #(m/set-string! this diagnostico :event %)})
         (ui-renglon-seleccion {:etiqueta "Diagnóstico operatorio"
                                :opciones patologias
                                :llave-valor-real :tbc_patologia/pat_codi
                                :llave-valor-mostrado :tbc_patologia/pat_descrip
                                :onChange #(m/set-string! this diagnostico_operatorio :event %)})
         (ui-renglon-seleccion {:etiqueta "Operación propuesta"
                                :opciones intervenciones
                                :llave-valor-real :tbc_interven/itv_codi
                                :llave-valor-mostrado :tbc_interven/itv_descripcion
                                :onChange #(m/set-string! this oper_propuesta :event %)})
         (ui-renglon-seleccion {:etiqueta "Operación realizada"
                                :opciones intervenciones
                                :llave-valor-real :tbc_interven/itv_codi
                                :llave-valor-mostrado :tbc_interven/itv_descripcion
                                :onChange #(m/set-string! this oper_realizada :event %)}))))

(def ui-patologias (comp/factory Patologias {:keyfn random-uuid}))

(defsc PersonalMedico [this {:keys [profesionales
                                    cirujano_legajo
                                    ayudante_legajo
                                    auxiliar_legajo
                                    anestesiologo_lega
                                    id]}]
  {:query [:profesionales
           :cirujano_legajo
           :ayudante_legajo
           :auxiliar_legajo
           :anestesiologo_lega
           :id
           fs/form-config-join]
   :ident :id
   :form-fields #{:cirujano_legajo
                  :ayudante_legajo
                  :auxiliar_legajo
                  :anestesiologo_lega}}
  (print profesionales)
  (let [profesionales (->> profesionales 
                           (sort-by :tbc_medicos_personal/medperapeynom))] 
    (div :#personal-medico
               (ui-renglon-seleccion {:etiqueta "Cirujano"
                                      :opciones profesionales
                                      :llave-valor-real :tbc_medicos_personal/medpercod
                                      :llave-valor-mostrado :tbc_medicos_personal/medperapeynom
                                      :onChange #(m/set-integer! this cirujano_legajo :event %)})
               (ui-renglon-seleccion {:etiqueta "Anestesiólogo"
                                      :opciones profesionales
                                      :llave-valor-real :tbc_medicos_personal/medpercod
                                      :llave-valor-mostrado :tbc_medicos_personal/medperapeynom
                                      :onChange #(m/set-integer! this anestesiologo_lega :event %)})
               (ui-renglon-seleccion {:etiqueta "Ayudante"
                                      :opciones profesionales
                                      :llave-valor-real :tbc_medicos_personal/medpercod
                                      :llave-valor-mostrado :tbc_medicos_personal/medperapeynom
                                      :onChange #(m/set-integer! this ayudante_legajo :event %)})
               (ui-renglon-seleccion {:etiqueta "Auxiliar"
                                      :opciones profesionales
                                      :llave-valor-real :tbc_medicos_personal/medpercod
                                      :llave-valor-mostrado :tbc_medicos_personal/medperapeynom
                                      :onChange #(m/set-integer! this auxiliar_legajo :event %)}))))

(def ui-personal-medico (comp/factory PersonalMedico {:keyfn random-uuid}))

#_'[tbc_interven/itv_codi
 tbc_interven/itv_descripcion
 ui/diagnostico
 ui/diagnostico-operatorio
 ui/operacion-propuesta
 ui/operacion-realizada
 paciente/id
 diagnostico
 diagnostico_operatorio
 oper_propuesta
 oper_realizada
 cirujano_legajo
 ayudante_legajo
 auxiliar_legajo
 anestesiologo_lega
 resp_frec_x_min
 riesgo_op_grado
 resp_tipo
 t_art_habitual_max
 t_art_habitual_min
 t_art_actual_max
 t_art_actual_min
 talla
 peso
 piso
 habitacion
 cama
 pulso
 complic_preoperatoria
 anest_gral
 anest_conductiva
 anest_local
 anest_nla
 urgencia
 premedicacion
 droga_dosis]

#_'[:tbc_patologia/pat_codi
    :tbc_patologia/pat_descrip
    :tbc_interven/itv_codi
    :tbc_interven/itv_descripcion
    :ui/diagnostico
    :ui/diagnostico-operatorio
    :ui/operacion-propuesta
    :ui/operacion-realizada]

(defsc Encabezado [this {:keys [todas-las-patologias
                                intervenciones
                                lista-profesionales
                                id] :as props}]
  {:query  [:todas-las-patologias
            :intervenciones
            :id
            :lista-profesionales
            fs/form-config-join]
   :ident :id
   #_#_:form-fields #{:diagnostico
                      :diagnostico_operatorio
                      :oper_propuesta
                      :oper_realizada
                      :cirujano_legajo
                      :ayudante_legajo
                      :auxiliar_legajo
                      :anestesiologo_lega
                      :resp_frec_x_min
                      :riesgo_op_grado
                      :resp_tipo
                      :t_art_habitual_max
                      :t_art_habitual_min
                      :t_art_actual_max
                      :t_art_actual_min
                      :talla
                      :peso
                      :piso
                      :habitacion
                      :cama
                      :pulso
                      :complic_preoperatoria
                      :anest_gral
                      :anest_conductiva
                      :anest_local
                      :anest_nla
                      :urgencia
                      :premedicacion
                      :droga_dosis}
   :initial-state {}}
  (div :#encabezado.p-3.grid.grid-cols-2.gap-2
       (ui-patologias {:todas-las-patologias todas-las-patologias
                       :intervenciones intervenciones
                       :id id})
       (ui-personal-medico {:profesionales lista-profesionales
                            :id id})))

(def ui-encabezado (comp/factory Encabezado))

(defsc Grilla [this {:keys [id]}]
  {:query [:id fs/form-config-join]
   :ident :id
   :form-fields #{}}
  (div :#grilla))

(def ui-grilla (comp/factory Grilla))

(defsc Medicamentos [this {}]
  {}
  (div :#medicamentos))

(def ui-medicamentos (comp/factory Medicamentos))

(defsc Pie [this {}]
  {}
  (div :#pie))

(def ui-pie (comp/factory Pie))

(defsc Observaciones [this {}]
  {}
  (div :#observaciones))

(def ui-observaciones (comp/factory Observaciones))

(defsc Nomencladores [this {}]
  {}
  (div :#nomencladores))

(def ui-nomencladores (comp/factory Nomencladores))

(defsc FormularioCarga [this {:keys [ui/current-time paciente-seleccionado datos-encabezado datos-profesionales] :as props}]
  {:use-hooks? true
   :query [:paciente-seleccionado
           {:ui/current-time (comp/get-query HoraActual)}
           {:datos-encabezado (comp/get-query Encabezado)} 
           {:datos-profesionales (comp/get-query Encabezado)}] 
   :ident (fn [_] [:component/id ::FormularioCarga])
   :route-segment ["carga" :paciente-id]
   :initial-state (fn [_] 
                    {:ui/current-time (comp/get-initial-state HoraActual)
                     :paciente-seleccionado {}
                     :datos-encabezado (comp/get-initial-state Encabezado)
                     :datos-profesionales (comp/get-initial-state Encabezado)})} 
  #_(print props) 
  (section :.size-full.m-2.p-4
           (nav :.justify-items-center
                (ul :.flex.flew-row.p-3.gap-4
                    (li (div :.p-2.border-solid.border-2.border-cyan-950.hover:text-white.box-border (a {:href "#datospaciente"} "Datos personales")))
                    (li (div :.p-2.border-solid.border-2.border-cyan-950.hover:text-white.box-border (a {:href "#encabezado"} "Encabezado")))
                    (li (div :.p-2.border-solid.border-2.border-cyan-950.hover:text-white.box-border (a {:href "#grilla"} "Grilla")))
                    (li (div :.p-2.border-solid.border-2.border-cyan-950.hover:text-white.box-border (a {:href "#medicamentos"} "Medicamentos")))
                    (li (div :.p-2.border-solid.border-2.border-cyan-950.hover:text-white.box-border (a {:href "#pie"} "Pie")))
                    (li (div :.p-2.border-solid.border-2.border-cyan-950.hover:text-white.box-border (a {:href "#observaciones"} "Observaciones")))
                    (li (div :.p-2.border-solid.border-2.border-cyan-950.hover:text-white.box-border (a {:onClick (fn [_]) #_(dr/change-route! this ["lista_pacientes"])} "Selección de paciente"))))) 
           (ui-cabecera current-time) 
           (ui-datos-paciente paciente-seleccionado)
           (div :#cuerpo.size-full
                (form 
                 (ui-encabezado (merge datos-profesionales (first datos-encabezado) (second datos-encabezado) paciente-seleccionado))
                 (ui-grilla)
                 (ui-medicamentos)
                 (ui-pie)
                 (ui-observaciones)
                 (ui-nomencladores)))))


(comment
  
  (hooks/use-effect)

  (m/default-result-action!)
  
  :rcf)