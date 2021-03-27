/**
 * @author Amir Abdrakmanov, Ethan Sengsavang, Liana Goodman
 *
 * @version 1.0
 * @since 1.0
 */

package edu.ucalgary.ensf409;

public class Manager{
    private final SQLAccess databaseAccess;
    private final FileIO orderPrinter;
    private final orderedParts;

    public Manager(){
        this.databaseAccess = new SQLAccess();
        this.orderPrinter = new SQLAccess();
    }

    /**
     * Save the order with the cheapest necessary items within the database to
     * a .text file.
     * This will invoke a method within FileIO to save the files.
     * Information is sent in as raw data.
     * TODO: ensure data is sent in the correct order and format.
     *
     */
    public void saveOrder(){
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
     *  @param itemCategory The overall category that the item falls under.
     *   - This should be one of the tables in the Database.
     *  @param itemType The specific type of the item that is desired.
     *   - This word should be contained within the "type" field within each
     *     table.
     *  
     *  eg. if the query is: <code>mesh chair, 2</code>
     *  This method should be called twice.
     *
     * @throws IllegalArgumentException Thrown if item cannot be found within
     * database.
     *
     * @return A String array containing all ordered parts for the request.
     */
    private String[] findCheapestItems(String itemCategory, String itemType){
    }
}
