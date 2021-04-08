/**
 * @author Amir Abdrakmanov, Ethan Sengsavang, Liana Goodman
 *
 * @version 1.1
 * @since 1.0
 */
 package edu.ucalgary.ensf409;
import static org.junit.Assert.*;
import org.junit.*;
import java.io.*;
import java.util.*;
import java.util.Scanner;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import java.sql.*;

/**
Test Class for Manager.java, USERNAME and PASSWORD need to be changed to run successfully
*/
public class ManagerTest{
	private static final String USERNAME="amir";
	private static final String PASSWORD="ensf409";
	private static final String URL="jdbc:mysql://localhost/INVENTORY";
	
	@Test
	/**
	Basic test to check if Manager's constructor fails
	*/
	public void TestConstructorBasic(){
		try{
		Manager film= new Manager(USERNAME, PASSWORD, URL);
		assertTrue("Constructor did not create instance correctly", film!=null);
		}catch(Exception sql){
			System.err.println("unexpected error");
		}
	}
	@Test
	/**
	Less basic test to check if Manager's constructor calls exit(1) from incorrect credentials
	*/
	public void TestConstructorLessBasic(){
		try{
	//	exit.expectSystemExitWithStatus(1);
		assertThrows(SQLException.class, ()->{
			Manager film = new Manager("amit",PASSWORD,URL);
		});
		}catch(Exception Ex){
			System.out.println("Unknown Exception");
		}
	}
	@Test
	/**
	Basic test to check Manager's parseOrder(), simple test based off of given example output 
	*/
	public void TestParseOrderBasic(){
		try{
		Manager Dan=new Manager(USERNAME, PASSWORD, URL);
		assertTrue("parseOrder returned incorrect value", Dan.parseOrder("mesh","chair", 1)==150);
		}catch(Exception we){
			System.err.println("Unexpected error");
		}
	}
	@Test 
	/**
	Test to check Manager's parseOrder(), checks behaviour when a non existant adjective is passed
	*/
	public void TestParseOrder1(){
		try{
		Manager Dan = new Manager(USERNAME, PASSWORD, URL);
		double down=Dan.parseOrder("meh","chair",1);
		assertTrue("expected -2", down==-2);
		}catch(Exception re){
			System.err.println("Unexpected error");
		}
	}
	@Test 
	/**
	Test to check Manager's parseOrder(), checks behaviour when a non existant noun is passed
	*/
	public void TestParseOrder2(){
		try{
		Manager Dan = new Manager(USERNAME, PASSWORD, URL);
		assertThrows(SQLException.class, ()->{
			double down=Dan.parseOrder("mesh","air",1);
		});
		}catch(Exception te){
			System.err.println("Unexpected error");
		}
	}
	@Test
	/**
	Test to check Manager's parseOrder(), checks behaviour when an impossible amount is passed
	*/
	public void TestParseOrder3(){
		try{
		Manager Dan = new Manager(USERNAME, PASSWORD, URL);
		double down=Dan.parseOrder("mesh","chair",200);
		assertTrue("parseOrder returned wrong value"+down, down==-2);
		}catch(Exception rpe){
			System.err.println("Unexpected error");
		}
	}
    @Test
    /**
     * Test checking slightly more complicated order combinations.
     * This will check if two traditional desks can be ordered, where the
     * cheapest combination will be $200 and 3 parts will be ordered.
     */
    public void TestComplexOrder(){
        try{
            Manager Dan = new Manager(USERNAME, PASSWORD, URL);
            double price = Dan.parseOrder("traditional", "desk", 2);
            int itemCount = Dan.getOrderedParts().size();
            assertTrue("Complex orders not handled properly", price==200 && itemCount == 3);
        }catch(Exception e){
            System.out.println("Unwanted unexpected error");
        }
    }
	@Test
	/**
	Test to check functionality of getManufacturersList()
	*/
	public void TestGetManufacturersList(){
		try{
		Manager film= new Manager(USERNAME, PASSWORD, URL);
		String [] result = film.getManufacturersList("Chair");
		String [] expected={"Office Furnishings", "Chairs R Us", "Furniture Goods", "Fine Office Supplies"};
		boolean match = true;
		for(String exp : expected){
            boolean tempMatch = false;
            for(String manufacture : result){
			    tempMatch = tempMatch || manufacture.equals(exp);
			}
            match = match && tempMatch;
		}
		assertTrue("getManufacturersList does not match expected list", match);
		}catch(Exception sql){
			System.err.println("unexpected error");
		}
		
	}
	@Test
	/**
	Test to check getManufacturersList() receiving invalid input
	*/
	public void TestGetManufacturersListNull(){
		try{
		Manager film= new Manager(USERNAME, PASSWORD, URL);
		String [] result = film.getManufacturersList("Man");
		assertTrue("getManufacturersList did not return null when expected", result==null);
		}catch(Exception sql){
			System.err.println("unexpected error");
		}
	}
	@Test
	/**
	Test to check functionality of reset()
	*/
	public void TestReset(){
		try{
		Manager film= new Manager(USERNAME, PASSWORD, URL);
		film.parseOrder("Mesh", "Chair", 1);
		film.reset();
		assertTrue("Reset did not correctly reset", film.getOrderedParts().size()==0);
		}catch(Exception sql){
			System.err.println("unexpected error");
		}
	}
	
	@Test
	/**
	Test to check functionality of verify()
	*/
	public void TestVerify(){
		try{
		Manager film= new Manager(USERNAME, PASSWORD, URL);
		boolean check =film.verify("Mesh", "Chair");
		assertTrue("Verify failed to confirm the legitmacy of the input", check);
		}catch(Exception sql){
			System.err.println("unexpected error");
		}
	}
	
	@Test
	/**
	Test to check response of verify() to bad adjective
	*/
	public void TestVerifyBadAdjective(){
		try{
		Manager film= new Manager(USERNAME, PASSWORD, URL);
		assertThrows(Exception.class, ()->{
		film.verify("Meh", "Chair");
		});
		}catch(Exception sql){
			System.err.println("unexpected error");
		}
	}
	@Test
	/**
	Test to check response of verify() to bad noun
	*/
	public void TestVerifyBadNoun(){
		try{
		Manager film= new Manager(USERNAME, PASSWORD, URL);
		assertThrows(SQLException.class, ()->{
		film.verify("Mesh", "Cair");
		});
		}catch(Exception sql){
			System.err.println("unexpected error");
		}
	}
	@Test
	/**
	Test to check response of verify() to bad input
	*/
	public void TestVerifyBadInput(){
		try{
		Manager film= new Manager(USERNAME, PASSWORD, URL);
		assertThrows(Exception.class, ()->{
		film.verify("pepsi", "man");
		});
		}catch(Exception sql){
			System.err.println("unexpected error");
		}
	}
	
	  @Rule
  // Handle System.exit() status
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();
}
