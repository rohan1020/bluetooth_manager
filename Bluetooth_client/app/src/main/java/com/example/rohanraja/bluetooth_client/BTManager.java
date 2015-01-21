package com.example.rohanraja.bluetooth_client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by rohanraja on 21/01/15.
 */


public class BTManager {

    private boolean isBTsupported = true ;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    private String  NAME = "RohanBT";
    private UUID MY_UUID = new UUID(1,5);
    private BluetoothDevice remoteDevice ;
    private String REMOTE_MAC_ADDRESS = "B8:5E:7B:0C:0A:6E";

    private  BluetoothSocket mmSocket;
    private  BluetoothDevice mmDevice;
    private  BluetoothServerSocket mmServerSocket;

    public void BTManager()
    {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        if(myBluetoothAdapter == null)
            isBTsupported = false ;


    }

    public void connectBTDevice(String pServerMacAddress) throws IOException, InterruptedException {

        REMOTE_MAC_ADDRESS = pServerMacAddress ;

//        Thread newT = new Thread(new Runnable() {
//            @Override
//            public void run() {

                remoteDevice =  myBluetoothAdapter.getRemoteDevice(REMOTE_MAC_ADDRESS);
                Log.d("BTDevice", remoteDevice.getName());
                ConnectThread connectThread = new ConnectThread(remoteDevice);
                connectThread.start();

                try {
                    connectThread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//            }

//        });
//
//        newT.start();

        // mmSocket contains the network socket

    }

    public class AcceptThread extends Thread {

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                Log.d("BListen", "UUID = " + MY_UUID.toString());
                tmp = myBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);


                Log.d("BListen", "Listening");
                Log.d("BListen", tmp.toString());

            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {

                    Log.d("BListen", "SOCKECT FOUND!!!!");

                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }

    }

    private class ConnectThread extends Thread {


        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            myBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
            this.notify();
        }

        public BluetoothSocket getSocket()
        {
            return mmSocket ;
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void manageConnectedSocket(BluetoothSocket pSocket)
    {


    }

}
