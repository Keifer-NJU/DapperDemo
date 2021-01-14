# CheckStyle

### 介绍及使用

#### 1. 引入依赖
两种方式，[参考官网](http://maven.apache.org/plugins/maven-checkstyle-plugin/usage.html)
* 一种是reporting，只能生成report,不能影响build结果）
* 一种是build中，可以在控制台输出校验结果，并根据校验结果影响build过程是否成功
```xml
<project>
  ...
   <reporting>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-checkstyle-plugin</artifactId>
          <version>3.1.1</version>
          <reportSets>
            <reportSet>
              <reports>
                <report>checkstyle</report>
              </reports>
            </reportSet>
          </reportSets>
        </plugin>
      </plugins>
    </reporting>
  ...
</project>
```
```xml
<plugin>
   <groupId>org.apache.maven.plugins</groupId>
   <artifactId>maven-checkstyle-plugin</artifactId>
   <version>3.1.1</version>
   <configuration>
     <configLocation>checkstyle.xml</configLocation>
     <encoding>UTF-8</encoding>
     <consoleOutput>true</consoleOutput>
     <failsOnError>true</failsOnError>
     <linkXRef>false</linkXRef>
   </configuration>
   <executions>
     <execution>
       <id>validate</id>
       <phase>validate</phase>
       <goals>
         <goal>check</goal>
       </goals>
     </execution>
   </executions>
 </plugin>
```
#### 2. 定制checkstyle.xml
参考checkstyle的官方网站：https://checkstyle.sourceforge.io/config.html
参考[Sun](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/sun_checks.xml)与[Google](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml)公司
##### **简单实践（生产中会长很多）及采坑**
* 首先checkstyle.xml文件位置要与pom.xml中<configLocation>checkstyle.xml</configLocation>的一致，（这里需要搞明白classpath:checkstyle.xml对应哪里，而我现在没搞懂，很惭愧，因此，暂没采取这种方式，而是直接用路径。）
* 再者，下面这个例子中的 module name="MethodLength"必须放在module name =" TreeWalker"下，具体是可以参考官网xml的结构与原理(有点不擅长看英文囧)
```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
        "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
        "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">

    <property name="charset" value="UTF-8"/>
    <property name="severity" value="warning"/>

    <module name =" TreeWalker">
        <module name="MethodLength">
            <property name="tokens" value="METHOD_DEF"/>
            <property name="max" value="20"/>
            <property name="countEmpty" value="false"/>
        </module>
    </module>
</module>

```
#### 3.checkstyle 应用-运行核验
* mvn install/validate（如果失败不报详细错误）
* mvn checkstyle:checkstyle（失败报详细错误）
* mvn site