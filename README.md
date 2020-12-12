# IoT_Smart_Safe (도난 방지를 위한 IoT 스마트 금고)
## 목차
[1. IoT 스마트 금고 구조도](#1-IoI-스마트-금고-구조도)

[2. IoT 스마트 금고 주요 기능](#2-IoI-스마트-금고-주요-기능)

[3. Arduino](#3-Arduino)

[4. AWS](#4-AWS)

[5. Android](#5-Android)

## 1. IoT 스마트 금고 구조도

![AWS_서비스_구조도](https://user-images.githubusercontent.com/71610969/101992748-c9323980-3cf8-11eb-8de2-5bb12c1d0cb0.png)

## 2. IoT 스마트 금고 주요 기능

**1) 금고 여닫힘 시간 조회<br/>**
&nbsp;&nbsp;금고가 열리고 닫히는 시간을 DynamoDB에 저장했다가 안드로이드 앱을 통하여 어디서든 조회할 수 있다. 금고의 여닫힘 시간을 통해 금고를 언제 사용했는지 확인하여 사용자가 아닌 다른 사람이 금고를 사용하였는지 알 수 있다.<br/>
<br/>
**2) 금고 충격 발생 경고 메일<br/>**
&nbsp;&nbsp;금고에 외부에 의한 충격이 가해졌을 때, 금고가 충격이 발생하여 위험하다는 경고 메일을 보내 사용자가 외부에 있어도 빠르게 금고에 위험을 감지할 수 있다.<br/>
<br/>
**3) 충격 발생 감지 제어<br/>**
&nbsp;&nbsp;안드로이드 앱을 통해 금고에 충격 감지를 실행시킬지 말지 제어할 수 있다. 이 제어는 가까이 있지 않아도 가능하다.<br/>
<br/>
**4) 충격 발생 시간 조회<br/>**
&nbsp;&nbsp;금고에 충격이 발생한 시간을 DynamoDB에 저장했다가 안드로이드 앱을 통하여 어디서든 조회할 수 있다. 금고에 충격이 발생한 시간을 조회하여 그 시간에 맞게 CCTV등을 활용하여 금고에 손 댄 사람을 찾으면 좀 더 찾기 수월해진다.

## 3. Arduino

![ArduinoMKR](https://user-images.githubusercontent.com/71610969/101993183-f8967580-3cfb-11eb-9314-6bf5bfbc95f1.PNG)

- IoT 스마트 금고에 사용한 부품
  - ArduinoMKRWiFi1010
  - 디지털 충격 센서 : 금고에 충격을 감지
  - 조도 모듈 센서 : 빛의 밝기를 이용하여 금고에 여닫힘을 감지
  - LED
  
### AWS_IoT_Safe (아두이노 소스코드)
**1) arduino_secrets.h<br/>**
&nbsp;와이파이를 연결하고 AWS의 인증서를 사용하여 AWS와 연동하는 코드<br/><br/>
**2) Shock.h, Shock.cpp<br/>**
&nbsp;디지털 충격 센서 연결, 충격에 의한 상태 정보 설정<br/><br/>
**3) Open.h, Open.cpp<br/>**
&nbsp;조도 모듈 센서 연결, 금고에 여닫힘 상태 정보 설정<br/><br/>
**4) LED.h, LED.cpp<br/>**
&nbsp;LED 연결, LED 경고등 및 충격 감지 실행 LED 설정<br/><br/>
**5) AWS_IoT_Safe.ino<br/>**
&nbsp;디지털 충격 센서와 조도 모듈 센서 값에 따라 금고에 상태 정보를 바꾸고 AWS에 데이터를 보내고 받는 아두이노 코드 <br/><br/>

## 4. AWS
### 1. AWS IoT 설정
1. AWS IoT Core에서 'SmartSAFE' 사물 생성
2. Amazon SNS에 새 주제 생성 후, 사용자의 메일로 구독
3. DynamoDB에서 기본키= deviceId, 정렬키 = time인 ShockLogging 테이블 생성
4. DynamoDB에서 기본키= deviceId, 정렬키 = time인 OpenLogging 테이블 생성

### 2. AWS Lambda를 이용해 함수 생성
**1)	ShockLogDeviceLambdaJavaProject** <br/> 
– DynamoDB에 ShockLogging에서 SHOCK 정보를 조회<br/> <br/> 
**2)	RecordingShockDataLambdaJavaProject** <br/> 
– 충격이 발생하면 DynamoDB에 ShockLogging 테이블에 SHOCK 정보 저장<br/> <br/> 
**3)	RecordingOpenDataLambdaProject** <br/> 
– 금고가 열리고 닫히면 DynamoDB에 OpenLogging 테이블에 SAFE 정보 저장<br/> <br/> 
**4)	GetShockRunLambdaJavaProject** <br/> 
– 충격 감지 실행 상태(SHOCK_RUN) 정보 조회<br/> <br/> 
**5)	ShockSNSLambda** <br/> 
– 충격 발생 시, 경고 메일 전송<br/> <br/> 
**6)	UpdateShockRunLambdaJavaProject** <br/> 
– 충격 감지 실행 상태(SHOCK_RUN) 변경<br/> <br/> 
**7)	OpenLogDeviceLambdaJavaProject** <br/> 
– DynamoDB에 OpenLogging에서 SAFE 정보를 조회<br/> <br/> 

### 3. Rule 생성
AWS IoT Core에서 SNS를 보내기 위한 Rule과 DynamoDB에 상태 정보를 저장하기 위한 Rule 생성<br/>
1. SNS Rule
규칙(Rule)을 생성하고 다음 규칙 쿼리 설명문으로 설정
```
SELECT * FROM '$aws/things/SmartSAFE/shadow/update/accepted'
```
작업을 추가하여 *'메시지 데이터를 전달하는 Lambda 함수 호출'* 을 추가하고 앞서 ShockSNSLambda를 이용해 만든 Lambda 함수를 적용시킨다.

2. DynamoDB Logging Rule
규칙(Rule)을 생성하고 다음 규칙 쿼리 설명문으로 설정

```
SELECT *, 'SmartSAFE' as device FROM '$aws/things/SmartSAFE/shadow/update/documents'
```
작업을 추가하여 *'메시지 데이터를 전달하는 Lambda 함수 호출'* 을 추가하고 앞서 RecordingShockDataLambdaJavaProject와 RecordingOpenDataLambdaProject를 이용해 만든 Lambda 함수를 적용시킨다.

### 4. RestAPI 생성

API Gateway에서 다음과 같이 RestAPI 생성<br/>
![api](https://user-images.githubusercontent.com/71610969/101994034-81181480-3d02-11eb-9dfe-1f2cc8fce13d.JPG)
<br/>

**1) /devices/{device}<br/>**
  -	GET: 금고(아두이노)의 충격 감지 실행 상태 정보를 조회하는 RestAPI - [GetShockRunLambdaJavaProject를 이용해 만든 Lambda 함수를 적용]<br/>
  
  매핑 템플릿 설정 - [application/json]
  ```
  {
  "device": "$input.params('device')"
  }
  ```
  -	PUT: 금고(아두이노)의 충격 감지를 제어하는 RestAPI - [UpdateShockRunLambdaJavaProject를 이용해 만든 Lambda 함수를 적용]<br/>
<br/>
  
  매핑 템플릿 설정 - [application/json]
  ```
  #set($inputRoot = $input.path('$'))
{
    "device": "$input.params('device')",
    "tags" : [
    ##TODO: Update this foreach loop to reference array from input json
        #foreach($elem in $inputRoot.tags)
        {
            "tagName" : "$elem.tagName",
            "tagValue" : "$elem.tagValue"
        } 
        #if($foreach.hasNext),#end
        #end
    ]
}
  ```

**2) /devices/{device}/openlog<br/>**
  -	GET: DynamoDB에서 OpenLogging 테이블에 있는 내용을 조회하는 RestAPI - [OpenLogDeviceLambdaJavaProject를 이용해 만든 Lambda 함수를 적용]<br/>
<br/>
  
  매핑 템플릿 설정 - [application/json]
  ```
{
  "device": "$input.params('device')",
  "from": "$input.params('from')",
  "to":  "$input.params('to')"
}
  ```

**3) /devices/{device}/shocklog<br/>**
  -	GET: DynamoDB에서 ShockLogging 테이블에 있는 내용을 조회하는 RestAPI - [ShockLogDeviceLambdaJavaProject를 이용해 만든 Lambda 함수를 적용]<br/>
<br/>
  
  매핑 템플릿 설정 - [application/json]
  ```
{
  "device": "$input.params('device')",
  "from": "$input.params('from')",
  "to":  "$input.params('to')"
}
  ```

## 5. Android

### 1. UI Java Code

**1) MainActivity**<br/>
  - 안드로이드 홈 화면
  - 버튼을 클릭하면 다음 Activity에 RestAPI URL 전달
  
![androidHome](https://user-images.githubusercontent.com/71610969/101994269-8a09e580-3d04-11eb-87c0-229a8b469818.PNG)
<br/> [Smart_Safe 홈 화면]<br/><br/>

**2) DeviceActivity.java** <br/>
  - 금고 충격 감지 제어 화면
  - 조회 시작을 누르면 현재 금고에 충격 감지 실행 정보를 표시
  - 실행을 누르면 충격 감지를 실행, 중지를 누르면 충격 감지 중지

![androidShockRun](https://user-images.githubusercontent.com/71610969/101994482-18cb3200-3d06-11eb-815d-46e9c21d984e.PNG)
<br/> [충격 감지 제어 화면]<br/><br/>
![androidShockRunStop](https://user-images.githubusercontent.com/71610969/101994523-42845900-3d06-11eb-9d0c-7923c759817c.PNG)
<br/> [조회 시작 클릭 시]<br/><br/>
![안드로이드 실행중](https://user-images.githubusercontent.com/71610969/101994534-592ab000-3d06-11eb-8258-5161bfd14bb2.PNG)
<br/> [실행 클릭 시]<br/><br/>

**3) OpenLogActivity.java, ShockLogActivity.java** <br/>
  - 금고 여닫힘 시간 조회 및 충격 발생 시간 조회 화면
  - Day를 누르면 일별로 시간을 조회, Month를 누르면 월별로 시간을 조회
![금고 여닫힘 조회](https://user-images.githubusercontent.com/71610969/101994663-6005f280-3d07-11eb-9630-4a6a8e88d7e2.PNG)
<br/> [금고 여닫힘 시간 조회]<br/><br/>
![충격 발생 로그 조회](https://user-images.githubusercontent.com/71610969/101994680-78760d00-3d07-11eb-9c41-cbd771b52640.PNG)
<br/> [충격 발생 시간 조회]<br/><br/>

**4) MyYearMonthPickerDialog.java** <br/>
  - 년도와 월만 선택할 수 있는 DatePickerDialog
  - 월별 시간 조회를 하기 위해 생성

<br/>

### 2. apicall Java Code

**1) GetThingShadow.java**<br/>
  - API URL에서 얻어온 json 코드를 바탕으로 충격 감지 실행 상태 확인
  
**2) UpdateShadow.java**<br/>
  - 앱에서 API URL을 통해 AWS에 DeviceShadow를 변경시켜 충격 감지 상태 변경
  
**3) GetOpenLog.java, GetShockLog.java**<br/>
  - API URL에서 얻어온 json 코드를 바탕으로 OpenLogging과 ShockLogging 데이터 조회
  
<br/>
