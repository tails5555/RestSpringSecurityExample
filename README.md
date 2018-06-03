# Spring Security 5 With REST API Example

## Issues
- `Spring Security`를 `REST API`로 접목하는 연습을 진행합니다.
- `AuthenticationEntryPoint`, `AuthenticationSuccessHandler`, `AuthenticationFailureHandler`, `AccessDeniedHandler` 등 여태 동안 몰랐던 Handler Component들을 다뤄볼 수 있는 기회를 가집니다.
- `Spring Data JPA`의 변동 사항을 반영하여 `AuthenticationProvider`에서 쓸 수 있도록 합니다.
- `JUnit`, `Mockito Mock MVC`를 이용해서 `Spring Security`을 적용한 Controller 클래스를 테스팅하는 연습을 진행합니다.

## Relational Database Structure

![security_example](/image/security_example.png)

RDBMS는 `MySQL Workbench`를 이용하였으며, Schema 이름은 `security_example`입니다.

- `authinfo`
    - 사용자가 인증할 수 있는 username, password 정보가 들어있습니다.
    - password는 Message Digest 5 암호화 알고리즘을 적용하고 저장하였습니다.
- `authrole`
    - 사용자에게 주어지는 권한의 종류를 저장합니다.
    - 권한 종류는 `USER`, `MANAGER`, `ADMIN` 3가지로 구성이 됩니다.
- `authdetail`
    - 사용자의 이름(한글 기준 2~4자), 생일, E-Mail, 주소 등 세부 정보를 저장합니다.
    - E-Mail은 Unique Constraint로 설정하였습니다.
- `infoandrole`
    - 사용자와 권한에 대해서 M:N 관계를 적용하기 위해 형성한 테이블입니다.
    - 사용자 인증 정보 id(`authinfo.id`)와 권한 정보 id(`role.id`)로 구성되어 이 둘을 Unique Constraint로 저장합니다.

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

## The Setting Of application.properties

- src > main > resources > application.properties에 현존하는 설정을 아래와 같은 방식으로 작성해서 이용하시면 됩니다.

```
spring.mvc.view.prefix=[MVC에서 View 위치에 대한 설정]
spring.mvc.view.suffix=[MVC에서 View 확장자 설정]
spring.datasource.driver-class-name=[JDBC 이용 클래스 이름 입력]
spring.datasource.url=[JDBC와 연동하기 위한 URL]
spring.datasource.username=[DB 사용자 이름]
spring.datasource.password=[DB 사용자 비밀번호]

<--! Hibernate JPA를 적용하기 위한 설정 -->
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

<--! Hibernate JPA에 대해 SQL 결과를 인지하기 위한 문장. DEBUG를 통해 일일히 확인 시킨다. --> 
logging.level.org.hibernate.SQL=DEBUG

<--! Hibernate JPA에 대해서 SQL의 기본 문장에 대해 TRACE 단계를 통해 확인을 시켜준다. -->
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Sequence Diagram of Spring Security REST API

Spring Security에서 사용자 인증 정보(username, password)를 이용한 로그인 과정을 도식하였습니다.

![login_sequence](/image/login_sequence.png)

1. 사용자가 로그인을 진행할 때, Basic Authentication(with User ID, Password)을 이용합니다.

    - 만일 사용자 인증을 거치지 않은 비회원에 대해서는 401 Unauthorized Status를 넘기고 Redirect를 진행합니다.
    
    - 이 역할을 하는 Handler는 `AuthenticationEntryPoint`입니다.

2. AuthenticationProvider를 통해 사용자의 권한을 부여한 후에 Http Basic을 이용하여 Token을 부여하고 현재 사용자 로그인 정보를 session에 저장합니다.

3. 사용자 권한이 현재 요청하는 URI에 적합한가에 대해 확인 여부를 진행하고, 올바르지 않으면 403 Forbidden Status로 넘기면서 Redirect를 진행합니다.
    
    - 이 역할을 하는 Handler는 `AccessDeniedHandler` 입니다.
    
4. 접근 성공이 완료되면 AuthenticationSuccessHandler를 통해서 200 OK Status를 반환하고 정상적으로 이용이 가능하게 합니다.

    - 접근 권한을 관리하는 Configuration Class에 자세히 기재되어 있으니 이를 참고하시어 문제 발생이 없으시길 바라겠습니다.

## REST API Screenshot

![REST_Screenshot01](/image/REST_Screenshot01.png)

USER 권한을 가진 사람 로그인 작업

![REST_Screenshot02](/image/REST_Screenshot02.png)

회원 정보 조회

![REST_Screenshot03](/image/REST_Screenshot03.png)

로그아웃 작업

![REST_Screenshot04](/image/REST_Screenshot04.png)

회원 가입 진행

![REST_Screenshot05](/image/REST_Screenshot05.png)

회원 가입 완료 후 회원 등록 확인

![REST_Screenshot06](/image/REST_Screenshot06.png)

회원 가입 완료 후 Basic Authentication 등록 확인

![REST_Screenshot07](/image/REST_Screenshot07.png)

회원 가입 완료 후 권한 부여 확인. roleId에서 1은 Admin, 2는 Manager, 3은 User이다. 

## JUnit Test Screenshot

![JUnit_Result01](/image/JUnit_Result01.jpg)

![JUnit_Result02](/image/JUnit_Result02.jpg)

JUnit 실행 결과에 대해서 [JUnit_Test_Result.pdf](https://github.com/tails5555/RestSpringSecurityExample/blob/master/JUnit_Test_Result.pdf)을 참고하셔도 좋습니다.

## Author 

- [강인성](https://github.com/tails5555) (tails5555)

## References
- https://github.com/tails5555/KangBakSa_Note/blob/master/Application_Computer_Science/3_Securities_Framework/01_Spring_Security.md
    - KangBakSa에 기재된 Spring Security Note와 함께 읽어보면 좋습니다.