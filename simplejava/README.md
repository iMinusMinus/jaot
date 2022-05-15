# build and run

```sh
mvn  -DskipTests clean package
java -jar simplejava-1.0-SNAPSHOT.jar
```

# native-image

```shell
# 自动生成部分配置
java -agentlib:native-image-agent=config-output-dir=resourceDir/META-INF/native-image -jar simplejava-1.0-SNAPSHOT.jar
# 修改配置：去除idea启动产生的jni-config.json，空的配置文件也可以去除（如serialization-config.json）
# 修改配置：如果执行没有覆盖某些场景，需要人工添加，如proxy-config.json只有在加载类失败时才会正确生成，如实现类的反射信息
# 添加native-image.properties，或在native-maven-plugin的buildArg添加配置

mvn -P21 -DskipTests clean package
cd target
# 测试
simplejava -Dfibonacci.algorithm.impl=NotExist
```
