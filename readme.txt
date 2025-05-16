

하실때, .env 파일 생성 필요합니다.

DB_URL=jdbc:oracle:thin:@localhost:1521:XE
DB_USERNAME=system
DB_PASSWORD=

password는 docker로 oracle 이미지를 올리셨을 경우, 그때 비밀번호로 작성하시면됩니다.
env파일은 gitignore로 올려놨으니 걱정하지 않으셔도됩니다.

그리고, 제 도커가 8080포트를 사용해서, 일단 8081포트로 서버를 띄우도록 변경하였습니다.