export JAVA_HOME="/usr/lib/jvm/java-10-openjdk-amd64"
# jooq needs jaxb module
export MAVEN_OPTS="--add-modules java.xml.bind"
mvn jooby:run -Dapplication.debug=5001 -DlogLevel=debug