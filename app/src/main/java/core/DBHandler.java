package core;

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
    private static final String DB_IP_ADDRESS = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_DATABASE = "openrunning";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";


    /*
        Functions that delas with user data.
    */

    /**
     * Add new user to database.
     *
     * @param userTyp - Use static variables from this class.
     * @param userName - The name which has been choosen from the user.
     * @param mailAdress - The mailadress which has been choosen from the user.
     * @param passwordHash - The password as hash which has been choosen from the user.
     * @param favoriteRoutes - The routes which the user marked as favorites.
     *
     * @return true if adding the user to database was successful.
     */
    public static boolean addUser(int userTyp, String userName, String mailAdress,
                                  String passwordHash, String favoriteRoutes) {
        try {
            Class.forName( "com.mysql.jdbc.Driver" );

            String sqlInsertStatement =
                    "INSERT INTO `personen`(`Benutzertyp`, `Benutzername`, `Mailadresse`, "
                            + "`Passworthash`, `Favoriten`) VALUES (\""+userTyp+"\", \""+userName+
                            "\", \""+mailAdress+"\", \""+passwordHash+"\", \""+favoriteRoutes+"\") ";

            try {

                Connection connection = DriverManager.getConnection("jdbc:mysql://"+DBHandler.DB_IP_ADDRESS+":"+DB_PORT+"/"+DB_DATABASE, DBHandler.DB_USER, DBHandler.DB_PASSWORD);
                Statement statement = connection.createStatement();
                statement.execute(sqlInsertStatement);

                return true;

            } catch (SQLException ex) {
                return false;
            }
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    /**
     * Remove user from database.
     *
     * @param userName
     *
     * @return "notexisting" if username is not in database, "failure" if any
     * failure occured and an empty String if successfull.
     */
    public static String removeUser(String userName){
        try {

            if (DBHandler.isUserInUse(userName, "").contains("user")){
                Class.forName( "com.mysql.jdbc.Driver" );

                String sqlDeleteStatement =
                        "DELETE FROM `personen` WHERE `Benutzername` like \""+userName+"\"";

                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+DBHandler.DB_IP_ADDRESS+":"+DB_PORT+"/"+DB_DATABASE, DBHandler.DB_USER, DBHandler.DB_PASSWORD);
                    Statement statement = connection.createStatement();
                    statement.execute(sqlDeleteStatement);

                    return "";

                } catch (SQLException ex) {
                    return "failure";
                }
            } else {
                return "notexisting";
            }
        } catch (ClassNotFoundException ex) {
            return "failure";
        }
    }

    /**
     * Check if username and / or mailadress is in database.
     *
     * @param userName
     * @param mailAddress
     *
     * @return Empty String if both are not existing, "user" if username exists,
     * "mail" if mailaddress exists and "usermail" if both exists. "Failure" is
     * returned if any failure occured.
     */
    public static String isUserInUse(String userName, String mailAddress) {

        String returnString = "";

        try {
            Class.forName( "com.mysql.jdbc.Driver" );
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+DBHandler.DB_IP_ADDRESS+":"+DB_PORT+"/"+DB_DATABASE, DBHandler.DB_USER, DBHandler.DB_PASSWORD);

            // username
            String userSelect = "SELECT `Benutzername` FROM `personen` WHERE `Benutzername` like \""+userName+"\"";
            Statement userStatement = connection.createStatement();
            ResultSet userQuery = userStatement.executeQuery(userSelect);
            boolean userExists = userQuery.first();

            // mailaddress
            String mailSelect = "SELECT `Mailadresse` FROM `personen` WHERE `Mailadresse` like \""+mailAddress+"\"";
            Statement mailStatement = connection.createStatement();
            ResultSet mailQuery = mailStatement.executeQuery(mailSelect);
            boolean mailExists = mailQuery.first();

            if (userExists) {
                returnString += "user";
            }
            if (mailExists) {
                returnString += "mail";
            }

            return returnString;

        } catch (ClassNotFoundException | SQLException ex) {
            return "failure";
        }
    }

    /*
        Functions that deals with route data.
    */

    /**
     * Add new route to database.
     *
     * @param owner
     * @param description
     * @param routeLength
     * @param numberVotes
     * @param averageVotes
     * @param waypoints
     *
     * @return Empty String if successfull, "user" if route owner does not exist
     * and "failure" if any failure occured.
     */
    public static String addRoute(int owner, String description, double routeLength,
                                  int numberVotes, double averageVotes, String waypoints){
        try {
            if (DBHandler.existsRouteId(owner)){
                Class.forName( "com.mysql.jdbc.Driver" );

                String sqlInsertStatement =
                        "INSERT INTO `strecken`(`Ersteller`, `Beschreibung`, "
                                + "`Streckenlänge`, `Anzahl_Bewertungen`, `Durchschnittsbewertung`, "
                                + "`Wegpunkte`) VALUES (\""+owner+"\", \""+description
                                +"\", \""+routeLength+"\", \""+numberVotes+"\", \""
                                +averageVotes+"\", \""+waypoints+"\") ";

                try {

                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+DBHandler.DB_IP_ADDRESS+":"+DB_PORT+"/"+DB_DATABASE, DBHandler.DB_USER, DBHandler.DB_PASSWORD);
                    Statement statement = connection.createStatement();
                    statement.execute(sqlInsertStatement);

                    return "";

                } catch (SQLException ex) {
                    return "failure";
                }
            } else {
                return "user";
            }
        } catch (ClassNotFoundException ex) {
            return "failure";
        }
    }

    /**
     * Remove route from database.
     *
     * @param routeId
     *
     * @return "notexisting" if route is not in database, "failure" if any
     * failure occured and an empty String if successfull.
     */
    public static String removeRoute(int routeId){
        try {

            if (DBHandler.existsRouteId(routeId)){
                Class.forName( "com.mysql.jdbc.Driver" );

                String sqlDeleteStatement =
                        "DELETE FROM `strecken` WHERE `SID` like \""+routeId+"\"";

                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+DBHandler.DB_IP_ADDRESS+":"+DB_PORT+"/"+DB_DATABASE, DBHandler.DB_USER, DBHandler.DB_PASSWORD);
                    Statement statement = connection.createStatement();
                    statement.execute(sqlDeleteStatement);

                    return "";

                } catch (SQLException ex) {
                    return "failure";
                }
            } else {
                return "notexisting";
            }
        } catch (ClassNotFoundException ex) {
            return "failure";
        }
    }

    /**
     * Return the
     *
     * @param whereClause - SQL-WHERE clause
     *
     * @return ArrayList filled with Route objects. If any failure occure it returns null.
     */
    public static ArrayList<Route> getRoutes(String whereClause){
        ArrayList<Route> routes = new ArrayList<>();

        try {
            Class.forName( "com.mysql.jdbc.Driver" );
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+DBHandler.DB_IP_ADDRESS+":"+DB_PORT+"/"+DB_DATABASE, DBHandler.DB_USER, DBHandler.DB_PASSWORD);

            String routeSelect;
            if (whereClause.equals("")){
                routeSelect = "SELECT * FROM `strecken`";
            } else {
                routeSelect = "SELECT * FROM `strecken` WHERE "+whereClause;
            }

            Statement routeStatement = connection.createStatement();
            ResultSet userQuery = routeStatement.executeQuery(routeSelect);


            int i = 1;
            while(userQuery.next()){
                int sid = Integer.parseInt(userQuery.getString("SID"));
                int owner = Integer.parseInt(userQuery.getString("Ersteller"));
                String description = userQuery.getString("Beschreibung");
                double length = Double.parseDouble(userQuery.getString("Streckenlänge"));
                int numberVotes = Integer.parseInt(userQuery.getString("Anzahl_Bewertungen"));
                double averageVotes = Double.parseDouble(userQuery.getString("Durchschnittsbewertung"));
                String waypoints = userQuery.getString("Wegpunkte");

                routes.add(new Route(sid, owner, description, length, numberVotes, averageVotes, waypoints));

                i++;
            }

            return routes;
        } catch (ClassNotFoundException | SQLException ex) {
            return null;
        }
    }

    /*
        HELP FUNCTIONS
    */

    /**
     * Checks if RID in database exists.
     *
     * @param id
     * @return
     */
    private static boolean existsRouteId(int id){
        try {
            Class.forName( "com.mysql.jdbc.Driver" );
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+DBHandler.DB_IP_ADDRESS+":"+DB_PORT+"/"+DB_DATABASE, DBHandler.DB_USER, DBHandler.DB_PASSWORD);

            String userSelect = "SELECT `SID` FROM `strecken` WHERE `SID` like \""+id+"\"";
            Statement userStatement = connection.createStatement();
            ResultSet userQuery = userStatement.executeQuery(userSelect);

            return userQuery.first();

        } catch (ClassNotFoundException | SQLException ex) {
            return false;
        }
    }
}
