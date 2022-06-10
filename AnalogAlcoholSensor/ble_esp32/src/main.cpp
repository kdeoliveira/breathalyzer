#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <Arduino.h>

// BLE server name
#define bleServerName "ABreath"

uint8_t ledR = 2;
uint8_t ledG = 4;
uint8_t ledB = 5; 

uint8_t ledArray[3] = {1, 2, 3}; // three led channels

uint8_t color = 0;          // a value from 0 to 255 representing the hue
uint32_t R, G, B;           // the Red Green and Blue color components
uint8_t brightness = 255;  // 255 is maximum brightness, but can be changed.  Might need 256 for common anode to fully turn off.

int drunk = 2000; // threshold of drunk
int peak = 0; //peak value
int prepeak = 0; //previous peak value

std::__cxx11::string message = "";//message from app
char incomingChar; // message from app

int analogValue = 0;
bool Flag = false; //flag for start measure
int counter = 0; // counter if has reach the peak value

float hum;

// Timer variables
unsigned long lastTime = 0;
unsigned long timerDelay = 300;

// Controls the flow of communication
bool deviceConnected = false;
bool prevDeviceConnected = false;

bool isReadyToSend = false;

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/
#define SERVICE_UUID "91bad492-b950-4226-aa2b-4ede9fa42f59"
#define CHARACTERISTIC_UUID "ca73b3ba-39f6-4ab3-91ae-186dc9577d99"

// Setup callbacks onConnect and onDisconnect
class MyServerCallbacks : public BLEServerCallbacks
{
  void onConnect(BLEServer *pServer)
  {
    deviceConnected = true;
  };
  void onDisconnect(BLEServer *pServer)
  {
    deviceConnected = false;
  }
};

class MyCharacteristicCallback : public BLECharacteristicCallbacks
{

  void onWrite(BLECharacteristic *pCharacteristic, esp_ble_gatts_cb_param_t *param)
  {
    isReadyToSend = true;
    message = pCharacteristic->getValue();    
  }
};

BLECharacteristic *pCharacteristic;
BLEServer *pServer;
void setup()
{
  Serial.begin(115200);
  Serial.println("Starting BLE work!");

  BLEDevice::init(bleServerName);
  pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);
  pServer->setCallbacks(new MyServerCallbacks());
  pCharacteristic = pService->createCharacteristic(CHARACTERISTIC_UUID, BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_WRITE);
  // pCharacteristic->setValue("First string sent");
  pCharacteristic->setCallbacks(new MyCharacteristicCallback());

  pService->start();

  // BLEAdvertising *pAdvertising = pServer->getAdvertising();  // this still is working for backward compatibility
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();

  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(true);
  pAdvertising->setMinPreferred(0x06); // functions that help with iPhone connections issue
  pAdvertising->setMinPreferred(0x12);

  BLEDevice::startAdvertising();
  Serial.println("Characteristic defined! Now you can read it in your phone!");
  // hardware setting
  analogReadResolution(12); //adc resolution

  ledcAttachPin(ledR, 1); // assign RGB led pins to channels
  ledcAttachPin(ledG, 2);
  ledcAttachPin(ledB, 3);
  
  // Initialize channels 
  ledcSetup(1, 12000, 8); // 12 kHz PWM, 8-bit resolution
  ledcSetup(2, 12000, 8);
  ledcSetup(3, 12000, 8);
  
}

void loop()
{
  Serial.println("Working");
  if (deviceConnected)
  {
    Serial.println("deviceConnected");
    if ((millis() - lastTime) > timerDelay) {
    // Generating random numbers

  //check from app for starting
  if(message == "M") 
  Flag = true;

  // measuring
  if(Flag){
  // read the analog / millivolts value for pin 2:
  analogValue = analogRead(14);
  Serial.println(analogValue);
  //store peak value
  if (prepeak>=analogValue){
  peak = prepeak;
  counter++;
  }
  else{
  peak = analogValue;
  prepeak = peak;
  counter = 0;}
  //
  if(counter<20&&counter!=0){
  R=255;
  G=255;
  B=255;
  //white
  ledcWrite(1, R); // write red component to channel 1, etc.
  ledcWrite(2, G);   
  ledcWrite(3, B); 
  //send peak
  if(isReadyToSend){
  static char buff[3];
  itoa(peak,buff, 6);
  pCharacteristic->setValue(buff);
  pCharacteristic->notify();  
  }
  } 
  //has reach the peak value
  else{
  if(isReadyToSend){
  static char buff[3];
  itoa(9999,buff, 6);
  pCharacteristic->setValue(buff);
  pCharacteristic->notify();  
  isReadyToSend = false;
  }
  counter = 0;
  prepeak = 0;
  Flag = false;
  if(peak > drunk){
  R=255;
  G=0;
  B=0;
  ledcWrite(1, R); // write red component to channel 1, etc.
  ledcWrite(2, G);   
  ledcWrite(3, B); 
  }
  else{
  R=0;
  G=255;
  B=0;
  ledcWrite(1, R); // write red component to channel 1, etc.
  ledcWrite(2, G);   
  ledcWrite(3, B); 
  }
  }
  prevDeviceConnected = true;
  lastTime = millis();
    }
  }
  }
  else if (!deviceConnected && prevDeviceConnected)
  {
        Serial.println("!deviceConnected && prevDeviceConnected");
    delay(500);
    pServer->startAdvertising(); // Restart scanning
    Serial.println("Scanning");
    prevDeviceConnected = deviceConnected;
  }
  else if (deviceConnected && !prevDeviceConnected)
  {
        Serial.println("!deviceConnected && !prevDeviceConnected");
    prevDeviceConnected = deviceConnected;
    Serial.println("Connecting...");
  }
  
}