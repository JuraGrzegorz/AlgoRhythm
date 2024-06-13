package org.example;

import org.json.*;
import netscape.javascript.JSObject;

import java.io.IOException;

import static org.example.MainActivity.sleep;

public class TESTER {


    public static void main(String[] args) throws IOException {
        String email="testmail6969123@gmail.com";
        String pass="jurastopedal@@@6969xD";
//        boolean res = API.registerUser(email,pass);
//        System.out.println(res);

        String jwt = API.loginUser(email,pass);
        if(jwt==null){
            System.out.println("XDDDD");
        }
        System.out.println(jwt);

        String res = API.getUserPlaylists(1,jwt);

        System.out.println(res);
//        String res = API.getLikedMusic(10,jwt);
//
//        System.out.println(res);




    }
}
