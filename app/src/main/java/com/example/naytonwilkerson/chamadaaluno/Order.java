package com.example.naytonwilkerson.chamadaaluno;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Order extends AppCompatActivity {

    static Button btnMandar;
    static TextView Status, presenca;
    static EditText msgNome, msgMatricula;
    static final int MESSAGE_READ = 1;
    ClientClass clientClass;

    SendReceiver sendReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order1);

        btnMandar = findViewById(R.id.sendButton2);
        msgNome =  findViewById(R.id.msgNome);
        msgMatricula = findViewById(R.id.msgMatricula);
        Status =  findViewById(R.id.Status);
        presenca = findViewById(R.id.presenca);

        btnMandar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg ="Nome: "+ msgNome.getText().toString()+"  ";
                msg = msg+"  Matricula: "+msgMatricula.getText().toString();
                msgNome.setText("");
                msgMatricula.setText("");

                SendReceiver.write(msg.getBytes());
            }
        });

    }


    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;

                clientClass = new Order.ClientClass(groupOwnerAddress);
                clientClass.start();

        }
    };

   static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:

                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);

                    Status.setText(tempMsg);
                    presenca.setText(tempMsg);

                    break;
            }
            return true;
        }
    });

    public static class SendReceiver extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private  static OutputStream outputStream;


        SendReceiver(Socket s) {
            socket = s;

            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run()
        {
            byte[] buffer = new byte[1024];
            int bytes;

            while(socket!=null){
                try {
                    bytes=inputStream.read(buffer);
                    if (bytes>0){
                        handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

          static void write(byte[] bytes) {

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        ClientClass(InetAddress hostAddress)
        {
            hostAdd=hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
                sendReceiver = new Order.SendReceiver(socket);
                sendReceiver.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}






