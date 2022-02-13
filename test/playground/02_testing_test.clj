(ns playground.02-testing-test
  (:require [clojure.test :refer [deftest is are]]
            [playground.02-testing :as sut]))

;; ## Look up!
;;
;; The `ns` form above this paragraph defines a namespace, Clojure's way of
;; organizing modules/packages. The `:require` form contains references to
;; other namespaces, either importing functions directly (`:refer`) or giving
;; the whole package an alias (`:as`).
;;
;; In the latter case, where the alias is `sut`, you can now access public
;; variables from that namespace using the form `sut/...`.

(sut/merge-sort [1 2 3])

;; ## Testing
;;
;; Clojure comes bundled with its own test framework: `clojure.test`. Main
;; components are:
;;
;; - `deftest` creates a function that is _marked_ as a testcase (more on that
;; later). You can call it just like any normal function.
;; - `is` runs an assertion. It does not short-circuit, so usually all assertions
;; in a testcase are run. And `is` works with any function returning a
;; truthy/falsey value.
;;
;; Let's define the contract of our merge sort operation:

(deftest t-merge-sort
  (is (= [] (sut/merge-sort [])))
  (is (= [1] (sut/merge-sort [1])))
  (is (= [1 2 3] (sut/merge-sort [3 1 2])))
  (is (= [0 1 2 3 4] (sut/merge-sort (range 5))))
  (is (= [0 1 2 3 4] (sut/merge-sort (reverse (range 5)))))
  (is (= [0 1 2 3 4] (sut/merge-sort (shuffle (range 5))))))

;; Your editor integration will provide you with a way of running testcases
;; and showing their results. In vim-iced, this is done using `<leader>tn` to
;; execute everything in a namespace, or `<leader>tt` to execute the testcase
;; that the cursor is currently in.
;;
;; There are also test runners that can run testcases whenever something
;; changes. One of these is [kaocha](https://github.com/lambdaisland/kaocha)
;; which is set up for this repository. You can try the following on the
;; command line: `lein kaocha --watch`
;;
;; For the above example, where the same condition is checked over and over with
;; different inputs/results, there is a _tabular_ assertion format using the
;; `are` form. Actually, what we could do here, is just make sure that our
;; merge sort behaves like the built-in sorting function:

(deftest t-merge-sort-are
  (are [input] (= (sort input) (sut/merge-sort input))
       []
       [1]
       [3 1 2]
       (range 50)
       (reverse (range 50))
       (shuffle (range 50))))

;; Now that we have defined our contract, let's go back to
;; `src/playground/02_testing.clj` and create our actual function.
