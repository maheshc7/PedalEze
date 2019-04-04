package com.smazee.product.pedaleze;

import java.util.UUID;


public class CustomBluetoothProfile {

    public static class Basic {
        // Mi Band
//        public static UUID service = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");
//        public static UUID batteryCharacteristic = UUID.fromString("00000006-0000-3512-2118-0009af100700");

        // i6HRc

        public static UUID service = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
//        public static UUID batteryCharacteristic = UUID.fromString("0000180F-b5a3-f393-e0a9-e50e24dcca9e");

        public static UUID batteryCharacteristic = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    }

    public static class AlertNotification {
        public static UUID service = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
        public static UUID alertCharacteristic = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");
    }

    public static class HeartRate {
//        Mi Band
        public static UUID service = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
        public static UUID measurementCharacteristic = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
        public static UUID descriptor = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        public static UUID controlCharacteristic = UUID.fromString("00002a38-0000-1000-8000-00805f9b34fb");

//        i6HR c
//        public static UUID service = UUID.fromString("0000180d-b5a3-f393-e0a9-e50e24dcca9e");
//        public static UUID measurementCharacteristic = UUID.fromString("00002a37-b5a3-f393-e0a9-e50e24dcca9e");
//        public static UUID descriptor = UUID.fromString("00002902-b5a3-f393-e0a9-e50e24dcca9e");
//        public static UUID controlCharacteristic = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");
//
    }

}
