# Generating the target:
mvn clean package
# Find the path of the jar:
path=$(find ./target -name *.jar)
# Find the version in the `pom.xml`:
version=$(xpath -q -e '/project/version/text()' pom.xml) # sudo apt install libxml-xpath-perl
artifactId=$(xpath -q -e '/project/artifactId/text()' pom.xml)
groupId=$(xpath -q -e '/project/groupId/text()' pom.xml)
# Install the jar locally via Maven:
mvn install:install-file -Dfile=$path -DgroupId=$groupId -DartifactId=$artifactId -Dpackaging=jar -Dversion=$version -DgeneratePom=true