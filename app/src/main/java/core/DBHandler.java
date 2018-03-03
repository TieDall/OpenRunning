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

    public static final int LIMITED_USER = 0;
    public static final int NORMAL_USER = 1;
    public static final int EXPERIENCED_USER = 2;
    public static final int ADMIN_USER = 3;

    // Change variables below for configuration
    private static final String DB_PROTOCOL = "http";
    private static final String DB_IP_ADDRESS = "172.31.155.179";

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

    public static String getRoutes(String length, float rating) {

        String login_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/getRoutes.php";
        double tolerance = 0.5;
        double length_min = Double.valueOf(length)-(Double.valueOf(length)*tolerance);
        double length_max = Double.valueOf(length)+(Double.valueOf(length)*tolerance);

        try {

            URL url = new URL(login_url);
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

    public static Route getRoute(int sid){
        int bid = 0;
        String description = null;
        double length = 0;
        int numberVotes = 0;
        double averageVotes = 0;
        String waypoints = null;

        String login_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/route_info.php";

        try {

            URL url = new URL(login_url);
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

    public static String gethash(String username) {
        String login_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/getHash.php";

        try {

            URL url = new URL(login_url);
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

    public static String getUser_info(String username, String mailadresse) {
        String login_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/getUser_info.php";

        try {

            URL url = new URL(login_url);
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

    public static String removeUser(String user) {
        String login_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/removeUser.php";

        try {

            URL url = new URL(login_url);
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

    public static String updateuser(String bid) {
        String login_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/getUser_update.php";

        try {

            URL url = new URL(login_url);
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

    public static String setRouteStatus(String sid, String status) {

        String login_url = DB_PROTOCOL+"://"+DB_IP_ADDRESS+"/setRouteStatus.php";

        try {

            URL url = new URL(login_url);
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
}
