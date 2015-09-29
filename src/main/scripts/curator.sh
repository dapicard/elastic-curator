#!/bin/sh
#
# Init file for ElasticSearch Curator
#
# chkconfig: 2345 60 25
# description: ElasticSearch Curator

. /etc/rc.d/init.d/functions

ESC_BASE="/opt/es-curator"
ESC_USER="curator"
lockfile=/var/lock/subsys/es-curator

case "$1" in
start)
  daemon --user=$ESC_USER java -DlogPath=/var/data/elk-work/log -jar $ESC_BASE/lib/elasticsearch-curator-1.0-SNAPSHOT.jar > /var/data/elk-work/log/curator.out 2>&1 &
  rm -f $lockfile
  touch $lockfile
;;

stop)
  rm -f $lockfile
  ps ax -opid,cmd | grep ".*java.*elasticsearch-curator" | grep -Ev 'grep' | awk '{print $1}' | xargs --no-run-if-empty kill -9
;;


restart)
  $0 stop
  $0 start
;;

status)
  gfpid=`ps ax -opid,cmd | grep ".*java.*elasticsearch-curator.*" | grep -Ev 'grep' | awk '{print $1}'`;
  if [ -z "$gfpid" ] ; then
      echo "Curator is NOT running. Consider to restart it."
      exit 1
  else
      echo "Curator (pid $gfpid) is running"
  fi
;;

*)
echo "Usage: $0 {start|stop|restart|status}"
exit 1
esac
exit 0