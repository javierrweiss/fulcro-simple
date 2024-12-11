(ns main.modelo.paciente
  (:require [com.fulcrologic.fulcro.mutations :refer [defmutation]]))

(defmutation selecciona-paciente [props]
  (action [{:keys [state]}]
          (swap! state assoc-in [:component/id :main.frontend.formulariocarga/FormularioCarga :paciente-seleccionado] props)))

(defmutation toggle-tipo-paciente [_]
  (action [{:keys [state]}]
          (swap! state update-in [:component/id :PacienteList :ui/tipo-paciente] (fn [val] (if (= val :internado) :ambulatorio :internado)))))