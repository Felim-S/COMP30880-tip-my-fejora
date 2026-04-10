# ATLAS - Analogical Reasoning & Mapping Project

This is the repository for the COMP30880 group project for the team "Tip My Fejora"

## Running the Application

### Prerequisites
- Java 25 (OpenJDK)
- IntelliJ IDEA (recommended)

### Setup
1. Clone the repository
2. Open the project in IntelliJ IDEA
3. Build the project: Build > Build Project
4. Ensure `rewrite rules.txt` is in the project root

### Running from IntelliJ
In IntelliJ, open the run configuration for `atlas.Main` and set the program arguments:

````
"rewrite rules.txt" "(predicate *arg1 arg2)"
````

For example:
````
"rewrite rules.txt" "(exercise *athlete muscle)"
````

This will print the original structure followed by all rewrites.

### Running from the Command Line

Ensure Java 25 is installed and on your PATH, then from the project root:

**Compile:**
````
javac -d target/classes src/main/java/atlas/*.java
````

**Run:**
````
java -cp target/classes atlas.Main "rewrite rules.txt" "(exercise *athlete muscle)"
````

### Output
````
Original:
(exercise *athlete muscle)

Rewrites (1):
(by exercising (perform *athlete exercise (of muscle)))
````

### Logging
Any malformed rules encountered during parsing are logged to `atlas.log` in the project root.