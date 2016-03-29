#include <Servo.h>

#define pin_l 10
#define pin_r 11

Servo l;
Servo r;

class ServoController;

class ServoController{
public:
  
  void move(int langle, int rangle) {
    l.write(langle);
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

void setup() {
  Serial.begin(115200);
  l.attach(10);
  r.attach(11);
  l.write(90);
  r.write(90);
}



void loop() {
  Serial.println("Adding to stack...");
  serialCommander->addToStack();
  Serial.println("Executing stack...");
  serialCommander->executeStack();
  
}




