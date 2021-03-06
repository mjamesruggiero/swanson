(ns swanson.models.matcher
  (:require [clojure.string :as string]))

(defn- has-category
  "test to see if the description contains the target value"
  [regular-expression desc]
  (cond
    (re-find regular-expression (string/lower-case desc)) true
    :else false))

(defn match-description
  "match a description to a value"
  [description]
  (cond
    (has-category #"alameda alliance payroll" description) "salary"
    (has-category #"alameda gas" description) "transportation"
    (has-category #"alameda natural" description) "grocery"
    (has-category #"alameda power" description) "utilities"
    (has-category #"amazon" description) "amazon"
    (has-category #"atm withdrawal" description) "atm"
    (has-category #"aveda" description) "household"
    (has-category #"bill pay alameda county i" description) "utilities"
    (has-category #"bill pay sbc" description) "utilities"
    (has-category #"books inc" description) "entertainment"
    (has-category #"bounce" description) "clothing"
    (has-category #"ca dmv" description) "transportation"
    (has-category #"check #" description) "check"
    (has-category #"check deposit" description) "asset"
    (has-category #"cole hardware" description) "household"
    (has-category #"costplus" description) "household"
    (has-category #"credomobile" description) "utilities"
    (has-category #"cvs" description) "household"
    (has-category #"destroy all software" description) "entertainment"
    (has-category #"east bay municip" description) "utilities"
    (has-category #"edward jones" description) "savings"
    (has-category #"encinal market" description) "grocery"
    (has-category #"engine works" description) "transportation"
    (has-category #"feel good bakery" description) "dining"
    (has-category #"github" description) "utilities"
    (has-category #"hsbc online transfer" description) "savings"
    (has-category #"ikea" description) "household"
    (has-category #"interest payment" description) "interest"
    (has-category #"jazzercise" description) "health and exercise"
    (has-category #"lamorinda spanis" description) "education"
    (has-category #"love at first bite" description) "dining"
    (has-category #"natural gr" description) "grocery"
    (has-category #"netflix.com" description) "entertainment"
    (has-category #"nob hill" description) "grocery"
    (has-category #"nob hill" description) "grocery"
    (has-category #"office max" description) "household"
    (has-category #"old navy" description) "clothing"
    (has-category #"pacific gas" description) "utilities"
    (has-category #"paypal" description) "paypal"
    (has-category #"peet's" description) "grocery"
    (has-category #"poppy red" description) "entertainment"
    (has-category #"power" description) "utilities"
    (has-category #"purchase - kohl" description) "household"
    (has-category #"railscasts" description) "entertainment"
    (has-category #"redbubble" description) "clothing"
    (has-category #"reilly media" description) "entertainment"
    (has-category #"restaurant" description) "dining"
    (has-category #"safeway" description) "grocery"
    (has-category #"sharethrough inc direct" description) "salary"
    (has-category #"shell oil" description) "transportation"
    (has-category #"target" description) "household"
    (has-category #"the melt-embarcade" description) "dining"
    (has-category #"tj maxx" description) "clothing"
    (has-category #"to savings" description) "savings"
    (has-category #"tot tank" description) "household"
    (has-category #"toy safari" description) "entertainment"
    (has-category #"treasury direct" description) "savings"
    (has-category #"valero" description) "transportation"
    (has-category #"visa wells" description) "credit card"
    (has-category #"walgreen" description) "household"
    (has-category #"whole foods" description) "grocery"
    (has-category #"wildflower cafe" description) "dining"
    (has-category #"withdrawal in branch" description) "cash"
    (has-category #"yoshi" description) "entertainment"
    (has-category #"etsy.com" description) "entertainment"
    (has-category #"dandelion flow" description) "entertainment"
    (has-category #"trader joe" description) "grocery"
    (has-category #"recurring transfer" description) "savings"
    (has-category #"anthropologie" description) "entertainment"
    :else "unknown"))
