# 요구사항 정의서 개인 프로젝트
[Techit] 백엔드 스쿨 10기 : velog 클론 프로젝트

<br>

## 요구사항
[백엔드10기_요구사항정의서_프로젝트요구사항(1).pdf](https://github.com/user-attachments/files/16124893/10._._.1.pdf)
<br>
[프로젝트요구사항02.pdf](https://github.com/user-attachments/files/16124897/02.pdf)

<br>

## 기능 및 API 명세서
[기능 및 API 명세서](https://www.notion.so/6-18-7-19-velog-1c65278f35c247a79896f8f9c9bb1d48)

<br>

## ✔️ 기능 체크리스트

### 1. 회원가입
- [x] 회원 가입 폼
- [x] 같은 ID, Email Check API
- [x] 회원 등록 기능
- [x] 회원 가입 후 환영 페이지로 이동 -> 홈, 로그인 페이지로 이동

### 2. 로그인
- [x] 로그인 폼
- [x] 로그인 기능
    - [x] 로그인 성공 후 `/`로 이동
    - [x] 로그인 실패 후 다시 로그인 폼으로 이동 (오류 메시지 출력)
- [x] Spring Security 를 이용한 로그인 구현
    - [x] Form Login
    - [x] JWT Login
    - [x] OAuth2 로그인

### 3. 사이트 상단
- [x] 사이트 로고가 좌측 상단에 보여짐
- [x] 로그인 여부에 따른 우측 정보 표시
    - [x] 로그인하지 않았을 경우 로그인 링크
    - [x] 로그인했을 경우 사용자 프로필 이미지
        - [x] 사용자 이름 클릭 시 설정, 해당 사용자 블로그 이동 링크, 임시 저장글 목록 보기, 로그아웃 링크 표시

### 4. 로그아웃
- [x] 로그아웃 기능

### 5. 메인 페이지 (/)
- [x] 블로그 글 목록 보기
    - [x] 최신 순
    - [x] 일간 트렌딩
    - [x] 주간 트렌딩
    - [x] 월간 트렌딩
    - [x] 연간 트렌딩
    - [ ] 피드 (팔로우한 사람들의 최신순 글)
- [x] 페이징 처리 또는 무한 스크롤 구현 (한 번에 전부 불러옴)
- [ ] 검색 기능
    - [ ] 제목
    - [ ] 내용
    - [ ] 사용자 이름

### 6. 블로그 글 쓰기
- [x] 블로그 제목, 내용, 사진 입력 기능
- [x] "출간하기" 버튼 (기존 출간 유무에 따라 "수정하기")
    - [x] 블로그 썸네일(이미지)
    - [x] 공개 유무
    - [ ] 시리즈 설정
    - [x] "출간하기" 클릭 시 글 등록
    - [x] 게시글 작성 실시간 미리보기
- [x] "임시저장" 버튼 (첫 임시저장과 기존 임시저장)
- [x] "나가기" 버튼 - 이전 위치로 이동
- [x] 출간하기 폼

### 7. 임시 글 저장 목록 보기
- [x] 로그인 시 메뉴 바 - 임시 글 목록 보기 링크
- [x] 임시글 저장 목록 표시
    - [x] 글 제목 클릭 시 글 수정 가능
    - [x] 삭제 클릭 시 임시 글 삭제 가능 
    - [x] "임시저장" 및 "출간하기" 기능

### 8. 특정 사용자 블로그 글 보기 (/@사용자아이디)
- [ ] 사용자 정보 보기
- [x] 사용자 글 목록 보기
    - [x] 최신 순
- [x] 페이징 처리 또는 무한 스크롤 구현 (한 번에 전부 불러옴)
- [x] 사용자 태그 목록 보기
    - [x] 태그당 글 수 표시
    - [x] 특정 태그 클릭 시 해당 태그로 필터링한 게시글 조회
- [ ] 검색 기능
    - [ ] 제목
    - [ ] 내용

### 9. 시리즈 목록 보기
- [ ] 시리즈 목록 보기 기능
- [ ] 시리즈 제목 클릭 시 시리즈에 포함된 블로그 글 목록 보기

### 10. 블로그 글 상세 보기
- [x] 메인 페이지에서 이미지, 제목 클릭 시 블로그 글 상세 보기
- [x] 특정 사용자 블로그에서 이미지, 제목 클릭 시 블로그 글 상세 보기
- [ ] 시리즈에 속한 블로그 글 목록에서 제목 클릭 시 블로그 글 상세 보기

### 11. 사용자 정보 보기 (설정)
- [ ] 메뉴 바에서 설정 클릭 시 사용자 정보 보기
    - [ ] 사용자 이름
    - [ ] 이메일
    - [ ] 회원 탈퇴 링크

### 12. 회원 탈퇴
- [ ] 회원 탈퇴 확인 폼
- [ ] 폼에서 확인 시 회원 탈퇴 (회원 정보 삭제)

### 13. 댓글 목록 보기
- [x] 블로그 글 상세 보기에서 댓글 목록 표시
    - [x] 자신이 단 댓글 삭제
    - [x] 자신이 단 댓글 수정
- [x] 댓글과 대댓글 오래된 순 표시
- [ ] 댓글 최대 20개 페이징 처리

### 14. 댓글 달기
- [x] 블로그에 댓글 달기
- [x] 댓글에 대댓글 달기

### 15. 블로그 글에 좋아요 하기
- [ ] 블로그 글에 좋아요 기능

### 16. 이외의 기능
- [ ] 벨로그 사이트 분석 (URL 및 파라미터 등)
- [ ] 벨로그와 유사하거나 더 편리한 기능 구현
- [ ] 프론트 개발 학습 및 React.js 등 프론트 개발 별도 구현 제안
- [x] 벨로그의 특별한 기능 추가 구현
    - [x] 게시글 작성 시 마크다운 지원
    - [x] 이미지 붙여넣기 및 파일 선택으로 삽입 가능
- [x] 게시글 삭제 (썸네일, 게시글 내 이미지도 로컬에서 삭제)