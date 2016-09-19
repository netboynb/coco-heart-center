#!/bin/sh
#JAVA_HOME=/usr/java/jdk1.8.0_66
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_66.jdk/Contents/Home
APP_NAME="soa-heart-center"
JAVA_OPTS="-Ds_name=$APP_NAME"
##MY_HOME=/home/admin/$APP_NAME
CURRENT_DIR=`pwd`
run_app() {
        #cd $MY_HOME
        echo "run dir `pwd`"
		
        JAVA_OPTS="$JAVA_OPTS -Xms512m -Xmx512m"

        JAVA_OPTS="$JAVA_OPTS -Xmn256m"
        #JAVA_OPTS="$JAVA_OPTS -XX:PermSize=96m -XX:MaxPermSize=256m -Xss4m"
        JAVA_OPTS="$JAVA_OPTS -XX:SurvivorRatio=6"
        JAVA_OPTS="$JAVA_OPTS -XX:MaxTenuringThreshold=2"

        JAVA_OPTS="$JAVA_OPTS -XX:+UseCompressedOops"
        JAVA_OPTS="$JAVA_OPTS -XX:+DoEscapeAnalysis"

        JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
        JAVA_OPTS="$JAVA_OPTS -XX:SoftRefLRUPolicyMSPerMB=0"
        JAVA_OPTS="$JAVA_OPTS -XX:-UseAdaptiveSizePolicy"
        JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC -XX:+UseParNewGC"
        JAVA_OPTS="$JAVA_OPTS -XX:+CMSParallelRemarkEnabled"
        JAVA_OPTS="$JAVA_OPTS -XX:+CMSClassUnloadingEnabled"
        JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSCompactAtFullCollection"
        JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 -XX:+CMSScavengeBeforeRemark"

        JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC"
        JAVA_OPTS="$JAVA_OPTS -verbose:gc -Xloggc:./logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGC"
        #JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCApplicationStoppedTime -XX:PrintFLSStatistics=1"
        #JAVA_OPTS="$JAVA_OPTS -XX:ParallelGCThreads=8"

        JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
        JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"
        JAVA_OPTS="$JAVA_OPTS -Dsun.net.client.defaultConnectTimeout=10000"
        JAVA_OPTS="$JAVA_OPTS -Dsun.net.client.defaultReadTimeout=30000"

        #JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000 "

        echo "$JAVA_HOME/bin/java $JAVA_OPTS -cp .:config/ -Dlogback.configurationFile=config/logback.xml -Djava.ext.dirs=lib com.carkey.heart.RunMain &"
        $JAVA_HOME/bin/java $JAVA_OPTS -cp .:config/ -Dlogback.configurationFile=config/logback.xml -Djava.ext.dirs=lib com.carkey.heart.RunMain &
}

stop() {
        echo "kill `ps -ef | grep java | grep $APP_NAME | awk '{print $2}'` ..."
        kill `ps -ef | grep java | grep $APP_NAME | awk '{print $2}'`
}

my_pid() {
	echo "`ps -ef | grep java | grep $APP_NAME | awk '{print $2}'`"
}

my_jstat() {
	echo "$JAVA_HOME/bin/jstat -gcutil `ps -ef | grep java | grep $APP_NAME | awk '{print $2}'` $1"
	$JAVA_HOME/bin/jstat -gcutil `ps -ef | grep java | grep $APP_NAME | awk '{print $2}'` $1
}

my_ps() {
        echo "ps -ef | grep java | grep $APP_NAME"
        ps -ef | grep java | grep $APP_NAME
}

help() {
        echo "run.sh [args]"
        echo "args has:"
        echo ""
        echo "  ps              ps -ef my app"
        echo "  jstat           run.sh jstat [time interval ms]"
        echo "  pid             print my process id"
        echo ""
        echo "  stop            stop my app"
        echo "  start           start my app"
        echo "                  help"
}

case "$1" in
        ps)
			my_ps
        ;;
        pid)
			my_pid
		;;
        jstat)
			my_jstat $2
        ;;
        stop)
			stop
        ;;
        start)
			run_app
        ;;
        help)
			help
        ;;
        *)
			help
esac