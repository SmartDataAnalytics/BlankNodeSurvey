#!/bin/bash

MAX=${1:-10}

echo "@prefix eg: <http://www.example.org/> ."
for i in `seq 1 $MAX`; do
  echo "[] a eg:Bnode ; eg:bid $i ."
  echo "eg:iri$i a eg:Iri ; eg:iriId $i ."
done



