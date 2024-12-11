
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