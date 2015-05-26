package com.example.shivam.openmrs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Shivam on 13/05/15 at 12:47 PM.
 */
public class VitalsFragment extends Fragment {

    ImageView playButton;
    ImageButton pauseButton,recordButton;
    LineChart chart;
    String str = "0.0";
    double strd = 0.0;
    int count = 0;
    int i = 0;
    private int _Patient_Id=0;
    // variables for serial communication
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    ArrayList<String> ar = new ArrayList<String>();
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;

    // plot variables
    LineData data1;
    LineDataSet set;
    ArrayList<LineDataSet> dataSets1 = new ArrayList<LineDataSet>();
    ArrayList<String> xVals1 = new ArrayList<String>();
    ArrayList<com.github.mikephil.charting.data.Entry> yVals1 = new ArrayList<com.github.mikephil.charting.data.Entry>();
    TextView sampleView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_vitals, null);
        playButton = (ImageView)root.findViewById(R.id.startGraph);
        pauseButton = (ImageButton)root.findViewById(R.id.pauseButton);
        recordButton = (ImageButton)root.findViewById(R.id.recordButton);
        sampleView = (TextView)root.findViewById(R.id.textView);
        chart = (LineChart)root.findViewById(R.id.lineChart);
        chart.setDrawGridBackground(false);
        chart.setDescription("Samples");
        chart.setData(new LineData());
        //chart.setTouchEnabled(false);
        chart.notifyDataSetChanged();
        //addEmptyData();
        removeLastEntry();
        chart.invalidate();
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                recordButton.setVisibility(View.VISIBLE);
                chart.setVisibility(View.VISIBLE);
                findBT();
            }
        });
        return root;

    }

    public VitalsFragment newInstance(){
        VitalsFragment mFragment = new VitalsFragment();
        return mFragment;
    }

    void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {

            }
            // Enable BT adapter
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            // Check arduino BT in pair list
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("HC-05")) {
                        // if (device.getName().equals("APA2")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.getLocalizedMessage();
            Log.e("YOUR_APP_LOG_TAG", "In findBT", ex);
        }

        try {
            openBT();
        } catch (IOException e) {
            e.getLocalizedMessage();
            Log.e("YOUR_APP_LOG_TAG", "In openBT", e);
        }

    }

    void openBT() throws IOException {
        try {
            // Connect arduino BT
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Standard
            // SerialPortService
            // ID
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            if (mmSocket.isConnected()) {
            } else {
            }
        } catch (Exception ex) {
            ex.getLocalizedMessage();
            Log.e("YOUR_APP_LOG_TAG", "In openBT", ex);
        }
        // progress1.dismiss();
        beginListenForData();

    }

    void beginListenForData() {

        // Set serial communication
        final Handler handler = new Handler();
        final byte delimiter = 10; // This is the ASCII code for a newline
        // character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.length);
                                    final String data = new String(
                                            encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            sampleView.setText(data);
                                            str = sampleView.getText()
                                                    .toString();// DO PROCESSING
                                            // ON "str"
                                            ar.add(str);
                                            processing();

                                        }

                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (IOException ex) {
                        stopWorker = true;
                    } catch (NumberFormatException e) {
                        Log.e("Communication error", "msg");
                        e.printStackTrace();
                    }
                }

            }
        });

        workerThread.start();
        try {
            mmOutputStream.write("$".getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // send();
    }

    void send() {
        try {
            mmOutputStream.write("$".getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }}

    void addEntry() {

        data1 = chart.getData();

        if (data1 != null) {

            LineDataSet set = data1.getDataSetByIndex(0);
            // set.addEntry(...);

            if (set == null) {
                set = createSet();
                data1.addDataSet(set);
            }
            //for(int i=1000;i<5000;i+=500)
            {

                data1.addXValue(String.valueOf(i));
                i += 1;
            }
            data1.addEntry(new Entry((float) strd, set.getEntryCount()), 0);

            //chart.centerViewPort(500,count+10);

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();
            chart.setVisibleXRange(10000);
chart.invalidate();
            // move to the latest entry
            chart.moveViewToX(data1.getXValCount() - 10001);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Real Time Raw Samples");

        set.setHighLightColor(Color.rgb(190, 190, 190));

        set.setColor(Color.BLUE);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(1.5f);
        set.setCircleSize(0f);
        // set1.setFillAlpha(65);
        set.setFillColor(Color.WHITE);

        return set;
    }

    private void removeLastEntry() {

        LineData data = chart.getData();

        if (data != null) {

            LineDataSet set = data.getDataSetByIndex(0);

            if (set != null) {

                Entry e = set.getEntryForXIndex(set.getEntryCount() - 1);

                data.removeEntry(e, 0);
                // or remove by index
                // mData.removeEntry(xIndex, dataSetIndex);

                chart.notifyDataSetChanged();
                chart.invalidate();
            }
        }
    }

    void processing() {

        strd = (Double.parseDouble(str));
        addEntry();
    }


}
