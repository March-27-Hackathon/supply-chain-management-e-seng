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
}
