# Hanaro

예금/적금 상품 조회, 가입, 해지, 만기 처리까지 다루는 금융 상품 관리 프로젝트입니다.  
관리자와 일반 사용자 기능을 분리했고, JWT 인증/인가, Swagger(OpenAPI 3), Actuator, Validation, 파일 업로드, 로그 분리까지 포함해 과제 요구사항을 구현했습니다.

## 1. 프로젝트 개요

- 관리자(Admin)
  - 상품 등록/조회/수정/삭제
  - 회원 목록 조회 및 검색
  - 회원별 가입 내역 조회
  - 만기 일괄 처리
- 일반 사용자(User)
  - 회원가입/로그인
  - 상품 목록/상세 조회
  - 상품 가입
  - 내 가입 내역 조회
  - 중도 해지
  - 상품 계좌 이체

회원가입이 완료되면 자유입출금 통장이 자동 생성됩니다.

## 2. 기술 스택

- Java 21
- Spring Boot 4.0.3
- Spring Security
- JWT
- Spring Data JPA
- MySQL 8
- Swagger / OpenAPI 3
- Spring Boot Actuator
- JaCoCo
- JUnit 5 / MockMvc

## 3. 주요 구현 내용

### 3-1. 인증 / 인가

- JWT 발급 및 검증
- `ROLE_ADMIN`, `ROLE_USER` 기반 권한 분리
- `@PreAuthorize` 기반 API 접근 제한
- JWT 필터에서 Swagger, 로그인/회원가입, Actuator 등 공개 경로는 제외 처리

### 3-2. Validation

- 회원가입: 이메일, 비밀번호, 닉네임, 전화번호 검증
- 상품 등록/수정: Bean Validation 기반 검증
- 계좌번호 검증: 숫자 11자리 형식 검증
- 예금/적금에 따라 납입 주기 입력 규칙 검증

### 3-3. 파일 업로드

- 저장 경로: `src/main/resources/static/upload/yyyyMMdd`
- 파일명: `UUID + 확장자`
- 대표 이미지 1개 업로드 지원
- 파일 크기 제한
  - 1개당 최대 2MB
  - 총 요청 최대 10MB

예시:

```text
src/main/resources/static/upload/20260318/ff29965d-ce6b-40a3-88e4-65f1204592f3.png
```

### 3-4. 로그

- `logback-spring.xml` 기반 파일 분리
- 생성 로그 파일
  - `logs/application.log`
  - `logs/user.log`
  - `logs/product.log`
  - `logs/service.log`
  - `logs/subscription.log`
- 날짜 기준 롤링 및 `.gz` 압축 보관

### 3-5. 문서화 / 운영 확인

- Swagger UI로 주요 서비스 API 테스트 가능
- Actuator로 `health`, `beans`, `env`, `metrics` 확인 가능
- 예외 응답과 검증 실패 메시지를 커스텀 문구로 정리

## 4. 패키지 구조

```text
src/main/java/com/hana8/hanaro
├── common
├── config
├── controller
├── dto
├── entity
├── mapper
├── repository
├── security
└── service
```

기본 구조는 `Controller / Service / Repository` 분리를 따르고, DTO 변환은 `mapper` 패키지로 분리했습니다.

## 5. 실행 방법

### 5-1. DB 실행

```bash
docker compose up -d
```

기본 MySQL 포트는 `3308`입니다.

### 5-2. 보안 설정 확인

`application.yml`에서 아래 파일을 import합니다.

```yaml
spring:
  config:
    import: classpath:security.yml
```

현재 프로젝트에는 로컬 실행용 [`security.yml`](src/main/resources/security.yml)이 포함되어 있습니다.  
실제 배포나 외부 제출 환경에서는 DB 비밀번호와 JWT secret을 별도 값으로 관리하는 것이 맞습니다.

### 5-3. 애플리케이션 실행

```bash
./gradlew bootRun
```

## 6. API 문서 / 운영 점검 URL

### Swagger

- Swagger UI: `http://localhost:8080/swagger.html`
- OpenAPI Docs: `http://localhost:8080/hana8/api-docs`
- Swagger 리다이렉트 호환 경로: `http://localhost:8080/swagger-ui/index/html`

Swagger에서는 `ADMIN`, `USER` 그룹으로 API를 나눠 확인할 수 있습니다.

### Actuator

Actuator는 애플리케이션 포트와 분리된 `9001` 포트에서 동작합니다.

- `http://localhost:9001/actuator`
- `http://localhost:9001/actuator/health`
- `http://localhost:9001/actuator/beans`
- `http://localhost:9001/actuator/env`
- `http://localhost:9001/actuator/metrics`

## 7. 대표 API

### 인증

- `POST /api/auth/signup`
- `POST /api/auth/login`

### 관리자

- `GET /api/admin/products`
- `POST /api/admin/products`
- `PUT /api/admin/products/{productId}`
- `DELETE /api/admin/products/{productId}`
- `GET /api/admin/users`
- `GET /api/admin/users/{userId}/subscriptions`
- `POST /api/admin/users/maturity`

### 사용자

- `GET /api/user/products`
- `GET /api/user/products/{productId}`
- `POST /api/user/subscriptions`
- `GET /api/user/subscriptions`
- `POST /api/user/subscriptions/{subscriptionId}/terminate`
- `POST /api/user/subscriptions/transfer`

## 8. 테스트

현재 `src/main/java`와 `src/test/java` 파일 수를 동일하게 맞춰, 파일명 기준 대응 테스트 파일을 정리했습니다.

- main source file: 56개
- test source file: 56개

실행 명령:

```bash
./gradlew build
```

추가로 필요하면 다음도 확인할 수 있습니다.

```bash
./gradlew test
./gradlew jacocoTestReport
```

## 9. 구현하면서 개선한 부분

- DTO 파일명을 `...DTO` 형태로 정리해 역할을 명확히 했습니다.
- 수동 매핑 코드를 줄이기 위해 `mapper` 패키지를 추가했습니다.
- 파일 업로드를 날짜별 디렉토리 + UUID 파일명 방식으로 정리했습니다.
- `ControllerExceptionHandler`를 중심으로 공통 예외 처리를 통합했습니다.
- Swagger 각 API에 `@Tag`, `@Operation`, `@Parameters`, `@ApiResponses`를 추가해 문서 가독성을 높였습니다.
- `JwtAuthenticationFilter`에 제외 경로 패턴을 추가해 인증 흐름을 명확히 했습니다.
- 로깅은 별도 커스텀 퍼블리셔 대신 `SLF4J + logback-spring.xml` 방식으로 단순화했습니다.

## 10. 아쉬운 점 / 추가 개선 포인트

- 관리자 검색은 현재 `이메일/닉네임/전화번호` 키워드 검색만 지원하고, 날짜 조건 검색은 아직 없습니다.
- 상품 등록/수정은 `multipart/form-data` 구조 때문에 `@Valid` 직접 바인딩 대신 컨트롤러에서 JSON 파싱 후 Bean Validation을 수행합니다.
- Actuator는 현재 확인용으로 넓게 열려 있어 운영 환경에서는 접근 제어를 더 강화해야 합니다.
- 업로드 경로가 `resources/static` 하위에 있어 개발/과제 환경에는 편하지만, 운영 환경에서는 외부 스토리지나 별도 파일 서버로 분리하는 편이 더 적절합니다.
- 현재 상품은 대표 이미지 1개만 지원합니다. 다중 이미지가 필요하면 `Product`와 이미지 연관 구조를 추가로 설계해야 합니다.

## 11. 산출물

- ERD: [`docs/hanaro-erd.svg`](docs/hanaro-erd.svg)

<img width="781" height="657" alt="hanaro" src="https://github.com/user-attachments/assets/5ffeb83d-d819-43af-8d6f-b9669ce190ef" />

- 로그 예시: [`logs`](logs)
- 업로드 예시: [`src/main/resources/static/upload`](src/main/resources/static/upload)

---

<details>
<summary> ✅ 평가 항목 단계별 확인</summary>

## 1. 인증/인가 (10)
### 1-1. JWT 발급 및 검증
- [x] /api/auth/*signup* 으로 회원가입 후 /api/auth/*login* 으로 로그인
- [x] 200 OK 확인 후 JWT 토큰 발급 확인 *(예시: eyJHbGci...)*

### 1-2. ADMIN / USER 구분
- [x] USER 토큰으로 /api/user/products **200 OK**인지 확인
- [x] USER 토큰으로 GET /api/admin/products 또는 POST /api/admin/products 호출 시 **403** 인지 확인

### 1-3. ROLE 기반 API 접근 제한
- [x] 잘못된(만료 및 위조) JWT로 /api/admin/products API 호출 시 **401** 인지 확인

## 2. 파일 업로드 (10)
- [x] 관리자 상품 등록에서 이미지 업로드

### 2-1. 날짜별 경로 저장 (/resources/static/upload/yyyyMMdd)
- [x] 실제 저장 경로가 src/main/resources/**static**/**upload**/**yyyyMMdd** 인지 확인 *(예시 : src/main/resources/static/upload/20260319)*

### 2-2. 파일명 중복 방지
- [x] 같은 파일 두 번 업로드 후 저장명이 다른지(난수, uuid 차이) 확인

### 2-3. 파일 크기 제한 (2M, 총 10MB)
- [x] 2MB 초과 파일, 10MB 초과하는 파일 업로드 후 요청이 막히는지 확인 (첨부한 2.3MB 사진 업로드 해보기)

## 3. Actuator (5)
### 3-1. 기능 명세에서 요구한 모든 엔드포인트 (/health, /metrics 등) 확인 가능
- [x] http://localhost:9001/actuator 접속
- [x] http://localhost:9001/actuator/health, /metrics, /env, /beans 각각 접속
- [x] 각각 JSON 데이터 잘 나오는지 확인

## 4. 로깅 (10)
### 4-1. logs 폴더에 분리된 로그파일 확인
- [x] logs 폴더에 **user.log**, **product.log**, **service.log** 있는지 확인
- [x] 회원가입/로그인/상품등록 후 각 로그 파일에 기록 생기는지 확인

### 4-2. 로그 파일 보관 디렉토리 구성 및 롤링
- [x] 날짜별(하루 기준) '날짜.log.gz' 롤링 파일 있는지 확인

## 5. 기본 기능 구현 (20)
- [x] Swagger에서 전부 실제 호출(try it out)해 보기

### 5-1. 관리자 및 사용자 필수 기능 전체 구현 여부
- [x] **관리자(admin)**:
	- [x] 예적금 상품 등록/조회/수정/삭제
```json
// 상품 수정
{
  "name": "하나 프리미엄 정기예금 리뉴얼",
  "type": "DEPOSIT",
  "paymentAmount": 5500000,
  "savingsCycle": null,
  "periodMonths": 12,
  "maturityRate": 4.1,
  "terminationRate": 1.5
}
```
- [x] 회원 관리(목록, 회원별 가입내역, 만기처리 등)
- [x] **사용자(user)**
	- [x] 회원가입/로그인
	- [x] 예적금 상품 목록/상세/가입/이체/해지() API 등 *junho@test.com/12345678으로*

## 6. Swagger (OpenAPI 3) (10)
- [x] http://localhost:8080/swagger.html

### 6-1. 사용자/관리자 API 구분
- [x] (CRUD) /api/**user**/... 확인
- [x] (CRUD) /api/**admin**/... 확인

### 6-2. Swagger UI 로 모든 API 테스트 가능
- [x] **Authorize 버튼** 클릭 후 **JWT 토큰** 넣고 모든 API 통신 테스트 가능한지 확인

### 6-3. 명세 명확
- [x] 각 API에 설명, 파라미터, 응답 코드가 보이는지 확인

## 7. Validation (10)
### 7-1. 회원가입, 상품 등록 시 @valid/@validated 적용
- [x] 회원가입에 잘못된 이메일(@ 없음 등)/비밀번호로 **400** 확인
- [x] 상품 등록에 잘못된 값(상품 가격이 - · 음수값)으로 **400** 확인

### 7-2. 계좌번호 validator 확인
- [x] 계좌번호 11자리 숫자 아닌 값(문자 삽입, 10자리 및 12자리)으로 **400** 에러 확인

## 8. 코드 품질 및 구조화 (10)
### 8-1. Controller/Service/Repository 분리
- [x] 패키지가 **controller**/**service**/**repository** 로 분리됐는지 확인

### 8-2. 중복 코드 최소화
- [x] mapper, exception handler 등 공통화(똑같은 코드를 여러번 쓰지 않았는지 등) 여부 확인

### 8-3. 명확한 네이밍과 로직 정리
- [x] 메서드명과 클래스명이 역할에 맞는지(변수나 메서드명만 봐도 무슨 일 하는지 유추 가능한지) 확인

## 9. 예외 처리 및 에러 응답 명세화 (5)
### 9-1. 공통 예외 처리 (@ControllerAdvice, @ExceptionHandler)구현
1) 공통 예외 처리
   - [x] 공통 예외 처리 파일인 ControllerExceptionHandler 확인
   - [x] @ControllerAdvice, @ExceptionHandler 잘 적용했는지 확인

2) Jacoco Coverage 확인
   - [x] build 후, build/reports/jacoco/test/html/index.html 접속
   - [x] report 커버 비율 검사 (Mixxed Instructions *60~80%* 이상으로 맞추기)

### 9-2. 커스텀 예외 메시지
- [x] 상황에 맞는 정확한 HTTP 상태 코드(400: 잘못된 요청, 401: JWT 오류, 403: 권한 없음, 404: 없는 데이터, 413: 파일 크기 초과)와 "계좌번호 형식이 틀렸습니다" 같은 구체적인 이유를 프론트엔드에 내려주는지 확인

</details>
