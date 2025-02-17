(ns pretty.core
  "Standard protocol to make it easier to nicely print a custom type in the REPL or elsewhere."
  (:require [clojure.pprint :as pprint])
  (:import [clojure.lang IPersistentMap IRecord ISeq]
           #?(:cljr System.Collections.IDictionary :default java.util.Map)))

(defprotocol PrettyPrintable
  "Implmement this protocol to return custom representations of objects when printing them. For best results, implement
  this protocol in the class declaration form (i.e., as part of the `defrecord`/`deftype`/`reify`/etc. form) rather
  than via `extend-protocol` or the like:

    ;; GOOD
    (defrecord MyRecordType []
      PrettyPrintable
      (pretty [_] ...))

    ;; BAD
    (defrecord MyRecordType [])

    (extend-protocol PrettyPrintable
      MyRecordType
      (pretty [_] ...))

  Why? In the former example, `MyRecordType` actually implements the underlying `pretty.core.PrettyPrintable`
  interface, in other words,

    (instance? (Class/forName \"pretty.core.PrettyPrintable\") (->MyRecordType))

  is true; this is not the case case when using `extend-protocol` and the like. The the `prefer-method` calls below
  will have no effect on types that implement the protocol but not the underlying interface class itself."
  (pretty [_]
    "Return an appropriate representation of this object to be used when printing it, such as in the REPL or in log
    messages."))

(defmethod print-method pretty.core.PrettyPrintable
  [s writer]
  (print-method (pretty s) writer))

(defmethod pprint/simple-dispatch pretty.core.PrettyPrintable
  [s]
  (pprint/write-out (pretty s)))

(when-not *compile-files*
  (doseq [method [print-method pprint/simple-dispatch]
          tyype  [IRecord #?(:cljr IDictionary :default Map) IPersistentMap ISeq]]
    (prefer-method method pretty.core.PrettyPrintable tyype)))

(defn qualify-symbol-for-*ns*
  "Change the namespace of `qualified-symb` to an appropriate alias for `*ns*` if one exists, or remove it entirely"
  [qualified-symb]
  (let [ns-symb-str (namespace qualified-symb)
        symb-str    (name qualified-symb)
        orig-ns     (the-ns (symbol ns-symb-str))]
    (if (= *ns* orig-ns)
      (symbol symb-str)
      (or (when-let [alias-symb (get (apply zipmap ((juxt vals keys) (ns-aliases *ns*)))
                                     orig-ns)]
            (symbol (name alias-symb) symb-str))
          (when-let [varr (get (ns-refers *ns*) (symbol symb-str))]
            (when (= (symbol varr) qualified-symb)
              (symbol symb-str)))
          (symbol ns-symb-str symb-str)))))