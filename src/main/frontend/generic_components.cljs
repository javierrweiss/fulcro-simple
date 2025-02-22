(ns main.frontend.generic-components
  (:require 
   [clojure.string :as string]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom :refer [form
                                               button
                                               div
                                               h1 h2 h3 h4 h5
                                               nav
                                               main
                                               section
                                               article
                                               header
                                               select
                                               option
                                               p
                                               input
                                               img
                                               ul ol li
                                               label
                                               span
                                               table tbody tr th td]]))

(defsc Renglon [this {:keys [etiqueta valid? error-message] :as input-props}]
  {:use-hooks? true}
  (let [name (-> etiqueta (string/replace #"(?i)\W+" "_") string/lower-case)
        props (-> input-props (assoc :name etiqueta :id name) (dissoc :etiqueta))]
    (div
     (label {:htmlFor name} etiqueta)
     (input props)
     (div :.ui.error.message {:classes [(when valid? "hidden")]}
              error-message))))

(def ui-renglon (comp/factory Renglon))

(defsc Opcion [this {:keys [real-value show-value]}]
  {:ident (fn [_] [:component/id ::Opcion])
   :initial-state {:value ""}} 
  (option {:value real-value :key real-value} (string/trim show-value)))

(def ui-opcion (comp/factory Opcion))

(defsc RenglonSeleccion [this {:keys [etiqueta opciones llave-valor-real llave-valor-mostrado] :as input-props}]
  {:use-hooks? true}
  (let [name (-> etiqueta (string/replace #"(?i)\W+" "_") string/lower-case)
        props (-> input-props (assoc :name etiqueta :id name :key (random-uuid)) (dissoc :etiqueta :opciones))]
    (when (seq opciones)
      (div :.flex.flew-row.flex-wrap.gap-2.p-2.m-2
           (label :.font-bold {:htmlFor name} etiqueta)
           (select props
                   (map #(ui-opcion {:real-value (llave-valor-real %)
                                     :show-value (llave-valor-mostrado %)}) opciones))))))

(def ui-renglon-seleccion (comp/factory RenglonSeleccion))

(comment  

(seq [{} {} {}])
)