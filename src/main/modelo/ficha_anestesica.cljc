(ns main.modelo.ficha-anestesica
  (:require #?@(:clj [[main.backend.db.conexion :refer [obtener-conexion]]
                       [main.backend.db.config :as config]]
                 :cljs [])
            [com.wsscode.pathom3.connect.operation :as pco]))

#?(:clj (import java.sql.SQLException))

#?(:clj (pco/defresolver obtener-cabecera
          [{:keys [histcli histcli_unico cirprotocolo]}]
          {::pco/output [:cabecera]}
          {:cabecera (try
                       (with-open [c (.getConnection (obtener-conexion :desal))]
                         (config/obtener-ficha-anestesica c {:histcli histcli
                                                             :histcli_unico histcli_unico
                                                             :cirprotocolo cirprotocolo}))
                       (catch SQLException e (throw (ex-info "Excepcion al obtener cabecera de ficha anestesica" {:excepcion e})))
                       (catch Exception e (throw (ex-info "Excepcion al obtener cabecera de ficha anestesica" {:excepcion e}))))}))

#?(:clj (pco/defresolver obtener-cabecera-por-id
          [{:keys [ficha-anestesica-id]}]
          {::pco/output [:cabecera]}
          {:cabecera (try
                       (with-open [c (.getConnection (obtener-conexion :desal))]
                         (config/obtener-ficha-anestesica-por-id c {:fichaaneste_cab_id ficha-anestesica-id}))
                       (catch SQLException e (throw (ex-info "Excepcion al obtener cabecera de ficha anestesica por id" {:excepcion e})))
                       (catch Exception e (throw (ex-info "Excepcion al obtener cabecera de ficha anestesica por id" {:excepcion e}))))}))

#?(:clj (pco/defresolver obtener-detalle-por-id
          [{:keys [ficha-anestesica-id]}]
          {::pco/output [:detalle]}
          {:detalle (try
                      (with-open [c (.getConnection (obtener-conexion :desal))]
                        (config/obtener-detalle-ficha-anestesica c {:fichaaneste_cab_id ficha-anestesica-id}))
                      (catch SQLException e (throw (ex-info "Excepcion al obtener detalle de ficha anestesica por id" {:excepcion e})))
                      (catch Exception e (throw (ex-info "Excepcion al obtener detalle de ficha anestesica por id" {:excepcion e}))))}))

#?(:clj (pco/defresolver obtener-nomencladores
          [{:keys [ficha-anestesica-id]}]
          {::pco/output [:nomencladores]}
          {:nomencladores (try
                            (with-open [c (.getConnection (obtener-conexion :desal))]
                              (config/obtener-nomencladores-ficha-anestesica c {:fichaaneste_cab_id ficha-anestesica-id}))
                            (catch SQLException e (throw (ex-info "Excepcion al obtener nomencladores de ficha anestesica" {:excepcion e})))
                            (catch Exception e (throw (ex-info "Excepcion al obtener nomencladores de ficha anestesica" {:excepcion e}))))}))

#?(:clj (pco/defresolver obtener-medicamentos
          [{:keys [ficha-anestesica-id]}]
          {::pco/output [:medicamentos]}
          {:medicamentos (try
                           (with-open [c (.getConnection (obtener-conexion :desal))]
                             (config/obtener-medicamentos-ficha-anestesica c {:fichaaneste_cab_id ficha-anestesica-id}))
                           (catch SQLException e (throw (ex-info "Excepcion al obtener medicamentos de la ficha anestesica" {:excepcion e})))
                           (catch Exception e (throw (ex-info "Excepcion al obtener medicamentos de la ficha anestesica" {:excepcion e}))))}))

#?(:clj (pco/defresolver obtener-tipo-medicion
          [{:keys [fichaaneste-tipomedicion-id]}]
          {::pco/output [:tipomedicion]}
          {:tipomedicion (try
                           (with-open [c (.getConnection (obtener-conexion :desal))]
                             (config/obtener-tipo-medicion-ficha-anestesica c {:fichaaneste_tipomedicion_id fichaaneste-tipomedicion-id}))
                           (catch SQLException e (throw (ex-info "Excepcion al obtener tipo medicion en la grilla de la ficha anestesica" {:excepcion e})))
                           (catch Exception e (throw (ex-info "Excepcion al obtener tipo medicion en la grilla de la ficha anestesica" {:excepcion e}))))}))

#?(:clj (pco/defresolver obtener-ficha-anestesica-por-id
          [{:keys [ficha-anestesica-id fichaaneste-tipomedicion-id]}]
          {::pco/output [:ficha-anestesica]}
          {:ficha-anestesica [(obtener-cabecera-por-id {:ficha-anestesica-id ficha-anestesica-id})
                              (obtener-detalle-por-id {:ficha-anestesica-id ficha-anestesica-id})
                              (obtener-nomencladores {:ficha-anestesica-id ficha-anestesica-id})
                              (obtener-medicamentos {:ficha-anestesica-id ficha-anestesica-id})
                              (obtener-tipo-medicion {:fichaaneste-tipomedicion-id fichaaneste-tipomedicion-id})]}))

#?(:clj (pco/defmutation crear-ficha-anestesica-cabecera
          [{:keys [tempid histcli fecha edad piso pulso riesgo_op_grado posicion cirujano_legajo ayudante_legajo auxiliar_legajo urgencia
                   complic_preoperatoria premedicacion droga_dosis analgesia zona_inyeccion agente_anestesico cant_inyectada_cc anest_inhalatoria anest_endovenosa
                   intubacion_traqueal tubo_nro mango respiracion_espontanea respiracion_asistida resp_controlada_manual resp_controlada_mecanica sistema_sin_reinhalacion
                   sistema_con_rehin_parcial sistema_con_rehin_total habitacion resp_frec_x_min resp_tipo t_art_habitual_max t_art_habitual_min t_art_actual_max
                   t_art_actual_min induccion mantenimiento estado observaciones fecha_inicio fecha_final anest_gral anest_conductiva anest_local anest_nla
                   diagnostico diagnostico_operatorio dextrosa fisiologica sangre anestesiologo_legajo talla peso sexo hora_inicio_grilla hora_inyectada
                   grilla_pasomin grilla_horas obra_social histcli_unico oper_propuesta oper_realizada cama anestesiologo_tipo cirprotocolo cirujano_tipo
                   ayudante_tipo auxiliar_tipo anes_numero anestesiologo_lega tbc_anest_carga_fecha tbc_anest_carga_hora modif_legajo modif_fechahora]}]
          (try
            (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                       (-> (config/insertar-cabecera-ficha-anestesica c {:histcli histcli
                                                                         :fecha fecha
                                                                         :edad edad
                                                                         :piso piso
                                                                         :pulso pulso
                                                                         :riesgo_op_grado riesgo_op_grado
                                                                         :posicion posicion
                                                                         :cirujano_legajo cirujano_legajo
                                                                         :ayudante_legajo ayudante_legajo
                                                                         :auxiliar_legajo auxiliar_legajo
                                                                         :urgencia urgencia
                                                                         :complic_preoperatoria complic_preoperatoria
                                                                         :premedicacion premedicacion
                                                                         :droga_dosis droga_dosis
                                                                         :analgesia analgesia
                                                                         :zona_inyeccion zona_inyeccion
                                                                         :agente_anestesico agente_anestesico
                                                                         :cant_inyectada_cc cant_inyectada_cc
                                                                         :anest_inhalatoria anest_inhalatoria
                                                                         :anest_endovenosa anest_endovenosa
                                                                         :intubacion_traqueal intubacion_traqueal
                                                                         :tubo_nro tubo_nro
                                                                         :mango mango
                                                                         :respiracion_espontanea respiracion_espontanea
                                                                         :respiracion_asistida respiracion_asistida
                                                                         :resp_controlada_manual resp_controlada_manual
                                                                         :resp_controlada_mecanica resp_controlada_mecanica
                                                                         :sistema_sin_reinhalacion sistema_sin_reinhalacion
                                                                         :sistema_con_rehin_parcial sistema_con_rehin_parcial
                                                                         :sistema_con_rehin_total sistema_con_rehin_total
                                                                         :habitacion habitacion
                                                                         :resp_frec_x_min resp_frec_x_min
                                                                         :resp_tipo resp_tipo
                                                                         :t_art_habitual_max t_art_habitual_max
                                                                         :t_art_habitual_min t_art_habitual_min
                                                                         :t_art_actual_max t_art_actual_max
                                                                         :t_art_actual_min t_art_actual_min
                                                                         :induccion induccion
                                                                         :mantenimiento mantenimiento
                                                                         :estado estado
                                                                         :observaciones observaciones
                                                                         :fecha_inicio fecha_inicio
                                                                         :fecha_final fecha_final
                                                                         :anest_gral anest_gral
                                                                         :anest_conductiva anest_conductiva
                                                                         :anest_local anest_local
                                                                         :anest_nla anest_nla
                                                                         :diagnostico diagnostico
                                                                         :diagnostico_operatorio diagnostico_operatorio
                                                                         :dextrosa dextrosa
                                                                         :fisiologica fisiologica
                                                                         :sangre sangre
                                                                         :anestesiologo_legajo anestesiologo_legajo
                                                                         :talla talla
                                                                         :peso peso
                                                                         :sexo sexo
                                                                         :hora_inicio_grilla hora_inicio_grilla
                                                                         :hora_inyectada hora_inyectada
                                                                         :grilla_pasomin grilla_pasomin
                                                                         :grilla_horas grilla_horas
                                                                         :obra_social obra_social
                                                                         :histcli_unico histcli_unico
                                                                         :oper_propuesta oper_propuesta
                                                                         :oper_realizada oper_realizada
                                                                         :cama cama
                                                                         :anestesiologo_tipo anestesiologo_tipo
                                                                         :cirprotocolo cirprotocolo
                                                                         :cirujano_tipo cirujano_tipo
                                                                         :ayudante_tipo ayudante_tipo
                                                                         :auxiliar_tipo auxiliar_tipo
                                                                         :anes_numero anes_numero
                                                                         :anestesiologo_lega anestesiologo_lega
                                                                         :tbc_anest_carga_fecha tbc_anest_carga_fecha
                                                                         :tbc_anest_carga_hora tbc_anest_carga_hora
                                                                         :modif_legajo modif_legajo
                                                                         :modif_fechahora modif_fechahora})
                           first
                           :fichaaneste_cab_id))]
              {:ficha-anestesica-id id
               :tempids {tempid id}})
            (catch SQLException e (throw (ex-info "Excepcion al insertar cabecera de ficha anestesica" {:excepcion e})))
            (catch Exception e (throw (ex-info "Excepcion al insertar cabecera de ficha anestesica" {:excepcion e}))))))

#?(:clj (pco/defmutation crear-ficha-anestesica-detalle
          [{:keys [tempid min_col valor fichaaneste_cab_id tipo_medicion_cod]}]
          (try
            (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                       (-> (config/insertar-detalle-ficha-anestesica c {:min_col min_col
                                                                        :valor valor
                                                                        :fichaaneste_cab_id fichaaneste_cab_id
                                                                        :tipo_medicion_cod tipo_medicion_cod})
                           first
                           :fichaaneste_det_id))]
              {:fichaaneste_det_id id
               :tempids {tempid id}})
            (catch SQLException e (throw (ex-info "Excepcion al insertar detalle de ficha anestesica" {:excepcion e})))
            (catch Exception e (throw (ex-info "Excepcion al insertar detalle de ficha anestesica" {:excepcion e}))))))

#?(:clj (pco/defmutation crear-ficha-anestesica-medicamentos
          [{:keys [tempid id_ficha_anestesica codigo_medicamento dosis imed unidad_de_medida]}]
          (try
            (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                       (-> (config/insertar-medicamentos-ficha-anestesica c {:id_ficha_anestesica id_ficha_anestesica
                                                                             :codigo_medicamento codigo_medicamento
                                                                             :dosis dosis
                                                                             :imed imed
                                                                             :unidad_de_medida unidad_de_medida})
                           first
                           :id))]
              {:id id
               :tempids {tempid id}})
            (catch SQLException e (throw (ex-info "Excepcion al insertar medicamentos de la ficha anestesica" {:excepcion e})))
            (catch Exception e (throw (ex-info "Excepcion al insertar medicamentos de la ficha anestesica" {:excepcion e}))))))

#?(:clj (pco/defmutation crear-ficha-anestesica-nomencladores
          [{:keys [tempid
                   protocolo
                   historia_clinica
                   historia_clinica_unica
                   legajo_anestesista
                   tipo_legajo
                   tipo_nomenclador
                   codigo_nomenclador
                   grupo_nomenclador
                   porcentaje
                   id_ficha_anestesica]}]
          (try
            (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                       (-> (config/insertar-nomencladores-ficha-anestesica c {:protocolo protocolo
                                                                              :historia_clinica historia_clinica
                                                                              :historia_clinica_unica historia_clinica_unica
                                                                              :legajo_anestesista legajo_anestesista
                                                                              :tipo_legajo tipo_legajo
                                                                              :tipo_nomenclador tipo_nomenclador
                                                                              :codigo_nomenclador codigo_nomenclador
                                                                              :grupo_nomenclador grupo_nomenclador
                                                                              :porcentaje porcentaje
                                                                              :id_ficha_anestesica id_ficha_anestesica})
                           first
                           :id))]
              {:id id
               :tempids {tempid id}})
            (catch SQLException e (throw (ex-info "Excepcion al insertar nomencladores de ficha anestesica" {:excepcion e})))
            (catch Exception e (throw (ex-info "Excepcion al insertar nomencladores de ficha anestesica" {:excepcion e}))))))

#?(:clj (pco/defmutation crear-ficha-anestesica-mediciones
          [{:keys [tempid codigo descripcion minimo maximo color simbolo grilla_nro]}]
          (try
            (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                       (-> (config/insertar-tipomedicion-ficha-anestesica c {:codigo codigo
                                                                             :descripcion descripcion
                                                                             :minimo minimo
                                                                             :maximo maximo
                                                                             :color color
                                                                             :simbolo simbolo
                                                                             :grilla_nro grilla_nro})
                           first
                           :fichaaneste_tipomedicion_id))]
              {:fichaaneste_tipomedicion_id id
               :tempids {tempid id}})
            (catch SQLException e (throw (ex-info "Excepcion al insertar mediciones en la grilla de la ficha anestesica" {:excepcion e})))
            (catch Exception e (throw (ex-info "Excepcion al insertar mediciones en la grilla de la ficha anestesica" {:excepcion e}))))))

#?(:clj (def resolvers [obtener-cabecera
                        obtener-cabecera-por-id
                        obtener-detalle-por-id
                        obtener-nomencladores
                        obtener-medicamentos
                        obtener-tipo-medicion
                        obtener-ficha-anestesica-por-id
                        crear-ficha-anestesica-cabecera
                        crear-ficha-anestesica-detalle
                        crear-ficha-anestesica-medicamentos
                        crear-ficha-anestesica-nomencladores
                        crear-ficha-anestesica-mediciones]))

(comment
  ;; Si ejecutas, te cierra el pool
  (with-open [c (obtener-conexion :desal)]
    (config/obtener-ficha-anestesica c {:histcli 3267330
                                        :histcli_unico 0
                                        :cirprotocolo 123654}))
;; Hay que estar atento y tomar una conexión del pool y cerrar la conexión, no el pool.  
  (with-open [c (.getConnection (obtener-conexion :desal))]
    #_(print (.isClosed c))
    (config/obtener-ficha-anestesica c {:histcli 0
                                        :histcli_unico 303630
                                        :cirprotocolo 500575}))

  (with-open [c (.getConnection (obtener-conexion :desal))]
    (config/obtener-ficha-anestesica c {:histcli 3263980
                                        :histcli_unico 0
                                        :cirprotocolo 500575}))

  (with-open [c (.getConnection (obtener-conexion :desal))]
    (config/obtener-ficha-anestesica-abierta c {:histcli 3263980
                                                :histcli_unico 0
                                                :cirprotocolo 500575}))

  (with-open [c (.getConnection (obtener-conexion :desal))]
    (config/obtener-ficha-anestesica-abierta c {:histcli 324378
                                                :histcli_unico 0
                                                :cirprotocolo 116495}))

  (with-open [c (.getConnection (obtener-conexion :desal))]
    (config/obtener-fichas-anestesicas-abiertas c))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/obtener-ficha-anestesica-por-id c {:fichaaneste_cab_id 73161}))
    (catch SQLException e (throw (ex-message e))))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/obtener-detalle-ficha-anestesica c {:fichaaneste_cab_id 73779}))
    (catch SQLException e (throw (ex-message e))))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/obtener-medicamentos-ficha-anestesica c {:id_ficha_anestesica 73779}))
    (catch SQLException e (throw (ex-message e))))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/obtener-nomencladores-ficha-anestesica c {:id_ficha_anestesica 73779}))
    (catch SQLException e (throw (ex-message e))))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/obtener-tipo-medicion-ficha-anestesica c {:fichaaneste_tipomedicion_id 9}))
    (catch SQLException e (throw (ex-message e))))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/insertar-cabecera-ficha-anestesica c {:histcli 123
                                                    :fecha  (clojure.instant/read-instant-timestamp "2025-01-09T16:00:25")
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
                                                    :fecha_inicio  (clojure.instant/read-instant-timestamp "2025-01-09T16:00:25")
                                                    :fecha_final  (clojure.instant/read-instant-timestamp "2025-01-09T18:00:25")
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
                                                    :modif_legajo 0
                                                    :modif_fechahora nil}))
    (catch SQLException e (print (ex-message e))))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/insertar-detalle-ficha-anestesica c {:min_col 1
                                                   :valor 2
                                                   :fichaaneste_cab_id 7722
                                                   :tipo_medicion_cod 23}))
    (catch SQLException e (throw (ex-message e))))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/insertar-medicamentos-ficha-anestesica c {:id_ficha_anestesica 2332
                                                        :codigo_medicamento 2323
                                                        :dosis 3
                                                        :imed 2
                                                        :unidad_de_medida "gr"}))
    (catch SQLException e (throw (ex-message e))))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/insertar-nomencladores-ficha-anestesica c {:protocolo 1666
                                                         :historia_clinica 3356
                                                         :historia_clinica_unica 10
                                                         :legajo_anestesista 148
                                                         :tipo_legajo 1
                                                         :tipo_nomenclador 1
                                                         :codigo_nomenclador 11010
                                                         :grupo_nomenclador 1
                                                         :porcentaje 100
                                                         :id_ficha_anestesica 33}))
    (catch SQLException e (throw (ex-message e))))

  (try
    (with-open [c (.getConnection (obtener-conexion :desal))]
      (config/insertar-tipomedicion-ficha-anestesica c {:codigo 3264930
                                                        :descripcion "dsadsaaa" 
                                                        :minimo 32 
                                                        :maximo 32 
                                                        :color "ds" 
                                                        :simbolo "dsda" 
                                                        :grilla_nro 2}))
    (catch SQLException e (print (ex-message e))))


  (obtener-ficha-anestesica-por-id {:ficha-anestesica-id 67429})

  :rcf)