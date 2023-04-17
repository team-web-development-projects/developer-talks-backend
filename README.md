# 사용환경
jdk 17

# 실행 방법

## 로컬에서 실행하는 방법
1. jdk 설치 (https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. 환경 변수 설정 (https://coding-factory.tistory.com/823)
3. 백엔드 레포지토리 git clone
4. cmd창에서 서버 실행 (https://ottl-seo.tistory.com/21)

# swagger
http://localhost:8080/swagger-ui/index.html

# git branch 전략
개발자는 feature* 브랜치를 따와서 각자 작업한다.

각 개발자들이 개발한 기능들은 통합하여 dev에서 테스트 한다.

일정 단위의 통합 기능들을 모두 테스트 하고 이상 없으면 main으로 올린다.