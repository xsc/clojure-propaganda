(ns playground.03-webservices-part1)

;; ## HTTP Servers are Functions!
;;
;; Honestly, think about it: All an HTTP server does is take a request (which
;; is data), performs some kind of logic, and returns a response (which, also,
;; is data). It's often not a _pure_ function (i.e. one that does not have side-
;; effects - we might want to change something in a database after all), but a
;; function, nonetheless.
;;
;; The core of a simple "Hello World!" service is thus basically:

(defn hello-world-handler
  [_request]
  {:status 200
   :body   "Hello World!"})

;; At some point in the very beginning of Clojure web development, people
;; agreed on what constitutes a valid request/response map. Cleverly, they wrote
;; it down as the so-called _ring spec_:
;;
;;   https://github.com/ring-clojure/ring/blob/master/SPEC
;;
;; Basically, all Clojure web development relies on this spec - which has done
;; wonders for compatibility and interoperability of different libraries,
;; allowing you to build exactly the web stack you need out of battle-tested
;; components. (On the flip side, because it's very much encouraged to mix and
;; match, the selection of _web development frameworks_ is not as varied as
;; it might be in other language ecosystems.)
;;
;; I'm getting a bit ahead of myself, sorry for that. We haven't even done
;; anything useful yet, have we?

;; ## Connecting to the World
;;
;; What we need is something that takes actual HTTP requests, turns them into
;; ring-compliant Clojure maps and passes them to our `hello-world-handler`.
;; Then, when provided with the response data, it needs to make sure there is an
;; actual HTTP response sent to the original client.
;;
;; Luckily, there is a huge variety of _ring adapters_, all with their own
;; intricacies, focus and power. There are some written from scratch (http-kit),
;; but mostly they are wrapping existing, proven and powerful, server
;; implementations. You can find a comprehensive list here:
;;
;;  https://github.com/ring-clojure/ring/wiki/Third-Party-Libraries#adapters
;;
;; For our purposes, we'll use [aleph](https://github.com/clj-commons/aleph)
;; which is built upon Netty.

(require '[aleph.http :as http])
;; ^--- Usually, you'll have this as a `:require` in the `ns` declaration at the
;;      top of the file.

(comment
  (defonce hello-world-server
    ;; ^--- 'defonce' prevents re-evaluation when reloading the namespace
    (http/start-server hello-world-handler {:port 14001})))

;; Now, visit `http://localhost:14001` and you should be greeted with the
;; expected message. Celebrate!
;;
;; But not so fast! Try making a change to `hello-world-handler` and evaluating
;; it; you'll notice that your endpoint continues to send the same response,
;; ignoring your changes. That's not the interactive development experience
;; we're used to, so let's clean up and improve our approach!

(comment
  (.close hello-world-server))

;; ## What's the issue?
;;
;; When we called `http/start-server` we passed the `hello-world-handler`
;; directly. This will use the _value contained in the variable at that point in
;; time_ - subsequent changes to the function won't be propagated. Try it out:

(defn f [x] (+ x 2))

(def f-as-value f)
;; ... make some changes to `f` ...
[(f 1) (f-as-value 1)]

;; We need to introduce some kind of indirection, so that a lookup of the
;; function happens. There are two ways of achieving that.
;;
;; First, calling the function inside another one - this will rely on the name
;; and thus perform a lookup:

(def f-nested (fn [request] (f request)))
;; ... make some changes to `f` ...
[(f 1) (f-nested 1)]

;; Alternatively, you can use a reference to the _var_ itself. We haven't really
;; gone into that, but when you use `def` or `defn` you're creating a named
;; container for a value or function. Clojure nows to automatically retrieve
;; the value of such a container when used as an operator in a function call:

(def f-var #'f)
;; ... make some changes to `f` ...
[(f 1) (f-var 1)]

;; Awesome, now we know what to do!

;; ## Back to Business
;;
;; It might be interesting to see what's contained in a request, so let's build
;; a handler that returns it in the body:

(defn handler
  [request]
  {:status 200
   :body   (pr-str request)})

;; And now, as we discussed, let's make sure we can make changes to `handler` on
;; the fly.

(defonce server
  (http/start-server #'handler {:port 14002}))

;; Play around with `handler` and see changes appear in the browser on reload.
;; No server restarts required!
;;
;; Also, play around with the URL, adding a path, adding a query string, etc...
;; Or take 'curl' and send a POST request. Each change causes a part of the
;; request map to update.
;;
;; We can use this to implement our own very basic routing logic, dispatching to
;; different handlers (which, themselves, are just more functions):

(defn- pi-handler
  [_]
  {:status  200
   :headers {"content-type" "application/json"}
   :body "  { \"pi\": 3.141592 }"})

(defn- echo-handler
  [request]
  {:status 200, :body (pr-str request)})

(defn- not-found-handler
  [_]
  {:status 404, :body "Not Found!"})

#_:clj-kondo/ignore ;; this pacifies my linter, ignore it :)
(defn handler
  [request]
  ;; Hey, look, literal dispatch, like `switch`:
  (case [(:request-method request) (:uri request)]
    [:get "/"]     (pi-handler request)
    [:get "/echo"] (echo-handler request)
    (not-found-handler request)))
;;  ^--- The last form will be used if nothing else matches.

;; These are the very basics of building web services in Clojure. It's very
;; functional and declarative, and there are many good things that result from
;; this approach. And we'll get into them next!
