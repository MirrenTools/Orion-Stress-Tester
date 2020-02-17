# Orion-Stress-Tester (OST)
一个简易,高效,精准的压力测试器,支持HTTP,WebSocket,TCP
## 如何运行
该项目基于[https://vertx.io/](vert.x 3.8.5)创建,运行环境要求&gt;= java 1.8(免JDK教程),编码与测试环境java 1.8.0_121

方式一:

```
在releases中下载最新版发行版,解压后执行对应的start.bat或start.sh
```

方式二:

```
mvn clean package
将根目录的data文件夹、webroot文件夹与target中的 Orion-Stress-Tester-fat.jar复制到一个文件夹中执行
java -jar  Orion-Stress-Tester-fat.jar
```
在浏览器访问: http://127.0.0.1:7090

## 配置文件说明
OST的配置文件为data文件夹中的config.json
```
httpPort(int): OST运行的端口号,默认7090
instances(int): 测试任务运行的实例数量,默认0=处理器数量,如果你不了解vert.x你可以不用关心这个配置
```

## 客户端说明
客户端基于vue 2.6.10编写,你可以通过客户端项目[Orion-Stress-Tester-Client](https://github.com/MirrenTools/Orion-Stress-Tester-Client)进行修改

## 客户端演示
![example-zh](https://raw.githubusercontent.com/MirrenTools/Orion-Stress-Tester/master/data/example-zh.png)
