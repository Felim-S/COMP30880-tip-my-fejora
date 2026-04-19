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
java -Xmx8g -jar target/COMP30880-tip-my-fejora-1.0-SNAPSHOT.jar [target]
````
> `-Xmx8g` is required — the knowledge base file is ~430MB and exceeds the default JVM heap.

`target` overrides the topic in `config.properties` (default: `apple`).

## Configuration
Edit `config.properties` to change the knowledge base file, rules file, target topic, beta parameter, or results limit
without recompiling.

## Logging
Warnings (e.g. malformed structures) are logged via `java.util.logging`.