/**
 * @author Amir Abdrakmanov, Ethan Sengsavang, Liana Goodman
 *
 * @version 1.1 - Exception handling
 * 1.0 - Basic functionality
 * @since 1.0
 */

package edu.ucalgary.ensf409;

import java.util.ArrayList;
import java.util.Arrays;
import java.sql.SQLException;


/**
 * Manage data collected from the database and pass that data into the file
 * writer if a possible order can be made.
 * This will use information of the desired order from UserIO to call
 * SQLAccess to query.
 * This will determine the total cost, and the minimal cost for complete items.
 *
 * @author Ethan Sengsavang
 */
public class Manager{
    private SQLAccess databaseAccess;
    private ArrayList<String> orderedParts;
    private int extraParts[];
    private int totalPrice;
    private int orderCount;

    private String dbUsername;
    private String dbPassword;
    private String dbUrl;
    private String fileName;


    public Manager(String dbUsername, String dbPassword, String dbUrl) throws SQLException, Exception{
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
        reset();
    }


    /**
     * Getters
     */
    public ArrayList<String> getOrderedParts(){
        return this.orderedParts;
    }


    /**
     * Resets the Manager instance, removing previous history.
     */
    public void reset() throws SQLException, Exception{
        if(databaseAccess != null){
            databaseAccess.close();
        }

        databaseAccess = new SQLAccess(dbUsername, dbPassword, dbUrl);
        // Clear previous order if any
        if(orderedParts == null){
            orderedParts = new ArrayList<String>();
        }
        orderedParts.clear();
        extraParts = null;
        totalPrice = 0;
        orderCount = 0;
    }


    /**
     * Save the order with the cheapest necessary items within the database to
     * a .text file.
     * This will invoke a method within FileIO to save the files.
     * Information is sent in as raw data.
     *
	 * @param origReq the original request made by the user
     *
     * @throws SQLException
     * @throws Exception
     */
    private void saveOrder(String origReq, String itemCategory) throws SQLException, Exception{
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
	 * Simple function that calls a database function to find a list of
	 * manufacturers based on the desired item
     *
	 * @param descript String adjective used to describe item, i.e. "mesh" or "executive"
	 * @param item String noun that describes the item, i.e "chair" or "lamp"
     *
     * @throws SQLException
     * @throws Exception
	 */
	public String[] getManufacturersList(String item) throws SQLException, Exception{
		//return this.databaseAccess.getManuIDs(item, descript);
		String temp[] = null;
		String[][] temp1=null;
		if(item.equals("Chair")){
			String [] manufac=this.databaseAccess.getChairManufacturer();
			temp= new String[manufac.length];
			for(int i=0;i<manufac.length;i++){
			temp1=this.databaseAccess.searchFor("Manufacturer", "ManuID",manufac[i]);
			temp[i]=temp1[0][1];
			}
		}else if(item.equals("Desk")){
			String [] manufac=this.databaseAccess.getDeskManufacturer();
			temp= new String[manufac.length];
			for(int i=0;i<manufac.length;i++){
			temp1=this.databaseAccess.searchFor("Manufacturer", "ManuID",manufac[i]);
			temp[i]=temp1[0][1];
			}
		}else if(item.equals("Lamp")){
			String [] manufac=this.databaseAccess.getLampManufacturer();
			temp= new String[manufac.length];
			for(int i=0;i<manufac.length;i++){
			temp1=this.databaseAccess.searchFor("Manufacturer", "ManuID",manufac[i]);
			temp[i]=temp1[0][1];
			}
		}else if(item.equals("Filing")){
			String [] manufac=this.databaseAccess.getFilingManufacturer();
			temp= new String[manufac.length];
			for(int i=0;i<manufac.length;i++){
			temp1=this.databaseAccess.searchFor("Manufacturer", "ManuID",manufac[i]);
			temp[i]=temp1[0][1];
			}
		}
		return temp;
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
     * @param table The overall category that the item falls under.
     *  - This should be one of the tables in the Database.
     *
     *  eg. if the query is: <code>mesh chair, 2</code>,
     *  this method should be called twice.
     *
     * @return A String array containing all ordered parts for the request.
     *
     * @throws SQLException
     * @throws Exception
     */
    private String[] findCheapestItems(String itemType, String itemCategory) throws SQLException, Exception{
        String[] fieldNames = databaseAccess.getFields(itemCategory);
        String[] partNames = isolateParts(fieldNames);
        
        // Create a saved count of extra parts a purchased item might have
        // which would not contribute to the current item if it isn't already.
        if(extraParts == null){
            extraParts = new int[partNames.length];
            for(int index = 0; index < partNames.length; index++){
                extraParts[index] = 0;
            }
        }

        // Store, in an array which parts have been satisfied. (initialized false)
        boolean hasPart[] = new boolean[partNames.length];
        for(int index = 0; index < partNames.length; index++){
            // if there are extra parts from before which can be used to build
            // this next item, then we are good.
            // Will default to "false" on first run.
            hasPart[index] = extraParts[index] > 0;
        }
     
        boolean foundCheapest = false;
        String[] lowestIDs = new String[0];
        String[][] lowestItems = new String[0][];

        // set some constants for readability
        final String FLAG_HAS = "Y";
        final String FLAG_NOT_HAS = "N";
        final int START_PADDING = 2;
        final int END_PADDING = 2;

        final int COST_INDEX = fieldNames.length - 2;
        final int ID_INDEX = 0;
        final double MAX = 9999999;
        while(!foundCheapest){

            // Get all items that contain some number of the missing parts
            String potentialItems[][] = new String[0][partNames.length+4];
            for(int index = 0; index < partNames.length; index++){
                if(hasPart[index]){
                    continue;
                }

                // Get every item with each part
                String partName = partNames[index];
                String[][] rows = databaseAccess.filter(itemCategory, itemType, partName, FLAG_HAS);
                if(rows.length == 0){
                    return new String[0];
                }
                for(String[] row : rows){
                    // Ignore the item if it's already been ordered
                    if(orderedParts.size() > 0 && orderedParts.contains(row[ID_INDEX])){
                        continue;
                    }

                    potentialItems = arrAppend(potentialItems, row);
                }
            }

            // DEBUG
            /*/
            for(String[] item : potentialItems){
                System.out.println(item[ID_INDEX]);
            }//*/

            // Find cheapest item per part
            // ie. minimize price per part
            double lowestCost = MAX;
            String[] lowestItem = null;

            for(int index = 0; index < potentialItems.length; index++){
                int hasPartCount = 0;
                String[] focusItem = potentialItems[index];

                // minimize the average price across multiple parts an item has
                // ie. most parts for the price
                for(int j = START_PADDING; j < focusItem.length - END_PADDING; j++){
                    // DEBUG
                    // System.out.println("Conditions: " + focusItem[ID_INDEX] + ": " + focusItem[j] + " " + extraParts[j-START_PADDING]);
                    if(focusItem[j].equals(FLAG_NOT_HAS) || extraParts[j-START_PADDING] > 0){
                        continue;
                    }

                    hasPartCount++;
                }
                double totalCost = Double.parseDouble(focusItem[COST_INDEX]);
                double costPerPart = totalCost / hasPartCount;

                if(costPerPart > lowestCost){
                    continue;
                }

                // DEBUG
                // System.out.println(costPerPart + " " + focusItem[ID_INDEX] + " " + focusItem[COST_INDEX] + " " + hasPartCount);

                lowestCost = costPerPart;
                lowestItem = focusItem;
            }

            // by this point, all potential items have been checked and 
            // the best value item has been found

            // Nothing found, cannot continue.
            if(lowestItem == null){
                return new String[0];
            }

            // DEBUG
            // System.out.println("Lowest item is: " + lowestItem[ID_INDEX]);

            lowestItems = arrAppend(lowestItems, lowestItem);

            // Check which parts still need to be found;
            for(int i = START_PADDING; i < lowestItem.length - END_PADDING; i++){
                boolean currentState = hasPart[i-START_PADDING];
                boolean partFound = lowestItem[i].equals(FLAG_HAS);
                hasPart[i-START_PADDING] = currentState || partFound;
                // DEBUG
                // System.out.println(item[ID_INDEX] + " " + i + " " + hasPart[i-START_PADDING]);
            }

            // If there is only up to one order remaining, try to minimize the
            // number of extra parts.
            if(orderCount <= 1){
                String[] parts = isolateParts(lowestItem);
                // Determine if there are extra parts left over from this combination
                for(int j = 0; j < parts.length; j++){
                    String partAvailable = parts[j];
                    // System.out.println(partAvailable);
                    if(partAvailable.equals(FLAG_NOT_HAS)){
                        continue;
                    }

                    extraParts[j]++;
                }
            }

            foundCheapest = true;
            for(boolean partCheck : hasPart){
                foundCheapest = foundCheapest && partCheck;
            }
        }

        // Save all IDs, count the number of remaining parts on file.

        // Add the total number of every part that would be ordered
        for(int index = 0; index < lowestItems.length; index++){
            String[] item = lowestItems[index];
            lowestIDs = arrAppend(lowestIDs, item[ID_INDEX]);
            String[] parts = isolateParts(item);

            // Determine if there are extra parts left over from this combination
            // This will ensure the next item does not reorder extra parts
            for(int j = 0; j < parts.length; j++){
                String partAvailable = parts[j];
                // DEBUG
                // System.out.println(partAvailable);
                if(partAvailable.equals(FLAG_NOT_HAS)){
                    continue;
                }

                extraParts[j]++;
            }//*/
        }

        // Remove one full item from the pile of parts.
        for(int index = 0; index < extraParts.length; index++){
            extraParts[index]--;
            // DEBUG
            // System.out.println("COUNT: " + index + " " + extraParts[index]);
        }

        // DEBUG
        /*/
        for(String id: lowestIDs){
            System.out.println("LOWEST IDs: " + id);
        }//*/
        orderCount--;

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
     *
     * @throws SQLException
     * @throws Exception
     */
    private double getPrice(String[] ids, String itemCategory) throws SQLException, Exception{
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
            // DEBUG
            // System.out.println("purchasing: " + id);

            // :/
            String[][] itemRows = databaseAccess.searchFor(itemCategory, "ID", id);
            String[] itemRow = itemRows[0];
            String priceStr = itemRow[priceIndex];
            totalCost += Double.parseDouble(priceStr);
        }

        return totalCost;

    }


    /**
	@param adjective String parameter is used to describe object
	@param noun String parameter that describes the object
	@return boolean returns whether such a furniture piece exists within the data based
	simple function for determining whether a furniture piece exists in the data based
	*/
	public boolean verify(String adjective, String noun) throws SQLException, Exception{
		String [][] temp=this.databaseAccess.searchFor(noun,"Type",adjective);
		if(temp[0]!=null){
			return true;
		}
		return false;
	}


    /**
     * Parses the desired order request from user input.
     * Also prints to the terminal which items will be ordered.
     *
     * @param order The order that the user is trying to make
     *
     * @return -1.00 if the item does not exist,
     *         -2.00 if the item exists but cannot be made,
     *         the price otherwise.
     *
     * @throws SQLException
     * @throws Exception
     */
    public double parseOrder(String itemType, String itemCategory, int quantity) throws SQLException, Exception{
        // Check if the inputs are valid.
        if(itemType == null || itemCategory == null || quantity == 0){
            System.out.println("Invalid request.");
            System.exit(1);
        }

        this.orderCount = quantity;

        // Find all necessary parts to complete the order
        while(orderCount > 0){
            String orderCombination[] = findCheapestItems(itemType, itemCategory);

            // If there are too few parts to complete an order, exit.
            if(orderCombination.length == 0){
                return -2;
            }

            for(String partID : orderCombination){
                orderedParts.add(partID);
            }

            // quantity--;
            // orderCount is reduced by one in findCheapestItems
        }

        String finalParts[] = orderedParts.toArray(new String[orderedParts.size()]);

        System.out.println("About to purchase:");
        for(String id : finalParts){
            System.out.println("  - " + id);
        }

        return getPrice(finalParts, itemCategory);
    }


    /**
     * Purchases the items and removes the items from the database.
     * A purchased item has no need to be in the database anymore.
     *
     * @param itemCategory The category of the item
     *
     * @throws SQLException
     * @throws Exception
     */
    private void purchaseItems(String itemCategory) throws SQLException, Exception{
        for(String id : orderedParts){
            databaseAccess.removeFurniture(itemCategory, id);
        }
    }


    /**
     * Confirms the order if the user wishes to.
     * This will remove all bought instances within the database.
     * This will also reset the manager once the file is written.
     *
     * @throws SQLException
     * @throws Exception
     */

    public void confirmOrder(String origReq) throws SQLException, Exception{
        String[] requestParts = origReq.split(" ");
        // String itemType = requestParts[0];
        String itemCategory = requestParts[1].substring(0, requestParts[1].length()-1);
        // String count = requestParts[2];

        saveOrder(origReq, itemCategory);
        purchaseItems(itemCategory);
        // reset();
        
        databaseAccess.close();
    }
}
