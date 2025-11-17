# 사용할 기본 이미지
FROM ubuntu:22.04

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y build-essential libmodbus-dev git

# apt 캐시 정리 (선택 사항)
RUN rm -rf /var/lib/apt/lists/*

# 작업 디렉토리 설정
WORKDIR /app

# 현재 디렉토리의 모든 파일을 컨테이너의 /app 디렉토리로 복사
COPY . /app

# C++ 소스 코드 컴파일
# -I/usr/include/modbus 옵션을 추가하여 modbus.h 헤더 파일 경로를 지정
RUN g++ modbus_server.cpp -o modbus_server -lmodbus -I/usr/include/modbus

# 컨테이너가 시작될 때 실행할 명령어
CMD ["./modbus_server"]