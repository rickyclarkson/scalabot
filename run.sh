#!/bin/bash

~/scala-2.7.5.final/bin/scalac -classpath pircbot-1.4.6/pircbot.jar Scalabot.scala && ~/scala-2.7.5.final/bin/scala -classpath pircbot-1.4.6/pircbot.jar:. Scalabot
