#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h> // https://github.com/tzapu/WiFiManager
#include <time.h> // 시계 라이브러리

#include <Ticker.h>
#include <Servo.h>

#define TRIGGER_PIN 0   // trigger pin 0(D3) 2(D4)
#define SERVO_PIN 2     // 서보모터 핀(D4)

ESP8266WebServer server(80);
String sChipId="";
char cChipId[40]="";

Ticker ticker;  // LED status
Servo servo;    // 서보모터 status
String timeCode[2] = {"", ""};
char curTime[20]; 
struct tm *t;

int bootMode=0; //0:station  1:AP
int LED = LED_BUILTIN;
unsigned long now,lastConnectTry = 0,count=0;

void tick() {
  //toggle state
  digitalWrite(LED, !digitalRead(LED));     // set pin to the opposite state
}

void setup() {
  Serial.begin(115200);

  // 서보모터 핀 세팅
  servo.attach(SERVO_PIN);

  // AP 이름 자동으로 만듬 i2r-chipid
  sChipId = "SmartSwitch(i2r-" + String(ESP.getChipId(),HEX) + ")";
  sChipId.toCharArray(cChipId,sChipId.length()+1);
  Serial.println(sChipId);

  wifiManager();

  server.on("/", handleRoot);
  server.on("/scan", handleScan);
  server.on("/wifi", handleWifi);
  server.on("/onoff", handleOnOff);
  server.on("/setAlarm0", handleSetAlarm0);
  server.on("/setAlarm1", handleSetAlarm1);
  
  server.onNotFound(handleNotFound);
  
  server.begin();
  Serial.println("HTTP server started");

  configTime(9*3600, 0, "pool.ntp.org", "time.nist.gov");  // Timezone 9 for Korea
  while (!time(nullptr)) delay(500);
}

void wifiManager() {
  pinMode(LED, OUTPUT);
  // start ticker with 0.5 because we start in AP mode and try to connect
  ticker.attach(0.6, tick);

  //WiFiManager
  WiFi.mode(WIFI_STA); // explicitly set mode, esp defaults to STA+AP
  WiFiManager wm;
  // wm.resetSettings(); //reset settings - for testing

  if (!wm.autoConnect(cChipId)) {
    Serial.println("failed to connect and hit timeout");
    //reset and try again, or maybe put it to deep sleep
    ESP.restart();
    delay(1000);
  }

  //if you get here you have connected to the WiFi
  Serial.println("connected... :)");
  ticker.detach();
  //keep LED on

  digitalWrite(LED, LOW);  
}

void loop() {
  server.handleClient();

  now = millis();
  //와이파이 연결되면 1초 간격으로 점등
  unsigned int sWifi = WiFi.status();
  if(now >= (lastConnectTry + 3000)) {
    Serial.println ( WiFi.localIP() );
    lastConnectTry=now;
    count++;
    if(count>=3600*24)
      count=0;
  }

  // 서버에서 현재 시각 가져오기
  time_t now = time(nullptr);
  t = localtime(&now);
  // sprintf(curTime,"%04d-%02d-%02d %02d:%02d:%02d", t->tm_year+1900, t->tm_mon+1, t->tm_mday, t->tm_hour, t->tm_min, t->tm_sec);
  // Serial.println(curTime);

  // 알람 0: 현재 시각과 알람 시각이 일치하면 알람 작동
  if(timeCode[0] == String(t->tm_hour*100 + t->tm_min)) {
    setServo0();
  }

  // 알람 1: 현재 시각과 알람 시각이 일치하면 알람 작동
  if(timeCode[1] == String(t->tm_hour*100 + t->tm_min)) {
    setServo1();
  }

  // 공장 초기화
  if ( digitalRead(TRIGGER_PIN) == LOW ) 
    factoryDefault();
}
