#!/bin/bash

# Create lib directory if it doesn't exist
mkdir -p lib

echo "Downloading required libraries..."

# Download Apache PDFBox libraries
echo "Downloading Apache PDFBox libraries..."
curl -L -o lib/pdfbox-2.0.27.jar https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox/2.0.27/pdfbox-2.0.27.jar
curl -L -o lib/pdfbox-tools-2.0.27.jar https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox-tools/2.0.27/pdfbox-tools-2.0.27.jar
curl -L -o lib/fontbox-2.0.27.jar https://repo1.maven.org/maven2/org/apache/pdfbox/fontbox/2.0.27/fontbox-2.0.27.jar
curl -L -o lib/commons-logging-1.2.jar https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar

# Download Apache POI libraries
echo "Downloading Apache POI libraries..."
curl -L -o lib/poi-5.2.3.jar https://repo1.maven.org/maven2/org/apache/poi/poi/5.2.3/poi-5.2.3.jar
curl -L -o lib/poi-ooxml-5.2.3.jar https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml/5.2.3/poi-ooxml-5.2.3.jar
curl -L -o lib/poi-ooxml-lite-5.2.3.jar https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml-lite/5.2.3/poi-ooxml-lite-5.2.3.jar
curl -L -o lib/xmlbeans-5.1.1.jar https://repo1.maven.org/maven2/org/apache/xmlbeans/xmlbeans/5.1.1/xmlbeans-5.1.1.jar
curl -L -o lib/commons-compress-1.21.jar https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.21/commons-compress-1.21.jar
curl -L -o lib/commons-collections4-4.4.jar https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar
curl -L -o lib/commons-io-2.11.0.jar https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar
curl -L -o lib/log4j-api-2.18.0.jar https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.18.0/log4j-api-2.18.0.jar

echo "All libraries downloaded successfully."
echo "You can now import the project into Eclipse IDE." 