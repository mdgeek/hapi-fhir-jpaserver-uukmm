#!/bin/bash
#

function getProperty {
   PROP_KEY=$2
   PROP_VALUE=`cat $1 | grep "$PROP_KEY=" | cut -d'=' -f2`
   echo $PROP_VALUE
}


set -e

if [[ "$1" == "" ]]
then
  echo "You must specify the location of the build property file:"
  echo "./build.sh <property file path>"
  exit 1
fi

echo Generating fhir-server-demo build...
mvn clean package -Dbuild-properties=$1
fhir_version=$(getProperty $1 "fhir-version-lc")
war_file=fhir-server-$fhir_version-demo.war
cp target/ROOT.war target/$war_file
echo Copied generated war file to $war_file.


