
-- :name carga-internados-por-nombre :? :*
SELECT adm_histclin, adm_apelnom, adm_habita, adm_cama, adm_fecing, adm_fecaltaefec, adm_histclinuni, adm_sexo, adm_obrsoc, adm_fecnac 
FROM tbc_admision_scroll 
WHERE Adm_ApelNom LIKE :nombre AND adm_fecaltaefec = 0 
ORDER BY Adm_ApelNom ASC

-- :name carga-internados :? :*
SELECT adm_histclin, adm_apelnom, adm_habita, adm_cama, adm_fecing, adm_fecaltaefec, adm_histclinuni, adm_sexo, adm_obrsoc, adm_fecnac, adm_horing 
FROM tbc_admision_scroll 
WHERE adm_fecaltaefec = 0 
ORDER BY Adm_ApelNom ASC

-- :name carga-guardia :? :*
SELECT Guar_ApeNom, Guar_FechaIngreso, Guar_HoraIngreso, Guar_Estado, Guar_HistClinica, Guar_Diagnostico 
FROM tbc_guardia   
WHERE Guar_Especialidad = 5 AND Guar_Estado < 4

-- :name obtener-patologias :?:*
SELECT pat_codi, pat_descrip
FROM tbc_patologia

-- :name obtener-intervenciones :? :*
SELECT itv_codi, itv_codiesp, itv_nivelcomplejidad, itv_estad, itv_dadores, itv_abdomen, itv_vigila, itv_tipestudio, itv_descripcion
FROM tbc_interven

-- :name obtener-descr-intervencion-por-id :? :1
SELECT itv_descripcion
FROM tbc_interven
WHERE itv_codi = :itv_codi

-- :name carga-ambulatorios :? :*
SELECT HistCabNroUnico, HistCabSexo, HistCabFechaNac, HistCabTipoDoc, HistCabNroDoc, HistCabFecAten, HistCabObra, HistCabPlanX, HistCabApellNom, HistCabNroBenef
FROM tbc_hist_cab_new

-- :name carga-ambulatorios-por-hc :? :1
SELECT HistCabSexo, HistCabFechaNac, HistCabTipoDoc, HistCabNroDoc, HistCabFecAten, HistCabObra, HistCabPlanX, HistCabApellNom, HistCabNroBenef
FROM tbc_hist_cab_new
WHERE HistCabNroUnico = :histcabnrounico

-- :name obtener-obras :? :*
SELECT obr_codigo, obr_razonsoc
FROM tbc_obras

-- :name obtener-obra-por-id :? :1
SELECT obr_razonsoc
FROM tbc_obras
WHERE obr_codigo = :obr_codigo

-- :name obtener-ficha-anestesica :? :*
SELECT fichaaneste_cab_id, histcli, fecha, edad, piso, pulso, riesgo_op_grado, posicion, cirujano_legajo, ayudante_legajo, auxiliar_legajo, urgencia,
       complic_preoperatoria, premedicacion, droga_dosis, analgesia, zona_inyeccion, agente_anestesico, cant_inyectada_cc, anest_inhalatoria, anest_endovenosa
       intubacion_traqueal, tubo_nro, mango, respiracion_espontanea, respiracion_asistida, resp_controlada_manual, resp_controlada_mecanica, sistema_sin_reinhalacion,
       sistema_con_rehin_parcial, sistema_con_rehin_total, habitacion, resp_frec_x_min, resp_tipo, t_art_habitual_max, t_art_habitual_min, t_art_actual_max,
       t_art_actual_min, induccion, mantenimiento, estado, observaciones, fecha_inicio, fecha_final, anest_gral, anest_conductiva, anest_local, anest_nla,
       diagnostico, diagnostico_operatorio, dextrosa, fisiologica, sangre, anestesiologo_legajo, talla, peso, sexo, hora_inicio_grilla, hora_inyectada,
       grilla_pasomin, grilla_horas, obra_social, histcli_unico, oper_propuesta, oper_realizada, cama, anestesiologo_tipo, cirprotocolo, cirujano_tipo,
       ayudante_tipo, auxiliar_tipo, anes_numero, anestesiologo_lega, tbc_anest_carga_fecha, tbc_anest_carga_hora, modif_legajo, modif_fechahora
FROM fichaaneste_cab
WHERE (histcli = :histcli OR histcli_unico = :histcli_unico) AND cirprotocolo = :cirprotocolo 

-- :name obtener-ficha-anestesica-abierta :? :*
SELECT fichaaneste_cab_id, histcli, fecha, edad, piso, pulso, riesgo_op_grado, posicion, cirujano_legajo, ayudante_legajo, auxiliar_legajo, urgencia,
       complic_preoperatoria, premedicacion, droga_dosis, analgesia, zona_inyeccion, agente_anestesico, cant_inyectada_cc, anest_inhalatoria, anest_endovenosa
       intubacion_traqueal, tubo_nro, mango, respiracion_espontanea, respiracion_asistida, resp_controlada_manual, resp_controlada_mecanica, sistema_sin_reinhalacion,
       sistema_con_rehin_parcial, sistema_con_rehin_total, habitacion, resp_frec_x_min, resp_tipo, t_art_habitual_max, t_art_habitual_min, t_art_actual_max,
       t_art_actual_min, induccion, mantenimiento, estado, observaciones, fecha_inicio, fecha_final, anest_gral, anest_conductiva, anest_local, anest_nla,
       diagnostico, diagnostico_operatorio, dextrosa, fisiologica, sangre, anestesiologo_legajo, talla, peso, sexo, hora_inicio_grilla, hora_inyectada,
       grilla_pasomin, grilla_horas, obra_social, histcli_unico, oper_propuesta, oper_realizada, cama, anestesiologo_tipo, cirprotocolo, cirujano_tipo,
       ayudante_tipo, auxiliar_tipo, anes_numero, anestesiologo_lega, tbc_anest_carga_fecha, tbc_anest_carga_hora, modif_legajo, modif_fechahora
FROM fichaaneste_cab
WHERE (histcli = :histcli OR histcli_unico = :histcli_unico) AND cirprotocolo = :cirprotocolo AND estado = 0

-- :name obtener-fichas-anestesicas-abiertas :? :*
SELECT fichaaneste_cab_id, histcli, fecha, edad, piso, pulso, riesgo_op_grado, posicion, cirujano_legajo, ayudante_legajo, auxiliar_legajo, urgencia,
       complic_preoperatoria, premedicacion, droga_dosis, analgesia, zona_inyeccion, agente_anestesico, cant_inyectada_cc, anest_inhalatoria, anest_endovenosa
       intubacion_traqueal, tubo_nro, mango, respiracion_espontanea, respiracion_asistida, resp_controlada_manual, resp_controlada_mecanica, sistema_sin_reinhalacion,
       sistema_con_rehin_parcial, sistema_con_rehin_total, habitacion, resp_frec_x_min, resp_tipo, t_art_habitual_max, t_art_habitual_min, t_art_actual_max,
       t_art_actual_min, induccion, mantenimiento, estado, observaciones, fecha_inicio, fecha_final, anest_gral, anest_conductiva, anest_local, anest_nla,
       diagnostico, diagnostico_operatorio, dextrosa, fisiologica, sangre, anestesiologo_legajo, talla, peso, sexo, hora_inicio_grilla, hora_inyectada,
       grilla_pasomin, grilla_horas, obra_social, histcli_unico, oper_propuesta, oper_realizada, cama, anestesiologo_tipo, cirprotocolo, cirujano_tipo,
       ayudante_tipo, auxiliar_tipo, anes_numero, anestesiologo_lega, tbc_anest_carga_fecha, tbc_anest_carga_hora, modif_legajo, modif_fechahora
FROM fichaaneste_cab
WHERE estado = 0

-- :name obtener-ficha-anestesica-por-id :? :1
SELECT fichaaneste_cab_id, histcli, fecha, edad, piso, pulso, riesgo_op_grado, posicion, cirujano_legajo, ayudante_legajo, auxiliar_legajo, urgencia,
       complic_preoperatoria, premedicacion, droga_dosis, analgesia, zona_inyeccion, agente_anestesico, cant_inyectada_cc, anest_inhalatoria, anest_endovenosa
       intubacion_traqueal, tubo_nro, mango, respiracion_espontanea, respiracion_asistida, resp_controlada_manual, resp_controlada_mecanica, sistema_sin_reinhalacion,
       sistema_con_rehin_parcial, sistema_con_rehin_total, habitacion, resp_frec_x_min, resp_tipo, t_art_habitual_max, t_art_habitual_min, t_art_actual_max,
       t_art_actual_min, induccion, mantenimiento, estado, observaciones, fecha_inicio, fecha_final, anest_gral, anest_conductiva, anest_local, anest_nla,
       diagnostico, diagnostico_operatorio, dextrosa, fisiologica, sangre, anestesiologo_legajo, talla, peso, sexo, hora_inicio_grilla, hora_inyectada,
       grilla_pasomin, grilla_horas, obra_social, histcli_unico, oper_propuesta, oper_realizada, cama, anestesiologo_tipo, cirprotocolo, cirujano_tipo,
       ayudante_tipo, auxiliar_tipo, anes_numero, anestesiologo_lega, tbc_anest_carga_fecha, tbc_anest_carga_hora, modif_legajo, modif_fechahora
FROM fichaaneste_cab
WHERE fichaaneste_cab_id = :fichaaneste_cab_id

-- :name obtener-detalle-ficha-anestesica :? :1
SELECT fichaaneste_det_id, min_col, valor, fichaaneste_cab_id, tipo_medicion_cod
FROM fichaaneste_det
WHERE fichaaneste_cab_id = :fichaaneste_cab_id

-- :name obtener-medicamentos-ficha-anestesica :? :1
SELECT id, id_ficha_anestesica, codigo_medicamento, dosis, imed, unidad_de_medida
FROM fichaaneste_medicamentos
WHERE id_ficha_anestesica = :id_ficha_anestesica

-- :name obtener-nomencladores-ficha-anestesica :? :1
SELECT id, protocolo, historia_clinica, historia_clinica_unica, legajo_anestesista, tipo_legajo, tipo_nomenclador, codigo_nomenclador, grupo_nomenclador, porcentaje, id_ficha_anestesica
FROM fichaaneste_val
WHERE id_ficha_anestesica = :id_ficha_anestesica

-- :name obtener-tipo-medicion-ficha-anestesica :? :1
SELECT fichaaneste_tipomedicion_id, codigo, descripcion, minimo, maximo, color, simbolo, grilla_nro
FROM fichaaneste_tipomedicion  
WHERE fichaaneste_tipomedicion_id = :fichaaneste_tipomedicion_id

-- :name insertar-cabecera-ficha-anestesica :<! 
INSERT INTO fichaaneste_cab (histcli, fecha, edad, piso, pulso, riesgo_op_grado, posicion, cirujano_legajo, ayudante_legajo, auxiliar_legajo, urgencia,
       complic_preoperatoria, premedicacion, droga_dosis, analgesia, zona_inyeccion, agente_anestesico, cant_inyectada_cc, anest_inhalatoria, anest_endovenosa,
       intubacion_traqueal, tubo_nro, mango, respiracion_espontanea, respiracion_asistida, resp_controlada_manual, resp_controlada_mecanica, sistema_sin_reinhalacion,
       sistema_con_rehin_parcial, sistema_con_rehin_total, habitacion, resp_frec_x_min, resp_tipo, t_art_habitual_max, t_art_habitual_min, t_art_actual_max,
       t_art_actual_min, induccion, mantenimiento, estado, observaciones, fecha_inicio, fecha_final, anest_gral, anest_conductiva, anest_local, anest_nla,
       diagnostico, diagnostico_operatorio, dextrosa, fisiologica, sangre, anestesiologo_legajo, talla, peso, sexo, hora_inicio_grilla, hora_inyectada,
       grilla_pasomin, grilla_horas, obra_social, histcli_unico, oper_propuesta, oper_realizada, cama, anestesiologo_tipo, cirprotocolo, cirujano_tipo,
       ayudante_tipo, auxiliar_tipo, anes_numero, anestesiologo_lega, tbc_anest_carga_fecha, tbc_anest_carga_hora, modif_legajo, modif_fechahora)
VALUES (:histcli, :fecha, :edad, :piso, :pulso, :riesgo_op_grado, :posicion, :cirujano_legajo, :ayudante_legajo, :auxiliar_legajo, :urgencia,
        :complic_preoperatoria, :premedicacion, :droga_dosis, :analgesia, :zona_inyeccion, :agente_anestesico, :cant_inyectada_cc, :anest_inhalatoria, :anest_endovenosa,
       :intubacion_traqueal, :tubo_nro, :mango, :respiracion_espontanea, :respiracion_asistida, :resp_controlada_manual, :resp_controlada_mecanica, :sistema_sin_reinhalacion,
       :sistema_con_rehin_parcial, :sistema_con_rehin_total, :habitacion, :resp_frec_x_min, :resp_tipo, :t_art_habitual_max, :t_art_habitual_min, :t_art_actual_max,
       :t_art_actual_min, :induccion, :mantenimiento, :estado, :observaciones, :fecha_inicio, :fecha_final, :anest_gral, :anest_conductiva, :anest_local, :anest_nla,
       :diagnostico, :diagnostico_operatorio, :dextrosa, :fisiologica, :sangre, :anestesiologo_legajo, :talla, :peso, :sexo, :hora_inicio_grilla, :hora_inyectada,
       :grilla_pasomin, :grilla_horas, :obra_social, :histcli_unico, :oper_propuesta, :oper_realizada, :cama, :anestesiologo_tipo, :cirprotocolo, :cirujano_tipo,
       :ayudante_tipo, :auxiliar_tipo, :anes_numero, :anestesiologo_lega, :tbc_anest_carga_fecha, :tbc_anest_carga_hora, :modif_legajo, :modif_fechahora) 
RETURNING fichaaneste_cab_id

-- :name insertar-detalle-ficha-anestesica :<!
INSERT INTO fichaaneste_det (min_col, valor, fichaaneste_cab_id, tipo_medicion_cod)
VALUES (:min_col, :valor, :fichaaneste_cab_id, :tipo_medicion_cod)
RETURNING fichaaneste_det_id


-- :name insertar-medicamentos-ficha-anestesica :<!
INSERT INTO fichaaneste_medicamentos (id_ficha_anestesica, codigo_medicamento, dosis, imed, unidad_de_medida)
VALUES (:id_ficha_anestesica, :codigo_medicamento, :dosis, :imed, :unidad_de_medida)
RETURNING id

-- :name insertar-nomencladores-ficha-anestesica :<!
INSERT INTO fichaaneste_val (protocolo, historia_clinica, historia_clinica_unica, legajo_anestesista, tipo_legajo, tipo_nomenclador, codigo_nomenclador, grupo_nomenclador, porcentaje, id_ficha_anestesica)
VALUES (:protocolo, :historia_clinica, :historia_clinica_unica, :legajo_anestesista, :tipo_legajo, :tipo_nomenclador, :codigo_nomenclador, :grupo_nomenclador, :porcentaje, :id_ficha_anestesica)
RETURNING id

-- :name insertar-tipomedicion-ficha-anestesica :<!
INSERT INTO fichaaneste_tipomedicion (codigo, descripcion, minimo, maximo, color, simbolo, grilla_nro)
VALUES (:codigo, :descripcion, :minimo, :maximo, :color, :simbolo, :grilla_nro)
RETURNING fichaaneste_tipomedicion_id