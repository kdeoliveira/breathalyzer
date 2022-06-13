package com.coen390.abreath.common;

import java.util.HashMap;
import java.util.UUID;

public class Constant {

    public static class BleAttributes{
        public static final String DEVICE_TO_CONNECT = "ABreath";
        public static UUID ABREATH_SERVICE_UUID = UUID.fromString("91bad492-b950-4226-aa2b-4ede9fa42f59");
        public static UUID ABREATH_SENSOR_CHARACTERISTICS_UUID = UUID.fromString(
                "ca73b3ba-39f6-4ab3-91ae-186dc9577d99"
        );

    }
}
