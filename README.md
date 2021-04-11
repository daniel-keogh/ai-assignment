# AI Assignment

Autonomous Computer-Controlled Game Characters.

## Description

A JavaFX program that uses fuzzy logic & a neural network to control a set of characters that are moving randomly through a game model.

## Build

### Requirements

The required JARs are already included in the `lib/` directory.

- [Java-FX 11](https://openjfx.io/).
- [Encog Java 3.4](https://github.com/jeffheaton/encog-java-core).
- [jFuzzyLogic](http://jfuzzylogic.sourceforge.net/html/index.html).

### Create JAR File

Run the following from inside the `bin/` directory.

```sh
$ jar -cf game.jar *
```

## Running

```sh
$ java --module-path .;/path/to/your/javafx-sdk-11.0.2/lib;/path/to/your/encog-3.4/;/path/to/your/jfuzzylogic --module gmit.software/ie.gmit.sw.ai.Runner
```
