# hanaro

예적금몰 서비스 프로젝트

## 기술 스택
- Java 21
- Spring Boot 4.x
- Gradle
- Spring Security + JWT
- Spring Data JPA
- MySQL 8 (Docker)
- Swagger(OpenAPI3)
- Actuator
- JaCoCo

## 실행 방법
1. MySQL 실행
```bash
docker compose up -d
```
2. 애플리케이션 실행
```bash
./gradlew bootRun
```

## 보안 설정
- DB 계정, 관리자 계정, JWT secret 같은 민감 정보는 `README.md`에 적지 않음.
- 로컬 실행 시 비밀값은 `security.yml` 또는 환경변수로 별도 주입함.
- 제출물에는 실제 운영용 비밀번호나 secret을 포함하지 않음.

## 주요 기능
- 회원가입/로그인 (가입 시 자유입출금 계좌 자동 생성)
- 관리자 상품 CRUD + 대표 이미지 업로드
- 사용자 상품 조회/가입/해지/이체
- 회원별 가입 내역 조회, 만기 처리
- 공통 예외 처리 및 Validation
- 로그 분리 (`logs/user.log`, `logs/product.log`, `logs/service.log`)

## 파일 업로드
- 저장 위치: `src/main/resources/static/upload/yyyyMMdd`
- 파일명: UUID 기반 중복 방지
- 제한: 1개 2MB, 전체 10MB

## API 문서
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- 호환 경로: `http://localhost:8080/swagger-ui/index/html`

## Actuator
- `http://localhost:8080/actuator/health`
- `http://localhost:8080/actuator/beans`
- `http://localhost:8080/actuator/env`
- `http://localhost:8080/actuator/metrics`

## DB 설정
- schema: `hanarodb`
- 계정 정보와 비밀번호는 로컬 비밀 설정 파일 또는 환경변수로 관리
- docker: `mysql:8.0`

## 제출 산출물 위치
- ERD 이미지: `docs/hanaro-erd.svg`
- 로그 샘플: `logs/user.log`, `logs/product.log`, `logs/service.log`
- 업로드 샘플: `src/main/resources/static/upload/20260316/`
- 보안 업로드 샘플: `src/main/upload/secure/`

## 제출 체크
- `data.sql` 포함
- 로그 샘플 파일 포함
- 업로드 샘플 파일 포함
- ERD 이미지 포함
- 압축 제외: `.gradle`, `.idea`, `build`
