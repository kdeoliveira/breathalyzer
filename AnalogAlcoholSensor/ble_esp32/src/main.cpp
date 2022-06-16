#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <Arduino.h>

// BLE server name
#define bleServerName "ABreath"

#define R0_BAC 400

const int buttonPin = 13;     // the number of the pushbutton pin
int buttonState = 0;
uint8_t ledR = 2;
uint8_t ledG = 4;
uint8_t ledB = 5;

uint8_t ledArray[3] = {1, 2, 3}; // three led channels

uint8_t color = 0;        // a value from 0 to 255 representing the hue
uint32_t R, G, B;         // the Red Green and Blue color components
uint8_t brightness = 255; // 255 is maximum brightness, but can be changed.  Might need 256 for common anode to fully turn off.

int drunk = 0.08; // threshold of drunk
int peak = 0;     // peak value
int prepeak = 0;  // previous peak value
int initial = 0; //intial value

std::__cxx11::string message = ""; // message from app
char incomingChar;                 // message from app

int analogValue = 0;
bool Flag = false; // flag for start measure
bool FlagBT = false; // flag for start measure from BT
int counter = 0;   // counter if has reach the peak value
int skipCounter = 0; // counter for skipping
float valueToSend;

// Timer variables
unsigned long lastTime = 0;
unsigned long timerDelay = 300;

// Controls the flow of communication
// the BLE part is comming from the example of Arduino BLE:
//https://github.com/espressif/arduino-esp32/tree/master/libraries/BLE/examples
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
    Serial.println("Callback onWrite");
    isReadyToSend = true;
    message = pCharacteristic->getValue();
  }
};

BLECharacteristic *pCharacteristic;
BLEServer *pServer;

//this algrathm is offerd by:
//DFRobot, “How to use the MQ-3 alcohol sensor,” DFRobot, 08-Apr-2018. [Online]. Available: https://www.dfrobot.com/blog-903.html. [Accessed: 15-Jun-2022]. 
float analogToBac(int analogValue){
    
    float RS_gas; // Get value of RS in a GAS
    float ratio; // Get ratio RS_GAS/RS_air
    float sensor_volt;
    sensor_volt=(float)analogValue/1024*5.0;
    RS_gas = (5.0-sensor_volt)/sensor_volt; // omit *RL
 
   /*-Replace the name "R0" with the value of R0 in the demo of First Test -*/
    ratio = RS_gas/R0_BAC;  // ratio = RS/R0

    return 0.1896*pow(ratio,2) - 8.6178*ratio/10 + 1.0792;   //BAC in mg/L
}


// the BLE part is comming from the example of Arduino BLE:
//https://github.com/espressif/arduino-esp32/tree/master/libraries/BLE/examples
void sendToBluetooth(int val){
  float num = static_cast<float>(val);
  static char buff[8];
  dtostrf(num, 5, 2, buff);
  pCharacteristic->setValue(buff);
  pCharacteristic->notify();
  Serial.print("val ble: ");
  Serial.println(num);
}

void setup()
{
  Serial.begin(115200);
  Serial.println("Starting BLE work!");
// the BLE part is comming from the example of Arduino BLE:
//https://github.com/espressif/arduino-esp32/tree/master/libraries/BLE/examples
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
  //Serial.println("Characteristic defined! Now you can read it in your phone!");
  // hardware setting
  analogReadResolution(12); // adc resolution
//the RGB is comming from ESP32 examples:
//https://github.com/espressif/arduino-esp32/tree/master/libraries/ESP32/examples
  ledcAttachPin(ledR, 1); // assign RGB led pins to channels
  ledcAttachPin(ledG, 2);
  ledcAttachPin(ledB, 3);

  // Initialize channels
  ledcSetup(1, 12000, 8); // 12 kHz PWM, 8-bit resolution
  ledcSetup(2, 12000, 8);
  ledcSetup(3, 12000, 8);

  pinMode(buttonPin, INPUT); // button pin
}

void loop()
{
  buttonState = digitalRead(buttonPin);

  if (buttonState == HIGH) {
    Serial.println("Button pressed");
    // turn LED on:
    Flag = true;
    initial = analogRead(14);
    Serial.print("Analog read: ");
    Serial.println(initial);
    delay(500);
    }


  if (Flag)
    {
        if (analogRead(14)>initial+100||analogRead(14)<initial-100 ){
            // read the analog / millivolts value for pin 2:
            analogValue = analogRead(14);
            // Serial.print("analogValue: ");
            Serial.println(analogValue);
            // Serial.print("prepeak: ");
            // Serial.println(prepeak);
            // Serial.print("counter: ");
            // Serial.println(counter);
            // Serial.print("peak: ");
            // Serial.println(peak);
            delay(150);
        


        
        
        // store peak value
            if (prepeak >= analogValue)
            {
            peak = prepeak;
            counter++;
            }
            else
            {
            peak = analogValue;
            prepeak = peak;
            counter = 0;
            }
            //
            
            R = 255;
            G = 255;
            B = 255;
            // white
            ledcWrite(1, R); // write red component to channel 1, etc.
            ledcWrite(2, G);
            ledcWrite(3, B);
            
        }
        // has reach the peak value
        if(counter >= 20)
        { counter = 0;
          prepeak = 0;
          Flag = false;
          if (analogToBac(peak) > drunk)
          {
            R = 255;
            G = 0;
            B = 0;
            ledcWrite(1, R); // write red component to channel 1, etc.
            ledcWrite(2, G);
            ledcWrite(3, B);
            initial = 0;
            Serial.print("BAC Value");
            Serial.println(analogToBac(peak));
            delay(2000);
          }
          else
          {
            R = 0;
            G = 255;
            B = 0;
            ledcWrite(1, R); // write red component to channel 1, etc.
            ledcWrite(2, G);
            ledcWrite(3, B);
          }
          Flag = false;
          peak = 0;
          prepeak = 0;
          initial = 0;
          Serial.print("BAC Value");
          Serial.println(analogToBac(peak));
          delay(2000);
        }
    }
  /******************************************************/
  if (deviceConnected)
  {
    if ((millis() - lastTime) > timerDelay)
    {

      // check from app for starting
      if (message == "M")
      {
        // Generating random numbers
        FlagBT = true;
        initial = analogRead(14);
        delay(1000); 
        message = "";
      }

      // measuring
      if (FlagBT)
      {
           if (analogRead(14)>initial+100||analogRead(14)<initial-100 ){
                // read the analog / millivolts value for pin 2:
                analogValue = analogRead(14);
                // Serial.print("analogValue: ");
                Serial.println(analogValue);
                // Serial.print("prepeak: ");
                // Serial.println(prepeak);
                // Serial.print("counter: ");
                // Serial.println(counter);
                // Serial.print("peak: ");
                // Serial.println(peak);




                
                
                // store peak value
                if (prepeak >= analogValue)
                {
                peak = prepeak;
                counter++;
                }
                else
                {
                peak = analogValue;
                prepeak = peak;
                counter = 0;
                }
                
                R = 255;
                G = 255;
                B = 255;
                // white
                ledcWrite(1, R); // write red component to channel 1, etc.
                ledcWrite(2, G);
                ledcWrite(3, B);
                // send peak
                if (isReadyToSend)
                {
                    int temp = analogToBac(peak) * 100;
                    sendToBluetooth(temp);
                }
                
           }
        // has reach the peak value
        if(counter >= 8)
        {
          if (isReadyToSend)
          {
            sendToBluetooth(-1);
            isReadyToSend = false;
          }
          counter = 0;
          prepeak = 0;
          FlagBT = false;
          if (analogToBac(peak) > drunk)
          {
            R = 255;
            G = 0;
            B = 0;
            ledcWrite(1, R); // write red component to channel 1, etc.
            ledcWrite(2, G);
            ledcWrite(3, B);
            initial = 0;
            delay(2000);
          }
          else
          {
            R = 0;
            G = 255;
            B = 0;
            ledcWrite(1, R); // write red component to channel 1, etc.
            ledcWrite(2, G);
            ledcWrite(3, B);
            initial = 0;
            delay(2000);
          }
          FlagBT = false;
          peak = 0;
          prepeak = 0;
          message = "";
        }
      }
      prevDeviceConnected = true;
      lastTime = millis();
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
