
# 콘서트 예약 서비스

## Docs
### [1. 마일스톤](docs/01_Milstone.md)
### [2. 이벤트 시퀀스 다이어그램](docs/02_Sequence.md)

### [3. ERD](docs/03_ERD.md)
### [4. API 명세 문서](docs/04_API_specification.md)
### [5. Swagger UI](docs/05_Swagger_UI.md)

## 패키지 구조
<img width="212" alt="image" src="https://github.com/user-attachments/assets/5096b9c7-477d-4969-872e-e5de2564050d">
  
> DDD 기반 4 Layered Architecture 채택하였습니다.

- `infrastructure`: presentation 계층으로, `controller` 및 `dto` 를 관리합니다. (추후 필터나 인터셉터 추가 예정)
- `application`: application 계층으로, `UseCase` 구현을 위해 Domain 과 분리하여 `Facade` 를 관리합니다.
- `domain`: domain 계층으로, 비즈니스 로직 구현을 담당합니다. `Service`, `Domain Object`, `Repository Interface`를 관리합니다.
- `interfaces`: interfaces 계층으로, Database 와 연동하여 쿼리를 수행하는 등 `Repository`, `Entity` 등을 관리합니다.
- `supports`: 계층과 상관없이 전역에서 사용할 수 있는 `common`, `util` 클래스들을 관리합니다. 전역 예외 처리나 `Enum`등을 관리할 예정입니다.

## 기술 스택
- Java 17
- Spring Boot 3.4
- Gradle build
- h2

- 프로젝트 의존성
  - Spring Data JPA
  - Spring Web
  - Spring Validation
  - Spring Boot Configuration
  - JUnit5
  - Lombok

---

### Chapter2 회고

- `2-1 서버 구축`   
> **시나리오 선택과 설계 자료 작성**   
> 결제 구현이나 외부 API 기능은 경험이 있었기에 낯설었던 "대기열 구현"에 도전하고 싶어 콘서트 예약 시스템을 선택했습니다.   
> 프로젝트 마일스톤 설계부터 시퀀스, ERD, API 명세를 작성해야 했는데, 지금까지는 무언갈 개발할 때 설계 단계를 생략하고 구현부터 했기에 낯설었습니다.   
> 사용자의 UseCase 들을 생각해내기 위해 뮤지컬 예약했던 경험을 떠올리며 작성하는 시간을 가졌습니다.   
> 시퀀스 다이어그램 작성할 때 컴포넌트(?)를 도메인 서비스 단위로 정해놓고 작업하니 추후 코드로 구현할 때 플로우를 금방 파악할 수 있었습니다.   
> 구체적인 프로젝트 설계는 일정 조율과 To-do list 를 만들기에 용이함을 느꼈습니다.

- `2-2 서버 구축`
> **비즈니스 로직 구현과 Swagger 도입**   
> 프로젝트 설계 단계에서 만들었던 UseCase 들을 직접 코드로 구현하는 시간을 가졌습니다. 시간이 너무 오래 걸려서 고통스러웠는데 ...   
> 코드 구현은 사실 어렵지 않았다고 생각합니다. 가장 시간을 많이 잡아먹은 부분은 비즈니스 로직의 흐름 설계였습니다.   
> `결제 요청`이 있을 때 일정이 예약 가능한 상태인지, 좌석이 사용 가능한 상태인지, 예약자와 결제자의 정보가 일치한지, 예약한 지 5분이 지나진 않았는지 등등 .. 비즈니스에 대해 고민하면 고민할수록 코드 구현은 늦춰져 간 것 같습니다.   
> 코드 구현을 잘 할 것인가, 비즈니스 플로우에 대해 깊게 고민할 것인가 갈등이 있었습니다만, 좀 더 나은 서비스를 제공하기 위해서는 유스케이스에 대한 심도있는 이해도가 필요하다고 느꼈습니다.   
> 상태 검증하는 부분이 많아질 수록 테스트 코드도 늘어나 정말 시간이 오래걸렸지만 값진 시간이었습니다...

- `2-3 서버 구축`
> **글로벌 에러 핸들링, 필터와 인터셉터 사용, 동시성 제어 및 통합테스트**   
> 글로벌 에러 핸들링은 이전주차에 끝내놓은지라 가벼운 마음으로 임했습니다. 이전 주 과제에는 코드 구현의 양이 많았어서 완성도가 떨어진다고 생각하여 리팩토링 하는 시간을 많이 가졌습니다.   
> 에러를 핸들링하는 방법은 정말 많습니다. try-catch, 각 컨트롤러에 @ExceptionHandler, @ControllerAdvice 등이 있는데   
> 도메인 레이어의 에러를 체계적으로 관리하고 싶었기에 @RestControllerAdvice 를 사용한 글로벌 에러 핸들러 클래스를 생성하고, ErrorCode 를 정의하여 사용했습니다.   
> `ErrorCode`에서 `HttpStatus`, `LogLevel`을 지정하면 더욱 깔끔한 에러 핸들링이 가능하여 흥미로웠습니다.   
> 필터나 인터셉터 사용을 위해 Servlet, DispatcherServlet 동작 방식을 찾아봤고, 다시금 이해하는데 도움을 받았습니다.   
> 동시성 제어로는 포인트 충전, 결제, 예약 생성과 같이 공유 자원의 상태 변경이 있는 부분들에 비관적락을 적용하여 처리하였습니다. `Filter`로 중복 요청을 방지할 수 있다는 인사이트를 얻어 추후 공부해볼 계획입니다.

**Chapter2 키워드**
- 시퀀스 다이어그램
- ERD 설계
- API 명세
- Restful API
- 대기열 시스템
- 토큰 발급과 검증
- 스프링 스케쥴러
- 글로벌 에러 핸들링
- Filter / Interceptor

## [콘서트 예약 시스템에서 동시성 제어 기법 활용하기](docs/06_Concurrency_Analysis_Report.md)