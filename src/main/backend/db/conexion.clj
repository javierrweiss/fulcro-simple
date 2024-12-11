(ns main.backend.db.conexion
  (:require [next.jdbc.connection :as connection]
            [next.jdbc :as jdbc]
            [aero.core :refer [read-config]]
            [clojure.java.io :as io])
  (:import com.zaxxer.hikari.HikariDataSource
           java.io.IOException))

(def conf (read-config (io/resource "config.edn")))
 
#_(def conn (connection/->pool HikariDataSource {}))

(def conexiones (atom {}))

(defn conectar
  [k connect-fn]
  (try
    (swap! conexiones assoc k (connect-fn)) 
    (catch IOException e (prn (ex-message e)))
    (catch Exception e (prn (ex-message e)))
    (finally @conexiones)))

(defn conectar-asistencial
  [] 
  (conectar :asistencial #(jdbc/get-connection (:asistencial conf))))

(defn conectar-maestros 
  []
  (conectar :maestros #(jdbc/get-connection (:maestros conf))))