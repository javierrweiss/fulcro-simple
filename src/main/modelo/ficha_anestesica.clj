(ns main.modelo.ficha-anestesica
   (:require [com.wsscode.pathom3.connect.operation :as pco] 
            [main.backend.db.conexion :refer [obtener-conexion]]
            [main.backend.db.config :as c]))

(comment
  ;; Si ejecutas, te cierra el pool
  (with-open [c (obtener-conexion :desal)]
    (c/buscar-ficha-anestesica c {:histcli 3267330
                                  :histcli_unico 0
                                  :cirprotocolo 123654}))
;; Hay que estar atento y tomar una conexión del pool y cerrar la conexión, no el pool.  
  (with-open [c (.getConnection (obtener-conexion :desal))]
    (print (.isClosed c)) 
    (c/buscar-ficha-anestesica c {:histcli 3267330
                                  :histcli_unico 0
                                  :cirprotocolo 123654}))
  :rcf)