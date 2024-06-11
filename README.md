# 🎬**OTT괌**❗️
- 소개
    - 한 줄 정리 : OTT 멤버 구하기
    - 내용 :  본 프로젝트는 OTT 서비스의 멤버를 구하고, 관리하는 웹 애플리케이션을 개발하는 것을 목표로 합니다. 이 애플리케이션을 통해 사용자들은 자신이 가입하고자 하는 OTT 서비스의 멤버를 구할 수 있습니다.

- 프로젝트 기간 : 2024.06.04 ~ 2024.06.11 (8일)
- Teck Stack :  <img src="https://img.shields.io/badge/Java-007396?style=flat-square&logo=Java&logoColor=white"> <img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=Spring&logoColor=white"/>
* 버전: JDK 17
* 개발 환경: IntelliJ

## 👨‍👩‍👧‍👦팀 소개 및 역할 분담

1. 팀명 : TIl
    
    팀소개 : 11조의 11을 살려 TIl(소문자)로 하였고, 열심히 작성하자! 는 의미로 팀명을 만들었습니당!😘
    
2. 구현기능
   <details>
     <summary>✅필수 구현</summary> 
        - 사용자 인증 기능: 회원가입, 회원탈퇴, 로그인, 로그아웃<br>
        - 프로필 관리 기능: 프로필 조회, 프로필 수정<br>
        - 뉴스피드 게시물 CRUD 기능
     </details>
     
     <details>
    <summary>☑️추가 구현</summary>
        - 뉴스피드 추가구현: 페이지네이션, 정렬, 기간별 검색<br>
        - 댓글 CRUD<br>
        - 이메일 인증<br>
        - 게시글, 댓글 좋아요 기능<br>
        - swagger 적용<br>
        - 소셜 로그인<br>
        - 사진 업로드
    </details>
3. 역할분담
    - 이가은 [팀장]
        - 회원가입 / 회원탈퇴 (기본)
        - 좋아요 기능 구현 (추가)
    - 정현경
        - 회원정보 관리 (기본)
        - Swagger, Exception 적용 (추가)
        - 소셜 로그인  (추가)
    - 김경태
        - 로그인, 로그아웃 (기본)
        - 사진 업로드(회원 프로필) (추가)
    - 김세림
        - Newsfeed crud (기본)
        - 회원가입 - email 인증 / (추가)
    - 최현진
        - Comment CRUD (기본)
        - 뉴스피드 추가 기능 (추가)
## ✍🏻 API 명세
![newsfeed api](https://github.com/kyungtae42/newsfeed/assets/50200959/0350344b-ef60-47f0-8425-6cc396da4fba)

## 📋ERD
![스크린샷 2024-06-10 오후 8 57 39](https://github.com/kyungtae42/newsfeed/assets/50200959/ba8f0b4a-0e7a-43e3-ad9a-f29e347e100e)

## 😺 Github Rules
### 커밋 규칙

**[#이슈번호] +**

| 작업 타입 | 작업내용 |
| --- | --- |
| ✨ update   | 해당 파일에 새로운 기능이 생김 |
| 🎉 add | 없던 파일을 생성함, 초기 세팅 |
| 🐛 bugfix | 버그 수정 |
| ♻️ refactor | 코드 리팩토링 |
| 🩹 fix | 코드 수정 |
| 🚚 move | 파일 옮김/정리 |
| 🔥 del | 기능/파일을 삭제 |
| 🍻 test | 테스트 코드를 작성 |
| 💄 style | css |
| 🙈 gitfix | gitignore 수정 |
| 🔨script | package.json 변경(npm 설치 등) |

### 브랜치 규칙
{작업타입}/{이슈번호}-{이슈이름}<br>
ex) **feat/1-Newsfeed-CRUD**

<details>
  <summary>Issue 예시</summary>

  ![Untitled](https://github.com/kyungtae42/newsfeed/assets/50200959/718d9d89-8bbd-4691-8e20-d1f31bc0293a)

  ![image](https://github.com/kyungtae42/newsfeed/assets/50200959/b7ad75e1-cbb2-462d-bfec-92837f6a77c0)

</details>

<details>
  <summary>commit 예시</summary>
  
![Untitled (1)](https://github.com/kyungtae42/newsfeed/assets/50200959/9b10ce42-e247-4ac0-ad1d-9ee00cf9b961)
</details>
<details>
<summary>pr 예시</summary>
  ![image](https://github.com/kyungtae42/newsfeed/assets/50200959/a696d775-5535-45ed-acee-b8348e6fd177)
</details>

## 😅트러블 슈팅
1. h2를 사용하였더니 user, like와 같은 예약어로 테이블을 생성했을 때 `via JDBC [Syntax error in SQL statement "\000d\000a    create table [*]user (\000d\000a` 와 같은 에러 출력

→ 예약어 사용으로 테이블이 생성되지 않았기에 테이블명을 `user → users`로 변경

2. 비밀번호 비교 시 문자열을 encode() 하여 Objects.equals()로 비교하였더니 항상 false 출력

→ salt값과 rounds가 다르기에 문자열로 비교가 불가능하고 passwordEncoder.matches()를 이용하여 비밀번호를 비교해야함

3. 팀원과의 충분한 상의 없이 dev 브랜치로 merge 시도

→ 구현한 기능들을 merge할 때는 팀원과 충분히 충돌 여부를 상의하고 팀원들이 보는 앞에서 할 것
