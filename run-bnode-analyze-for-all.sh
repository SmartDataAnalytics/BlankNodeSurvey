# GraphDB
./bnode-analyze.sh -n graphdb-free -v 8.9.0 http://localhost:7200/repositories/test

#curl http://localhost:7200/repositories/test/statements -d 'update=LOAD <http://localhost/data-1m.nt>'


# Virtuoso
./bnode-analyze.sh http://localhost:8890/sparql


# Blazegraph
./bnode-analyze.sh -n blazegraph -v 2.1.5 http://localhost:9999/blazegraph/namespace/kb/sparql

# Stardog
./bnode-analyze.sh -n stardog -v 6.1.3 http://anonymous:anonymous@localhost:5820/test/query

# Jena / In-Memory
./bnode-analyze.sh -n jena-fuseki-mem -v 3.10.0 http://localhost:3030/mem/


# Jena / TDB
./bnode-analyze.sh -n jena-fuseki-tdb2 -v 3.10.0 http://localhost:3030/tdb2/



# RDF4J - Server
#http://172.29.0.2:8080/rdf4j-server
#http://172.29.0.2:8080/rdf4j-workbench
#http://172.29.0.2:8080/rdf4j-server/repositories/mem
./bnode-analyze.sh -n rdf4j -v 2.5.1  http://localhost:8080/rdf4j-server/repositories/mem 

# RDF4J - 


# rdflib



