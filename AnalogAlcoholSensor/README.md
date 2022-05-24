# Analog Alcohol Sensor - MQ3

Specification
-------------

| Item  | Parameter               | Min  | Typical    | Max | Unit |
|-------|-------------------------|------|------------|-----|------|
| VCC   | Working Voltage         | 4.9  | 5          | 5.1 | V    |
| PH    | Heating consumption     | 0.5  | -          | 750 | mW   |
| RL    | Load resistance         |      | adjustable |     |      |
| RH    | Heater resistance       | -    | 33         | -   | Ω    |
| Rs    | Sensing Resistance      | 1    | -          | 8   | MΩ   |
| Scope | Detecting Concentration | 0.05 | -          | 10  | mg/L |

Hardware Overview
-----------------

| Arduino   | Gas Sensor |
|-----------|------------|
| 5V        | VCC        |
| GND       | GND        |
| NC        | NC         |
| Analog A0 | SIG        |

The output voltage from the Gas sensor increases when the concentration of gas increases. Sensitivity can be adjusted by varying the potentiometer. <font color="Red">Please note that the best preheat time for the sensor is above 24 hours</font>. For detailed information about the MQ-3 sensor, please refer to the data-sheet provided in **Resources** section.
