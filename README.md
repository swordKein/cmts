# cmts

Run Script. <br><br>
nohup java -jar -Xmx4096m -Xss512k -Dspring.profiles.active="dev_crawl" -Dfile.encoding=utf8 -XX:+DisableExplicitGC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCDateStamps -Xloggc:/home/daisy/logs/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=2000k  -XX:+PrintTenuringDistribution -XX:+PrintGCCause /home/daisy/.jenkins/workspace/cmts/target/cmts-1.0-spring-boot.jar &


Contents Meta Tagging System
