# ATLAS - Analogical Reasoning & Mapping Project

COMP30880 group project — team "Tip My Fejora"

## Prerequisites
- Java 25
- `rewrite rules.txt` in the project root

## Build
````
mvn package
````

## Run
````
java -jar target/COMP30880-tip-my-fejora-1.0-SNAPSHOT.jar "rewrite rules.txt" "(exercise *athlete muscle)"
````

## Output
````
Original:
(exercise *athlete muscle)

Rewrites (1):
(by exercising (perform *athlete exercise (of muscle)))
````

## Logging
Malformed rules are logged to `atlas.log` in the project root.