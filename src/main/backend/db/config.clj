(ns main.backend.db.config
  (:require [hugsql.core :as hugsql :refer [def-db-fns set-adapter!]]
            [hugsql.adapter.next-jdbc :as next-adapter]
            [next.jdbc.result-set :as rs]
            [clojure.java.io :as io]))

(set-adapter! (next-adapter/hugsql-adapter-next-jdbc {:builder-fn rs/as-lower-maps}))

(def-db-fns (io/resource "operaciones.sql"))

(comment 
  
  (hugsql/map-of-db-fns (io/resource "operaciones.sql"))

  (hugsql/def-sqlvec-fns (io/resource "operaciones.sql"))

  (print (insertar-cabecera-ficha-anestesica-sqlvec {:histcli 123
                                                     :fecha (clojure.instant/read-instant-timestamp "2025-01-09T16:00:25")
                                                     :edad 25
                                                     :piso 1
                                                     :pulso 152
                                                     :riesgo_op_grado 1
                                                     :posicion ""
                                                     :cirujano_legajo 11
                                                     :ayudante_legajo 12
                                                     :auxiliar_legajo 13
                                                     :urgencia 0
                                                     :complic_preoperatoria "A"
                                                     :premedicacion 12
                                                     :droga_dosis "12"
                                                     :analgesia "dadsd"
                                                     :zona_inyeccion ""
                                                     :agente_anestesico "sds"
                                                     :cant_inyectada_cc "120cc"
                                                     :anest_inhalatoria 0
                                                     :anest_endovenosa 1
                                                     :intubacion_traqueal 0
                                                     :tubo_nro 0
                                                     :mango 0
                                                     :respiracion_espontanea 1
                                                     :respiracion_asistida 0
                                                     :resp_controlada_manual 0
                                                     :resp_controlada_mecanica 0
                                                     :sistema_sin_reinhalacion 0
                                                     :sistema_con_rehin_parcial 0
                                                     :sistema_con_rehin_total 0
                                                     :habitacion 226
                                                     :resp_frec_x_min 180
                                                     :resp_tipo 1
                                                     :t_art_habitual_max 150
                                                     :t_art_habitual_min 120
                                                     :t_art_actual_max 180
                                                     :t_art_actual_min 120
                                                     :induccion "165"
                                                     :mantenimiento "sdsd"
                                                     :estado 1
                                                     :observaciones "dsadvcsfefqe"
                                                     :fecha_inicio "2025-01-09 16:00:25.000"
                                                     :fecha_final "2025-01-09 18:00:25.000"
                                                     :anest_gral 1
                                                     :anest_conductiva 0
                                                     :anest_local 0
                                                     :anest_nla 0
                                                     :diagnostico 134
                                                     :diagnostico_operatorio 671
                                                     :dextrosa "sds"
                                                     :fisiologica "deeea"
                                                     :sangre "sad"
                                                     :anestesiologo_legajo 166
                                                     :talla 1.89
                                                     :peso 52
                                                     :sexo 1
                                                     :hora_inicio_grilla 1623
                                                     :hora_inyectada 1400
                                                     :grilla_pasomin 5
                                                     :grilla_horas 2
                                                     :obra_social 1820
                                                     :histcli_unico 455
                                                     :oper_propuesta 456
                                                     :oper_realizada 111
                                                     :cama "D"
                                                     :anestesiologo_tipo 1
                                                     :cirprotocolo 1155
                                                     :cirujano_tipo 1
                                                     :ayudante_tipo 2
                                                     :auxiliar_tipo 2
                                                     :anes_numero 1212
                                                     :anestesiologo_lega 332
                                                     :tbc_anest_carga_fecha 20240220
                                                     :tbc_anest_carga_hora 1562
                                                     :modif_legajo ""
                                                     :modif_fechahora nil}))
  
  )