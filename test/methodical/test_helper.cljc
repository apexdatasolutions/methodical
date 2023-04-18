(ns methodical.test-helper)

(let [nl Environment/NewLine]
  (defn platform-newlines [s] (.Replace s "\n" nl))) 
