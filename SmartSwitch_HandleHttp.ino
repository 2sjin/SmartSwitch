
/** Handle root or redirect to captive portal */
void handleRoot() {
  String s; 
  s="<meta name='viewport' content='width=device-width, initial-scale=1.0'/>";
  //s += "<meta http-equiv='refresh' content='5'/>";
  s += "<meta http-equiv='Content-Type' content='text/html;charset=utf-8' />";
  s += "<h1>아두이노 웹 서버 i2r.link</h1>";

  s += "<FORM method='get' action='onoff'>";
  s += "<p><button type='submit' name='LEDstatus' value='1' style='background-color:Lime; color:blue;'>켜짐 on</button></p>";
  s += "&nbsp&nbsp";
  s += "<p><button type='submit' name='LEDstatus' value='0' style='background-color:Gray; color:blue;'>꺼짐 off</button></p>";
  s += "</FORM>";
  
  s += "<br><br>AP & IP :&emsp;"+sChipId+"&emsp;<a href='http://"+WiFi.localIP().toString()+"'/>"+WiFi.localIP().toString()+"</a>";
  s += "<p><a href='/wifi'>네트웍공유기변경-누른후 와이파설정부터 다시하세요</a></p>";

  server.send(200, "text/html", s);
}

void handleWifi() {
  WiFiManager wm;
  wm.resetSettings();
  wm.resetSettings();
  ESP.reset();
}

void GoHome() {
  String s,ipS;
  //IPAddress ip;
  ipS=toStringIp(WiFi.localIP());
  s="<meta http-equiv='refresh' content=\"0;url='http://"+ipS+"/'\">";
  server.send(200, "text/html", s);
}

void handleNotFound() {
  String message = "File Not Found\n\n";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET) ? "GET" : "POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i = 0; i < server.args(); i++) {
    message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
  }
  server.send(404, "text/plain", message);
}

void handleScan() {
  String s;
  s="{\"chipId\":\""+sChipId+"\",\"ip\":\""+WiFi.localIP().toString()+"\"}";
  server.send(200, "text/html", s);
}

void handleOnOff() {
  String value = server.arg("LEDstatus");
  if (server.hasArg("LEDstatus")) {
    if (value == "0") {
      digitalWrite(LED, LOW);
      setServo0();
      GoHome();
    }
    else if (value == "1") {
      digitalWrite(LED, HIGH);
      setServo1();
      GoHome();
    }
    else
      Serial.println(value);
  }
  else
    Serial.println("null");
}

void setServo0() {
  Serial.print("서보모터 0 동작(+40)\t");
  for(int i = 0; i < 40; i++) {  // 현재 각도에서 40도 더해주기
    angle = angle + 1;   
    if(angle >= 180)
      angle = 180;  
    servo.write(angle); 
    delay(10);
  }
  Serial.println(angle);  // 현재 각도 출력
}

void setServo1() {
  Serial.print("서보모터 1 동작(-40)\t");
  for(int i = 0 ; i < 40 ; i++) {  // 현재 각도에서 40도 빼주기
    angle = angle - 1;
    if(angle <= 0)
      angle = 0;
    servo.write(angle);
    delay(10);
  }
  Serial.println(angle);  // 현재 각도 출력
}

void handleSetAlarm0() { setAlarm(0); }
void handleSetAlarm1() { setAlarm(1); }

void setAlarm(int num) {
  String nameOfArg = "timeCode" + String(num);
  String valueOfArg = server.arg(nameOfArg);
  timeCode[num] = valueOfArg;
  if (server.hasArg(nameOfArg))
    Serial.println(nameOfArg + " = " + valueOfArg + ", now = " + String(t->tm_hour*100 + t->tm_min));
  else
    Serial.println("null");
}
