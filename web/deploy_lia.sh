#!/bin/sh

APPDIR=`dirname $0`;

# copy common html for packaging
cp -R Common LIA_Web/

# update servlet classes
rm $APPDIR/LIA_Web/WEB-INF/classes/org/omelogic/lia_web/*.class
cp $APPDIR/src/org/omelogic/lia_web/*.class $APPDIR/LIA_Web/WEB-INF/classes/org/omelogic/lia_web/

# update HocusLocus & LIA jars
cp $APPDIR/../src/HocusLocus.jar $APPDIR/LIA_Web/WEB-INF/lib/
cp $APPDIR/../../LIA/src/LIA.jar $APPDIR/LIA_Web/WEB-INF/lib/

# Change timestamps to fix daylight savings snafu
find LIA_Web/ -exec touch -t $(date --date='+1 hour' +%Y%m%d%H%M) '{}' \;

# Make war
jar cf LIA_Web.war -C LIA_Web .

# Undo timestamp update - set to now
find LIA_Web/ -exec touch -t $(date +%Y%m%d%H%M) '{}' \;

# Upload
scp LIA_Web.war jboss@sunfire1.eastcampus.albany.edu:/opt/jboss/jboss/server/default/deploy/

# remove copied common html
rm -R LIA_Web/Common
