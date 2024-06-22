package com.example.algorythm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class API {
    public static String sendPostJWT(String apiUrl, String requestBody, String jwt) throws IOException {
        if (jwt == null){
            jwt = "NO.JWT.TOKEN";
        }
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        }
    }
    public static String sendGetJWT(String apiUrl, String requestBody, String jwt) throws IOException {
        if (jwt == null){
            jwt = "NO.JWT.TOKEN";
        }
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);
        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        }
    }
    public static boolean forgotPassword(String email,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Identity/forgotPassword";
        try{
            String requestBody = "{\"email\":\"" + email + "\"}";
            String res = sendPostJWT(apiUrl,requestBody,jwt);
            if (res.equals("User registered successfully."))
                return true;
            else
                return false;
        }
        catch (Exception e){
            return false;
        }
    }
    public static boolean verifyForgotPasswordCode(String email,String code,String newPassword,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Identity/verifyForgotPasswordCode";
        try{
            String requestBody = "{\"email\":\"" + email + "\"," +
                    "\"code\":\"" + code + "\","+
                    "\"newPassword\":\""+newPassword+"\""+ "}";
            System.out.println(requestBody);
            String res = sendPostJWT(apiUrl,requestBody,jwt);
            if (res.equals("User registered successfully."))
                return true;
            else
                return false;
        }
        catch (Exception e){
            return false;
        }

    }
    public static boolean registerUser(String email, String password) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Identity/register";
        try{
            String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
            String res = sendPostJWT(apiUrl,requestBody,"NONE.JWT.TOKEN");
            if (res.equals("User registered successfully."))
                return true;
            else
                return false;
        }
        catch (Exception e){
            return false;
        }
    }
    public static Map<String,String> loginUser(String email, String password) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Identity/login";
        try{
            String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
            Map<String,String> res = new HashMap<>();
            res.put("token",extractToken(sendPostJWT(apiUrl,requestBody,"NONE.JWT.TOKEN")));
            res.put("username",extractUsername(sendPostJWT(apiUrl,requestBody,"NONE.JWT.TOKEN")));
            return res;
        }
        catch (Exception e){
            System.out.println("FAILED TO LOGIN");
            return null;
        }
    }
    public static String extractToken(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        return rootNode.get("jwtToken").asText();
    }
    public static String extractUsername(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        return rootNode.get("userName").asText();
    }
    public static String getMusic(int id, String password,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Music/GetMusic";
        try{
            String requestBody = "{\"musicId\":\"" + id + "\",\"socketId\":\"" + password + "\"}";
            return sendPostJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e);
            return null;
        }
    }
    public static String getProposedMusic(int count,String jwt){
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Music/GetProposedMusic?CountOfProposedMusic="+count;
        try{
            String requestBody = "{\"CountOfProposedMusic\":" + count + "}";
            String res = sendGetJWT(apiUrl,requestBody,jwt);
            return res;
        }
        catch (Exception e){
            return "FAILED TO FETCH DATA";
        }
    }

    public static String getMusicByTitle(String title,int count,String jwt,String genre){
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Music/GetMusicByTitle?titleSubString="+title+"&countOfReturnedMusic="+count;
        if (genre != null){
            apiUrl += "&categoryName=" + genre;
        }
        System.out.println(apiUrl);
        try{
            String requestBody = "{\"countOfReturnedMusic\":" + count + "}";
            String res = sendGetJWT(apiUrl,requestBody,jwt);
            return res;
        }
        catch (Exception e){
            return "FAILED TO FETCH DATA";
        }
    }
    public static String getGenres(){
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Music/GetAllGenres";

        System.out.println(apiUrl);
        try{
            String requestBody = "{}";
            String res = sendGetJWT(apiUrl,requestBody,null);
            return res;
        }
        catch (Exception e){
            return "FAILED TO FETCH DATA" + e;
        }
    }

    public static String createPlaylist(String playlistName,String id,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/PlayList/CreatePlayList?playlistName="+playlistName+"&musicId="+id;
        try{
            String requestBody = "{\"playlistName\":\"" + playlistName + "\",\"musicId\":" + id + "}";
            return sendPostJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e);
            return null;
        }
    }
    public static String addToPlaylist(int playlistID,String songID,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/PlayList/AddSongToPlaylist?playlistId="+playlistID+"&musicId="+songID;
        System.out.println(apiUrl);
        try{
            String requestBody = "{\"playlistId\":\"" + playlistID + "\",\"musicId\":\"" + songID + "\"}";
            return sendPostJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e + "NIE DZIALA");
            return null;
        }
    }
    public static String likeMusic(String songID,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Music/LikeMusic?musicId="+songID;
        System.out.println(apiUrl);
        try{
            String requestBody = "{}";
            return sendPostJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e);
            return null;
        }
    }
    public static String unlikeMusic(String songID,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Music/UnLikeMusic?musicId="+songID;
        System.out.println(apiUrl);
        try{
            String requestBody = "{}";
            return sendPostJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e);
            return null;
        }
    }
    public static String getLikedMusic(int countOfReturnedMusic,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Music/GetLikedUserMusic?countOfReturnedMusic="+countOfReturnedMusic;
        try{
            String requestBody = "{}";
            return sendGetJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e);
            return null;
        }
    }
    public static String getUserPlaylists(int countOfReturnedMusic,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/PlayList/GetUserPlaylist?countOfReturnedPlaylist="+countOfReturnedMusic;
        try{
            String requestBody = "{}";
            return sendGetJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e);
            return null;
        }
    }

    public static String getPlaylistThumbnail(int playlistID,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/PlayList/GetPlaylistThumbnailData?playlistId="+playlistID;
        try{
            String requestBody = "{}";
            return sendGetJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e);
            return null;
        }
    }

    public static String getPlaylistMusic(int playlistId,int offset,int count,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/PlayList/GetPlaylistMusic?playlistId="+playlistId+"&offset="+offset+"&countOfReturnedMusic="+count;
        try{
            String requestBody = "{}";
            return sendGetJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e);
            return null;
        }
    }

    public static String isLiked(String ID,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Music/IsLiked?musicId="+ID;
        try{
            String requestBody = "{}";
            return sendGetJWT(apiUrl,requestBody,jwt);
        }
        catch (Exception e){
            e.getStackTrace();
            System.out.println(e);
            return null;
        }
    }


    public static boolean deleteFromPlaylist(String playlistID, String musicID,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/PlayList/DelSongFromPlaylist?playlistId="+playlistID+"&musicId="+musicID;
        try{
            String requestBody = "{}";
            String res = sendPostJWT(apiUrl,requestBody,jwt);
            if (res.equals("User registered successfully."))
                return true;
            else
                return false;
        }
        catch (Exception e){
            return false;
        }
    }

    public static boolean deletePlaylist(String playlistID,String jwt) throws IOException {
        String apiUrl = "https://thewebapiserver20240424215817.azurewebsites.net/PlayList/DelPlaylist?playlistId="+playlistID;
        try{
            String requestBody = "{}";
            String res = sendPostJWT(apiUrl,requestBody,jwt);
            if (res.equals("User registered successfully."))
                return true;
            else
                return false;
        }
        catch (Exception e){
            System.out.println("Can't delete " + e);
            return false;
        }
    }



}
