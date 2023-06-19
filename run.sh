#!/bin/bash
javac --module-path lib/junit-4.11.jar --source-path java-solutions/ java-solutions/info/kgeorgiy/ja/smirnov/i18n/TextStatistics.java
java -cp java-solutions/:lib/junit-4.11.jar info/kgeorgiy/ja/smirnov/i18n/TextStatistics $1 $2 $3 $4