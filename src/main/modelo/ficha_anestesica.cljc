(ns main.modelo.ficha-anestesica
  (:require #?@(:clj [[main.backend.db.conexion :refer [obtener-conexion]]
                      [main.backend.db.config :as config]
                      [com.potetm.fusebox.timeout :as to]
                      [com.brunobonacci.mulog :as µ]]
                :cljs [[com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
                       [com.fulcrologic.fulcro.algorithms.form-state :as fs]
                       [com.fulcrologic.fulcro.components :as comp]
                       [com.fulcrologic.fulcro.algorithms.tempid :as tempid]])
            [com.wsscode.pathom3.connect.operation :as pco]
            [tick.core :as t]))

#?(:clj (import java.sql.SQLException))

;; El timeout debería ser 1seg, pero la tabla de fichaaneste_det es muy grande y demora en promedio 1.3seg. 
;; No hay forma obvia de optimizarla, ya que la consulta se hace ya por la llave foránea y no existe un campo plausible para indexar.
#?(:clj (def timeout (to/init {:com.potetm.fusebox.timeout/timeout-ms 1500})))

#?(:clj (pco/defresolver obtener-cabecera
          [{:keys [histcli histcli_unico cirprotocolo]}]
          {::pco/output [:cabecera]}
          {:cabecera (to/try-interruptible
                      (with-open [c (.getConnection (obtener-conexion :desal))]
                        (to/with-timeout timeout
                          (config/obtener-ficha-anestesica c {:histcli histcli
                                                              :histcli_unico histcli_unico
                                                              :cirprotocolo cirprotocolo})))
                      (catch SQLException e (do (µ/log ::excepcion-obtener-cabecera :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                                (throw (ex-info "Excepcion al obtener cabecera de ficha anestesica" {:excepcion e}))))
                      (catch Exception e (do (µ/log ::excepcion-obtener-cabecera :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                             (throw (ex-info "Excepcion al obtener cabecera de ficha anestesica" {:excepcion e})))))}))

#?(:clj (pco/defresolver obtener-cabecera-por-id
          [{:keys [ficha-anestesica-id]}]
          {::pco/output [:cabecera]}
          {:cabecera (to/try-interruptible
                      (with-open [c (.getConnection (obtener-conexion :desal))]
                        (to/with-timeout timeout
                          (config/obtener-ficha-anestesica-por-id c {:fichaaneste_cab_id ficha-anestesica-id})))
                      (catch SQLException e (do (µ/log ::excepcion-obtener-cabecera-por-id :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                                (throw (ex-info "Excepcion al obtener cabecera de ficha anestesica por id" {:excepcion e}))))
                      (catch Exception e (do (µ/log ::excepcion-obtener-cabecera-por-id :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                             (throw (ex-info "Excepcion al obtener cabecera de ficha anestesica por id" {:excepcion e})))))}))

#?(:clj (pco/defresolver obtener-detalle-por-id
          [{:keys [ficha-anestesica-id]}]
          {::pco/output [:detalle]}
          {:detalle (to/try-interruptible
                     (with-open [c (.getConnection (obtener-conexion :desal))]
                       (to/with-timeout timeout
                         (config/obtener-detalle-ficha-anestesica c {:fichaaneste_cab_id ficha-anestesica-id})))
                     (catch SQLException e (do (µ/log ::excepcion-obtener-detalle-por-id :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                               (throw (ex-info "Excepcion al obtener detalle de ficha anestesica por id" {:excepcion e}))))
                     (catch Exception e (do (µ/log ::excepcion-obtener-detalle-por-id :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                            (throw (ex-info "Excepcion al obtener detalle de ficha anestesica por id" {:excepcion e})))))}))

#?(:clj (pco/defresolver obtener-nomencladores
          [{:keys [ficha-anestesica-id]}]
          {::pco/output [:nomencladores]}
          {:nomencladores (to/try-interruptible
                           (to/with-timeout timeout
                             (with-open [c (.getConnection (obtener-conexion :desal))]
                               (config/obtener-nomencladores-ficha-anestesica c {:id_ficha_anestesica ficha-anestesica-id})))
                           (catch SQLException e (do (µ/log ::excepcion-obtener-nomencladores :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                                     (throw (ex-info "Excepcion al obtener nomencladores de ficha anestesica" {:excepcion e}))))
                           (catch Exception e (do (µ/log ::excepcion-obtener-nomencladores :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                                  (throw (ex-info "Excepcion al obtener nomencladores de ficha anestesica" {:excepcion e})))))}))

#?(:clj (pco/defresolver obtener-medicamentos
          [{:keys [ficha-anestesica-id]}]
          {::pco/output [:medicamentos]}
          {:medicamentos (to/try-interruptible
                          (with-open [c (.getConnection (obtener-conexion :desal))]
                            (to/with-timeout timeout
                              (config/obtener-medicamentos-ficha-anestesica c {:id_ficha_anestesica ficha-anestesica-id})))
                          (catch SQLException e (do (µ/log ::excepcion-obtener-medicamentos :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                                    (throw (ex-info "Excepcion al obtener medicamentos de la ficha anestesica" {:excepcion e}))))
                          (catch Exception e (do (µ/log ::excepcion-obtener-medicamentos :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                                 (throw (ex-info "Excepcion al obtener medicamentos de la ficha anestesica" {:excepcion e})))))}))

#?(:clj (pco/defresolver obtener-tipo-medicion
          [{:keys [fichaaneste-tipomedicion-id]}]
          {::pco/output [:tipomedicion]}
          {:tipomedicion (to/try-interruptible
                          (with-open [c (.getConnection (obtener-conexion :desal))]
                            (to/with-timeout timeout
                              (config/obtener-tipo-medicion-ficha-anestesica c {:fichaaneste_tipomedicion_id fichaaneste-tipomedicion-id})))
                          (catch SQLException e (do (µ/log ::excepcion-obtener-tipo-medicion :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                                    (throw (ex-info "Excepcion al obtener tipo medicion en la grilla de la ficha anestesica" {:excepcion e}))))
                          (catch Exception e (do (µ/log ::excepcion-obtener-tipo-medicion :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                                 (throw (ex-info "Excepcion al obtener tipo medicion en la grilla de la ficha anestesica" {:excepcion e})))))}))

#?(:clj (pco/defresolver obtener-ficha-anestesica-por-id
          [{:keys [ficha-anestesica-id fichaaneste-tipomedicion-id]}]
          {::pco/output [:ficha-anestesica]}
          {:ficha-anestesica [(obtener-cabecera-por-id {:ficha-anestesica-id ficha-anestesica-id})
                              (obtener-detalle-por-id {:ficha-anestesica-id ficha-anestesica-id})
                              (obtener-nomencladores {:ficha-anestesica-id ficha-anestesica-id})
                              (obtener-medicamentos {:ficha-anestesica-id ficha-anestesica-id})
                              (obtener-tipo-medicion {:fichaaneste-tipomedicion-id fichaaneste-tipomedicion-id})]}))

#?(:cljs
   (defn inicializar-registro-cabecera*
     [id hc hcu edad sexo obra_social]
     {:fichaaneste_cab/tempid id
      :fichaaneste_cab/histcli hc
      :fichaaneste_cab/fecha 0
      :fichaaneste_cab/edad edad
      :fichaaneste_cab/piso ""
      :fichaaneste_cab/pulso 0
      :fichaaneste_cab/riesgo_op_grado 0
      :fichaaneste_cab/posicion ""
      :fichaaneste_cab/cirujano_legajo 0
      :fichaaneste_cab/ayudante_legajo 0
      :fichaaneste_cab/auxiliar_legajo 0
      :fichaaneste_cab/urgencia 0
      :fichaaneste_cab/complic_preoperatoria ""
      :fichaaneste_cab/premedicacion 0
      :fichaaneste_cab/droga_dosis ""
      :fichaaneste_cab/analgesia ""
      :fichaaneste_cab/zona_inyeccion ""
      :fichaaneste_cab/agente_anestesico ""
      :fichaaneste_cab/cant_inyectada_cc ""
      :fichaaneste_cab/anest_inhalatoria 0
      :fichaaneste_cab/anest_endovenosa 0
      :fichaaneste_cab/intubacion_traqueal 0
      :fichaaneste_cab/tubo_nro 0
      :fichaaneste_cab/mango 0
      :fichaaneste_cab/respiracion_espontanea 0
      :fichaaneste_cab/respiracion_asistida 0
      :fichaaneste_cab/resp_controlada_manual 0
      :fichaaneste_cab/resp_controlada_mecanica 0
      :fichaaneste_cab/sistema_sin_reinhalacion 0
      :fichaaneste_cab/sistema_con_rehin_parcial 0
      :fichaaneste_cab/sistema_con_rehin_total 0
      :fichaaneste_cab/habitacion 0
      :fichaaneste_cab/resp_frec_x_min 0
      :fichaaneste_cab/resp_tipo 0
      :fichaaneste_cab/t_art_habitual_max 0
      :fichaaneste_cab/t_art_habitual_min 0
      :fichaaneste_cab/t_art_actual_max 0
      :fichaaneste_cab/t_art_actual_min 0
      :fichaaneste_cab/induccion ""
      :fichaaneste_cab/mantenimiento ""
      :fichaaneste_cab/estado 0
      :fichaaneste_cab/observaciones ""
      :fichaaneste_cab/fecha_inicio nil
      :fichaaneste_cab/fecha_final nil
      :fichaaneste_cab/anest_gral 0
      :fichaaneste_cab/anest_conductiva 0
      :fichaaneste_cab/anest_local 0
      :fichaaneste_cab/anest_nla 0
      :fichaaneste_cab/diagnostico 0
      :fichaaneste_cab/diagnostico_operatorio 0
      :fichaaneste_cab/dextrosa ""
      :fichaaneste_cab/fisiologica ""
      :fichaaneste_cab/sangre ""
      :fichaaneste_cab/anestesiologo_legajo 0
      :fichaaneste_cab/talla 0.0
      :fichaaneste_cab/peso 0.0
      :fichaaneste_cab/sexo sexo
      :fichaaneste_cab/hora_inicio_grilla 0
      :fichaaneste_cab/hora_inyectada 0
      :fichaaneste_cab/grilla_pasomin 0
      :fichaaneste_cab/grilla_horas 0
      :fichaaneste_cab/obra_social obra_social
      :fichaaneste_cab/histcli_unico hcu
      :fichaaneste_cab/oper_propuesta 0
      :fichaaneste_cab/oper_realizada 0
      :fichaaneste_cab/cama ""
      :fichaaneste_cab/anestesiologo_tipo 0
      :fichaaneste_cab/cirprotocolo 0
      :fichaaneste_cab/cirujano_tipo 0
      :fichaaneste_cab/ayudante_tipo 0
      :fichaaneste_cab/auxiliar_tipo 0
      :fichaaneste_cab/anes_numero 0
      :fichaaneste_cab/anestesiologo_lega 0
      :fichaaneste_cab/tbc_anest_carga_fecha 0
      :fichaaneste_cab/tbc_anest_carga_hora 0
      :fichaaneste_cab/modif_legajo 0
      :fichaaneste_cab/modif_fechahora nil}))

#?(:cljs
   (defn inicializar-registro-detalle*
     [temp-id ficha-id]
     {:tempid temp-id
      :min_col 0
      :valor 0
      :fichaaneste_cab_id ficha-id
      :tipo_medicion_cod 0}))

#?(:cljs
   (defn inicializar-registro-medicion*
     [medicion-id]
     {:tempid medicion-id
      :codigo 0
      :descripcion ""
      :minimo 0
      :maximo 0
      :color ""
      :simbolo ""
      :grilla_nro 0}))

#?(:cljs
   (defn inicializar-registro-medicamento*
     [medicamento-id ficha-id]
     {:tempid medicamento-id
      :id_ficha_anestesica ficha-id
      :codigo_medicamento 0
      :dosis 0
      :imed 0
      :unidad_de_medida ""}))

#?(:cljs
   (defn inicializar-registro-nomenclador*
     [nomenclador-id ficha-id hc hcu]
     {:tempid nomenclador-id
      :protocolo 0
      :historia_clinica hc
      :historia_clinica_unica hcu
      :legajo_anestesista 0
      :tipo_legajo 0
      :tipo_nomenclador 0
      :codigo_nomenclador 0
      :grupo_nomenclador 0
      :porcentaje 0
      :id_ficha_anestesica ficha-id}))

#?(:cljs
   (defn agregar-cabecera*
     [{:keys [id obra_social hc hcu sexo edad]}]
     (inicializar-registro-cabecera* id hc hcu edad sexo obra_social)))
 
#?(:cljs
   (defmutation inicializar-ficha-anestesica
     "Paciente es un mapa con las llaves: id obra_social hc hcu sexo edad"
     [{:keys [id] :as paciente}]
     (action [{:keys [state]}]
             (swap! state (fn [estado] (-> estado 
                                           (assoc-in [:component/id :main.frontend.formulariocarga/FormularioCarga :paciente-seleccionado] paciente)
                                           #_(assoc-in [:component/id :main.frontend.formulariocarga/FormularioCarga :fichaaneste_cab/fichaaneste_cab_id] id)
                                           (assoc-in [:fichaaneste_cab/fichaaneste_cab_id id] (agregar-cabecera* paciente))
                                           (fs/add-form-config* (comp/registry-key->class :main.frontend.formulariocarga/DatosPaciente) [:fichaaneste_cab/fichaaneste_cab_id id])))))))
#?(:cljs
   (defmutation agregar-detalle-ficha-anestesica
     [{:keys [fichaaneste_cab_id]}]
     (action [{:keys [state]}]
             (let [tempid (tempid/tempid)
                   detalle (inicializar-registro-detalle* tempid fichaaneste_cab_id)]
               (swap! state update-in [:fichaaneste_cab/ficha_anestecab_id fichaaneste_cab_id :detalles] conj detalle)))))

#?(:cljs
   (defmutation agregar-medicion-ficha-anestesica
     [{:keys [fichaaneste_cab_id]}]
     (action [{:keys [state]}]
             (let [id (tempid/tempid)
                   medicion (inicializar-registro-medicion* id)]
               (swap! state update-in [:fichaaneste_cab/ficha_anestecab_id fichaaneste_cab_id :mediciones] conj medicion)))))

#?(:cljs
   (defmutation agregar-medicamento-ficha-anestesica
     [{:keys [fichaaneste_cab_id]}]
     (action [{:keys [state]}]
             (let [tempid (tempid/tempid)
                   medicamento (inicializar-registro-medicamento* tempid fichaaneste_cab_id)]
               (swap! state update-in [:fichaaneste_cab/ficha_anestecab_id fichaaneste_cab_id :medicamentos] conj medicamento)))))

#?(:cljs
   (defmutation agregar-nomenclador-ficha-anestesica
     [{:keys [fichaaneste_cab_id hc hcu]}]
     (action [{:keys [state]}]
             (let [tempid (tempid/tempid)
                   nomenclador (inicializar-registro-nomenclador* tempid fichaaneste_cab_id hc hcu)]
               (swap! state update-in [:fichaaneste_cab/ficha_anestecab_id fichaaneste_cab_id :nomencladores] conj nomenclador)))))

#?(:clj
   (pco/defmutation guardar-ficha-anestesica-cabecera
     [{:fichaaneste_cab/keys [tempid histcli fecha edad piso pulso riesgo_op_grado posicion cirujano_legajo ayudante_legajo auxiliar_legajo urgencia
              complic_preoperatoria premedicacion droga_dosis analgesia zona_inyeccion agente_anestesico cant_inyectada_cc anest_inhalatoria anest_endovenosa
              intubacion_traqueal tubo_nro mango respiracion_espontanea respiracion_asistida resp_controlada_manual resp_controlada_mecanica sistema_sin_reinhalacion
              sistema_con_rehin_parcial sistema_con_rehin_total habitacion resp_frec_x_min resp_tipo t_art_habitual_max t_art_habitual_min t_art_actual_max
              t_art_actual_min induccion mantenimiento estado observaciones fecha_inicio fecha_final anest_gral anest_conductiva anest_local anest_nla
              diagnostico diagnostico_operatorio dextrosa fisiologica sangre anestesiologo_legajo talla peso sexo hora_inicio_grilla hora_inyectada
              grilla_pasomin grilla_horas obra_social histcli_unico oper_propuesta oper_realizada cama anestesiologo_tipo cirprotocolo cirujano_tipo
              ayudante_tipo auxiliar_tipo anes_numero anestesiologo_lega tbc_anest_carga_fecha tbc_anest_carga_hora modif_legajo modif_fechahora]}]
     (to/try-interruptible
      (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                 (to/with-timeout timeout
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
                       :fichaaneste_cab_id)))]
        {:ficha-anestesica-id id
         :tempids {tempid id}})
      (catch SQLException e (do (µ/log ::excepcion-crear-ficha-anestesica-cabecera :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                (throw (ex-info "Excepcion al insertar cabecera de ficha anestesica" {:excepcion e}))))
      (catch Exception e (do (µ/log ::excepcion-crear-ficha-anestesica-cabecera :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                             (throw (ex-info "Excepcion al insertar cabecera de ficha anestesica" {:excepcion e}))))))
   :cljs
   (defmutation guardar-ficha-anestesica-cabecera
     [{:keys [id histcli fecha edad piso pulso riesgo_op_grado posicion cirujano_legajo ayudante_legajo auxiliar_legajo urgencia
              complic_preoperatoria premedicacion droga_dosis analgesia zona_inyeccion agente_anestesico cant_inyectada_cc anest_inhalatoria anest_endovenosa
              intubacion_traqueal tubo_nro mango respiracion_espontanea respiracion_asistida resp_controlada_manual resp_controlada_mecanica sistema_sin_reinhalacion
              sistema_con_rehin_parcial sistema_con_rehin_total habitacion resp_frec_x_min resp_tipo t_art_habitual_max t_art_habitual_min t_art_actual_max
              t_art_actual_min induccion mantenimiento estado observaciones fecha_inicio fecha_final anest_gral anest_conductiva anest_local anest_nla
              diagnostico diagnostico_operatorio dextrosa fisiologica sangre anestesiologo_legajo talla peso sexo hora_inicio_grilla hora_inyectada
              grilla_pasomin grilla_horas obra_social histcli_unico oper_propuesta oper_realizada cama anestesiologo_tipo cirprotocolo cirujano_tipo
              ayudante_tipo auxiliar_tipo anes_numero anestesiologo_lega tbc_anest_carga_fecha tbc_anest_carga_hora modif_legajo modif_fechahora] :as cabecera}]
     (action [{:keys [state]}]
             (swap! state fs/entity->pristine* [:fichaaneste_cab/ficha_anestecab_id id]))
     (remote [_] true)))

#?(:clj (pco/defmutation guardar-ficha-anestesica-detalle
          [{:keys [tempid min_col valor fichaaneste_cab_id tipo_medicion_cod]}]
          (to/try-interruptible
           (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                      (to/with-timeout timeout
                        (-> (config/insertar-detalle-ficha-anestesica c {:min_col min_col
                                                                         :valor valor
                                                                         :fichaaneste_cab_id fichaaneste_cab_id
                                                                         :tipo_medicion_cod tipo_medicion_cod})
                            first
                            :fichaaneste_det_id)))]
             {:fichaaneste_det_id id
              :tempids {tempid id}})
           (catch SQLException e (do (µ/log ::excepcion-crear-ficha-anestesica-detalle :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                     (throw (ex-info "Excepcion al insertar detalle de ficha anestesica" {:excepcion e}))))
           (catch Exception e (do (µ/log ::excepcion-crear-ficha-anestesica-detalle :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                  (throw (ex-info "Excepcion al insertar detalle de ficha anestesica" {:excepcion e})))))))

#?(:clj (pco/defmutation guardar-ficha-anestesica-medicamentos
          [{:keys [tempid id_ficha_anestesica codigo_medicamento dosis imed unidad_de_medida]}]
          (to/try-interruptible
           (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                      (to/with-timeout timeout
                        (-> (config/insertar-medicamentos-ficha-anestesica c {:id_ficha_anestesica id_ficha_anestesica
                                                                              :codigo_medicamento codigo_medicamento
                                                                              :dosis dosis
                                                                              :imed imed
                                                                              :unidad_de_medida unidad_de_medida})
                            first
                            :id)))]
             {:id id
              :tempids {tempid id}})
           (catch SQLException e (do (µ/log ::excepcion-crear-ficha-anestesica-medicamentos :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                     (throw (ex-info "Excepcion al insertar medicamentos de la ficha anestesica" {:excepcion e}))))
           (catch Exception e (do (µ/log ::excepcion-crear-ficha-anestesica-medicamentos :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                  (throw (ex-info "Excepcion al insertar medicamentos de la ficha anestesica" {:excepcion e})))))))

#?(:clj (pco/defmutation guardar-ficha-anestesica-nomencladores
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
          (to/try-interruptible
           (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                      (to/with-timeout timeout
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
                            :id)))]
             {:id id
              :tempids {tempid id}})
           (catch SQLException e (do (µ/log ::excepcion-crear-ficha-anestesica-nomencladores :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                     (throw (ex-info "Excepcion al insertar nomencladores de ficha anestesica" {:excepcion e}))))
           (catch Exception e (do (µ/log ::excepcion-crear-ficha-anestesica-nomencladores :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                  (throw (ex-info "Excepcion al insertar nomencladores de ficha anestesica" {:excepcion e})))))))

#?(:clj (pco/defmutation guardar-ficha-anestesica-mediciones
          [{:keys [tempid codigo descripcion minimo maximo color simbolo grilla_nro]}]
          (to/try-interruptible
           (let [id (with-open [c (.getConnection (obtener-conexion :desal))]
                      (to/with-timeout timeout
                        (-> (config/insertar-tipomedicion-ficha-anestesica c {:codigo codigo
                                                                              :descripcion descripcion
                                                                              :minimo minimo
                                                                              :maximo maximo
                                                                              :color color
                                                                              :simbolo simbolo
                                                                              :grilla_nro grilla_nro})
                            first
                            :fichaaneste_tipomedicion_id)))]
             {:fichaaneste_tipomedicion_id id
              :tempids {tempid id}})
           (catch SQLException e (do (µ/log ::excepcion-crear-ficha-anestesica-mediciones :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                     (throw (ex-info "Excepcion al insertar mediciones en la grilla de la ficha anestesica" {:excepcion e}))))
           (catch Exception e (do (µ/log ::excepcion-crear-ficha-anestesica-mediciones :fecha (t/date-time) :mensaje (ex-message e) :excepcion e)
                                  (throw (ex-info "Excepcion al insertar mediciones en la grilla de la ficha anestesica" {:excepcion e})))))))

#?(:clj (def resolvers [obtener-cabecera
                        obtener-cabecera-por-id
                        obtener-detalle-por-id
                        obtener-nomencladores
                        obtener-medicamentos
                        obtener-tipo-medicion
                        obtener-ficha-anestesica-por-id
                        guardar-ficha-anestesica-cabecera
                        guardar-ficha-anestesica-detalle
                        guardar-ficha-anestesica-medicamentos
                        guardar-ficha-anestesica-nomencladores
                        guardar-ficha-anestesica-mediciones]))

#?(:clj
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
         (config/obtener-nomencladores-ficha-anestesica c {:id_ficha_anestesica #_73779 73161}))
       (catch SQLException e (print (ex-cause e))))

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


     (obtener-ficha-anestesica-por-id {:ficha-anestesica-id 73161})

     (obtener-nomencladores {:ficha-anestesica-id 73161})

     timeout

     :rcf)) 