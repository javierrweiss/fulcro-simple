(ns main.backend.db.config
  (:require [hugsql.core :as hugsql :refer [def-db-fns set-adapter!]]
            [hugsql.adapter.next-jdbc :as next-adapter]
            [next.jdbc.result-set :as rs]
            [clojure.java.io :as io]))

(set-adapter! (next-adapter/hugsql-adapter-next-jdbc {:builder-fn rs/as-lower-maps}))

(def-db-fns (io/resource "operaciones.sql"))


(comment 
  
  (hugsql/map-of-db-fns (io/resource "operaciones.sql"))

  (hugsql/def-sqlvec-fns (io/resource "operaciones.sql"))

  (carga-guardia-sqlvec)
  
  )