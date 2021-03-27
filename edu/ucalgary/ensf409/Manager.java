/**
 * @author Amir Abdrakmanov, Ethan Sengsavang, Liana Goodman
 *
 * @version 1.0
 * @since 1.0
 */

package edu.ucalgary.ensf409;

import java.util.ArrayList;

public class Manager{
    private SQLAccess databaseAccess;
    private ArrayList<String> orderedParts;
    private int totalPrice;

    private String dbUsername;
    private String dbPassword;
    private String dbUrl;
    private String fileName;

    public Manager(String dbUsername, String dbPassword, String dbUrl, String fileName){
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
        this.fileName = fileName;
        reset();
    }


    /**
     * Resets the Manager instance, removing previous history.
     */
    private void reset(){
        if(databaseAccess != null){
            databaseAccess.close();
        }

        databaseAccess = new SQLAccess(dbUsername, dbPassword, dbUrl);
        orderedParts = new ArrayList<String>();
        totalPrice = 0;
    }


    /**
     * Save the order with the cheapest necessary items within the database to
     * a .text file.
     * This will invoke a method within FileIO to save the files.
     * Information is sent in as raw data.
     * TODO: ensure data is sent in the correct order and format.
     *
     */
    private void saveOrder(){
    }


    /**
     * Finds the cheapest desired item and return all relevent IDs.
     *
     * As compete items are determined based off a non-zero number of items
     * satisfying all properties of an object, this will compare every
     * combination of possible peices in attempt to minimize the total cost only.
     *
     * A completed desired item will be returned as a String array. The length
     * will be the number of needed pieces to complete the item. Each element
     * will be the item's unique ID within the database - not the item's 
     * manufacturer ID.
     *  - This is to follow the convention provided by the example output
     *
     *  If the item is already ordered, then ignore it.
     *
     * @param itemType The specific type of the item that is desired.
     *  - This word should be contained within the "type" field within each
     *    table.
     * @param itemCategory The overall category that the item falls under.
     *  - This should be one of the tables in the Database.
     *
     *  eg. if the query is: <code>mesh chair, 2</code>,
     *  this method should be called twice.
     *
     * @return A String array containing all ordered parts for the request.
     */
    private String[] findCheapestItems(String itemType, String itemCategory){
    }


    /**
     * Gets the total cost of all items within the given array of IDs.
     *
     * @param ids The array of ids to get prices from.
     *
     * @return The combined cost of each specified part.
     */
    private double getPrice(String[] ids){
    }


    /**
     * Parses the desired order request from user input.
     *
     * @param order The order that the user is trying to make
     *
     * @return -1.00 if the order cannot be completed, the price otherwise.
     */
    public double parseOrder(String itemType, String itemCategory, int quantity){
        // Check if the inputs are valid.
        if(itemType == null || itemCategory == null || quantity == 0){
            System.out.println("Invalid request.");
            System.exit(1);
        }

        // Find all necessary parts to complete the order
        while(quantity > 0){
            String orderCombination[] = findCheapestItems(itemType, itemCategory);

            // If there are too few parts to complete an order, exit.
            if(orderCombination.length == 0){
                return -1;
            }

            for(String partID : orderCombination){
                orderedParts.add(partID);
            }

            quantity--;
        }
    }


    /**
     * Purchases the items and removes the items from the database.
     * A purchased item has no need to be in the database anymore.
     */
    private void purchaseItems(){
        for(String id : orderedParts){
            databaseAccess.removeFurniture()
        }
    }


    /**
     * Confirms the order if the user wishes to.
     * This will remove all bought instances within the database.
     * This will also reset the manager once the file is written.
     */
    public void confirmOrder(){
        saveOrder();
        purchaseItems();
        reset();
    }
}
