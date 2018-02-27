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

}
