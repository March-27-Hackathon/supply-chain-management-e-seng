/**
 * @author Amir Abdrakmanov, Ethan Sengsavang, Liana Goodman
 *
 * @version 1.0
 * @since 1.0
 */

package edu.ucalgary.ensf409;

import java.util.ArrayList;
import java.util.Arrays;

public class Manager{
    private SQLAccess databaseAccess;
    private ArrayList<String> orderedParts;
    private int totalPrice;

    private String dbUsername;
    private String dbPassword;
    private String dbUrl;
    private String fileName;

    public Manager(String dbUsername, String dbPassword, String dbUrl){
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
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
	 * @param origReq the original request made by the user
     * Save the order with the cheapest necessary items within the database to
     * a .text file.
     * This will invoke a method within FileIO to save the files.
     * Information is sent in as raw data.
     * TODO: ensure data is sent in the correct order and format.
     *
     */
    private void saveOrder(String origReq, String itemCategory){
        if(fileName == null){
            System.out.println("File name not specified.");
            System.exit(1);
        }
        FileIO orderWriter = new FileIO(fileName);
		String [] temp = this.orderedParts.toArray(new String[0]);
		orderWriter.write(temp,origReq, this.getPrice(temp, itemCategory));
    }

  
    /**
	 * @param fileName String parameter to set fileName
	 * setter function for fileName
	 */
	public void setFileName(String fileName){
	    this.fileName=fileName;
	}
	
    
    /**
	 * @param descript String adjective used to describe item, i.e. "mesh" or "executive"
	 * @param item String noun that describes the item, i.e "chair" or "lamp"
	 * Simple function that calls a database function to find a list of
	 * manufacturers based on the desired item
	 */
	public String[] getManufacturersList(String descript, String item){
		return this.databaseAccess.getManuIDs(item, descript);
	}

    /**
     * Finds the cheapest desired item and return all relevent IDs.
     *
     * As compete items are determined based off a non-zero number of items
     * satisfying all parts of an object, this will compare every
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
        // Get all relevant ids for the item type.
        String[][] relevantRows = databaseAccess.searchFor(itemCategory, "Type", itemType);
        String[] ids = new String[relevantRows.length];
        String[] parts = isolateParts(relevantRows[0]);

        for(int index = 0; index < relevantRows.length; index++){
            ids[index] = relevantRows[index][0]; // IDs are stored at index 0;
        }

        boolean[] missingParts = new boolean[parts.length];
        for(int index = 0; index < parts.length; index++){
            missingParts[index] = true;
        }
        
        return minimizePrice(new String[0], new String[0], missingParts, itemCategory);
    }


    /**
     * Recusively finds the set of ids that make a full item, with the lowest
     * overall price.
     *
     * @param chosenIDs The backlog of chosen IDs to determine price
     *  - Needs to be empty when initially called
     * @param idPool The pool of ids to choose new ids from
     *  - Needs to be empty when initially called
     * @param missingParts All parts that the item is missing before
     * it becomes a complete part.
     *  - Needs to have the same number of parts that make up that item,
     *    all filled with true.
     * @param itemCategory the overall category that the item falls under.
     *  - This should be one of the tables in the database.
     *
     * @return The set with the lowest price.
     */
    private String[] minimizePrice(String[] chosenIDs, String[] idPool, boolean[] missingParts, String itemCategory){
        // no need for additional parts? return with the previous ids.
        boolean complete = true;
        for(boolean partMissing : missingParts){
            complete = complete && !partMissing;
        }
        if(complete){
            return chosenIDs;
        }
        // still need things but nowhere to pull from? failed - return
        if(idPool.length == 0){
            return new String[0];
        }

        // Determine which parts still need to be searched for.
        // hash map pls
        boolean stillMissing[] = missingParts;
        if(chosenIDs.length != 0){
            stillMissing = new boolean[missingParts.length];
            String id = chosenIDs[chosenIDs.length - 1];
            String[] itemRow = databaseAccess.searchFor(itemCategory, "ID", id)[0];
            String[] itemParts = isolateParts(itemRow);

            for(int i = 0; i < missingParts.length; i++){
                boolean hasPart = itemParts[i].equals("Y");
                stillMissing[i] = !hasPart && missingParts[i];
                // This is only true if the row does not have the part 
                // and the part is still missing.
            }
        }

        // Preparing first comparison item
        String[] chosen1 = arrAppend(chosenIDs, idPool[0]);
        String[] pool1 = arrRemove(idPool, 0);

        String[] lowest = minimizePrice(chosen1, pool1, stillMissing, itemCategory);
        if(lowest.length == 0){
            return new String[0]; // This combination cannot be used
        }

        for(int index = 1; index < idPool.length; index++){
            // Preparing next comparison items
            String[] chosen2 = arrAppend(chosenIDs, idPool[index]);
            String[] pool2 = arrRemove(idPool, index);
            String[] comp2 = minimizePrice(chosen2, pool2, stillMissing, itemCategory);

            if(comp2 == null){
                continue; // This combination cannot be used
            }

            if(getPrice(lowest, itemCategory) < getPrice(comp2, itemCategory)){
                lowest = comp2;
            }
        }

        return lowest;
    }


    private String[] isolateParts(String[] row){
        // Stuff to ignore exists at index 0, 1, row.length - 1 and row.length - 2
        // ie. remove first two things and last two things
        
        // garbage
        String[] returnArr = arrRemove(row, 0);
        returnArr = arrRemove(returnArr, 0);
        returnArr = arrRemove(returnArr, returnArr.length-1);
        returnArr = arrRemove(returnArr, returnArr.length-1);

        return returnArr;
    }


    private String[] arrAppend(String[] original, String item){
        String[] returnedArray = new String[original.length+1];
        for(int index = 0; index < original.length; index++){
            returnedArray[index] = original[index];
        }

        returnedArray[original.length] = item;
        return returnedArray;
    }


    private String[] arrRemove(String[] original, int ignoreIndex){
        return arrRemove(original, original[ignoreIndex]);
    }


    private String[] arrRemove(String[] original, String item){
        String[] returnedArray = new String[original.length-1];

        int index = 0;
        for(String origStr : original){
            if(origStr.equals(item)){
                continue;
            }

            returnedArray[index] = origStr;
            index++;
        }

        return returnedArray;
    }


    /**
     * Gets the total cost of all items within the given array of IDs.
     *
     * @param ids The array of ids to get prices from.
     * @param itemCategory The overall category that the item falls under.
     *  - This should be one of the tables in the Database.
     *
     * @return The combined cost of each specified part.
     */
    private double getPrice(String[] ids, String itemCategory){
        String[] fields = databaseAccess.getFields(itemCategory);

        // Ensure that the price field is handled
        int priceIndex = 0;
        for(String field : fields){
            if(field.equals("Price")){
                break;
            }

            priceIndex++;
        }

        double totalCost = 0;
        for(String id : ids){
            // :/
            String[] itemRow = databaseAccess.searchFor(itemCategory, "ID", id)[0];
            String priceStr = itemRow[priceIndex];
            priceIndex += Double.parseDouble(priceStr);
        }

        return totalCost;

    }


    /**
     * Parses the desired order request from user input.
     *
     * @param order The order that the user is trying to make
     *
     * @return -1.00 if the item does not exist,
     *         -2.00 if the item exists but cannot be made,
     *         the price otherwise.
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
                return -2;
            }

            for(String partID : orderCombination){
                orderedParts.add(partID);
            }

            quantity--;
        }

        return getPrice(orderedParts.toArray(new String[orderedParts.size()]), itemCategory);
    }


    /**
     * Purchases the items and removes the items from the database.
     * A purchased item has no need to be in the database anymore.
     *
     * @param itemCategory The category of the item
     */
    private void purchaseItems(String itemCategory){
        for(String id : orderedParts){
            databaseAccess.removeFurniture(itemCategory, id);
        }
    }


    /**
     * Confirms the order if the user wishes to.
     * This will remove all bought instances within the database.
     * This will also reset the manager once the file is written.
     */

    public void confirmOrder(String origReq){
        String[] requestParts = origReq.split(" ");
        // String itemType = requestParts[0];
        String itemCategory = requestParts[1];
        // String count = requestParts[2];

        saveOrder(origReq, itemCategory);
        purchaseItems(itemCategory);
        // reset();
        
        databaseAccess.close();
    }
}
