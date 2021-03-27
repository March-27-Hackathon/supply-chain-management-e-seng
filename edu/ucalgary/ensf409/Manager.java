/**
 * @author Amir Abdrakmanov, Ethan Sengsavang, Liana Goodman
 *
 * @version 1.0
 * @since 1.0
 */

package edu.ucalgary.ensf409;

import java.util.ArrayList;

public class Manager{
    private final SQLAccess databaseAccess;
    private final FileIO orderPrinter;
    private final ArrayList<String> orderedParts;

    public Manager(){
        this.databaseAccess = new SQLAccess();
        this.orderPrinter = new SQLAccess();
        orderedParts = new ArrayList<String>();
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
     *  @param itemType The specific type of the item that is desired.
     *   - This word should be contained within the "type" field within each
     *     table.
     *  @param itemCategory The overall category that the item falls under.
     *   - This should be one of the tables in the Database.
     *  
     *  eg. if the query is: <code>mesh chair, 2</code>
     *  This method should be called twice.
     *
     * @return A String array containing all ordered parts for the request.
     */
    private String[] findCheapestItems(String itemType, String itemCategory){
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
            return -1;
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
     * Confirms the order if the user wishes to.
     */
    public void confirmOrder(){
        saveOrder();
    }
}
