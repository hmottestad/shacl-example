# shacl-example
Shacl example for RDF4J

# How to build

RDF4J 2.3 has not been released yet, so you will have to build the source code yourself. 

In a new directory call

```bash

git clone https://github.com/eclipse/rdf4j-storage.git .
mvn install -DskipTests 

```

To run the SHACL example code, got back to the shacl-example directory:
```bash

mvn install
mvn exec:java

```
 
To play around with the validation. Try changing what data gets added in the transaction. There are some comments to help you out. 
