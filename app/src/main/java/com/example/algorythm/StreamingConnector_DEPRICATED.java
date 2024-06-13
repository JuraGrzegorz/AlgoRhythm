package com.example.algorythm;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;
import static com.example.algorythm.LoginEndpoints_DEPRICATED.sendPostReq;


public class StreamingConnector_DEPRICATED implements MessageCallback {
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Sleep interrupted: " + e.getMessage());
        }
    }

    public SignalRClient_DEPRICATED signalRClient;

    private boolean isPlaying = false;

    public byte[] musicData;
    int position;
    private Lock lock;

    public void start(String musicID) throws IOException {
        musicData=new byte[1024*1024*5];
        position=0;
        lock= new ReentrantLock();
        signalRClient = new SignalRClient_DEPRICATED();
        signalRClient.getID();
        signalRClient.startConnection();
        signalRClient.setMessageCallback(this);
        signalRClient.startListening();
        sleep(2000);
        System.out.println(signalRClient.socketID);
        String url ="https://thewebapiserver20240424215817.azurewebsites.net/Music/GetMusicDataStream";
        String requestBody = "{\"musicId\":" + musicID + ",\"socketId\":\"" + signalRClient.socketID +
                "\",\"musicOffSet\":" + 0 + "," + "\"sizeOfMusicData\":" + 1024*1024 + ",\"sizeOfDataFrame\":" + 1024*100 +

                "}";
//        System.out.println(requestBody);
//        sendPostReq(url,requestBody);

        new Thread(() -> {
            try {
                sendPostReq(url, requestBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        sleep(2000);
        System.out.println(musicData.length);
        convertToMP3(musicData,"test2.mp3");

    }
    @Override
    public void onMessageReceived(byte[] message) {

        lock.lock();
        System.arraycopy(message, 0, musicData, position, message.length);
        position+=message.length;
        System.out.println(Integer.toString(position));
        lock.unlock();

    }
    public static void convertToMP3(byte[] musicData, String outputPath) {
        try {
            File tempFile = File.createTempFile("temp", ".mp3");
            tempFile.deleteOnExit();


            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(musicData);
            fos.close();


            Mp3File mp3file = new Mp3File(tempFile);

            mp3file.save(outputPath);

            System.out.println("MP3 file created successfully.");
        } catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException e) {
            e.printStackTrace();
        }
    }
}
