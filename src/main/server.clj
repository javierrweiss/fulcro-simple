(ns main.server
  (:require
   [org.httpkit.server :as http] 
   [com.fulcrologic.fulcro.server.api-middleware :as fmw :refer [wrap-api]] 
   [com.wsscode.pathom3.interface.eql :as p.eql] 
   [com.wsscode.pathom3.connect.indexes :as pci] 
   [com.wsscode.pathom3.plugin :as p.plugin]
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
   [main.modelo.intervenciones-patologia :as intervenciones-patologia]
   [main.modelo.profesionales :as profesionales]
   [com.brunobonacci.mulog :as µ]
   [tick.core :as t]))

(defonce server (atom {}))

(defn start-publisher []
  (µ/start-publisher! {:type :multi
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
                intervenciones-patologia/resolvers
                profesionales/resolvers
                (pbir/equivalence-resolver :tbc_interven/itv_codi :tbc_guardia/guar_diagnostico)
                (pbir/equivalence-resolver :tbc_guardia/guar_histclinica :tbc_hist_cab_new/histcabnrounico)
                (pbir/equivalence-resolver :tbc_admision_scroll/adm_obrsoc :tbc_obras/obr_codigo)
                (pbir/equivalence-resolver :tbc_hist_cab_new/histcabobra :tbc_obras/obr_codigo)
                (pbir/equivalence-resolver :tbc_admision_scroll/adm_histclinuni :tbc_hist_cab_new/histcabnrounico)])

(def env (-> {::plan-cache* plan-cache*
              :com.wsscode.pathom3.error/lenient-mode? true}
             (pci/register resolvers)
             (p.plugin/register {::p.plugin/id 'err
                                 :com.wsscode.pathom3.connect.runner/wrap-resolver-error (fn [_]
                                                                                           (fn [_ node error]
                                                                                             (let [msj (ex-message error)]
                                                                                               (µ/log ::error-en-resolver-pathom :fecha (t/date-time) :error msj :node node)
                                                                                               {:com.wsscode.pathom3.connect.runner/error msj})))})
             (p.plugin/register {::p.plugin/id 'err-mutation
                                 :com.wsscode.pathom3.connect.runner/wrap-mutation-error
                                 (fn [_]
                                   (fn [_ ast error]
                                     (let [msj (ex-message error)]
                                       (µ/log ::error-en-mutacion-pathom :at (str "Error on" (:key ast)) :exception msj)
                                       {:com.wsscode.pathom3.connect.runner/error msj})))})))

(def parser (p.eql/boundary-interface env))

(defn redirect-not-found [request]
  (resource-response "index.html" {:root "public"}))

(defn parser-handler
  [req] 
  (parser req))

(def middleware (-> redirect-not-found
                    (wrap-api {:uri "/api"
                               :parser parser-handler})
                    (wrap-resource "public")
                    wrap-content-type
                    wrap-not-modified
                    (fmw/wrap-transit-params)
                    (fmw/wrap-transit-response)))

(defn start []
  (let [srv (http/run-server #'middleware {:port 3500
                                           :error-logger (fn [msg ex]
                                                           (µ/log ::error-servidor :fecha (t/date-time) :mensaje msg :excepcion ex))
                                           :warn-logger (fn [msg ex]
                                                          (µ/log ::advertencia-servidor :fecha (t/date-time) :mensaje msg :excepcion ex))
                                           :event-logger (fn [event]
                                                           (µ/log (keyword (str *ns*) event) :fecha (t/date-time)))})]
    (swap! server assoc :server srv :publisher (start-publisher))
    (µ/log ::servidor-iniciado :fecha (t/date-time))))

(defn stop []
  (when @server
    ((:server @server) :timeout 100)
    (:publisher @server)
    (reset! server {})))
      
 (comment
   (stop)
   (start)
   @server
   (p.eql/process env [:todos-los-pacientes])

   (as-> (-> (p.eql/process env [:todos-los-profesionales]) :todos-los-profesionales :lista-profesionales) m
     (mapv #(update % :tbc_medicos_personal/medperapeynom clojure.string/trim) m)   
     (sort-by :tbc_medicos_personal/medperapeynom m))

   (p.eql/process env [:patologias-e-intervenciones])

   (p.eql/process env [:pacientes-internados])
   
   (fmw/transit-response [:error 'error])
   (fmw/transit-response {:error 'error})
   (fmw/transit-response 'a) 
   
   
   (def mock-request [{:todos-los-pacientes
                       [{:todos-los-pacientes
                         [{:pacientes-ambulatorios
                           [:tbc_guardia/id 
                            :tbc_guardia/guar_apenom
                            :tbc_guardia/guar_histclinica
                            :tbc_guardia/guar_estado
                            :intervencion {:paciente-ambulatorio-histcab 
                                           [:obra 
                                            :tbc_hist_cab_new/histcabsexo 
                                            :tbc_hist_cab_new/histcabfechanac]} 
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
                            :tbc_admision_scroll/adm_horing
                            :tbc_admision_scroll/adm_sexo
                            :tbc_admision_scroll/adm_obrsoc
                            :tbc_admision_scroll/adm_fecnac]}]} 
                            [:ui.fulcro.client.data-fetch.load-markers/by-id :carga-paciente]]}])
    
   (p.eql/process env mock-request) 
   (p.eql/process env (vector (update-in (first mock-request) [:todos-los-pacientes] conj [:com.wsscode.pathom3.connect.runner/attribute-errors])))
    
   (try
     (throw (ex-info "Excepcion X" {:error "Error grave"}))
     (catch Exception err {:com.wsscode.pathom3.connect.runner/mutation-error (ex-message err)}))

   :rcf)