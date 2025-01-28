(ns main.backend.db.conexion
  (:require [next.jdbc.connection :as connection]
            [next.jdbc :as jdbc]
            [aero.core :refer [read-config]]
            [clojure.java.io :as io]
            [com.potetm.fusebox.timeout :as to]
            [com.brunobonacci.mulog :as µ]
            [tick.core :as t])
  (:import com.zaxxer.hikari.HikariDataSource
           java.io.IOException
           java.sql.SQLException))

(def conf (try 
            (read-config (io/resource "config.edn"))
            (catch IOException e (let [msj (ex-message e)] 
                                   (µ/log ::excepcion-en-config :fecha (t/date-time) :excepcion msj)
                                   (throw (ex-info "Error al leer archivo de configuracion" {:excepcion (ex-message e)}))))))
 
(def timeout (to/init {::to/timeout-ms 5000}))
  
(defonce pool-desal (delay 
                      (try
                        (connection/->pool HikariDataSource (:desal conf))
                        (catch SQLException e (let [msj (ex-message e)] 
                                                (µ/log ::excepcion-en-pooling :fecha (t/date-time) :excepcion msj)
                                                   (throw (ex-info "Error al crear connection pool" {:excepcion (ex-message e)}))))
                        (catch IOException e (let [msj (ex-message e)] 
                                               (µ/log ::excepcion-en-pooling :fecha (t/date-time) :excepcion msj) 
                                               (throw (ex-info "Error al crear connection pool" {:excepcion (ex-message e)}))))
                        (catch Exception e (let [msj (ex-message e)] 
                                             (µ/log ::excepcion-en-pooling :fecha (t/date-time) :excepcion msj) 
                                             (throw (ex-info "Error al crear connection pool" {:excepcion (ex-message e)})))))))

(defmulti obtener-conexion identity)

(defmethod obtener-conexion :asistencial [_]
  (to/try-interruptible
    (to/with-timeout timeout
      (jdbc/get-connection (:asistencial conf))) 
    (catch IOException e (let [msj (ex-message e)] 
                           (µ/log ::excepcion-en-conexion-asistencial :fecha (t/date-time) :excepcion msj)
                           (throw (ex-info "Error al crear conexion con asistencial" {:excepcion (ex-message e)}))))
    (catch Exception e (let [msj (ex-message e)]
                         (µ/log ::excepcion-en-conexion-asistencial :fecha (t/date-time) :excepcion msj)
                         (throw (ex-info "Error al crear conexion con asistencial" {:excepcion (ex-message e)}))))))

(defmethod obtener-conexion :maestros [_]
  (to/try-interruptible
    (to/with-timeout timeout 
      (jdbc/get-connection (:maestros conf)))
    (catch IOException e (let [msj (ex-message e)]
                           (µ/log ::excepcion-en-conexion-maestros :fecha (t/date-time) :excepcion msj)
                           (throw (ex-info "Error al crear conexion con maestros" {:excepcion (ex-message e)}))))
    (catch Exception e (let [msj (ex-message e)]
                         (µ/log ::excepcion-en-conexion-maestros :fecha (t/date-time) :excepcion msj)
                         (throw (ex-info "Error al crear conexion con maestros" {:excepcion (ex-message e)}))))))

(defmethod obtener-conexion :desal [_]
  (deref pool-desal))

(defmethod obtener-conexion :default
 [_]
 (throw (IllegalArgumentException. "No existe la conexión seleccionada")))

(comment
  
  (ns-unmap *ns* 'obtener-conexion)

  (ns-unmap *ns* 'pool-desal)

  ;; Lanza AbstractMethodError, lo que significa que no está implementado por Relativity. ¡Vaya sorpresa!
  (with-open [c (obtener-conexion :asistencial)]
    (.getNetworkTimeout c))
  
  (.close *1)
  
  (with-open [^HikariDataSource ds (connection/->pool com.zaxxer.hikari.HikariDataSource
                                                      (:desal conf))]
    (jdbc/execute! ds ["SELECT * FROM fichaaneste_val"]))
   
  (.isRunning @pool-desal)
  (def hikari-bean (.getHikariConfigMXBean @pool-desal)) 
  (.getMaximumPoolSize hikari-bean)
  (.getMaxLifetime hikari-bean)
  (.getMinimumIdle hikari-bean) 
  (.getJdbcUrl hikari-bean)
  (.getUsername hikari-bean)
  (.getPassword hikari-bean)
  (.getPoolName hikari-bean) 
  (.getLeakDetectionThreshold hikari-bean)
  (.getValidationTimeout hikari-bean)
  (.getConnectionTimeout hikari-bean) 
  (.getDataSourceProperties hikari-bean)

  :rcf)