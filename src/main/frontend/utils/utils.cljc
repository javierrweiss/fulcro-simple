(ns main.frontend.utils.utils
  (:require [tick.core :as t]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]))

(defn field-attrs
  "A helper function for getting aspects of a particular field."
  [component field]
  (let [form         (comp/props component)
        entity-ident (comp/get-ident component form)
        id           (str (first entity-ident) "-" (second entity-ident))
        is-dirty?    (fs/dirty? form field)
        clean?       (not is-dirty?)
        validity     (fs/get-spec-validity form field)
        is-invalid?  (= :invalid validity)
        value        (get form field "")]
    {:dirty?   is-dirty?
     :ident    entity-ident
     :id       id
     :clean?   clean?
     :validity validity
     :invalid? is-invalid?
     :value    value}))

(defn obtener-edad
  [fnacimiento]
  (let [fec (str fnacimiento)]
    (if (re-seq #"\d{8}" fec) 
        (let [[an1 an2 mes dia] (->> fec
                                     (partition 2)
                                     (map #(apply str %)))
              ano (apply str an1 an2)
              birth (t/date (apply str (interpose "-" [ano mes dia])))]
          (t/between birth (t/date) :years))
        "Desconocida")))

(defn obtener-sexo
  [sexo]
  (let [s (str sexo)]
    (if (= s "1")
      "F"
      "M")))

(comment
  #?(:clj
     ...
     :cljs
     ...)

  (let [fec 19720827
        [an1 an2 mes dia] (->> fec
                               str
                               (partition 2)
                               (map #(apply str %)))
        ano (apply str an1 an2)]
    (t/date (apply str (interpose "-" [ano mes dia]))))

  (t/between (t/date "2020-01-01") (t/date) :years)

  (t/between (t/date) (t/date "2020-01-01"))

  (obtener-edad "19720827")
  (obtener-edad  19720827)
  (obtener-edad "0")
  (obtener-edad "cunaguar")
  (re-seq #"\d{8}" "197208271" #_"cunaguar"))