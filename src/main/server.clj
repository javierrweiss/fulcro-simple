(ns main.server
  (:require
   [org.httpkit.server :as http] 
   [com.fulcrologic.fulcro.server.api-middleware :as fmw :refer [wrap-api]]
   [com.wsscode.pathom3.interface.eql :as p.eql] 
   [com.wsscode.pathom3.connect.indexes :as pci]
   [com.wsscode.pathom3.connect.built-in.resolvers :as pbir]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.not-modified :refer [wrap-not-modified]]
   [ring.middleware.resource :refer [wrap-resource]]
   [ring.util.response :as response :refer [resource-response]]
   [main.modelo.paciente :as paciente]
   [main.modelo.patologia :as patologia]
   [main.modelo.intervencion :as intervencion]
   [main.modelo.obra-social :as obra-social]
   [main.modelo.ficha-anestesica :as ficha-anestesica]
   [com.brunobonacci.mulog :as µ]
   [tick.core :as t]))

(def stop-publisher (µ/start-publisher! {:type :multi 
                                         :publishers [{:type :simple-file :filename "/tmp/ficha-anestesica/events.log"}
                                                      {:type :console}]}))

(defonce plan-cache* (atom {}))

(µ/set-global-context! {:app-name "ficha-anestesica"
                        :version "0.0.1"
                        :os (System/getProperty "os.name")
                        :java-version (System/getProperty "java.version")})
 
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

(defn redirect-not-found [request]
  (resource-response "index.html" {:root "public"}))

(def middleware (-> redirect-not-found
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
  (let [srv (http/run-server #'middleware {:port 3000
                                           :error-logger (fn [msg ex]
                                                           (µ/log ::error-servidor :fecha (t/date-time) :mensaje msg :excepcion ex))
                                           :warn-logger (fn [msg ex]
                                                          (µ/log ::advertencia-servidor :fecha (t/date-time) :mensaje msg :excepcion ex))})]
    (reset! server srv)
    (µ/log ::servidor-iniciado :fecha (t/date-time))))

(defn stop []
  (when @server
    (@server :timeout 100)
    (reset! server nil)
    (stop-publisher)))
      
(comment  
  (stop)
  (start)
  (t/date-time)
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
  
  (p.eql/process env {:ficha-anestesica-id 73762} [:cabecera])

  (p.eql/process env {:ficha-anestesica-id 73762} [:detalle])
  
  (p.eql/process env {:ficha-anestesica-id 73762} [:nomencladores])
 
  (p.eql/process env {:ficha-anestesica-id 73762} [:medicamentos])

  (p.eql/process env {:fichaaneste-tipomedicion-id 9} [:tipomedicion])

  (p.eql/process env {:histcli 3263980
                      :histcli_unico 0
                      :cirprotocolo 500575} [:cabecera])
  
  (p.eql/process env {:ficha-anestesica-id 73762
                      :fichaaneste-tipomedicion-id 9} [:ficha-anestesica])
  
  (p.eql/process env {:ficha-anestesica-id 73161} [:detalle])
  
    
  
  :rcf) 