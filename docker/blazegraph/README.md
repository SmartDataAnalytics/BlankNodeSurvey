
* STR(?x) yields a type error
* BIND(IF(TYPE_ERROR, true, false) AS ?x) leaves ?x unbound due to invocation of 'IF' yielding a type error (so neither true nor false)
* BIND(IF(!TYPE_ERROR, true, false) AS ?x) also leaves ?x unbound due to type error


select * {
  BIND(if(abs(''), 'got true', 'got false') AS ?x)
  BIND(if(!bound(?x), 'now true', 'now false') AS ?y)
}

bound(?x) -> blazegraph does not allow an expression here!


select * { ?s ?p ?o . FILTER(isBLANK(?s)) BIND(IF(!STR(?s), 'got true', 'got false') AS ?x) }


CLI Usage examples:

```
curl '172.27.0.2:9999/blazegraph/namespace/kb/sparql' -d 'update=INSERT DATA { _:b0 <p> <o> }'
curl '172.27.0.2:9999/blazegraph/namespace/kb/sparql' -d 'query=SELECT * { ?s ?p ?o } LIMIT 10'
```

Service description supported:

curl -LH 'accept:application/x-turtle' '172.27.0.2:9999/blazegraph/namespace/kb/sparql'


https://wiki.blazegraph.com/wiki/index.php/DataMigration#Blank_nodes
>> Bigdata also supports a "told bnodes" option

https://jira.blazegraph.com/browse/BLZG-220



