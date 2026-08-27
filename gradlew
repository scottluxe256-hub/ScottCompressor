#!/bin/sh
# Gradle startup script for POSIX systems

PRG="$0"
while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done
SAVED="`pwd`"
CDPATH= cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/libexec/java" ] ; then
        JAVACMD="$JAVA_HOME/libexec/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
else
    JAVACMD="java"
fi

exec "$JAVACMD" "-classpath" "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
