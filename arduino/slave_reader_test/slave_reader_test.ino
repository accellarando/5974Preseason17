#include <Stepper.h>
#include <Wire.h>

void requestEvent();
char ducc;
int spped
void setup() {
  Wire.begin(8);
  Wire.onReceive(recieveEvent);
  Wire.onReceive(updateStep);
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
  ducc = c;
  spped = x;
  }


void requestEvent() {
  if (ducc == 'L'){
    Wire.write(0);
    }
  else if (ducc == 'R'){
    Wire.write(1);
    } 
   else {
   Wire.write(2);
    }
}

void updateStep(){
  int stepp = 1;
  if(ducc == 'L'){
    stepp*(-1)
  }
  else
}


