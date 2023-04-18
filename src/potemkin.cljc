(ns potemkin
  (:require
    [potemkin.namespaces]
    #_[potemkin.types]
    #_[potemkin.collections]
    #_[potemkin.macros]
    #_[potemkin.utils]))

(potemkin.namespaces/import-vars potemkin.namespaces/import-vars) ;; totally meta

(import-vars
  [potemkin.namespaces

   import-fn
   import-macro
   import-def]

  #_[potemkin.macros

   unify-gensyms
   normalize-gensyms
   equivalent?]

  #_[potemkin.utils

   condp-case
   try*
   fast-bound-fn
   fast-bound-fn*
   fast-memoize
   doit
   doary]

  #_[potemkin.types

   def-abstract-type
   reify+
   defprotocol+
   deftype+
   defrecord+
   definterface+
   extend-protocol+]

  #_[potemkin.collections

   reify-map-type
   def-derived-map
   def-map-type])