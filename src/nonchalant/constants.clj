(ns nonchalant.constants
  (:import (java.io File)))

(def user-home (. System getProperty "user.home"))
(def separator (File/separator))
(def downloads-folder (str user-home separator "Downloads"))
(def user-agent "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:80.0) Gecko/20100101 Firefox/80.0")
