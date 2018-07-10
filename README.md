# MqttDemo
android前端mqtt+activemq服务端的简单用法
## 初始化操作步骤如下：
1.下载最新Apache ActiveMQ，网址如下 http://activemq.apache.org/activemq-5154-release.html

2.找到bin目录下win32或者win64位目录的activemq.bat命令启动（我的是winndow系统，其他系统请自行上网查找）

3.mqtt的webDemo文件夹是一个eclipse项目，找个eclipse导入既可，里面有发送和接收，可做本地测试用

4.将导入android的工程运行起来，注意修改ip地址为你自己的（在com.cyd.util.MqttUtils包里面serverUri）

5.注意android端和服务端的项目要在一个局域网内
