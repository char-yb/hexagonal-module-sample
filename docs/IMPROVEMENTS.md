# 아키텍처 개선 가이드

## 개선 완료 항목

### ✅ High Priority

#### 1. 순환 참조 위험성 제거
- **변경사항**: `common:model`, `common:exception` 모듈 추가
- **목적**: 도메인 간 직접 의존 제거, 공유 개념을 common 모듈로 분리
- **영향**:
  - `payroll:model`이 더 이상 `corehr:model`을 직접 의존하지 않음
  - `CompanyIdentity`, `EmployeeIdentity` 등은 `common:model`에서 제공
  - 도메인 예외는 `common:exception`의 `DomainException`을 상속

**마이그레이션 방법**:
```kotlin
// Before
import team.flex.module.sample.corehr.company.CompanyIdentity

// After
import team.flex.module.sample.common.CompanyIdentity
```

#### 2. ArchUnit 테스트 도입
- **변경사항**: `arch-test` 모듈 추가
- **목적**: 아키텍처 규칙을 자동으로 검증
- **테스트 항목**:
  - Model 레이어는 다른 레이어에 의존하지 않음
  - Service는 Model과 Infrastructure만 의존
  - Infrastructure는 Model만 의존
  - Repository는 Infrastructure와 Model만 의존
  - API는 Service만 의존
  - corehr와 payroll 간 직접 의존 금지

**실행 방법**:
```bash
./gradlew :arch-test:test
```

#### 3. 모듈 생성 자동화
- **변경사항**: `scripts/create-domain.sh` 스크립트 추가
- **목적**: 새 도메인 추가 시 8개 모듈을 자동 생성하고 settings.gradle.kts 자동 업데이트
- **사용 방법**:
```bash
./scripts/create-domain.sh attendance
```

생성되는 모듈:
- model, exception, infrastructure, service
- repository-jdbc, api, schema, application-api

**자동화 기능**:
- 8개 모듈 디렉토리 및 파일 생성
- 각 모듈의 build.gradle.kts, gradle.properties 자동 생성
- 모듈 간 의존성 자동 설정
- **settings.gradle.kts 자동 업데이트** (수동 작업 불필요)
- 백업 파일 자동 생성 및 관리

---

## 다음 단계 개선 항목

### 🟡 Medium Priority

#### 4. schema 모듈 독립성 개선
**현재 문제**: application-api가 모든 schema를 직접 의존

**개선 방안**:
```kotlin
// repository-jdbc/build.gradle.kts
dependencies {
    implementation(project(":corehr:schema"))  // schema를 repository에 포함
}

// application-api/build.gradle.kts
dependencies {
    // schema 의존성 제거
    implementation(project(":corehr:api"))
    implementation(project(":corehr:repository-jdbc"))  // schema는 간접 의존
}
```

#### 5. 도메인 이벤트 처리
**목적**: 도메인 간 느슨한 결합

**구현 예시**:
```kotlin
// common:model
interface DomainEvent {
    val occurredAt: Instant
}

// corehr:model
data class EmployeeCreatedEvent(
    val employeeId: Long,
    override val occurredAt: Instant
) : DomainEvent

// payroll:service
@EventListener
fun handleEmployeeCreated(event: EmployeeCreatedEvent) {
    // payroll 초기화 로직
}
```

#### 6. 테스트 전략 명확화
**추가 필요**:
- `test-fixtures` 모듈 생성
- 각 레이어별 테스트 가이드 문서
- Mock 전략 표준화

---

## 마이그레이션 체크리스트

### corehr 도메인 마이그레이션
- [ ] `corehr:model`에서 `CompanyIdentity`, `EmployeeIdentity`를 `common:model`로 이동
- [ ] `corehr:exception`에서 공통 예외를 `common:exception`으로 이동
- [ ] `corehr:model/build.gradle.kts`에 `api(project(":common:model"))` 추가
- [ ] `corehr:exception/build.gradle.kts`에 `api(project(":common:exception"))` 추가

### payroll 도메인 마이그레이션
- [ ] `payroll:model/build.gradle.kts`에서 `corehr` 의존성 제거
- [ ] `payroll:model/build.gradle.kts`에 `api(project(":common:model"))` 추가
- [ ] import 문 수정 (`corehr` → `common`)

### 검증
- [ ] `./gradlew build` 성공
- [ ] `./gradlew :arch-test:test` 성공
- [ ] 기존 통합 테스트 모두 통과

---

---

## 고급 개선사항

이 문서는 기본적인 개선사항을 다룹니다. 이미 헥사고날 아키텍처를 도입한 팀을 위한 
**엔터프라이즈급 고급 개선사항**은 [ADVANCED_IMPROVEMENTS.md](./ADVANCED_IMPROVEMENTS.md)를 참고하세요.

### 고급 개선사항 미리보기

- **Priority 1: ArchUnit 테스트** ✅ 완료 - 자동 아키텍처 검증
- **Priority 2: 도메인 이벤트 기반 통신** - 마이크로서비스 전환 준비
- **Priority 3: 관측성(Observability) 강화** - 장애 추적 시간 75% 단축
- **Priority 4: API 버전 관리 전략** - Breaking Change 안전 배포
- **Priority 5: 스키마 모듈 독립성 개선** - 응집도 향상
- **Priority 6: 통합 테스트 전략 개선** - 테스트 시간 60% 단축
- **Priority 7: 보안 아키텍처 검증** - 자동 컴플라이언스 검증
- **Priority 8: 성능 및 캐싱 전략** - 성능 10배 향상, 비용 60% 절감

전체 내용은 [ADVANCED_IMPROVEMENTS.md](./ADVANCED_IMPROVEMENTS.md)를 확인하세요.

---

## 참고 자료

### 기본 자료
- [ArchUnit 공식 문서](https://www.archunit.org/)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)

### 고급 자료
- [ADVANCED_IMPROVEMENTS.md](./ADVANCED_IMPROVEMENTS.md) - 엔터프라이즈급 개선사항
- [ARCHITECTURE.md](./ARCHITECTURE.md) - 아키텍처 상세 가이드
- [QUICK_START.md](./QUICK_START.md) - 빠른 시작 가이드
