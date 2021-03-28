/**
 * @author Liana Goodman
 * @author Ethan Sengsavang
 * @author Amir Abdrakmanov
 * @version 2.0
 * @since 1.0
 */

package edu.ucalgary.ensf409;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class to access, manipulate and modify the SQL databases. We are not doing
 * anything to the data, just accessing.
 */
public class SQLAccess {
    private final String USERNAME;
    private final String PASSWORD;
    private final String DBURL;
    private Connection dbConnection;
    private ResultSet results;
    private Statement statement;

    /**
     * main used only for debugging purposes
     * @param args command line args.
     */
    public static void main (String [] args) {
        SQLAccess access = new SQLAccess("ensf409", "ensf409", "jdbc:mysql://localhost/INVENTORY");

        boolean success = access.removeFurniture("LAMP", "L013"); // WORK FINE
        System.out.println(success);

        success = access.removeFurniture("LAMP", "L015"); // returns false
        System.out.println(success);

        String [] fields = access.getFields("FILING");
        for (String item:fields) {
            System.out.print(item +"\t");
        }

        System.out.println("\nSearch results:");
        String [][] searchResults = access.searchFor("FILING", "Type", "Small");
        for (int i = 0; i < searchResults.length; i++) {
            for (int j = 0; j < searchResults[i].length; j++) {
                System.out.print(searchResults[i][j] + "\t");
            }
            System.out.println();
        }

        System.out.println("\nFilter results:");
        searchResults = access.filter("FILING", "Small", "Rails", "Y");
        for (int i = 0; i < searchResults.length; i++) {
            for (int j = 0; j < searchResults[i].length; j++) {
                System.out.print(searchResults[i][j] + "\t");
            }
            System.out.println();
        }

        access.close();
    }

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
            this.dbConnection = DriverManager.getConnection(this.DBURL, this.USERNAME, this.PASSWORD);
            this.statement = dbConnection.createStatement();
        } catch (SQLException e) {
            System.out.println("SQL connection failed.");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("Unknown error in initialization.");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Closes connection to the database and the ResultSet used throughout the class
     */
    public void close () {
        try {
            this.dbConnection.close();
            if (this.results != null) {
                this.results.close();
            }
            this.statement.close();
        } catch (SQLException e) {
            System.out.println("Error while closing database connection and result set");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("Unknown error while closing database connection and result set");
            ex.printStackTrace();
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
            String query = "DELETE FROM " + table + " WHERE id = \'" + id + "\'";    // set up query
            PreparedStatement delStmnt = dbConnection.prepareStatement(query);

            // delStmnt.setString(1, id);      // insert id to be removed

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
            }
        } catch (SQLException e) {
            System.out.println("Error deleting furniture item " + id
                + " from " + table);
            e.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("Unknown error deleting furniture item " + id
                + " from " + table);
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    /**
     * Retrieving the fields of a given table in an array to allow for a
     * description of the table.
     * @param table the table required to get all of this information from
     * @return String array of the fields in order
     */
    public String [] getFields(String table) {
        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    /**
     * Gets all the information in a given table and returns the results
     * @param table the table from which to retrive all the data from
     * @return ResultSet of all results in the table returns null if unsuccessful
     */
    public ResultSet getTableInformation (String table) {
        try {

            String query = "SELECT * FROM " + table; // set query with proper table

            this.results = statement.executeQuery(query); // update the ResultSet

            // get the number of columns
            ResultSetMetaData meta = this.results.getMetaData();
            int columns = meta.getColumnCount();

            return this.results; // return the result set (also functions as a getter method)
        } catch (SQLException e) {
            System.out.println("Error in accessing the table and it's information");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("Unknonw error in accessing the table and it's information");
            ex.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    /**
     * Allows access to the members in a given table and returns information
     * based on a field and a key. The method also checks for whether or not
     * a field exists within the table and whether the key exists.
     * @param table the table through which we will be searching.
     * @param field the field needed to be searched by (can be found originally 
     * using the getFileds() method)
     * @param key the key in the provided field to be searching for.
     * @return the entire information set as organized by field order into a 
     * double String array to accomodate multiple instances. 
     * null will be returned if the key does not exist or the field does not 
     * exist in the table.
     */
    public String [][] searchFor (String table, String field, String key) {
        /* Handling the errors for if the field does not exist */
        String [] fields = this.getFields(table);   // retrieve the fields
        String tmp = "";
        for (int i = 0; i < fields.length; i++) {   // if the field is found, exit
            if (fields[i].equals(field)) {
                tmp = fields[i];
                break;
            }
        }

        if (tmp.equals("")) {   // compare if the tmp was changed.
            return null;
        }

        try {
            // Proceeding with search
            String query = "SELECT * FROM " + table + " WHERE " + field + " = \'" + key + "\'";
            Statement searchStmt = dbConnection.createStatement();
            this.results = searchStmt.executeQuery(query);

            ArrayList<String []> arr = new ArrayList<String []>();

            while (this.results.next()) {
                String [] temp = new String [fields.length];

                for (int i = 0; i < temp.length; i++) {
                    temp [i] = this.results.getString(i + 1);
                }

                arr.add(temp);
            }

            return arr.toArray(new String[arr.size()][fields.length]);

        } catch (SQLException e) {
            System.out.println("Error in retrieving " + key + " from " + field + " in " + table);
            e.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("Unknown error in retrieving " + key + " from " + field + " in " + table);
            ex.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    /**
     * Search for item based on table, type and a third characteristic parameter
     * make sure that all instances are found.
     * @param table the table to be searched
     * @param type type value that matches the type
     * @param param third search parameter which is specific to a single table
     * @param key the value of param to be searched for.
     * @return returns a double String array of all the results that match the
     * criteria based on the parameters. null will be returned if there are no
     * results.
     */
    public String [][] filter (String table, String type, String param, String key) {
        try {
            // Search
            String [] fields = this.getFields(table);
            String query = "SELECT * FROM " + table + " WHERE Type = \'" + type + "\' AND "
                + param + " = \'" + key + "\'";
            Statement searchStmt = dbConnection.createStatement();
            this.results = searchStmt.executeQuery(query);

            ArrayList<String []> arr = new ArrayList<String []>();

            while (this.results.next()) {
                String [] temp = new String [fields.length];

                for (int i = 0; i < temp.length; i++) {
                    temp [i] = this.results.getString(i + 1);
                }

                arr.add(temp);
            }

            return arr.toArray(new String[arr.size()][fields.length]);

        } catch (SQLException e) {
            System.out.println("Error in retrieving values from " + table + 
                " filtered by type " + type + " and " + key + " under " + param);
            e.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("Unknown Error in retrieving values from " + table + 
                " filtered by type " + type + " and " + key + " under " + param);
            ex.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    /**
     * Getting all the possible manufacturers from the table
     * @param table the table to search
     * @param type the value of the type to filter by
     * @return String array of the values that fit the criteria but only of the
     * manufacturer id's
     */
    public String [] getManuIDs (String table, String type) {
        String [][] tableResults = this.searchFor(table, "Type", type);
        String [] fields = this.getFields(table);

        int index = -1;
        int i = 0;
        for (String item : fields) {    // get the index at where the ID will be kept
            if (item.equals("ManuID")) {
                index = i;
            }
            i ++;
        }

        if (index == -1) {  // if there are no ManuIDs
            System.out.println("Invalid table entry, cannot find ManuID");
            System.exit(1);
        }

        String [] manuIDs = new String [tableResults.length];
        for (int k = 0; k < tableResults.length; k++) {
            manuIDs [k] = tableResults [k][index];  // getting the id's
        }

        ArrayList <String> filteredIDs = new ArrayList<String>();
        for (int k = 0; k < manuIDs.length; k++) {
            // if the ID is not already in the list then add it
            if (!filteredIDs.contains(manuIDs[k])) {    
                filteredIDs.add(manuIDs[k]);
            }
        }

        return filteredIDs.toArray(new String [filteredIDs.size()]);
    }
}