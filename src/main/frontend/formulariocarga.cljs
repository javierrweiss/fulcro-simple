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
            [main.frontend.generic-components :refer [ui-renglon ui-opcion ui-renglon-seleccion]]
            [clojure.string :as string]
            [main.frontend.utils.utils :as u]))

(defsc Cabecera [this {}]
  {}
  (div :#cabecera
       (div
        (img {:url " " :alt "imagen_sanatorio"}))
       (div
        (h2 "Carga de datos"))
       (div
        (h3 (str "Fecha: " (js/Date))))))

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
  (print (str "Propiedades en DatosPaciente: " props))
  (print (type edad))
  (div :#datospaciente
       (div
        (div
         (label "Paciente")
         (span nombre))
        (div
         (label "Obra social")
         (span obra_social))
        (div
         (label "Historia Clínica")
         (span hc))
        (div
         (label "Historia Clínica Única")
         (span (or hcu 0)))
        (div
         (label "Sexo")
         (span sexo))
        (div
         (label "Edad")
         (span (u/obtener-edad edad)))
        (div
         (label "Fecha de inicio")
         (span (js/Date))))))

(def ui-datos-paciente (comp/factory DatosPaciente))

(defsc Encabezado [this props]
  {:query  [:tbc_patologia/pat_descrip
            :ui/diagnostico
            :ui/diagnostico-operatorio
            :ui/operacion-propuesta
            :ui/operacion-realizada]
   :ident (fn [] [:component/id ::Encabezado])
   :initial-state {}}
  (let [patologias (->> (:todas-las-patologias props)
                        (map :tbc_patologia/pat_descrip)
                        (map string/trim)
                        sort)]
    (div :#encabezado
         (ui-renglon-seleccion {:etiqueta "Diagnóstico"
                                :opciones patologias
                                :onChange #(m/set-string! this :ui/diagnostico :event %)})
         (ui-renglon-seleccion {:etiqueta "Diagnóstico operatorio"
                                :opciones patologias
                                :onChange #(m/set-string! this :ui/diagnostico-operatorio :event %)})
         (ui-renglon-seleccion {:etiqueta "Operación propuesta"
                                :opciones patologias
                                :onChange #(m/set-string! this :ui/operacion-propuesta :event %)})
         (ui-renglon-seleccion {:etiqueta "Operación realizada"
                                :opciones patologias
                                :onChange #(m/set-string! this :ui/operacion-realizada :event %)}))))

(def ui-encabezado (comp/factory Encabezado))

(defsc Grilla [this {}]
  {}
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

(defsc FormularioCarga [this {:keys [paciente-seleccionado patologias]}]
  {:use-hooks? true
   :query [:paciente-seleccionado
           {:patologias (comp/get-query Encabezado)}]
   :ident (fn [_] [:component/id ::FormularioCarga])
   :route-segment ["carga" :paciente-id] 
   :initial-state {:paciente-seleccionado {}
                   :patologias []}} 
  (section
   (nav
    (ul
     (li (a {:href "#datospaciente"} "Datos personales"))
     (li (a {:href "#encabezado"} "Encabezado"))
     (li (a {:href "#grilla"} "Grilla"))
     (li (a {:href "#medicamentos"} "Medicamentos"))
     (li (a {:href "#pie"} "Pie"))
     (li (a {:href "#observaciones"} "Observaciones"))))
   (ui-cabecera)
   (ui-datos-paciente paciente-seleccionado)
   (div :#cuerpo
        (form 
         (ui-encabezado {:todas-las-patologias patologias}) 
         (ui-grilla)
         (ui-medicamentos)
         (ui-pie)
         (ui-observaciones)
         (ui-nomencladores)))))


(comment
  
  )