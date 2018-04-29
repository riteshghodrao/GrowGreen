#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include "DHT.h"        // including the library of DHT11 temperature and humidity sensor
#define DHTTYPE DHT11   // DHT 11
#define dht_dpin D1 
DHT dht(dht_dpin, DHTTYPE); 
// Set these to run example.
// D2  -- > Temp Humidity Sensor
// A0  -- > Analog of Soil Moisture Sensor
// D5  -- > Motor Signal
// D1  -- > Light Signal
#define FIREBASE_HOST "project1-2c36e.firebaseio.com"
#define FIREBASE_AUTH "2Zb3XgvWlGgCt39vFU4ZPmBI7O6QDvAy90ukTMVV"
#define WIFI_SSID "growgreen"
#define WIFI_PASSWORD "sdpdproject"

#define motor D2
#define light D5

const int soil = A0;
int val=0;

void setup(void) {
        dht.begin();
        Serial.begin(9600);
        delay(800);
        pinMode(motor, OUTPUT);
        pinMode(light, OUTPUT);
        digitalWrite(motor,HIGH);
        digitalWrite(light,HIGH);
        WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
        Serial.print("connecting");
        while (WiFi.status() != WL_CONNECTED) {
              Serial.print(".");
              delay(500);
        }
        Serial.println();
        Serial.print("connected: ");
        Serial.println(WiFi.localIP());
        Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
        
}

void loop() {
    
    if(Firebase.getInt("motor"))
    {
        digitalWrite(motor,LOW);
        
    }
    else
    {
        digitalWrite(motor,HIGH);
    }
    if(Firebase.getInt("light"))
    {
        digitalWrite(light,LOW);
        
    }
    else
    {
        digitalWrite(light,HIGH);
    }
    
    if (Firebase.failed()) // Check for errors 
    {
        Serial.print("setting /number failed:");
        Serial.println(Firebase.error());
        return;
    }
    //delay(2000);
    
    float h = dht.readHumidity();
    float t = dht.readTemperature();
    Serial.print("Current humidity = ");
    Serial.print(h);
    Serial.print("%  ");
    Serial.print("temperature = ");
    Serial.print(t);
    Serial.println();
    
    val=analogRead(soil);
    //delay(1000);
    Serial.print("Soil Moisture = ");
    Serial.print(val);
    Serial.println();
    
    //delay(2000);
    
    Firebase.pushInt("Moisture", val);
    Firebase.pushFloat("Humidity", h);
    Firebase.pushFloat("Temperature", t);


    delay(2000);

}
