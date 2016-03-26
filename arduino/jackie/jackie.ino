
void execute(String cmd){
  Serial.println(cmd);
}

void setup() {
  Serial.begin(115200);
}


void loop() {
  String stack = "";
  int index = 0;
  String cmd = "";
  if(Serial.available()){
    stack.concat(Serial.readString());
  }
  while(true){
    index = stack.indexOf(";")+1;
    cmd = stack.substring(0, index);
    stack = stack.substring(index);
    if(cmd != "") execute(cmd);
    if(stack == ""){
      break;
    }
  }
}




