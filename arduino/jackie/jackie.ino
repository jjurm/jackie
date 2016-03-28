#include <Servo.h>

#define pin_l 10
#define pin_r 11

class ServoController;

class ServoController{
public:
  Servo l;
  Servo r;
  
  ServoController() {
    l.attach(pin_l);
    r.attach(pin_r);
  }
  
  void move(int langle, int rangle) {
    l.write(langle);
    r.write(rangle);
    Serial.print("Moving with angles");  
    Serial.print(langle);
    Serial.print(" ");
    Serial.println(rangle);
  }
  
  void movel(int angle){
    l.write(angle);
    Serial.print("Moving l with angle ");
    Serial.print(angle);
    Serial.println();
  }
  
  void mover(int angle){
    r.write(angle);
    Serial.print("Moving r with angle ");
    Serial.print(angle);
    Serial.println();
  }
};

ServoController *servoController = new ServoController();

class SerialCommander{
public:
  String stack = "";
  int index = 0;
  String cmd = "";
  
  void addToStack(){
    stack.concat(Serial.readString());
  }
  
  void executeStack(){
    while(true){
        index = stack.indexOf(";")+1;
        cmd = stack.substring(0, index);
        stack = stack.substring(index);
        if(cmd != "") execute(cmd);
        cmd.trim();
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
      
      servoController->movel(langle);
      servoController->mover(rangle);
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

void setup() {
  Serial.begin(115200);
}



void loop() {
  if(Serial.available()){
    serialCommander->addToStack();
  }
  serialCommander->executeStack();
  
}




