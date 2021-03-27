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
}
