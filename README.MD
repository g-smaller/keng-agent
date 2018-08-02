#### 使用方式

- 打包
    ```
    mvn clean package
    ```

    > 执行的后会产生一个 jar 文件目录在 `${project.basedir}/target/keng-agent-1.0-SNAPSHOT.jar`

- 在项目中使用

    > 在系统参数中添加
    ```
    -javaagent:${project.basedir}/target/keng-agent-1.0-SNAPSHOT.jar
    ```

#### 依赖
> 在使用的项目中一定要添加 [`javassist`](https://github.com/jboss-javassist/javassist) 依赖

```$xslt
<dependency>
    <groupId>javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.9.0.GA</version>
</dependency>
```

#### 参考资料
[https://blog.csdn.net/luanlouis/article/details/24589193](https://blog.csdn.net/luanlouis/article/details/24589193)