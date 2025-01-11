(ns main.frontend.application
  (:require [com.fulcrologic.fulcro.networking.http-remote :as http]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.devtools.common.target :refer [ido]]
            [fulcro.inspect.tool :as it]
            [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]))

(defonce APP (-> (app/fulcro-app {:remotes {:remote (http/fulcro-http-remote {})}}) (with-react18)))

(ido (it/add-fulcro-inspect! APP))