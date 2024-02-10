package org.asdanjer.slotcrafter;

// A class to hold MSPT values and their timestamps
class MsptValue {
    long timestamp;
    double mspt;

    MsptValue(long timestamp, double mspt) {
        this.timestamp = timestamp;
        this.mspt = mspt;
    }
}