#include <TinyGPS++.h>
#include <HardwareSerial.h>
#include "BluetoothSerial.h"

TinyGPSPlus gps;
HardwareSerial SerialGPS(2);
BluetoothSerial SerialBT;

#define BUZZER_PIN 25

float lastLat = 0.0;
float lastLng = 0.0;
bool locationInitialized = false;
bool alarmActive = false;
bool alarmUserActivated = false;

const float warningThreshold = 3.0;  // Peringatan jika motor bergerak lebih dari 20 meter
const float alarmThreshold = 5.0;    // Alarm aktif jika motor bergerak lebih dari 25 meter
const float gpsStabilizationThreshold = 0.0002;

unsigned long alarmActivatedTime = 0;
bool waitingToInitialize = false;
const unsigned long initDelay = 8000;

unsigned long lastSendTime = 0;
const unsigned long sendInterval = 2000;

void setup() {
  Serial.begin(115200);
  SerialGPS.begin(9600, SERIAL_8N1, 16, 17);
  SerialBT.begin("ESP32-GPS");
  pinMode(BUZZER_PIN, OUTPUT);
  digitalWrite(BUZZER_PIN, LOW);
  Serial.println("Bluetooth siap. Pasangkan dengan HP.");
  SerialBT.println("Bluetooth siap. Pasangkan dengan HP.");
}

void loop() {
  while (SerialGPS.available() > 0) {
    gps.encode(SerialGPS.read());
  }

  if (millis() - lastSendTime >= sendInterval && gps.location.isUpdated() && gps.location.isValid()) {
    lastSendTime = millis();

    float lat = gps.location.lat();
    float lng = gps.location.lng();

    Serial.print("Lat: "); Serial.println(lat, 6);
    Serial.print("Lng: "); Serial.println(lng, 6);
    SerialBT.print("Lat: "); SerialBT.println(lat, 6);
    SerialBT.print("Lng: "); SerialBT.println(lng, 6);

    if (alarmUserActivated && !locationInitialized) {
      if (waitingToInitialize && millis() - alarmActivatedTime >= initDelay) {
        lastLat = lat;
        lastLng = lng;
        locationInitialized = true;
        waitingToInitialize = false;
        Serial.println("✅ Lokasi awal disimpan setelah delay 8 detik.");
        SerialBT.println("✅ Lokasi awal disimpan setelah delay 8 detik.");
      }
    }

    if (alarmUserActivated && locationInitialized) {
      float distance = calculateDistance(lastLat, lastLng, lat, lng);
      Serial.print("Jarak ke titik awal: ");
      Serial.print(distance);
      Serial.println(" meter");

      SerialBT.print("Jarak ke titik awal: ");
      SerialBT.print(distance);
      SerialBT.println(" meter");

      if (distance > warningThreshold && distance <= alarmThreshold) {
        Serial.println("⚠️ Motor menjauh! (5m+)");
        SerialBT.println("⚠️ Motor menjauh! (5m+)");
      }

      if (distance > alarmThreshold && !alarmActive) {
        digitalWrite(BUZZER_PIN, HIGH);
        alarmActive = true;
        Serial.println("🚨 Alarm AKTIF! Jarak > 7m");
        SerialBT.println("🚨 ALARM! Motor terlalu jauh! (>7m)");
      }

      if (distance <= warningThreshold && alarmActive) {
        digitalWrite(BUZZER_PIN, LOW);
        alarmActive = false;
        Serial.println("✅ Alarm DIMATIKAN (motor kembali dekat)");
        SerialBT.println("✅ Alarm dimatikan otomatis.");
      }
    }

    SerialBT.println("----------------------");
    Serial.println("----------------------");
  }

  if (SerialBT.available()) {
    char cmd = SerialBT.read();
    Serial.print("Perintah dari HP: ");
    Serial.println(cmd);
    SerialBT.print("Perintah dari HP: ");
    SerialBT.println(cmd);

    if (cmd == '1') {
      alarmUserActivated = true;
      alarmActive = false;
      locationInitialized = false;
      digitalWrite(BUZZER_PIN, LOW);
      waitingToInitialize = true;
      alarmActivatedTime = millis();
      Serial.println("🔒 Alarm DIHIDUPKAN (menunggu GPS stabil 8 detik)");
      SerialBT.println("🔒 Alarm aktif. Tunggu GPS stabil.");
    } else if (cmd == '0') {
      alarmUserActivated = false;
      alarmActive = false;
      locationInitialized = false;
      digitalWrite(BUZZER_PIN, LOW);
      Serial.println("🔓 Alarm DIMATIKAN");
      SerialBT.println("🔓 Alarm dimatikan oleh pengguna.");
    }
  }
}

float calculateDistance(float lat1, float lng1, float lat2, float lng2) {
  const float R = 6371000;
  float dLat = radians(lat2 - lat1);
  float dLng = radians(lng2 - lng1);
  float a = sin(dLat / 2) * sin(dLat / 2) +
            cos(radians(lat1)) * cos(radians(lat2)) *
            sin(dLng / 2) * sin(dLng / 2);
  float c = 2 * atan2(sqrt(a), sqrt(1 - a));
  float distance = R * c;

  Serial.print("Menghitung jarak... ");
  Serial.print(distance);
  Serial.println(" meter");
  SerialBT.print("Menghitung jarak... ");
  SerialBT.print(distance);
  SerialBT.println(" meter");

  return distance;
}
