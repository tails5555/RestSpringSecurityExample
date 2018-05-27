# Spring Security 5 With REST API Example

## Issues
- `Spring Security`를 `REST API`로 접목하는 연습을 진행합니다.
- `Spring Security`에 있는 추가적인 설정을 공부하는 계기를 가져봅니다.
- `Spring Data JPA`의 변동 사항을 반영하여 `AuthenticationProvider`에서 쓸 수 있도록 합니다.
- `JUnit`, `Mockito Mock MVC`를 이용해서 `Spring Security`를 테스팅하는 연습을 진행합니다.

## Relational Database Structure

## Maven pom.xml

`pom.xml` 를 기반으로 Maven Dependency를 구성하여 Update Maven은 필수입니다.

```
<dependencies>
    <!-- 1. Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- 2. Spring Security Starter(web / core) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <!-- 3. Spring Web MVC -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- 4. MySQL JDBC Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <!-- 5. Lombok Project -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <!-- 6. Spring Boot Test(JUnit, Mockito) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <!-- 7. Spring Security Test(JUnit, Mockito) -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Screenshot

## Author 

- [강인성](https://github.com/tails5555) (tails5555)

## References

- https://github.com/tails5555/KangBakSa_Note/blob/master/Application_Computer_Science/3_Securities_Framework/01_Spring_Security.md
    - KangBakSa에 기재된 Spring Security Note와 함께 읽어보면 좋습니다.