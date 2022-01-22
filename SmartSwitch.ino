
#include <ESP8266WiFi.h>

const char* ssid = "HomeNet(2.4GHz)";
const char* password = "1593570ab";
int ledPin = 2; // D4 Pin
WiFiServer server(80);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  delay(10);

  //LED 세팅
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);

  //WiFi 연결
  WiFi.disconnect(true);
  delay(1000);
  Serial.print("\nConnecting to ");
  Serial.print(ssid);
  
  WiFi.begin(ssid, password);
  while(WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP Address: ");
  Serial.println(WiFi.localIP());

  //Server 시작
  server.begin();
  Serial.println("Server started");
  Serial.print("Use this URL to connect: ");
  Serial.print("http://");
  Serial.print(WiFi.localIP());
  Serial.print("/");

}

void loop() {
  // put your main code here, to run repeatedly:
  //client 접속 확인
  WiFiClient client = server.available();
  if(!client){
    return;
  }

  //client가 보내는 데이터를 기다린다.
  Serial.println("new client");
  while(!client.available()){
    delay(1);
  }

 //요청을 읽는다.
 String request = client.readStringUntil('\r');
 Serial.println(request);
 client.flush();

 //요청에 url에 따라 LED를 ON/Off
 if (request.indexOf("/ledOn") > 0){
  digitalWrite(ledPin, HIGH);
 }
 if (request.indexOf("/ledOff") > 0){
  digitalWrite(ledPin, LOW);
 }
 

 // Return the response 웹브라우저에 출력할 웹페이지
  client.println("HTTP/1.1 200 OK");  
  client.println("Content-Type: text/html");
  client.println(""); //  do not forget this one
  client.println("<!DOCTYPE HTML>");
  client.println("<html>");
  client.println("<head>");
  client.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
  client.println("<meta name='apple-mobile-web-app-capable' content='yes' />");
  client.println("<meta name='apple-mobile-web-app-status-bar-style' content='black-translucent' />");
 client.println("</head>");
  client.println("<body bgcolor = \"#fff\">"); 
  client.println("<div style=\"width: 100%; margin:0 auto; text-align: center;\"><h4>NodeMCU Device Control</h4></div>");
  client.println("<div style=\"width: 100%; margin:0 auto; text-align:center\">");
  client.println("LED<br>");
  client.println("<a href=\"/ledOn\"\"><button>Turn On</button></a>");
  client.println("<a href=\"/ledOff\"\"><button>Turn Off</button></a><br />");  
  client.println("</div>");
  client.println("</body>");  
  client.println("</html>"); 
  delay(1);
}
