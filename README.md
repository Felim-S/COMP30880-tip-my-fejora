# ATLAS - Analogical Reasoning & Mapping Project

COMP30880 group project — team "Tip My Fejora"

## Prerequisites
- Java 25
- `structured domains.txt` and`rewrite rules.txt` in the project root

## Build
````
mvn package
````

## Run
````
java -Xmx8g -jar target/COMP30880-tip-my-fejora-1.0-SNAPSHOT.jar [source] [target]
````
> `-Xmx8g` is required — the knowledge base file is ~430MB and exceeds the default JVM heap.

`source` and `target` override the topics in `config.properties`. (default: `source`=`priest`, `target`=`scientist`)

## Configuration
Edit `config.properties` to change the knowledge base file, rules file or source/target topics without recompiling.

## Logging
Warnings (e.g. malformed structures) are logged via `java.util.logging`.

All warnings are written to `atlas.log`