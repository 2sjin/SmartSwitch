/** Handle root or redirect to captive portal */
void handleRoot() {
  String s; 
  s="<meta name='viewport' content='width=device-width, initial-scale=1.0'/>";
  //s=s+"<meta http-equiv='refresh' content='5'/>";
  s=s+"<meta http-equiv='Content-Type' content='text/html;charset=utf-8' />";
  s=s+"<h1>아두이노 웹 서버 i2r.link</h1>";
  s=s+"<a href=\"on\"><button style='background-color:Lime; color:blue;'>켜짐 on</button></a>";
  s=s+"&nbsp&nbsp";
  s=s+"<a href=\"off\"><button style='background-color:Gray; color:blue;'>꺼짐 off</button></a>";
  s=s+"<br><br>AP & IP :&emsp;"+sChipId+"&emsp;<a href='http://"+WiFi.localIP().toString()+"'/>"+WiFi.localIP().toString()+"</a>";
  s=s+"<p><a href='/wifi'>네트웍공유기변경-누른후 와이파설정부터 다시하세요</a></p>";
  server.send(200, "text/html", s);
}

void handleWifi() {
  WiFiManager wm;
  wm.resetSettings();
  wm.resetSettings();
  ESP.reset();
}

void handleOn() {
  digitalWrite(LED, LOW);
  servo.write(180);
  GoHome();
}

void handleOff() {
  digitalWrite(LED, HIGH);
  servo.write(-90);
  GoHome();
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

void handleAlarm() {
  String s;
  s=s+"<meta http-equiv='Content-Type' content='text/html;charset=utf-8'/>\n";
  s=s+"<p class='a_on'>켜짐(ON) 예약 알람 설정 중입니다.</p>\n";
  s=s+"<p class='a_off'>꺼짐(OFF) 예약 알람 설정 중입니다.</p>\n";
  
  s=s+"<script type='text/javascript'>\n";

  s=s+"function setAlarm0(hour, minute) {\n";
  s=s+"var alarmOn = document.getElementsByClassName('a_on')[0];\n";
  s=s+"alarmOn.innerHTML = 'ON,\t' + hour + ', ' + minute;\n}";

  s=s+"function setAlarm1(hour, minute) {\n";
  s=s+"var alarmOff = document.getElementsByClassName('a_off')[0];\n";
  s=s+"alarmOff.innerHTML = 'OFF,\t' + hour + ', ' + minute;\n}";

  s=s+"</script>";
  server.send(200, "text/html", s);
}
