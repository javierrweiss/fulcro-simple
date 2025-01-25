(ns main.frontend.mutations
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation mostrar-error [{:keys [mensaje] :or {mensaje "Error al cargar datos"}}]
  (action [{:keys [state]}]
          (js/console.log "Mostrando error...")
          (js/console.log "Estado: " state)
          (swap! state assoc-in [:component/id :PacienteList :ui/error] mensaje)))