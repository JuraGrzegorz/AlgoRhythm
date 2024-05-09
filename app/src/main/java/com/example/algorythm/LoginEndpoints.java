package com.example.algorythm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginEndpoints {

    public static void main(String[] args) throws IOException {

        // Replace this with your server endpoint
        String email = "exam2p6lkjbkjbe@gmail.com";
        String password = "Exa2mple_passwsordD2";
        System.out.println(registerUser(email,password));
        //verifyForgotPasswordCode("t@ts","123","xd");

    }
    public static boolean forgotPassword(String email) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Identity/forgotPassword";
        try{
            String requestBody = "{\"email\":\"" + email + "\"}";
            String res = sendPostReq(apiUrl,requestBody);
            if (res.equals("User registered successfully."))
                return true;
            else
                return false;
        }
        catch (Exception e){
            return false;
        }
    }
    public static boolean verifyForgotPasswordCode(String email,String code,String newPassword) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Identity/verifyForgotPasswordCode";
        try{
            String requestBody = "{\"email\":\"" + email + "\"," +
                    "\"code\":\"" + code + "\","+
                    "\"newPassword\":\""+newPassword+"\""+ "}";
            System.out.println(requestBody);
            String res = sendPostReq(apiUrl,requestBody);
            if (res.equals("User registered successfully."))
                return true;
            else
                return false;
        }
        catch (Exception e){
            return false;
        }
    }
    public static boolean changePassword(String email,String code,String newPassword) throws IOException {
//        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Identity/forgotPassword";
//        try{
//            String requestBody = "{\"email\":\"" + email + "\"," +
//                    "\"code\":\"" + code + "\","+
//                    "\"newPassword\":\""+newPassword+"\""+ "}";
//            System.out.println(requestBody);
//            String res = sendPostReq(apiUrl,requestBody);
//            if (res.equals("User registered successfully."))
//                return true;
//            else
//                return false;
//        }
//        catch (Exception e){
//            return false;
//        }
        return false;
    }
    public static boolean changeEmail(String email,String code,String newPassword) throws IOException {
        return false;
    }

    public static boolean registerUser(String email, String password) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Identity/register";
        try{
            String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
            String res = sendPostReq(apiUrl,requestBody);
            if (res.equals("User registered successfully."))
                return true;
            else
                return false;
        }
        catch (Exception e){
            return false;
        }
    }
    public static String loginUser(String email, String password) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Identity/login";
        try{
            String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
            return extractToken(sendPostReq(apiUrl,requestBody));
        }
        catch (Exception e){
            System.out.println("FAILED TO LOGIN");
            return null;
        }
    }
    public static String extractToken(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        return rootNode.get("token").asText();
    }

    public static String sendPostReq(String apiUrl, String requestBody) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        System.out.println(requestBody);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            outputStream.write(input, 0, input.length);
        }
        catch (Exception e){
            System.out.println("wyjebalo sie");
        }
        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
//            Map<String, java.util.List<String>> headers = connection.getHeaderFields();
//            for (Map.Entry<String, java.util.List<String>> entry : headers.entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
//            }
            return response.toString();
        } else {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }


    public static String sendPostReqJWT(String apiUrl, String requestBody,String jwtToken) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
        System.out.println(requestBody);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            outputStream.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {

            throw new IOException("HTTP error code: " + responseCode);
        }
    }
}