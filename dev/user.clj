(ns user
  (:require [main.server :as server]
            [clojure.tools.namespace.repl :as tools-ns]))

(tools-ns/set-refresh-dirs "src/main" "dev")

(defn start []
  (server/start))
 
(defn restart []
  (when @server/server
    (server/stop)
    (Thread/sleep 1000)
    #_(server/start)
    (tools-ns/refresh :after 'server/start)
    :ok)
  :ya-esta-detenido)

(start)

(comment 
  (restart)  
  
  ) 