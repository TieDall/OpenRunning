package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBHandler {

    // Change variables below for configuration
    private static final String DB_PROTOCOL = "http";
    private static final String DB_IP_ADDRESS = "10.10.123.0";

    /**
     * adds a new Route to the database with bis, describtion, length and the waypoints
     * @param bid
     * @param describtion
     * @param length
     * @param waypoints
     * @return String with "New record created successfully" or an error
     */
    public static boolean addRoute(String bid, String describtion, String length, String waypoints){

        String add_route_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/route_add.php";

        try {
            URL url = new URL(add_route_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            String post_data =
                    URLEncoder.encode("creator","UTF-8")+"="+URLEncoder.encode(bid,"UTF-8")+"&"
                            +URLEncoder.encode("describtion","UTF-8")+"="+URLEncoder.encode(describtion,"UTF-8")+"&"
                            +URLEncoder.encode("length","UTF-8")+"="+URLEncoder.encode(length,"UTF-8")+"&"
                            +URLEncoder.encode("rating_count","UTF-8")+"="+URLEncoder.encode("","UTF-8")+"&"
                            +URLEncoder.encode("rating_average","UTF-8")+"="+URLEncoder.encode("","UTF-8")+"&"
                            +URLEncoder.encode("waypoints","UTF-8")+"="+URLEncoder.encode(waypoints,"UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();

            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            System.out.println(result);
            if (result.equals("New record created successfully")) {
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * checks username an password
     * @param username
     * @param password
     * @return String with BID  and Benutzertyp separated by "_" or an error
     */
    public static String login(String username, String password){

        String login_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/login.php";

        try {

            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                    +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;



        } catch (MalformedURLException e) {} catch (IOException e) {}

        return null;
    }

    /**
     * Gets SIDs from database where length and rating fits
     * @param length
     * @param rating
     * @return String with SIDs separated by "_" or an error
     */
    public static String getRoutes(String length, float rating) {

        String getRoutes_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/getRoutes.php";
        double tolerance = 0.5;
        double length_min = Double.valueOf(length)-(Double.valueOf(length)*tolerance);
        double length_max = Double.valueOf(length)+(Double.valueOf(length)*tolerance);

        try {

            URL url = new URL(getRoutes_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("length_min","UTF-8")+"="+URLEncoder.encode(String.valueOf(length_min),"UTF-8")+"&"
                    +URLEncoder.encode("length_max","UTF-8")+"="+URLEncoder.encode(String.valueOf(length_max),"UTF-8")+"&"
                    +URLEncoder.encode("rating","UTF-8")+"="+URLEncoder.encode(Float.toString(rating),"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;

        } catch (MalformedURLException e) {} catch (IOException e) {}

        return null;
    }

    /**
     * gets a Route from database with this SID
     * @param sid
     * @return Route
     */
    public static Route getRoute(int sid){
        int bid = 0;
        String description = null;
        double length = 0;
        int numberVotes = 0;
        double averageVotes = 0;
        String waypoints = null;

        String getRoute_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/route_info.php";

        try {

            URL url = new URL(getRoute_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(""+sid,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String line = "";
            while ((line = bufferedReader.readLine()) != null){

                if(!line.equals("failure")) {
                    int index;

                    index = line.indexOf("?");
                    bid = Integer.parseInt(line.substring(0, index));
                    line = line.substring(index+1);

                    index = line.indexOf("?");
                    description = line.substring(0, index);
                    line = line.substring(index+1);

                    index = line.indexOf("?");
                    length = Double.parseDouble(line.substring(0, index));
                    line = line.substring(index+1);

                    index = line.indexOf("?");
                    numberVotes = Integer.parseInt(line.substring(0, index));
                    line = line.substring(index+1);

                    index = line.indexOf("?");
                    averageVotes = Double.parseDouble(line.substring(0, index));
                    line = line.substring(index+1);

                    waypoints = line;

                } else {
                    System.out.println("fail");
                }

            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            System.out.println(sid);
            System.out.println(bid);
            System.out.println(description);
            System.out.println(length);
            System.out.println(numberVotes);
            System.out.println(averageVotes);
            System.out.println(waypoints);

            return new Route(sid, bid, description, length, numberVotes, averageVotes, waypoints);

        } catch (MalformedURLException e) {} catch (IOException e) {}

        return null;


    }

    /**
     * adds the new user to database with username, mailadresse and hash
     * @param username
     * @param mailadresse
     * @param hash
     * @return "Insert successfull" or an error
     */
    public static String register(String username, String mailadresse, String hash) {
        String register_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/register.php";

        try {
            URL url = new URL(register_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            String post_data =
                    URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                            +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(hash,"UTF-8")+"&"
                            +URLEncoder.encode("mailadresse","UTF-8")+"="+URLEncoder.encode(mailadresse,"UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();

            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * gets the passwordhash to the username
     * @param username
     * @return String with Passworthash or "no user"
     */
    public static String gethash(String username) {
        String gethash_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/getHash.php";

        try {

            URL url = new URL(gethash_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;



        } catch (MalformedURLException e) {} catch (IOException e) {}

        return null;
    }

    /**
     * checks if username or mailaddress already exist in database
     * @param username
     * @param mailadresse
     * @return String with Benutzername or ""
     */
    public static String getUser_info(String username, String mailadresse) {
        String getUser_info_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/getUser_info.php";

        try {

            URL url = new URL(getUser_info_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                    +URLEncoder.encode("mailadresse","UTF-8")+"="+URLEncoder.encode(mailadresse,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;



        } catch (MalformedURLException e) {} catch (IOException e) {}

        return null;
    }

    /**
     * deletes user from database with username
     * @param user
     * @return String Benutzername or "not found"
     */
    public static String removeUser(String user) {
        String removeUser_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/removeUser.php";

        try {

            URL url = new URL(removeUser_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(user,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;

        } catch (MalformedURLException e) {} catch (IOException e) {}

        return "Error";
    }

    /**
     * gets userType from database to the bid
     * @param bid
     * @return String with Benutzertyp or "not found"
     */
    public static String updateuser(String bid) {
        String updateuser_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/getUser_update.php";

        try {

            URL url = new URL(updateuser_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("bid","UTF-8")+"="+URLEncoder.encode(bid,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;

        } catch (MalformedURLException e) {} catch (IOException e) {}

        return "Error";
    }

    /**
     * sets the Streckenstatus in the route with this SID in the database to status
     * @param sid
     * @param status
     * @return String with "erfolgreich" or "failure"
     */
    public static String setRouteStatus(String sid, String status) {

        String setRouteStatus_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/setRouteStatus.php";

        try {

            URL url = new URL(setRouteStatus_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("sid","UTF-8")+"="+URLEncoder.encode(sid,"UTF-8")+"&"
                    +URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode(status,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;

        } catch (MalformedURLException e) {} catch (IOException e) {}

        return "Error";


    }

    /**
     * delete the Route with this SID from database
     * @param sid
     * @return String with "erfolgreich" or "error"
     */
    public static String deleteRoute(String sid) {
        String deleteRoute_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/deleteRoute.php";

        try {

            URL url = new URL(deleteRoute_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("sid","UTF-8")+"="+URLEncoder.encode(sid,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;

        } catch (MalformedURLException e) {} catch (IOException e) {}

        return "Error";
    }

    /**
     * gets SIDs with this Streckenstatus
     * @param status
     * @return String with all SIDs separated by "_"
     */
    public static String getRoutetoStatus(String status) {
        String getRoutetoStatus_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/getRoutetoStatus.php";

        try {

            URL url = new URL(getRoutetoStatus_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode(status,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;

        } catch (MalformedURLException e) {} catch (IOException e) {}

        return "Error";
    }
}
