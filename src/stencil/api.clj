(ns stencil.api
  "A simple public API for document generation from templates.")

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(defn prepare-template
  "Creates a prepared template instance from an input document."
  [input]
  (assert (instance? java.io.File input) "Expected File input.")
  ;; TODO: call java code here.

  nil)

(defn render!
  "Takes a prepared template instance and renders it.
   By default it returns an InputStream of the rendered document."
  [prepared-template template-data & {:as opts}]
  (assert (some? prepared-template))

  ;; TODO: call java code here.
  nil)

