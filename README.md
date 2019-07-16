## SPARQL BNode Survey and Query Rewriting System for BNode Skolemization

### Summary
This repository contains:

* Blank node survey resources, specifically
    * An analysis tool for types of bnode support in SPARQL endpoints
    * Docker-compose files for several triple stores
    * Corresponding analysis reports
* A SPARQL query rewriting proxy that virtually eliminates bnodes by rewriting them to IRIs based on their internal identifiers

### Introduction

Blank nodes (henceforth referred to as bnodes) are a feature of the Resource Description Framework (RDF).
Any resource (a more eloquent term than 'thing') must be identified by either an IRI or a bnode.
While IRIs are identifiers that are retained during data conversion and database import/export, pretty much the opposite holds for bnodes: Systems may re-label them as they see fit. In fact, bnodes may not even have any form of identifier, such as in  `[] a foaf:Person` or `[ a foaf:Agent ]`.

Blank nodes are a very convenient feature for data publishers: In many cases, blank nodes efficiently 'get a job done', such as when modeling OWL class expressions or RDF lists. Imagine the effort having to invent identifiers and ID schemes for these constructs...

Blank nodes are a huge pain for data consumers. As blank nodes may not even have an identifier, _direct_ refernces to information related to them are impossible. While it is to some extent possible to reference them _indirectly_ using queries, this approach ...

* ... is much more difficult to implement than simply performing (batch) lookups with given IRIs
* ... does not scale well (as for every bnode an individual query has to be determined)
* ... generally suffers from ambiguity issues (try to reference the bnode mentioned first in `:x :p [], []`)

#### Fret not - SPARQL Query Rewriting to the rescue!
Luckily, while RDF syntaxes may not require any form of identifier for bnodes, all triple stores internally need to allocate ids.
And many triple stores even support accessing these internal bnode ids through SPARQL extensions.
Hence, by using SPARQL query rewriting techniques, we can rewrite bnodes to IRIs based on their internal ids, thus yielding a virtual **bnode-free** RDF graph. Subsequently, applications are freed from the hassle of dealing with bnodes, while at the same time applications are empowered to deal with them.


### SPARQL BNode Skolemization Proxy Server

### Bnode Analysis Tools

#### Bnode Analysis Data Generator
A very tiny (less than 10 lines of code) data generator is used to generate datasets for subsequent bnode analysis.
Each generated record comprises two sets of triples that only differ in having an IRI or bnode in its subject position.
The bnode analysis tools compares the relative performance of lookups by IRI and bnode.
This data needs to be loaded into the SPARQL endpoint subject to bnode analysis.
Refer to [load-data.sh](load-data.sh) for approaches how to load the generated data into various triple stores.

##### Usage
```
./generate-bnode-data.sh [number of records] > data.nt
```

#### BNode Analyzer

##### Usage
```
./bnode-analyze.sh [-n triple-store-name] [-v version] sparql-endpoint-url
```

The bnode-analyze.sh tool analyzes the given sparql endpoint url by means of executing several SPARQL queries against it and using the responses togenerate an RDF report. By default, output is written to a file of name `bnode-report-${triple-store-name}-${version}.ttl`.
The triple store name and version arguments are used for informational purposes in the generated report.
Some triple stores provide SPARQL extensions for retrieving their name and version. In these cases, `-n` and `-v` can be omitted.
An error is raised if the ommitted arguments could not be inferred.





