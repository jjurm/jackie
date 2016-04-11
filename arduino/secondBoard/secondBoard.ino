#include <Wire.h>


const int SLAVE_ADRESS = 4;

const int BOTTOM_BACKLIGHT = 6;

byte requestedMessage[6];
int messageLength = 0;

void setup() {
  pinMode(BOTTOM_BACKLIGHT, OUTPUT);
  
  //====== I2C
  Wire.begin(SLAVE_ADRESS);
  Wire.onReceive(receiveEvent);
  Wire.onRequest(requestEvent);
}

void loop() {
  //nothing for now
}

void receiveEvent(int bytesReceived){
  byte receivedMessage[bytesReceived];
  for (int i = 0; i < bytesReceived; i++){
    receivedMessage[i] = Wire.read();
  }
  processMessage(receivedMessage);
}


void requestEvent(){
  Wire.write(requestedMessage, messageLength);
}


void processMessage(byte receivedMessage[]){
    switch (receivedMessage[0] >> 3){

      //========== TEST ==========
      case 1:{
        requestedMessage[0] = receivedMessage[1];
        messageLength = 1;
      }break;

      //========== LIGHTS ===========
      case 12:{
          switch (receivedMessage[0] & 7){
            case 0:
              digitalWrite(BOTTOM_BACKLIGHT, receivedMessage[1]);
              break;
            default:
              messageLength = 0;
              break;
          }
        }break;

      //========== DEFAULT ==========
      default:{
        messageLength = 0;
      }break;
    }
}

