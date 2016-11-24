package com.example.elisc.ancienttreasures;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by elisc on 21-Nov-16.
 */
public class FindIbeacons implements Runnable {

    BluetoothAdapter btAdapter;
    public static String device_name1;
    public static String device_name2;
    public static String device_name3;
    public static String distance1;
    public static String distance2;
    public static String distance3;
    private static final int MINOR_1 = 26274;
    private static final int MINOR_2 = 2176;
    private static final int MINOR_3 = 28689;
    private static final String BEACON_UUID = "f7826da64fa24e988024bc5b71e0893e";
    public static final String PROXIMITY_IMMEDIATE = "HOT";
    public static final String PROXIMITY_NEAR = "Warm";
    public static final String PROXIMITY_FAR = "COLD";
    public static final String PROXIMITY_UNKNOWN = "ICE AGE";

    public FindIbeacons(BluetoothAdapter btAdapter) {
        this.btAdapter = btAdapter;
    }

    @Override
    public void run() {
        while (true) {
            if (true) {
                btAdapter.startLeScan(leScanCallback);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                btAdapter.stopLeScan(leScanCallback);
            }
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice newdevice, final int rssi, final byte[] scanRecord) {
            int minor = FindIbeacons.getMinor(scanRecord);
            if (FindIbeacons.isIbeaconIneed(scanRecord)) {
                switch (minor) {
                    case MINOR_1:
                        device_name1 = "TREASURE 1";
                        distance1 = calculateProximity(FindIbeacons.calculateAccuracy(-70, rssi));
                        break;
                    case MINOR_2:
                        device_name2 = "TREASURE 2";
                        distance2 = calculateProximity(FindIbeacons.calculateAccuracy(-70, rssi));
                        break;
                    case MINOR_3:
                        device_name3 = "TREASURE 3";
                        distance3 = calculateProximity(FindIbeacons.calculateAccuracy(-70, rssi));
                        break;
                }
            }
        }
    };

    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1;
        }
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return 0.65 * Math.pow(ratio, 10.0);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return 0.65 * accuracy;
        }
    }

    protected static String calculateProximity(double accuracy) {
        if (accuracy < 0) {
            return PROXIMITY_UNKNOWN;
        }
        if (accuracy < 0.5) {
            return PROXIMITY_IMMEDIATE;
        }
        if (accuracy <= 4.0) {
            return PROXIMITY_NEAR;
        }
        return PROXIMITY_FAR;
    }

    protected static String getUuidAsString(byte[] ba) {
        StringBuilder sb = new StringBuilder();
        for (int i = 9; i < 25; i++) {
            sb.append(String.format("%02X", ba[i]));
        }
        return sb.toString();
    }

    protected static int getMinor(byte[] ba) {
        StringBuilder sb = new StringBuilder();
        for (int i = 27; i < 29; i++) {
            sb.append(String.format("%02X", ba[i]));
        }
        try {
            return Integer.valueOf(sb.toString().toLowerCase().trim(), 16);
        } catch (Exception e) {
            return -1;
        }
    }

    protected static boolean isIbeaconIneed(byte[] scanRec) {
        String uuid = getUuidAsString(scanRec);
        int minor = getMinor(scanRec);
        return BEACON_UUID.compareToIgnoreCase(uuid) == 0 &&
                ((MINOR_1 == minor)
                        || (MINOR_2 == minor)
                        || (MINOR_3 == minor));
    }
}
