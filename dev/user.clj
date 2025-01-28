(ns user
  (:require [main.server :as server]
            [clojure.tools.namespace.repl :as tools-ns]))

(tools-ns/set-refresh-dirs "src/main" "dev")

(defn start []
  (server/start))
 
(defn restart [] 
  (when @server/server
    (@server/server :timeout 100)
    (reset! server/server nil))
  (start)
  #_(tools-ns/refresh :after 'server/start))

(start)

(comment 
  (restart)  
 main.backend.db.conexion/timeout 
  )