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
      Serial.println("LED 켜짐");
      digitalWrite(LED, LOW);
      servo.write(0);
      GoHome();
    }
    else if (value == "1") {
      Serial.println("LED 꺼짐");
      digitalWrite(LED, HIGH);
      servo.write(100);
      GoHome();
    }
    else
      Serial.println(value);
  }
  else
    Serial.println("null");
}


void handleSetAlarm0() {
  String nameOfArg = "timeCode0";
  String valueOfArg = server.arg(nameOfArg);
  if (server.hasArg(nameOfArg)) {
    Serial.println("켜짐(ON) 예약: " + valueOfArg);
  }
  else
    Serial.println("null");
}

void handleSetAlarm1() {
  String nameOfArg = "timeCode1";
  String valueOfArg = server.arg(nameOfArg);
  if (server.hasArg(nameOfArg)) {
    Serial.println("꺼짐(OFF) 예약: " + valueOfArg);
  }
  else
    Serial.println("null");
}
