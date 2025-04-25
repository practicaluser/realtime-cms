# 실시간 협업 CMS

     ## 프로젝트 개요

     실시간 협업 CMS는 사용자 인증, 실시간 문서 편집, 문서 관리를 지원하는 웹 애플리케이션입니다. Java 17, Spring Boot, PostgreSQL 16.8, Gradle 기반으로 개발됩니다.

     ## 기술 스택

     - 백엔드: Java 17, Spring Boot, Gradle
     - 데이터베이스: PostgreSQL 16.8, Redis
     - 프론트엔드: React
     - 실시간 통신: WebSocket
     - DevOps: Docker, Kubernetes, Jenkins
     - 모니터링: Prometheus, Grafana

     ## 진행 상황

     - **스프린트 1 (예정)**: 개발 환경 설정, 사용자 인증 구현.

### 스프린트 1 - 2단계: Users 테이블 및 JPA 엔터티 구현

       - **목표**:
         - PostgreSQL `users` 테이블 설계 및 생성.
         - JPA `User` 엔터티 구현.
       - **산출물**:
         - [User.java](src/main/java/com/realtimecms/entity/User.java): JPA 엔터티.
         - `users` 테이블: id, email, password, role, created_at.
       - **테스트**:
         - `./gradlew bootRun`으로 `users` 테이블 자동 생성 확인.
         - psql로 테이블 스키마 및 테스트 데이터 확인.