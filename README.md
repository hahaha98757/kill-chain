# Kill Chain
GTA 온라인의 강제 종료 메크로입니다.

서버와 연결하여 유저 한명이 강제 종료를 하면 서버에 접속한 모든 유저도 강제 종료가 진행됩니다.

지원 정보<br>
- 운영체제: Windows 10 64비트
- 게임 정보: Grand Theft Auto 5 레거시 및 인핸스드

## 사용법
1. 서버를 실행하고, 포트를 지정하여 서버를 엽니다.
2. 클라이언트를 실행하고, 닉네임, 호스트의 IP, 포트를 입력하여 서버에 접속합니다.(닉네임은 중복될 수 없으며, 'server'로 설정할 수 없습니다.)
3. 'F2' 키를 눌러 테스트를 합니다. 테스트 성공 시 소리 재생과 함께 테스트를 시도한 유저로부터 테스트를 받았다고 출력됩니다.
4. 'ESC' 키와 'F1' 키를 동시에 눌러 강제 종료를 할 수 있습니다.

### 싱글모드
서버 접속 없이 단독으로 메크로를 사용합니다.

- 클라이언트를 실행하고, 닉네임을 'client'로 설정합니다.

## 명령어
명령어는 대소문자를 구분하지 않습니다.

- CLS: 출력된 텍스트를 위로 올립니다.
- EXIT: 프로그램을 종료합니다.
- HELP: 사용 가능한 명령어 목록을 출력합니다.
- KILL: 강제 종료 신호를 전달합니다.
- LIST: 서버에 접속한 유저 목록을 출력합니다. (싱글모드에서 사용 불가.)
- PORT: 서버의 포트를 확인합니다. (싱글모드에서 사용 불가.)
- TEST: 테스트 신호를 전달합니다.

## 라이선스
이 프로젝트는 [LICENSE](LICENSE) 파일의 전문에 따라 MIT 허가서가 적용됩니다.
<br>라이선스 및 저작권 고지 하에 개인적 이용, 수정, 배포, 상업적 이용이 가능하며 보증 및 책임을 지지 않습니다.

## 크레딧
- kwhat의 [jnativehook](https://github.com/kwhat/jnativehook/tree/2.2.2) ([GNU 약소 일반 공중 사용 허가서 v3.0](licenses/JNativeHook-LICENSE))
- JetBrains의 [Kotlin](https://github.com/JetBrains/kotlin) ([아파치 라이선스 2.0](https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt))
- [beep.wav](src/main/resources/beep.wav): Mixkit의 [Censorship beep](https://mixkit.co/free-sound-effects/beep/) ([Sound Effects Free License](https://mixkit.co/license/#sfxFree))
- [clientIcon](clientIcon.ico): Freepik의 [섬기는 사람 무료 아이콘](https://www.flaticon.com/kr/free-icon/server_3962020) ([Flaticon 라이센스](licenses/Icon-LICENSE))
- [serverIcon](serverIcon.ico): Freepik의 [체인 무료 아이콘](https://www.flaticon.com/kr/free-icon/chain_1660930?related_id=1660962&origin=search) ([Flaticon 라이센스](licenses/Icon-LICENSE))

----

## 업데이트 로그

### 1.0.1
- JVM 설치 필요 없는 exe 파일로 배포.

### 1.0.0
- 매크로 개발.