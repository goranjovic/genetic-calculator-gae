Genetic Calculator - GAE Version

##Introduction

Genetic Calculator is genetic algorithm implemented in Clojure that finds an equation that uses given numbers and any number of basic mathematical operations to calculate the given result (or a number as close to it as possible).

Goal of this project is to illustrate use of genetic algorithms, functional programming, and new programming language Clojure on example of a mathematical game.

##About the game

Objective of the game is to use 6 numbers and any of the basic arithmetic operations (+, -, *, /) to create an equation equal to the given goal value. If such equation does not exist, goal is to find an equation whose value is as close to goal as possible.

###Additional rules:

 - Not all 6 numbers have to be used, omitting is permitted.
 - Only integer values are permitted, both in input numbers, and the result (challenged only by division operation)
 - Unary operations are not allowed, i.e. using minus operation to negate a number.

##See it in action

To see Genetic Calculator on Google App Engine in action:
http://genetic-calculator.appspot.com/

Or, for the version without flash:
http://genetic-calculator.appspot.com/noflash

##Running Locally

###Prerequisites:

Any Git client
Java version 6
Leiningen
Google App Engine SDK

###Step-by-step install and run instructions:

 - Install prerequisites
 - Checkout genetic-calculator-gae
 - lein deps; lein clean; lein compile; lein appengine-prepare
 - PATH_TO_YOUR_GAE_SDK/bin/dev_appserver.sh ./war
 - Open page http://localhost:8080/noflash in your web browser.

Note: Flash based GUI is not on github repository yet. Only noflash version can be used locally at the time.

##Project Wiki
See more info about the project on wiki
https://github.com/goranjovic/genetic-calculator-gae/wiki

##License

Copyright (C) 2010 Goran Jovic, Nevena Vidojevic

Distributed under the Eclipse Public License, the same as Clojure.

