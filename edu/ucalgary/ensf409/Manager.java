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
        String[] partNames = isolateParts(databaseAccess.getFields(itemCategory));
        
        // Store, in an array which parts have been satisfied. (initialized false)
        boolean hasPart[] = new boolean[partNames.length];
        for(int index = 0; index < partNames.length; index++){
            hasPart[index] = false;
        }
     
        boolean foundCheapest = false;
        String[] lowestIDs = new String[0];

        final String FLAG_HAS = "Y";
        final String FLAG_NOT_HAS = "N";
        final int START_PADDING = 2;
        final int END_PADDING = 2;

        final int COST_INDEX = partNames.length + 3;
        final int ID_INDEX = 0;
        final double MAX = 9999999;
        while(!foundCheapest){

            // Get all items that contain some number of the missing parts
            String potentialItems[][] = new String[0][partNames.length+4];
            for(int index = 0; index < partNames.length; index++){
                if(hasPart[index]){continue;}
                String partName = partNames[index];
                String[][] rows = databaseAccess.filter(itemCategory, itemType, partName, FLAG_HAS);
                if(rows.length == 0){
                    return new String[0];
                }
                for(String[] row : rows){
                    potentialItems = arrAppend(potentialItems, row);
                }
            }


            // Find cheapest item per part
            // ie. minimize price per part
            double lowestCost = MAX;
            String[] lowestItem = null;
            for(int index = 0; index < potentialItems.length; index++){
                int hasPartCount = 0;
                String[] focusItem = potentialItems[index];
                for(int j = START_PADDING; j < focusItem.length - END_PADDING; j++){
                    if(focusItem[j].equals(FLAG_NOT_HAS)){
                        continue;
                    }

                    hasPartCount++;
                }
                double totalCost = Double.parseDouble(focusItem[COST_INDEX]);
                double costPerPart = totalCost / hasPartCount;

                if(costPerPart > lowestCost){
                    continue;
                }

                lowestCost = costPerPart;
                lowestItem = focusItem;
            }

            // Check which parts still need to be found;
            for(int i = START_PADDING; i < lowestItem.length - END_PADDING; i++){
                boolean currentState = hasPart[i-START_PADDING];
                boolean partFound = lowestItem[i].equals(FLAG_HAS);
                hasPart[i-START_PADDING] = currentState || partFound;
            }

            lowestIDs = arrAppend(lowestIDs, lowestItem[ID_INDEX]);

            foundCheapest = true;
            for(boolean partCheck : hasPart){
                foundCheapest = foundCheapest && partCheck;
                System.out.print(" " + partCheck);
            }
        }

        return lowestIDs;
    }


    /**
     * Isolates every part name that builds up the item.
     * As the first and last two items are consistently information about the
     * item, they can be removed.
     *
     * @param row The row with all fields from the database.
     *
     * @return An array containing all part names.
     */
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


    /**
     * Appends a String to the end of a given String array.
     *
     * @param original The String array to append to
     * @param item The String to append
     * 
     * @return The String array with the item appended at the ended
     */
    private String[] arrAppend(String[] original, String item){
        String[] returnedArray = new String[original.length+1];
        for(int index = 0; index < original.length; index++){
            returnedArray[index] = original[index];
        }

        returnedArray[original.length] = item;
        return returnedArray;
    }


    /**
     * Appends a String array to the end of a given 2D String array.
     *
     * @param original The 2D String array to append to.
     * @param item The String array to append.
     *
     * @return The 2D String array with the new String array appended to it.
     */
    private String[][] arrAppend(String[][] original, String item[]){
        String[][] returnedArray = new String[original.length+1][item.length];
        for(int index = 0; index < original.length; index++){
            returnedArray[index] = original[index];
        }

        returnedArray[original.length] = item;
        return returnedArray;
    }


    /**
     * Removes a specific object at a specific index.
     * Also makes the array one element smaller.
     *
     * @param original The String array to remove items from.
     * @param ignoreIndex The index of the item that needs to be removed.
     *
     * @return A smaller string array without the item at the given index.
     */
    private String[] arrRemove(String[] original, int ignoreIndex){
        return arrRemove(original, original[ignoreIndex]);
    }


    /**
     * Removes a specific object within a String array.
     * Also makes the array one element smaller.
     *
     * @param original The String array to remove items from.
     * @param ignoreIndex The item that needs to be removed.
     *
     * @return A smaller string array without the specified item.
     */
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
            totalCost += Double.parseDouble(priceStr);
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
