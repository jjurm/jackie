#include <Servo.h>

#define pin_l 10
#define pin_r 11

Servo l;
Servo r;

class ServoController;

class ServoController{
public:
  
  void move(int langle, int rangle) {
    l.write(180 - langle);
    r.write(rangle);
    Serial.print("Moving with angles: ");
    Serial.print(langle);
    Serial.print(", ");
    Serial.println(rangle);
  }
};

ServoController *servoController = new ServoController();

class SerialCommander{
public:
  String stack = "";
  int buff[10][10];
  int index = 0;
  String cmd = "";
  
  void addToStack(){
    //stack.concat(Serial.readString());
  }
  
  void executeStack(){
    while(true){
        index = stack.indexOf(";")+1;
        cmd = stack.substring(0, index);
        stack = stack.substring(index);
        cmd.trim();
        if(cmd != "") execute(cmd);
        if(stack == ""){
          break;
        }
      }
  }
  
  void execute(String cmd){
    int space = cmd.indexOf(" ");
    String first = cmd.substring(0, space);

    cmd = cmd.substring(space+1);
    
    switch(first.toInt()){
    case 1:
      space = cmd.indexOf(" ");
      
      int langle = cmd.substring(0, space).toInt();
      cmd = cmd.substring(space+1);
      
      int rangle = cmd.substring(0, cmd.indexOf(";")).toInt();
      
      servoController->move(langle, rangle);
      break;
    }
  }
  
  String buildCommand(char c[], int length){
    String s = "";
    for(int i = 0; i < length; i++){
      if(i == length-1){
        s.concat(c[i]);
        s.concat(";");
      } else {
        s.concat(c[i]);
        s.concat(" ");
      }
    }
    return s;
  }
  
  void send(String cmd) {
    Serial.println(cmd);
  }
};

SerialCommander *serialCommander = new SerialCommander();

int dIndex = 0;
int buff[5];
int nbuff[5];
int nIndex = 0;

int clangle = 90;
int crangle = 90;
int difference;

void processCommand(int arr[]) {
  switch(arr[0]){
    case 1:
    	int langle = arr[1];
    	int rangle = arr[2];
    	//int langle = arr[1] + (arr[1] - clangle)/3; //change the speed slowly
    	//int rangle = arr[1] + (arr[2] - crangle)/3;
      
      servoController->move(langle, rangle);
      break;
  }
}

int readCharacter(){
    int n = Serial.read();
    if (n == -1) {
      return 0;
    }
    char c = char(n);
    if(isDigit(c)){
      buff[dIndex] = c - '0';
      dIndex++;
    } else {
      int num = 0;
      for(int i = 0; i < 5;i++){
        if (buff[i] != -1) {
          num = num*10 + buff[i];
          buff[i] = -1;
        }
      }
      dIndex = 0;
      
      nbuff[nIndex] = num;
      nIndex++;
      if (c == ';') {
        processCommand(nbuff);
        for (int i = 0; i < 5; i++) {
          nbuff[i] = -1;
        }
        nIndex = 0;
      }
    }
    return 1;
}

int result;
unsigned long timediff;
unsigned long currtime;

void setup() {
  Serial.begin(115200);
  l.attach(10);
  r.attach(11);
  l.write(90);
  r.write(90);
  currtime = millis();
}

void loop() {
  timediff = millis() - currtime;
  //if (Serial.available())
  if(readCharacter()){
    currtime = millis();
  } else if(timediff > 1000) {//if not getting commands for a second, stop motors
    l.write(90);
    r.write(90);
  }
  //}
}
