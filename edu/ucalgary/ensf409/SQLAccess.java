/**
 * @author Liana Goodman
 * @author Ethan Sengsavang
 * @author Amir Abdrakmanov
 * @version 1.0
 * @since 1.0
 */

package edu.ucalgary.ensf409;

import java.sql.*;

public class SQLAccess {
    private final String USERNAME;
    private final String PASSWORD;
    private final String DBURL;
    private Connection dbConnection;

    /**
     * Constructor for SQLAccess to initialize server and database information
     * initializes all datamembers and the connection to the database
     * @param username username to access the database
     * @param password password to access the database
     * @param dburl url to access the database
     */
    public SQLAccess (String username, String password, String dburl) {
        this.USERNAME = username;
        this.PASSWORD = password;
        this.DBURL = dburl;
        initializeConnection();
    }

    /**
     * Initializes connection with instance username, password, and dburl
     */
    public void initializeConnection () {
        try{
            dbConnect = DriverManager.getConnection(this.DBURL, this.USERNAME, this.PASSWORD);
        } catch (SQLException e) {
            System.out.println("SQL connection failed.");
            e.printStackTrace();
        } catch (Exeption e) {
            System.out.println("Unknown error in initialization.");
            e.printStackTrace();
        }
    }

    /**
     * Closes connection to the database
     */
    private void closeConnection () {
        try {
            dbConnect.close();
        } catch (SQLException e) {
            System.out.println("Error while closing database connection");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknonw error while closing database connection");
            e.printStackTrace();
        }
    }

    /**
     * Removing used furniture from the database
     * @param table string argument of the table from which furniture was used
     * @param id a string id for the item which was used
     * @return boolean value of success or failure
     */
    public boolean removeFurniture (String table, String id) {

    }

    /**
     * Retrieving the fields of a given table in an array to allow for a
     * description of the table.
     * @param table the table required to get all of this information from
     * @return String array of the fields in order
     */
    public String [] getFields(String table) {

    }

    /**
     * Gets all the information in a given table and returns the results
     * @param table the table from which to retrive all the data from
     * @return ResultSet of all results in the table
     */
    public ResultSet getTableInformation (String table) {

    }

    /**
     * Allows access to the members in a given result set and returns information
     * based on a field and a key.
     * @param resultSet the ResultSet that is provided for search
     * @param field the field needed to be searched by (can be found originally 
     * using the getFileds() method)
     * @param key the key in the provided field to be searching for.
     * @return the entire information set as organized by field order into a 
     * String array
     */
    public String [] searchFor (ResultSet resultSet, String field, String key) {

    }
}