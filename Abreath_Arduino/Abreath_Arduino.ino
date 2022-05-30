

#include "BluetoothSerial.h" // include bluetooth library
 

int drunk = 2000; // threshold of drunk

BluetoothSerial SerialBT;
void setup() {

   pinMode(25, OUTPUT); //led pin
   
  // initialize serial communication at 115200 bits per second:
  Serial.begin(115200);
  // begin bluetooth serial
  SerialBT.begin("Abreath");
  //set the resolution to 12 bits (0-4096)
  analogReadResolution(12);
}

void loop() {

  // read the analog / millivolts value for pin 2:
  int analogValue = analogRead(14);
  //turn on led if user is drunk
  if(analogValue > drunk)
  digitalWrite(25, HIGH);
  else
  digitalWrite(25, LOW);
  //print out value in serial monitor/plotter
  Serial.println(analogValue);
  
  //transfer through bluetooth
  SerialBT.println(analogValue);

  delay(100);

   // delay in between reads for clear read from serial
}
