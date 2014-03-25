AndroidMonkeyAdapter
====================
Introduction
--------------------

  1. Install Android application, run Monkey testing job, analyze and classify crash automatically
  2. HTML-format report supported
  3. Multiple platform supported(Windows, Mac, Linux)
  4. Jenkins platform supported 
  

Usage
--------------------
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
Usage of Android Monkey Adapter runner
--------------------
``` sh
Usage: java -jar mra.jar -options [args...]

   --device-id <ids...>                 the id list of the devices which is need
 to run monkey test
   --single-duration <duration>         expected one monkey job duration (hour)
   --series-duration <duration>         expected total monkey jobs duration (hou
r)
   --pkg-path <package-path>            package path
   --pkg-name <package-name>            package name
   --pkg-version <package-version>      package version
   --unlock-cmd-path <unlock script>    point to an unlock script path which mus
t be standalone executable
```

example
---------------------
``` sh
java -jar monkey-adapter-runner.jar --device-id 45071c540c04197 --user-name xxxxxx --pkg-path ./example.apk --pkg-name com.example --pkg-version 3.0 --single-duration 8 --series-duration 8
java -jar monkey-adapter-analyzer.jar --workspaces ./logs/ --monkey-log-file-name monkey_log.txt --logcat-log-file-name logcat_log.txt --traces-log-file-name traces_log.txt --bugreport-log-file-name bugreport_log.txt --properties-file-name properties.txt --duration 8 --package-name com.example
```
