package com.example.algorythm;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.microsoft.signalr.OnClosedCallback;

import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class SignalRClient {
    private final HubConnection hubConnection;
    private MessageCallback messageCallback;

    public String getSocketID() {
        return socketID;
    }

    public String socketID;


    public SignalRClient() {
        hubConnection = HubConnectionBuilder.create("wss://thewebapiserver20240424215817.azurewebsites.net/music-hub")
                .build();
    }
    public void setMessageCallback(MessageCallback callback) {
        this.messageCallback = callback;
    }
    public void startConnection() {
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            hubConnection.start()
                    .timeout(20, TimeUnit.SECONDS)
                    .blockingAwait();
        }
    }

    public void stopConnection() {
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.stop();
        }
    }
    public void getID() {
        hubConnection.on("ReceiveMassage", (String base64String) -> {
            if (messageCallback != null) {
            /*byte[] musicBytes = new byte[99999];

                musicBytes = Base64.getDecoder().decode(base64String);
            }*/
                //++ messageCallback.onMessageReceived(Base64.getDecoder().decode(base64String));
                socketID = base64String;
                System.out.println(socketID);
            }
        }, String.class);
    }
    public void startListening() {
        hubConnection.on("GetMusicBytes", (String base64String) -> {
            if (messageCallback != null) {

                System.out.println("QWERTYUIOP");
                messageCallback.onMessageReceived(Base64.getDecoder().decode(base64String));
            }
        }, String.class);
    }

    public void sendMessage(String message) {
        if (hubConnection != null) {
            hubConnection.send("SendMessage",message);
        }
    }

    public void setOnClosedCallback(OnClosedCallback callback) {
        if (hubConnection != null) {
            hubConnection.onClosed(callback);
        }
    }
}

