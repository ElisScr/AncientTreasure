package com.example.elisc.ancienttreasures;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by elisc on 24-Oct-16.
 */
public class MainActivity2 extends ListActivity {

    private int number;
    String address = null;
    String myLaptop = "ELISLAPTOP";
    BluetoothSocket socket;
    Button hint, send;
    BluetoothDevice mDevice;
    ThreadConnected myThreadConnected;
    private UUID MY_UUID;
    private final String UUID_STRING = "04c6093b-0000-1000-8000-00805f9b34fb";
    ImageButton back;
    CheckBox cb;
    TextView pl_name, dista, tags, dev_nm, info, status;
    Tag detectTag;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    String[][] techListsArray;
    NfcAdapter mAdapter;
    String playerName;
    MediaPlayer bkgmusic;
    long startTime;
    double elapsedSeconds;
    ArrayList<byte[]> nfcs;
     private final String NO_MORE= "NO MORE TREASURES";
    private final String PLEASE_SEND="PLEASE SEND INFO TO BASE";
    private final String YOU_GOT="You got ";
    private final String TREASURES_IN=" treasures in ";
    private final String SECONDS=" seconds";
    private boolean scan = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        pl_name = (TextView) findViewById(R.id.plName);
        dev_nm = (TextView) findViewById(R.id.device_nm);
        dista = (TextView) findViewById(R.id.dista);
        info = (TextView) findViewById(R.id.infos);
        status = (TextView) findViewById(R.id.textstatus);
        hint = (Button) findViewById(R.id.redeem);
        send = (Button) findViewById(R.id.wifi);
        cb = (CheckBox) findViewById(R.id.checkbox);
        send.setVisibility(View.GONE);
        back = (ImageButton) findViewById(R.id.back);
        playerName = getIntent().getStringExtra("playerid");
        pl_name.setText(playerName.toUpperCase());
        bkgmusic = MediaPlayer.create(this, R.raw.fantasy);
        bkgmusic.setLooping(true);
        bkgmusic.start();
        number = 1;
        MY_UUID = UUID.fromString(UUID_STRING);
        nfcs=new ArrayList<byte[]>();
        //connect to the bluetooth server
        for (BluetoothDevice d : MainActivity.mBluetoothAdapter.getBondedDevices()) {
            if (d.getName().equals(myLaptop)) address = d.getAddress();
        }
        mDevice = MainActivity.mBluetoothAdapter.getRemoteDevice(address);

        try {
            socket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            myThreadConnected = new ThreadConnected(socket);
            myThreadConnected.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //sending a text to a bluetooth server
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myThreadConnected != null) {
                    byte[] bytesToSend = info.getText().toString().getBytes();
                    myThreadConnected.write(bytesToSend);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //turning music on / off
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    bkgmusic.start();
                } else {
                    bkgmusic.pause();
                }
            }
        });
        //every .5 sec set distance to the textview
        final Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    while (number == 1) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dista.setText(FindIbeacons.distance1);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        final Thread t2 = new Thread() {
            @Override
            public void run() {
                try {
                    while (number == 2) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dista.setText(FindIbeacons.distance2);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        final Thread t3 = new Thread() {
            @Override
            public void run() {
                try {
                    while (number == 3) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dista.setText(FindIbeacons.distance3);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        //show one iBeacon at a time
        hint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (number == 1) {
                                            hint.setEnabled(false);
                                            t1.start();
                                            startTime = SystemClock.elapsedRealtime();
                                            dev_nm.setText(FindIbeacons.device_name1);
                                            tags.setText("");
                                        } else if (number == 2) {
                                            hint.setEnabled(false);
                                            t2.start();
                                            dev_nm.setText(FindIbeacons.device_name2);
                                            tags.setText("");
                                        } else if (number == 3) {
                                            hint.setEnabled(false);
                                            t3.start();
                                            dev_nm.setText(FindIbeacons.device_name3);
                                            tags.setText("");
                                        } else {
                                            long endTime = SystemClock.elapsedRealtime();
                                            long elapsedMilliSeconds = endTime - startTime;
                                            elapsedSeconds = elapsedMilliSeconds / 1000.0;
                                            send.setVisibility(View.VISIBLE);
                                            hint.setVisibility(View.GONE);
                                            dev_nm.setText(NO_MORE);
                                            dista.setText(PLEASE_SEND);
                                            tags.setText("");
                                            info.setText(YOU_GOT + (number - 1) + TREASURES_IN
                                                    + elapsedSeconds + SECONDS);
                                        }
                                    }
                                }
        );

        //the NFC TAG discovering
        tags = (TextView) findViewById(R.id.tags);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        detectTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[]{ndef,};
        techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
        handleIntent(getIntent());

        //start searching / showing the iBeacons we need
        Runnable find = new FindIbeacons(MainActivity.mBluetoothAdapter);
        Thread t = new Thread(find);
        t.start();
    }

    private void handleIntent(Intent intent) {
        NdefMessage[] msgs;


        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                NdefRecord[] records = msgs[0].getRecords();
                for (NdefRecord ndefRecord : records) {
                    if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        for(byte[] id : nfcs){
                            if(Arrays.equals(id,detectTag.getId())){
                                return;
                            }
                        }

                        nfcs.add(detectTag.getId());

                        byte[] payload = ndefRecord.getPayload();
                        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                        int languageCodeLenght = payload[0] & 0063;

                        try {
                            String tagText = new String(payload, languageCodeLenght + 1, payload.length - languageCodeLenght - 1, textEncoding);
                            tags.setText(tagText + number);
                           // nfcs.add(detectTag.getId());
                            number++;
                            hint.setEnabled(true);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                            }


                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        scan = false;
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, pendingIntent,
                    intentFiltersArray, techListsArray);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scan = false;
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //  myThreadConnected.interrupt();
        bkgmusic.stop();
        bkgmusic.release();
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        detectTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        handleIntent(intent);
    }

    /*
    ThreadConnected to handle data communication with server- here the server part sends feedback
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            try {
                connectedBluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
                    final String msgReceived = String.valueOf(bytes) +
                            " bytes received:\n"
                            + strReceived;

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            status.setText(msgReceived);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            status.setText(msgConnectionLost);
                        }
                    });
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
                connectedOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}