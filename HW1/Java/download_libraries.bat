@echo off
SETLOCAL

REM Create lib directory if it doesn't exist
if not exist lib mkdir lib

echo Downloading required libraries...

REM Download Apache PDFBox libraries
echo Downloading Apache PDFBox libraries...
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox/2.0.27/pdfbox-2.0.27.jar -OutFile lib\pdfbox-2.0.27.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/pdfbox/pdfbox-tools/2.0.27/pdfbox-tools-2.0.27.jar -OutFile lib\pdfbox-tools-2.0.27.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/pdfbox/fontbox/2.0.27/fontbox-2.0.27.jar -OutFile lib\fontbox-2.0.27.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar -OutFile lib\commons-logging-1.2.jar"

REM Download Apache POI libraries
echo Downloading Apache POI libraries...
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/poi/poi/5.2.3/poi-5.2.3.jar -OutFile lib\poi-5.2.3.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml/5.2.3/poi-ooxml-5.2.3.jar -OutFile lib\poi-ooxml-5.2.3.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml-lite/5.2.3/poi-ooxml-lite-5.2.3.jar -OutFile lib\poi-ooxml-lite-5.2.3.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/xmlbeans/xmlbeans/5.1.1/xmlbeans-5.1.1.jar -OutFile lib\xmlbeans-5.1.1.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.21/commons-compress-1.21.jar -OutFile lib\commons-compress-1.21.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar -OutFile lib\commons-collections4-4.4.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar -OutFile lib\commons-io-2.11.0.jar"
powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.18.0/log4j-api-2.18.0.jar -OutFile lib\log4j-api-2.18.0.jar"

echo All libraries downloaded successfully.
echo You can now import the project into Eclipse IDE.

ENDLOCAL 