(ns playground.02-testing)

;; ## Testing in General
;;
;; Switch over to the file `test/playground/02_testing_test.clj` to get started
;; with testing in Clojure. Once you have gone through it, come back here!

;; ## Our first function
;;
;; Merge sort is a recursive sorting algorith and works like this:
;;
;; 1. If the sequence has less than two elements it's already sorted.
;; 2. Otherwise, split the sequence into two parts and apply merge sort to each.
;; 3. Loop over both results:
;;    3.1. If the first one is empty, return the second.
;;    3.2. If the second one is empty, return the first.
;;    3.3. Otherwise, compare the heads of the sequences and take the smaller
;;         one as the next element of the result.
;;    3.4. Loop, removing the chosen element from the respective sequence.
;;
;; It's hard to show how to build up the function in a static document like
;; this, but you'll usually start building a large function, then split it up
;; into smaller parts.
;;
;; Well, let's start with step 2: splitting a sequence into two parts:

(defn- split-sequence
  [sq]
  (split-at (quot (count sq) 2) sq))

;; As always, you can try them out right here in your editor:

(split-sequence [1 2 3])
(split-sequence [1 2])

;; Nice! Now, let's merge two sorted sequences together by always taking the
;; smaller element from each. This is a recursive solution that - in my
;; opinion - reads quite nicely:

(defn- merge-sorted-sequences
  [a b]
  (let [xa (first a)  ;; `first` will not complain if the seq is empty,
        xb (first b)] ;; it will just return `nil`.
    (cond (empty? a) b
          (empty? b) a
          (< xa xb)  (cons xa (merge-sorted-sequences (rest a) b))
          :else      (cons xb (merge-sorted-sequences a (rest b))))))

;; All you need to know here is that `let` creates local bindings (think: local
;; variables), and that `cond` - as a way to combat nested `if`-statements -
;; takes condition/result pairs, i.e. if `(empty? a)` return `b`.
;;
;; Time to try it out:

(merge-sorted-sequences [0 2] [1 3])
(merge-sorted-sequences [1 2] [0 3])

;; Note that, for large sequences, it will throw a `StackOverflowError`:

(comment
  ;; ^--- This effectively removes the form from the namespace but let's your
  ;;      editor still treat it as code. This way, you can still use structural
  ;;      editing and inline evaluation (which might not always be possible
  ;;      with normal comments, depending on your editor integration).
  (merge-sorted-sequences (range 1E6) (range 1E6)))

;; One way to solve that issue is to make the returned sequence lazy but since
;; we haven't talked about laziness yet, let's look at the tail-recursive
;; version using an accumulator instead:

(defn- merge-sorted-sequences*
  [a b result]
  (let [xa (first a)  ;; `first` will not complain if the seq is empty,
        xb (first b)] ;; it will just return `nil`.
    (cond (empty? a) (concat result b)
          (empty? b) (concat result a)
          (< xa xb)  (recur (rest a) b (conj result xa))
          :else      (recur a (rest b) (conj result xb)))))

;; One particularity here is that Clojure does not have tail-call-optimization,
;; so if you were to just call the function recursively you'd still end up with
;; a `StackOverflowError`. The `recur` form is what needs to be used instead,
;; telling the compiler to call the current function/loop again without creating
;; another element on the stack.

(merge-sorted-sequences* [0 2] [1 3] [])
(merge-sorted-sequences* [1 2] [0 3] [])
(dorun (merge-sorted-sequences* (range 1E6) (range 1E6) []))

;; `dorun` in the above example will realise the whole sequence without
;; returning it. We don't want to flood your editor with a 2M-element sequence,
;; right?
;;
;; Anyhow, once we have the building blocks, implementing the sorting is easy:

(defn merge-sort
  [sq]
  (if (<= (count sq) 1)
    sq
    (let [[a b] (split-sequence sq)] ;; Hey, look, destructuring!
      (merge-sorted-sequences*
        (merge-sort a)
        (merge-sort b)
        []))))

;; Let's try it:

(def unsorted-data (shuffle (range 20)))
(merge-sort unsorted-data)

;; Beautiful.
