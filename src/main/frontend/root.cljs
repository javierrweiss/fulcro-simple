(ns main.frontend.root
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
            [com.fulcrologic.fulcro.dom :as dom :refer [main div h1 p]]
            [main.frontend.seleccion-pacientes :refer [PacienteList]]
            [main.frontend.formulariocarga :refer [FormularioCarga]]))

(defrouter CargaRouter [_ {:keys [current-state] :as props}]
  {:router-targets [PacienteList FormularioCarga]})

(def ui-cargarouter (comp/factory CargaRouter))

(defsc Root [_ {:keys [router]}]
  {:use-hooks? true
   :query [{:router (comp/get-query CargaRouter)}]
   :initial-state {:router {}}}
  (main :.size-full.justify-items-center.bg-cyan-500
   (div :.bg-cyan-600.p-4.box-border.w-full
    (h1 :.text-center.text-6xl.font-black "Ficha Anestésica"))
   (ui-cargarouter router)))