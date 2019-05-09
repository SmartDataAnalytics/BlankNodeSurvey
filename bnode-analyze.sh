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

if [ -z "$DBMS_VERSION" ]; then DBMS_VERSION=`echo "$JSON" | jq -r ".[].s.dbmsVersion[]"`; fi
if [ -z "$DBMS_ID" ]; then DBMS_ID=`echo "$JSON" | jq -r ".[].s.dbmsId[]"`; fi


echoerr -e "DBMS Id: $DBMS_ID\nDBMS Version: $DBMS_VERSION\nURL: $REMOTE\n"


# Request missing mandatory arguments
if [ -z "$DBMS_VERSION" ]; then echoerr "[ERROR] dbms version is missing."; fi
if [ -z "$DBMS_ID" ]; then echoerr "[ERROR] dbms name is missing."; fi
if [ -z "$REMOTE" ]; then echoerr "[ERROR] Remote endpoint url is missing"; fi



#REMOTE=http://localhost:8890/sparql sparql-integrate model.sparql spo.sparql
#REMOTE="$1" STORE_NAME="$2" STORE_VERSION="$3" sparql-integrate model.sparql spo.sparql



