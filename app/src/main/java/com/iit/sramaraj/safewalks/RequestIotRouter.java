/* package com.iit.sramaraj.safewalks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


 * Created by Nivash on 6/24/2016.

public class RequestIotRouter extends AsyncTask<Void,Void,Integer> {

    String dstAddress;
    int dstPort;
    String response = "";
    private String messsage = "hello from android nexus 5";
    private String serverIp;
    private Socket client;
    int noOfPed=0;
    private PrintWriter printwriter;
    @Override
    protected Integer doInBackground(Void... params) {

        try {
            Log.d("ClientServer", "doInBackground: hiitng");
            client = new Socket("10.0.0.151", 6066);
            System.out.println("Just connected to "
                    + client.getRemoteSocketAddress());
              //connect to server
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("Hello from "
                    + client.getLocalSocketAddress());//write the message to output stream
            System.out.print("Executed");
            InputStream inFromServer = client.getInputStream();
            DataInputStream in =
                    new DataInputStream(inFromServer);
            noOfPed = in.readInt();
            System.out.println("Server says " + noOfPed);

            Log.d("IoTrequest", Integer.toString(noOfPed));

            client.close();   //closing the connection

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return noOfPed;
    }
}
*/