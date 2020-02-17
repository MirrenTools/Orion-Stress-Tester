# Orion-Stress-Tester (OST)
A simple, efficient and accurate stress tester, support HTTP, WebSocket and TCP

## How to run
The project was created based on [https://vertx.io/](vert.x 3.8.5),

Operation environment requirements &gt;= java 1.8(No JDK guidance),

Coding and test environment java 1.8.0_121

Mode 1:

```
Download the latest release in releases, and execute the corresponding start.bat or start.sh after decompression
```

Mode 2:

```
mvn clean package
Copy the root data folder, webroot folder and orion-stress-tester-fat.jar in target to one folder for execution
java -jar  Orion-Stress-Tester-fat.jar
```
Visit in browser: http://127.0.0.1:7090

## Config description
The configuration file of OST is config.json in the data folder
```
httpPort(int): Port, default 7090
instances(int): The number of instances the test task runs. default 0 of processors. If you don't know vert.x, you don't need to worry about this configuration
```

## Client description
The client is written based on Vue 2.6.10, and you can modify it through the client project [Orion-Stress-Tester-Client](https://github.com/MirrenTools/Orion-Stress-Tester-Client)

## Client example
![example-en](https://raw.githubusercontent.com/MirrenTools/Orion-Stress-Tester/master/data/example-en.png)
