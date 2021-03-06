package com.example.naytonwilkerson.chamadaaluno;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class BroadcastReceiver extends android.content.BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private Order order = new Order();




    public BroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, MainActivity mActivity)
    {
        this.mManager = mManager;
        this.mChannel=mChannel;
        this.mActivity = mActivity;

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();


        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context,"Wifi Está Ativo",Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(context,"Wifi Está Inativo",Toast.LENGTH_SHORT).show();
            }

        }else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){

            if(mManager!=null){
                mManager.requestPeers(mChannel, mActivity.peerListListener);

            }

        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

            if(mManager==null)
            {
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected())
            {
                Toast.makeText(context, "Conexão estabelecida!", Toast.LENGTH_SHORT).show();
               mManager.requestConnectionInfo(mChannel, order.connectionInfoListener);

            }else {
                if(Order.ClientClass.socket != null){
                    try {
                        Order.ClientClass.socket.close();
                        Toast.makeText(context, "Aberto a novas conexões", Toast.LENGTH_SHORT).show();
                        Log.i("SERVER SOCKET","FECHADO!!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

            Toast.makeText(context, "Configurações do dispositivo alteradas", Toast.LENGTH_SHORT).show();

        }



    }
}
