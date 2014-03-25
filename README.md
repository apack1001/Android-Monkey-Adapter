AndroidMonkeyAdapter
====================
Introduction
--------------------

- Install Android application, run Monkey testing job, analyze and classify crash automatically
- HTML-format report supported
- Multiple platform supported(Windows, Mac, Linux)
- Jenkins platform supported 

Usage
--------------------
Usage of Android Monkey Adapter runner
``` sh
usage: java -jar jarfile [-options/ --options]...
            
 -d,--device-id <arg>         the id list of the devices which is need to
                              run monkey test
 -h,--help                    Output help information!
 -n,--pkg-name <arg>          package name of this appliacation
 -p,--pkg-path <arg>          point to an Android application path in the
                              storage
 -r,--user-name <arg>         user name of this job owner
 -s,--single-duration <arg>   expected one monkey job duration (hour)
 -t,--series-duration <arg>   expected total monkey jobs duration (hour)
 -u,--unlock-cmd-path <arg>   point to an unlock script path which must be
                              standalone executable
 -v,--pkg-version <arg>       version of this application

```
Usage of Android Monkey Adapter analyzer
``` sh
usage: java -jar jarfile [-options/ --options]...

options are as below:
 -b,--bugreport-log-file-name <arg>   File name of bugreport log.
 -d,--duration <arg>                  Expected uration of single monkey
                                      job(8 hours or 4.5 hours).
 -h,--help                            Output help information!
 -l,--logcat-log-file-name <arg>      File name of logcat log.
 -m,--monkey-log-file-name <arg>      File name of monkey log.
 -n,--package-name <arg>              Package name of an Android
                                      Application.
 -p,--properties-file-name <arg>      File name of each monkey running
                                      summary.
 -t,--traces-log-file-name <arg>      File name of traces log.
 -w,--workspaces <w>                  Workspace of monkey running
                                      directoy.
```

example
---------------------
``` sh
java -jar monkey-adapter-runner.jar --device-id 45071c540c04197 --user-name xxxxxx --pkg-path ./example.apk --pkg-name com.example --pkg-version 3.0 --single-duration 8 --series-duration 8
java -jar monkey-adapter-analyzer.jar --workspaces ./logs/ --monkey-log-file-name monkey_log.txt --logcat-log-file-name logcat_log.txt --traces-log-file-name traces_log.txt --bugreport-log-file-name bugreport_log.txt --properties-file-name properties.txt --duration 8 --package-name com.example
```
