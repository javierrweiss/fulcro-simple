(ns user
  (:require [main.server :as server]
            [clojure.tools.namespace.repl :as tools-ns]))

(tools-ns/set-refresh-dirs "src/main" "dev")
 
(defn start []
  (server/start))
 
(defn restart []
  (server/stop)
  (tools-ns/refresh :after 'server/start))

(start)

(comment 
  (restart)  

  )