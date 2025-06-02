# Swagger API 문서 가이드

## 개요
이 프로젝트는 SpringDoc OpenAPI 3을 사용하여 API 문서를 자동 생성합니다.

## 접속 방법

### Swagger UI 접속
- **URL**: http://localhost:8080/swagger-ui.html
- **기능**: 대화형 API 문서 및 테스트 인터페이스

### OpenAPI JSON 문서
- **URL**: http://localhost:8080/api-docs
- **기능**: OpenAPI 3.0 스펙의 JSON 형태 문서


## JWT 인증 사용법

### 1. 로그인 수행
```json
{
  "customerInputId": "admin",
  "customerPw": "admin123"
}
```

### 2. 응답에서 토큰 복사
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "customerInputId": "admin",
  "authority": "ADMIN"
}
```

### 3. Swagger UI에서 인증 설정
1. Swagger UI 상단의 **"Authorize"** 버튼 클릭
2. **"bearerAuth"** 섹션에 JWT 토큰 입력 (Bearer 접두사 제외)
3. **"Authorize"** 버튼 클릭
4. 이후 모든 API 호출에 자동으로 인증 헤더가 포함됩니다.


## 관리자 계정 정보
- **아이디**: admin
- **비밀번호**: admin123

## 추가 설정
- **정렬**: API는 태그별로 알파벳 순으로 정렬됩니다.
- **필터**: 상단 검색창을 통해 API를 필터링할 수 있습니다.
- **Try it out**: 각 API 엔드포인트를 직접 테스트할 수 있습니다.
- **인증 유지**: 브라우저를 새로고침해도 JWT 토큰이 유지됩니다. 