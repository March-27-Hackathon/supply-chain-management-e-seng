/**
 * @author Liana Goodman
 * @author Ethan Sengsavang
 * @author Amir Abdrakmanov
 * @version 1.4
 * @since 1.0
 */

package edu.ucalgary.ensf409;

import java.sql.*;

public class SQLAccess {
    private final String USERNAME;
    private final String PASSWORD;
    private final String DBURL;
    private Connection dbConnection;
    private ResultSet results;

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
        this.initializeConnection();
    }

    /**
     * Initializes connection with instance username, password, and dburl
     */
    private void initializeConnection () {
        try{
            dbConnect = DriverManager.getConnection(this.DBURL, this.USERNAME, this.PASSWORD);
        } catch (SQLException e) {
            System.out.println("SQL connection failed.");
            e.printStackTrace();
            System.exit(1);
        } catch (Exeption e) {
            System.out.println("Unknown error in initialization.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Closes connection to the database and the ResultSet used throughout the class
     */
    private void close () {
        try {
            this.dbConnect.close();
            this.results.close();
        } catch (SQLException e) {
            System.out.println("Error while closing database connection and result set");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Unknown error while closing database connection and result set");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Removing used furniture from the database
     * @param table string argument of the table from which furniture was used
     * @param id a string id for the item which was used
     * @return boolean value of success or failure
     */
    public boolean removeFurniture (String table, String id) {
        try {
            String query = "DELETE FROM ? WHERE id = ?";    // set up query
            PreparedStatement delStmnt = dbConnection.prepareStatement(query);

            delStmnt.setString(1, table);   // insert specific table
            delStmnt.setString(2, id);      // insert id to be removed

            int rows = delStmnt.executeUpdate();    // update the table

            delStmnt.close();

            /* 
             * ID's are unique and there should only be one instance of it
             * Check to see if the row was effected and return true if the item
             * was deleted and false if there was an error such as the ID not 
             * existing.
             */
            if (rows == 1) {  
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error deleting furniture item " + id
                + " from " + table);
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Unknown error deleting furniture item " + id
                + " from " + table);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Retrieving the fields of a given table in an array to allow for a
     * description of the table.
     * @param table the table required to get all of this information from
     * @return String array of the fields in order
     */
    public String [] getFields(String table) {
        // Retrieve all results from the table (not necessary to pull all but simple)
        this.results = getTableInformation(table);
        ResultSetMetaData meta = this.results.getMetaData(); // retrieve metadata about the table

        // Make an array that will fit all the fields
        String [] fields = new String [meta.getColumnCount()];

        for (int i = 0; i < fields.length; i++) {   // fill the array
            /* Metadata getColumnName starts at index 1 where as arrays start
             * at 0th index. */
            fields[i] = meta.getColumnName(i + 1);
        }

        return fields;
    }

    /**
     * Gets all the information in a given table and returns the results
     * @param table the table from which to retrive all the data from
     * @return ResultSet of all results in the table
     */
    public ResultSet getTableInformation (String table) {
        try {
            String query = "SELECT * FROM " + table;

            Statement statement = dbConnection.createStatement();
            this.results = statement.executeQuery(query);

            statement.close();

            return this.results;
        } catch (SQLException e) {
            System.out.println("Error in accessing the table and it's information");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Unknonw error in accessing the table and it's information");
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Allows access to the members in a given result set and returns information
     * based on a field and a key.
     * @param table the table through which we will be searching.
     * @param field the field needed to be searched by (can be found originally 
     * using the getFileds() method)
     * @param key the key in the provided field to be searching for.
     * @return the entire information set as organized by field order into a 
     * String array
     */
    public String [] searchFor (String table, String field, String key) {

    }
}