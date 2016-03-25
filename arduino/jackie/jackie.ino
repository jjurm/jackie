using namespace std;

class SerialCommander;
class Command;

/*
 Comamnd utility class
 */
class Command {
public:
  char data[10];
  int length = 0;
  Command(){
    clean();
  }

  void clean() {
    for(int i = 0; i < 10; i++) {
      data[i] = 0;
    }
    length = 0;
  }
  
  bool append(char c) {
    data[length] = c;
    length++;
  }
  
  void send() {
    for (int i = 0; i < length; i++) {
      Serial.print(data[i]);
      if(i < length - 1){
        Serial.print(" ");
      } else {
        Serial.println(";");   
      }      
    }
  }
};

class SerialCommander {
public:
  
  void send(Command command) {
    command.send();
  }
  
  Command receive() {
    int cSize = 10;
    char c[cSize];
    Serial.readBytesUntil(';', c, cSize);
    Command command;
    for(int i = 0; i < cSize; i++) {
      command.append(c[i]);
    }
    return command;
  }
  
  void execute(Command command) {
    for(int i = 0; i < command.length;i++){
      Serial.print(command.data[i]);
    }
    Serial.println(";");
  }
};

void setup() {
  Serial.begin(115200);
}

SerialCommander serialCommander;

void loop() {
  if(Serial.available()){
  Command command = serialCommander.receive();
  serialCommander.execute(command);
  }
}




