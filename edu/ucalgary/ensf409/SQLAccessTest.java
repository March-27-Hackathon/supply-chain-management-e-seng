/**
 * @author Liana Goodman
 * @author Ethan Sengsavang
 * @author Amir Abdrakmanov
 * @version 1.0
 * @since 2.0
 */

package edu.ucalgary.ensf409;

import static org.junit.Assert.*;
import org.junit.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import java.sql.*;

/**
 * Tests fot the SQLAccess class. All tests will be using the constructor, 
 * the initialization and the closing method.
 */
public class SQLAccessTest {
    public final String USERNAME = "ensf409";
    public final String PASSWORD = "ensf409";
    public final String DBURL = "jdbc:mysql://localhost/INVENTORY";

    @Test (expected = Exception.class)
    /**
     * Tests the constructor and a failed connection.
     */
    public void constructorAndInitializationFailed () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, "jdbc:mysql://localho/INVENTORY");

        access.close();
    }

    @Test
    /**
     * Tests properly removing furniture that exists
     */
    public void removeFurnitureExists () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        boolean success = access.removeFurniture("LAMP", "L053");
        access.close();
        assertTrue("Furniture not removed", success);
    }

    @Test
    /**
     * Tests removing furniture that does not exist
     */
    public void removeFurnitureNotExists () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        boolean success = access.removeFurniture("LAMP", "L676767676");
        access.close();

        // might need to check for a throw instead
        assertFalse("Furniture removed/error occured", success);
    }

    @Test
    /**
     * Tests getting fields from LAMP
     */
    public void retrieveLAMPFields () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [] fields = access.getFields("LAMP");
        String [] orig = {"ID", "Type", "Base", "Bulb", "Price", "ManuID"};

        access.close();

        assertEquals (fields, orig);
    }

    @Test
    /**
     * Tests getting fields from CHAIR
     */
    public void retrieveCHAIRFields () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [] fields = access.getFields("CHAIR");
        String [] orig = {"ID", "Type", "Legs", "Arms", "Seat", "Cushion", "Price", "ManuID"};

        access.close();

        assertEquals (fields, orig);
    }

    @Test
    /**
     * Tests getting fields from FILING
     */
    public void retrieveFILINGFields () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [] fields = access.getFields("FILING");
        String [] orig = {"ID", "Type", "Rails", "Drawers", "Cabinet", "Price", "ManuID"};

        access.close();

        assertEquals (fields, orig);
    }

    @Test
    /**
     * Tests getting fields from DESK
     */
    public void retrieveDESKFields () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [] fields = access.getFields("DESK");
        String [] orig = {"ID", "Type", "Legs", "Top", "Drawer", "Price", "ManuID"};

        access.close();

        assertEquals (fields, orig);
    }

    @Test
    /**
     * Tests getting fields from MANUFACTURER
     */
    public void retrieveMANUFACTURERFields () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [] fields = access.getFields("MANUFACTURER");
        String [] orig = {"ManuID", "Name", "Phone", "Province"};

        access.close();

        assertEquals (fields, orig);
    }

    @Test
    /**
     * Search for existing unique value by ID
     */
    public void searchForExistingUniqueID () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [][] results = access.searchFor("MANUFACTURER", "ManuID", "001");

        String [][] orig = {{"001", "Academic Desks", "236-145-2542", "BC"}};

        access.close();

        assertEquals (results, orig);
    }

    @Test
    /**
     * Search for existing non-unique value
     */
    public void searchForExistingNonUniqueID () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [][] results = access.searchFor("FILING", "Price", "50");

        String [][] orig = {{"F001", "Small", "Y", "Y", "N", "50", "005"},
                            {"F006", "Small", "Y", "Y", "N", "50", "005"},
                            {"F008", "Medium", "Y", "N", "N", "50", "005"},
                            {"F013", "Small", "N", "N", "Y", "50", "002"}};

        access.close();

        assertEquals (results, orig);
    }

    @Test
    /**
     * Search for non-existing unique value
     */
    public void searchForNonExistingUniqueID () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [][] results = access.searchFor("LAMP", "ManuID", "007");

        access.close();

        ArrayList<String> arr = new ArrayList<>();

        assertEquals(results, arr.toArray(new String [arr.size()]));
    }

    @Test
    /**
     * Test where the field searched for does not exist and should return null 
     */
    public void searchWithInvalidField () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [][] results = access.searchFor("LAMP", "Condition", "007");

        access.close();

        assertNull(results);
    }

    @Test
    /**
     * Search for existing non-unique value
     */
    public void searchForNonExistingNonUniqueID () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [][] results = access.searchFor("CHAIR", "Price", "103");

        access.close();

        ArrayList<String> arr = new ArrayList<>();

        assertEquals(results, arr.toArray(new String [arr.size()]));
    }

    @Test
    /**
     * Filter for existing values
     */
    public void filterForExistingResult () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [][] filtered = access.filter("LAMP", "Study", "Bulb", "Y");

        String [][] orig = {
            {"L223", "Study", "N", "Y", "2", "005"},
            {"L928", "Study", "Y", "Y", "10", "002"},
            {"L980", "Study", "N", "Y", "2", "004"}
            };

        access.close();

        assertEquals (filtered, orig);
    }

    @Test
    /**
     * Filter for non-existing param
     */
    public void filterForNonExistingResult () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [][] filtered = access.filter("LAMP", "Study", "Bulb", "Yes");

        access.close();

        ArrayList<String> arr = new ArrayList<>();

        assertEquals(filtered, arr.toArray(new String [arr.size()]));
    }

    @Test
    /**
     * Filter for non-existing type
     */
    public void filterForNonExistingType () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [][] filtered = access.filter("LAMP", "Red", "Bulb", "Y");

        access.close();

        ArrayList<String> arr = new ArrayList<>();

        assertEquals(filtered, arr.toArray(new String [arr.size()]));
    }

    @Test
    /**
     * Filter for existing unique value
     */
    public void filterForExistingUniqueResult () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [][] filtered = access.filter("LAMP", "Study", "Bulb", "N");

        String [][] orig = {{"L982", "Study", "Y", "N", "8", "002"}};

        access.close();

        assertEquals (filtered, orig);
    }

    @Test
    /**
     * Finding valid manufacturer IDS
     */
    public void findManufacturerIDs () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [] ids = access.getManuIDs("CHAIR", "Mesh");
        String [] orig = {"005", "003"};

        access.close();

        assertEquals (ids, orig);
    }

    @Test
    /**
     * Finding invalid ids from MANUFACTURER
     */
    public void gettingInvalidIDs () throws SQLException, Exception {
        SQLAccess access = new SQLAccess(USERNAME, PASSWORD, DBURL);

        String [] ids = access.getManuIDs("CHAIR", "TrialPack");

        ArrayList <String> test = new ArrayList<>();
        String [] orig = test.toArray(new String [test.size()]);

        access.close();

        assertEquals (ids, orig);
    }
}