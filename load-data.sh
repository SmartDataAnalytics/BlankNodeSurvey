#http://localhost:7200


# Jena Fuseki (mem / tdb2)
sparql-load --e=http://localhost:3030/mem data-1m.nt

# Graph db
sparql-load --e=http://localhost:7200/repositories/test/statements data-1m.nt


# Stardog
sparql-load --e=http://admin:admin@localhost:5820/test data-1m.nt


# Blazegraph
curl http://localhost:9999/blazegraph/namespace/kb/sparql -d 'update=LOAD+<http://localhost/data-1m.nt>'


# Virtuoso
# Note the '+' after LOAD - not sure about the correct curl magic to encode the argument
curl http://localhost:8890/sparql -d 'update=LOAD+<http://localhost/data-1m.nt>'



