(ns main.server
  (:require
   [org.httpkit.server :as http] 
   [com.fulcrologic.fulcro.server.api-middleware :as fmw :refer [not-found-handler wrap-api]]
   [com.wsscode.pathom3.interface.eql :as p.eql] 
   [com.wsscode.pathom3.connect.indexes :as pci]
   [com.wsscode.pathom3.connect.built-in.resolvers :as pbir]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.not-modified :refer [wrap-not-modified]]
   [ring.middleware.resource :refer [wrap-resource]]
   [main.modelo.paciente :as paciente]
   [main.modelo.patologia :as patologia]
   [main.modelo.intervencion :as intervencion]
   [main.modelo.obra-social :as obra-social]
   [main.modelo.ficha-anestesica :as ficha-anestesica]))

(defonce plan-cache* (atom {}))

(def resolvers [paciente/resolvers
                patologia/resolvers
                intervencion/resolvers
                obra-social/resolvers
                ficha-anestesica/resolvers
                (pbir/equivalence-resolver :tbc_interven/itv_codi :tbc_guardia/guar_diagnostico)
                (pbir/equivalence-resolver :tbc_guardia/guar_histclinica :tbc_hist_cab_new/histcabnrounico)
                (pbir/equivalence-resolver :tbc_admision_scroll/adm_obrsoc :tbc_obras/obr_codigo)
                (pbir/equivalence-resolver :tbc_hist_cab_new/histcabobra :tbc_obras/obr_codigo)
                (pbir/equivalence-resolver :tbc_admision_scroll/adm_histclinuni :tbc_hist_cab_new/histcabnrounico)])

(def env (-> {::plan-cache* plan-cache*} {} (pci/register resolvers)))

(def parser (p.eql/boundary-interface env))

(def middleware (-> not-found-handler
                    (wrap-api {:uri "/api"
                               :parser (fn [request]
                                         (prn request)
                                         (parser request))})
                    (fmw/wrap-transit-params)
                    (fmw/wrap-transit-response)
                    (wrap-resource "public")
                    wrap-content-type
                    wrap-not-modified))

(defonce server (atom nil))

(defn start []
  (let [srv (http/run-server #'middleware {:port 3000})]
    (reset! server srv) 
    :ok))

(defn stop []
  (when @server
    (@server :timeout 100)
    (reset! server nil)))
      
(comment  
  (stop)
  (start)
  (p.eql/process env [:pacientes-ambulatorios])
  (p.eql/process env [:pacientes-internados])
  (p.eql/process env [:todas-las-patologias])
  (p.eql/process env [{:todas-las-patologias [:tbc_patologia/pat_descrip]}])
  (p.eql/process env [:todos-los-pacientes])
  (p.eql/process env {:tbc_interven/itv_codi 1014} [:intervencion])
  (p.eql/process env [{:todas-las-patologias
                       [{:todas-las-patologias [:tbc_patologia/pat_descrip]}]}])
  (p.eql/process env {:tbc_obras/obr_codigo 1820} [:obra])
  (p.eql/process env [:intervenciones])
  (def path [{:pacientes-ambulatorios
              [:tbc_guardia/id
               :tbc_guardia/guar_apenom
               :tbc_guardia/guar_histclinica
               :tbc_guardia/guar_estado 
               :tbc_guardia/guar_fechaingreso
               :tbc_guardia/guar_horaingreso
               {:intervencion [:tbc_guardia/guar_diagnostico]}]}])
  
  (def complete-path [{:todos-los-pacientes
                       [{:pacientes-ambulatorios
                         [:tbc_guardia/id
                          :tbc_guardia/guar_apenom
                          :tbc_guardia/guar_histclinica
                          :tbc_guardia/guar_estado
                          {:intervencion [:tbc_guardia/guar_diagnostico]}
                          :tbc_guardia/guar_fechaingreso
                          :tbc_guardia/guar_horaingreso]}
                        {:pacientes-internados
                         [:tbc_admision_scroll/id
                          :tbc_admision_scroll/adm_histclin
                          :tbc_admision_scroll/adm_histclinuni
                          :tbc_admision_scroll/adm_apelnom
                          :tbc_admision_scroll/adm_habita
                          :tbc_admision_scroll/adm_cama
                          :tbc_admision_scroll/adm_fecing
                          :tbc_admision_scroll/adm_horing]}
                        [:ui.fulcro.client.data-fetch.load-markers/by-id
                         :carga-paciente]]}
                      [:ui.fulcro.client.data-fetch.load-markers/by-id :carga-paciente]])
   
  (p.eql/process env path)

  (p.eql/process env complete-path)
  
  
  
  
  
  
  
  ) 