(ns playground.01-intro
  (:require [clojure.repl :refer [doc source]]))

;; ## REPL = Read Eval Print Loop
;;
;; - Read - Parse the expression
;; - Eval - Evaluate to get a result value
;; - Print - Print it back to the console
;; - Loop - Back to the beginning.
;;
;; There is real power in connecting your editor to the REPL! You are
;; no longer switching between windows or splits, you're not restarting
;; anything, just getting results inline.
;;
;; Programming becomes more of a conversation:
;;
;; - "What does this result in?"
;; - "Does this work?"
;; - "How about this?"
;;
;; There are Clojure integrations/plugins for many editors. I'm using
;; [vim-iced](https://github.com/liquidz/vim-iced).


;; ## Introduction
;;
;; This is just a very, very basic introduction to parts of the language. Let's
;; take away the shock of seeing parentheses or operators in weird places.
;;
;; Afterwards, we can decide what's worth exploring next, when we're able to
;; focus on the problem and are no longer distracted by syntax.

;; ### Basic Data Types
;;
;; These evaluate to themselves.

1        ;; integers
1.0      ;; doubles
3/8      ;; ratios
"str"    ;; strings
:one     ;; keywords
'one     ;; symbols
true     ;; boolean
nil      ;; null

;; You'll rarely use symbols, I'd say, but you'll see keywords everywhere. They
;; are Clojure's first choice when it comes to representing elements of a fixed
;; set of symbolic values. Think: enums, map keys, etc...
;;
;; Note that there are more numeric types - but we'll cross that bridge if ever
;; necessary.

;; ### Functions
;;
;; Here are the _operators in weird places_ I've mentioned. When Clojure sees
;; a _form_ with parentheses it will consider its first element the operator,
;; and everything else operands/parameters.
;;
;; For example, simple multiplication will look like this:

(* 3 2)

;; Note that, because my editor is connected to the REPL, I can get the result
;; of that form right here, and instantly. (It should be `6`, in case you are
;; wondering.)
;;
;; Now, since I'm doubling numbers a lot in my line of work, I think it's worth
;; giving a name to this piece of logic:

(defn make-realistic
  [estimate]
  (* estimate 2))

;; Again, `defn` will be considered the protagonist/operator of this form. There
;; is something special about it - it's a macro - but this is a topic for
;; another day. For now, it's enough to know that this defines a function
;;`make-realistic` that has one parameter named `estimate`, and a function body
;; consisting of the aforementioned doubling operation.

(make-realistic 3)

;; There is no `return` in Clojure - the last value of the chosen code path is
;; what is returned. Consider a similar function:

(defn make-realistic-but-trust-estimate-for-easy-tasks
  [estimate]
  (if (<= estimate 2)
    estimate
    (* estimate 2)))

(make-realistic-but-trust-estimate-for-easy-tasks 2)
(make-realistic-but-trust-estimate-for-easy-tasks 5)

;; Here, we can end up in one of two code paths, which both will return the
;; value of their last (and only) expression. Basically, any `if` in Clojure
;; behaves like the ternary operator: `x <= 2 ? x : x * 2`.
;;
;; Speaking of conditionals, there are specialized versions, like `if-not` and
;; `when` and `when-not`, as well as syntactic sugar for literal matches
;; (`case`) or more than two paths (`cond`, `condp`). I recommend you check out
;; their documentation, especially if you realise you're nesting `if`-statements
;; a lot.
;;
;; By the way, most editor integrations will allow you to access a functions
;; docstring inline - e.g. in vim-iced it's done by pressing `K` while the
;; cursor is over the symbol in question - which will rely again on the REPL
;; and the following function:

(doc cond)

;; You could even peek at the source code:

(source cond)

;; Anyways, we got side-tracked. Let's get back to what is the core of Clojure:
;; data.

;; ### Sequences
;;
;; There are three main sequence types, in order of popularity:

[1 2 3]      ;; vectors
#{1 2 3}     ;; sets
(list 1 2 3) ;; lists (you rarely use them explicitly, tbh)

;; (There is also maps - key/value pairs - but that's discussed in the next
;; section.)
;;
;; There are tons and tons of sequence functions, for example for inspection:

(first #{1 2 3})
(rest [1 2 3])
(nth [1 2 3] 2)

;; Or for manipulation:

(def data [1 2 3 4 5]) ;; Hey, look, a variable!
(cons 0 data) ;; always prepends to the front
(conj data 6) ;; depends on the sequence! (vector -> append, list -> prepend)

;; And the crown jewel: SEQUENCE. ITERATION. FUNCTIONS.

(filter odd? data) ;; only keep matching elements
(remove odd? data) ;; remove all matching elements
(map inc data)     ;; apply a function to every element

;; Stop the presses! Have a look at the original `data` variable:

data

;; It has not changed - which is because of the _immutability_ of Clojure's data
;; structures: Every operation will create a new sequence and leave the inputs
;; untouched. It will go about it in a smart way and share common values rather
;; than doing a deep copy of the input, but you never run the risk of changing
;; something you don't want to change.
;;
;; Resume the presses!

(reduce + data)    ;; classic fold operation
(reduce + 5 data)  ;; classic fold operation, initial value for the accumulator

(reduce
  (fn [acc x]         ;; Hey, look, an anonymous function!
    (+ acc (* x x)))  ;; A nested function call, meaning: acc + x^2
  0
  data)

;; Clojure can get concise if it wants to. Or code-golfy, depending on your
;; stance. This is the same as above, with syntactic sugar for anonymous
;; functions (`%1` = first parameter, `%2` = second parameter, ...):

(reduce #(+ %1 (* %2 %2)) 0 data)

;; I'd suggest to only use this form when there is only one parameter. In which
;; case you can use `%` instead of `%1`. For example:

(map #(+ % 5) data)

;; Utilities galore, btw. If you want to do something with a sequence, chances
;; are that Clojure has a function for it:

(take 2 data)
(drop 2 data)
(drop-while odd? data)
(take-while odd? data)
(split-at 2 data)
(partition 2 data)
(partition-all 2 data)
(frequencies data)

;; And so. Many. More.

;; ### Maps
;;
;; Key/value pairs, objects, hash maps, etc... You know this kind of data
;; structure:

{:a "a", :b "b"} ;; We tend to use keywords for map keys
{"a" 1, "b" 2}   ;; but we don't have to.

;; Get and put are, well, get and assoc in Clojure:

(def name->number {:one 1, :two 2, :three 3})
(get name->number :two)
(assoc name->number :four 4)

;; Without stopping the presses this time, you'll see that the original map has
;; not changed. Immutability is to blame/thank.
;;
;; Keywords are functions, btw, so there is a nice-looking way to lookup values:

(:two name->number)

;; This is, for one example, very useful to concisely get nested values (e.g.
;; from the result of an API call):

(map :name [{:name "Yannick"} {:name "Luke"} {:name "Leia"}])

;; Maps themselves? Also functions:

(name->number :two)
(map name->number [:one :one :two])

;; If you want to rip a map apart (you monster) this is how:

(keys name->number)
(vals name->number)

;; Finally, maps can be considered a sequence of key and value pairs, which
;; means that all the sequence iteration functions we looked at above will
;; work on maps too:

(seq name->number)       ;; converts map to seq (usually done automatically)
(map first name->number) ;; similar to `keys`

(filter
  (comp even? second) ;; Hey, look, function composition!
  name->number)

;; Enough, now. This should make you a bit more comfortable when looking
;; at Clojure code. There is still a lot more to discover.
