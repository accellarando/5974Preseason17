#include <Stepper.h>
#include <Wire.h>

void requestEvent();
char duck;
void setup() {
  Wire.begin(8);
  Wire.onReceive(recieveEvent);
  Wire.onRequest(requestEvent);
  Serial.begin(9600);
}
void loop() {
  delay(100);
}

void recieveEvent(int howMany) {
  char c;
  while (1 < Wire.available() ) {
    c = Wire.read();
    Serial.print(c); 
    }
  int x = Wire.read();
  Serial.println(x);
  duck = c;
  Serial.println(duck);
  }


void requestEvent() {
  if (duck == 'L'){
    Wire.write(0);
    }
  else if (duck == 'R'){
    Wire.write(1);
    } 
   else {
   Wire.write(2);
    }
}


