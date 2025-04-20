

## 📂 프로젝트 구조

```plaintext
reservio-monorepo/           ← 루트 모노레포
├── settings.gradle          ← 서브프로젝트 등록 및 리포지토리 설정
├── build.gradle             ← 루트 공통 설정 (플러그인 선언, 공통 속성)
├── gradlew(.bat), gradle/   ← Gradle Wrapper
└── services/                ← 모듈 디렉터리
    ├── common/              ← 공통 라이브러리 모듈
    │   └── build.gradle     ← java-library 플러그인, 공통 의존성
    └── booking-service/     ← Spring Boot 예약 서비스 모듈
        ├── build.gradle     ← plugins DSL, 의존성, preview 설정
        └── src/main/java/com/mycompany/bookingservice/
            └── BookingServiceApplication.java  ← @SpringBootApplication 진입점
```

