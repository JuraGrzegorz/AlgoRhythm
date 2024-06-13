package org.example;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.microsoft.signalr.OnClosedCallback;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class test {



    private HubConnection hubConnection;
    private MessageCallback messageCallback;
    public void SignalRClient() {
        hubConnection = HubConnectionBuilder.create("http://10.0.2.2:5196/chat-hub")
                .build();
    }

    public test(HubConnection hubConnection) {
        this.hubConnection = hubConnection;
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

    public void startListening() {
        hubConnection.on("GetMusicBytes", (String base64String) -> {
            if (messageCallback != null) {
                /*byte[] musicBytes = new byte[99999];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    musicBytes = Base64.getDecoder().decode(base64String);
                }*/
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