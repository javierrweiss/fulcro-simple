(ns main.modelo.paciente
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [main.backend.db.conexion :refer [obtener-conexion]]
            [main.backend.db.config :as c])
  (:import java.sql.SQLException))

(pco/defresolver pacientes-internados [env _]
  {::pco/output [{:pacientes-internados [:tbc_admision_scroll/adm_histclin
                                         :tbc_admision_scroll/adm_histclinuni
                                         :tbc_admision_scroll/adm_apelnom
                                         :tbc_admision_scroll/adm_habita
                                         :tbc_admision_scroll/adm_cama
                                         :tbc_admision_scroll/adm_fecing
                                         :tbc_admision_scroll/adm_fecaltaefec
                                         :tbc_admision_scroll/adm_sexo
                                         :tbc_admision_scroll/adm_obrsoc
                                         :tbc_admision_scroll/adm_horing
                                         :tbc_admision_scroll/adm_fecnac]}]} 
  {:pacientes-internados (mapv (fn [m]
                                 (-> m
                                     (assoc :tbc_admision_scroll/id (random-uuid))
                                     (update :tbc_admision_scroll/adm_histclin int)
                                     (update :tbc_admision_scroll/adm_histclinuni int)))
                               (try 
                                 (with-open [c (obtener-conexion :asistencial)] 
                                      (c/carga-internados c))
                                 (catch SQLException e (throw (ex-message e)))))})

(pco/defresolver pacientes-ambulatorios [env _]
  {::pco/output [{:pacientes-ambulatorios [:tbc_guardia/guar_apenom
                                            :tbc_guardia/guar_diagnostico
                                            :tbc_guardia/guar_estado
                                            :tbc_guardia/guar_fechaingreso
                                            :tbc_guardia/guar_histclinica
                                            :tbc_guardia/guar_horaingreso]}]} 
  {:pacientes-ambulatorios (mapv (fn [m]
                                  (-> (assoc m :tbc_guardia/id (random-uuid))
                                      (update :tbc_guardia/guar_histclinica int))) 
                                (try
                                  (with-open [c (obtener-conexion :asistencial)] 
                                    (c/carga-guardia c))
                                  (catch SQLException e (throw (ex-message e)))))})

(pco/defresolver todos-los-pacientes [env _]
  {::pco/output [:todos-los-pacientes [{:pacientes-internados [:tbc_admision_scroll/adm_histclin
                                                              :tbc_admision_scroll/adm_histclinuni
                                                              :tbc_admision_scroll/adm_apelnom
                                                              :tbc_admision_scroll/adm_habita
                                                              :tbc_admision_scroll/adm_cama
                                                              :tbc_admision_scroll/adm_fecing
                                                              :tbc_admision_scroll/adm_fecaltaefec
                                                              :tbc_admision_scroll/adm_sexo
                                                              :tbc_admision_scroll/adm_obrsoc
                                                              :tbc_admision_scroll/adm_horing
                                                              :tbc_admision_scroll/adm_fecnac]}
                                      {:pacientes-ambulatorios [:tbc_guardia/guar_apenom
                                                                :tbc_guardia/guar_diagnostico
                                                                :tbc_guardia/guar_estado
                                                                :tbc_guardia/guar_fechaingreso
                                                                :tbc_guardia/guar_histclinica
                                                                :tbc_guardia/guar_horaingreso]}]]}
  {:todos-los-pacientes [(pacientes-internados nil nil)
                         (pacientes-ambulatorios nil nil)]})

(pco/defresolver obtener-pacientes-ambulatorios-histcab
  [_ _]
  {::pco/output [{:pacientes-ambulatorios-histcab [:tbc_hist_cab_new/histcabnrounico
                                                   :tbc_hist_cab_new/histcabobra
                                                   :tbc_hist_cab_new/histcabsexo
                                                   :tbc_hist_cab_new/histcabfechanac
                                                   :tbc_hist_cab_new/histcabtipodoc
                                                   :tbc_hist_cab_new/histcabnrodoc
                                                   :tbc_hist_cab_new/histcabfecaten
                                                   :tbc_hist_cab_new/histcabplanx
                                                   :tbc_hist_cab_new/histcabapellnom
                                                   :tbc_hist_cab_new/histcabnrobenef]}]} 
  {:pacientes-ambulatorios-histcab 
   (try
     (with-open [c (obtener-conexion :asistencial)]
       (c/carga-ambulatorios c))
     (catch SQLException e (throw (ex-message e))))})

(pco/defresolver obtener-paciente-ambulatorio-histcab-por-hc
  [_ {:tbc_hist_cab_new/keys [histcabnrounico]}]
  {::pco/input [:tbc_hist_cab_new/histcabnrounico]
   ::pco/output [{:paciente-ambulatorio-histcab [:tbc_hist_cab_new/histcabnrounico
                                                 :tbc_hist_cab_new/histcabobra
                                                 :tbc_hist_cab_new/histcabsexo
                                                 :tbc_hist_cab_new/histcabfechanac
                                                 :tbc_hist_cab_new/histcabtipodoc
                                                 :tbc_hist_cab_new/histcabnrodoc
                                                 :tbc_hist_cab_new/histcabfecaten
                                                 :tbc_hist_cab_new/histcabplanx
                                                 :tbc_hist_cab_new/histcabapellnom
                                                 :tbc_hist_cab_new/histcabnrobenef]}]} 
  {:paciente-ambulatorio-histcab
   (try
     (with-open [c (obtener-conexion :asistencial)]
       (c/carga-ambulatorios-por-hc c {:histcabnrounico histcabnrounico}))
     (catch SQLException e (throw (ex-message e))))})
 
(def resolvers [pacientes-internados 
                pacientes-ambulatorios 
                todos-los-pacientes 
                obtener-pacientes-ambulatorios-histcab
                obtener-paciente-ambulatorio-histcab-por-hc])

(comment
  
  (pacientes-internados)

  (pacientes-ambulatorios)
  
  (todos-los-pacientes nil nil)

  (c/carga-internados-por-nombre (:asistencial @conexiones) {:nombre "BLANCO"})
  
  (time (with-open [c (obtener-conexion :asistencial)] 
          (c/carga-guardia c))) 
  
  (with-open [c (obtener-conexion :asistencial)]
    (c/carga-ambulatorios c))
  
 (todos-los-pacientes nil nil) 
  (obtener-pacientes-ambulatorios-histcab nil nil) 
  (obtener-paciente-ambulatorio-histcab-por-hc nil {:tbc_hist_cab_new/histcabnrounico 28})
  
  
  :rcf)   