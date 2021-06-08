![image](https://user-images.githubusercontent.com/34666301/121111148-576f1e80-c849-11eb-8210-f03fb126fd62.png)

우주지상국 서브시스템들과 1:1 통신을 위한 TCP 클라이언트 공통 모듈을 개발합니다. 


# Netty 프레임워크를 선택한 이유
- 팀내에서 POJO Socket 기반의 레거시 TCP 클라이언트 공통 모듈에 대한 문제 제기가 있었습니다. (복잡성, 낮은 가독성)
- 그리고 충분한 검토 후 다음의 가치 때문에 Netty 프레임워크를 선택하게 되었습니다. 

### 구조적 프로그래밍
- Netty 프레임워크의 Pipeline 아키텍처는 개발자로 하여금 구조적인 프로그래밍을 하도록 가이드합니다. 
- 예를 들면 다음과 같은 TCP 채널의 수신 처리 로직들이 Pipeline에 의해 구조화 될 수 있습니다. 
```
  1. 메시지 수신 (EventLoop 처리)
  2. 메시지 바운더리 구분
  3. 메시지 포멧 변환 
  4. 메시지 해석 
  5. 메시지 검증 
  6. 메시지 로깅 
  7. 메시지 전달
```
### 높은 확장성 
- 역시 Pipeline 아키텍처로 인하여 통신 처리 기능들을 확장하기 쉽습니다. 
- 그래서 소프트웨어를 점진적으로 확장해 나갈 수 있습니다. 예를 들면 최소 기능 제품(MVP)을 빠르게 만들어야 하는 경우 다음과 같이 핵심 기능만 빠르게 먼저 만들고 고도화된 기능들은 추후에 Pipeline 에 추가하기 쉽습니다. 
```
  1. 메시지 수신 (EventLoop 처리)
  2. 메시지 바운더리 구분
  3. 메시지 포멧 변환 
  4. 메시지 해석 
  5. 메시지 검증 -------> 안정화를 위한 기능임으로 추후 구현
  6. 메시지 로깅 -------> 선택적인 기능임으로 추후 구현
  7. 메시지 전달
```
- 그래서 초보개발자가 개발한 코드도 추후 개선해 나가고 확장해 나가기 쉽습니다. 

### 완벽한 수준의 메시지 바운더리 서비스
- TCP는 스트림기반 프로토콜이기 때문에 메시지를 구분해주는 로직이 사용자 코드에 반드시 추가되어야 합니다. ([Dealing with a Stream-based Transport
](https://netty.io/4.0/api/io/netty/handler/codec/DelimiterBasedFrameDecoder.html))
- 많은 개발자들이 위와 같은 필요를 알지 못하고, 일반적인 상황에서만 잘 동작하는 불안정한 코드를 작성하는 경우가 많습니다. 
- Netty 에서는 다양한 형태의 Frame Decoder를 제공합니다. 
  - 고정 길이 메시지 : [FixedLengthFrameDecoder](https://netty.io/4.0/api/io/netty/handler/codec/FixedLengthFrameDecoder.html)
  - 가변길이 메시지 (Length 필드 기반): [LengthFieldBasedFrameDecoder](https://netty.io/4.0/api/io/netty/handler/codec/LengthFieldBasedFrameDecoder.html)
  - 가변 길이 메시 (End 태그 기반) : [DelimiterBasedFrameDecoder](https://netty.io/4.0/api/io/netty/handler/codec/DelimiterBasedFrameDecoder.html)

### 수신 쓰레드 서비스 
- 메시지 수신을 위한 별도의 쓰레드를 생성하고 관리하지 않아도 Pipeline 과 연계되는 EventLoop 서비스를 제공합니다. 

### 안전한 종료 서비스 
- 대게 수신 쓰레드를 안전하게 종료하기 위하여 이벤트 객체 (또는 플래그)와 수신 루프상에 체크 로직을 필요로합니다. 
- EventLoop.shutdownGracefully() 서비스를 통해 사용자 코드에서 위와 같은 로직을 제거하고, 통신 코드 자체에 집중할 수 있습니다. 

# 주요 개발 이슈
- 안정적인 연결 코드 만들기
- 연결 성공할 때 까지 반복하기
- 끊어진 연결 자동 복구하기
