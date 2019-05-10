#!/bin/bash

echoerr() { echo "$@" 1>&2; }


# Support for simple command line args
# Source: http://mywiki.wooledge.org/BashFAQ/035#getopts
# A POSIX variable
OPTIND=1         # Reset in case getopts has been used previously in the shell.

while getopts "hv:n:" opt; do
    case "$opt" in
        h)
            echoerr "$usage"
            exit 0
            ;;
        v)  DBMS_VERSION="$OPTARG"
            ;;
        n)  DBMS_ID="$OPTARG"
            ;;
    esac
done

shift $((OPTIND-1))
[ "$1" = "--" ] && shift

export REMOTE="$1"

# Try to determine missing values automatically
JSON=`sparql-integrate check-version-*.sparql <(echo 'SELECT ?s { ?s a sd:ServiceDescription }') --jq`

if [ -z "$DBMS_ID" ]; then DBMS_ID=`echo "$JSON" | jq -r ".[].s.dbmsId[]"`; fi
if [ -z "$DBMS_VERSION" ]; then DBMS_VERSION=`echo "$JSON" | jq -r ".[].s.dbmsVersion[]"`; fi


# Print settings to enable sanity check
echoerr "-------------------------------------------------------"
echoerr -e "DBMS Id: $DBMS_ID\nDBMS Version: $DBMS_VERSION\nURL: $REMOTE\n"


# Request missing mandatory arguments
error=0
if [ -z "$DBMS_ID" ]; then error=1; echoerr "[ERROR] dbms name is missing."; fi
if [ -z "$DBMS_VERSION" ]; then error=1; echoerr "[ERROR] dbms version is missing."; fi
if [ -z "$REMOTE" ]; then error=1; echoerr "[ERROR] Remote endpoint url is missing"; fi

[ $error -eq 0 ] || exit $error


#export REMOTE
export DBMS_ID
export DBMS_VERSION


#REMOTE=http://localhost:8890/sparql
sparql-integrate model.sparql spo.sparql
#REMOTE="$1" STORE_NAME="$2" STORE_VERSION="$3" sparql-integrate model.sparql spo.sparql


# Obtain a bnode report - that is an rdf document which for a specific bnode holds information
# about the results of different approaches for obtaining the internal blank node identifier
BNODE_REPORT=`sparql-integrate select-bnode2.sparql <(echo 'SELECT ?s { ?s a eg:BnodeReport }') --jq`

# Sanity check to detect empty rdf graphs: The report must refer to a blank node
# TODO impl


BNODE_ID_HPAFN=`jq -r '.[].s.hpAfn[]' <<< "$BNODE_REPORT"`
BNODE_PLAIN=`jq -r '.[].s.node[].id' <<< "$BNODE_REPORT"`


# Prepend '_:' to the bnode id
BNODE_HPAFN=${BNODE_ID_HPAFN:+_:$BNODE_ID_HPAFN}

BNODE=${BNODE_HPAFN:-${BNODE_PLAIN:-foo}}

export BNODE

#echo "GOT $BNODE_ID_HPAFN --- $BNODE_HPAFN ---  $BNODE"


#BNODE_ID=`sparql-integrate select-bnode.sparql --jq | jq -r '.[].s.id'`

if [ -z "$BNODE" ]; then error=1; echoerr "[ERROR] received an empty; expected a blank node id from the given service. Is data loaded?"; fi
[ $error -eq 0 ] || exit $error


echoerr "[INFO] Retrieved blank node id: $BNODE"


sparql-integrate test-bnode-lookups.sparql spo.sparql





