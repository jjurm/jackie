#include <Wire.h>
#include <Servo.h>


const int SLAVE_ADRESS = 4;

const int LEFT_MOTOR = 11;
const int RIGHT_MOTOR = 10;
const int FRONT_ARM = 9;
const int BACK_SHUTTER = 6;
const int CAMERA_CONTROL = 5;

const int US_TRIGGER = A1;
const int US_ECHO[6] = {A2, A3, 0, 1, 2, 4};
const int NUMBER_OF_US_SENSORS = 6;

Servo servo[6];

byte requestedMessage[6];
int messageLength = 0;

void setup() {
  //====== SERVOS
  servo[0].attach(LEFT_MOTOR);
  servo[1].attach(RIGHT_MOTOR);
  servo[2].attach(FRONT_ARM);
  servo[4].attach(BACK_SHUTTER);
  servo[5].attach(CAMERA_CONTROL);

  //====== US SENSORS
  pinMode(US_TRIGGER, OUTPUT);
  for (int i = 0; i < NUMBER_OF_US_SENSORS; i++){
    pinMode(US_ECHO[i], INPUT);
  }
  
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
        
      //========== SINGLE SERVO COMMAND ========= 
      case 10:{
        if (receivedMessage[0] & 7 == 1){
          servo[receivedMessage[0] & 7].write(180 - receivedMessage[1]);
        } else {
          servo[receivedMessage[0] & 7].write(receivedMessage[1]);  
        }
                
      }break;

      //========== SERVO GROUP COMMAND ==========
      case 11:{
      
        switch (receivedMessage[0] & 7){
          
          case 0:
            servo[0].write(receivedMessage[1]);
            servo[1].write(180 - receivedMessage[2]);
            break;
          default:
            messageLength = 0;
            break;
        }
      }break;

      //=========== ULTRESONIC SENSORS ==========
      case 20:{
        ;
        digitalWrite(US_TRIGGER, HIGH);
        delayMicroseconds(2);
        digitalWrite(US_TRIGGER, LOW);
        int pulseDuration = pulseIn(US_ECHO[receivedMessage[0] & 7], HIGH);
        if (pulseDuration > 38000){
          pulseDuration = 38000;
        }
        requestedMessage[0] = pulseDuration >> 8;
        requestedMessage[1] = pulseDuration & 0xff;
        messageLength = 2;
        
      }break;

      //========== DEFAULT ==========
      default:{
        messageLength = 0;
      }break;
    }
}

