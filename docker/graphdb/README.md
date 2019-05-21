Graphdb uses a magic property:
http://graphdb.ontotext.com/documentation/standard/query-behaviour.html#how-to-access-internal-identifiers-for-entities

select * { ?x a <http://www.example.org/Test> . ?xd
         <http://www.ontotext.com/owlim/entity#id> ?z BIND(STR(?x) AS ?y)}
