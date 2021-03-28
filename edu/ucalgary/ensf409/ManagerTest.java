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

/**
Test Class for Manager.java
*/
public class ManagerTest{
	@Test
	/**
	Basic test to check if Manager's constructor fails
	*/
	public void TestConstructorBasic(){
		Manager film= new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
		
		assertTrue("Constructor did not create instance correctly", film!=null);
	}
	@Test
	/**
	Less basic test to check if Manager's constructor calls exit(1) from incorrect credentials
	*/
	public void TestConstructorLessBasic(){
		exit.expectSystemExitWithStatus(1);
		Manager film = new Manager("amit","ensf409","jdbc:mysql://localhost/INVENTORY");
	}
	@Test
	/**
	Basic test to check Manager's parseOrder(), simple test based off of given example output 
	*/
	public void TestParseOrderBasic(){
		Manager Dan=new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
		assertTrue("parseOrder returned incorrect value", Dan.parseOrder("mesh","chair", 1)==150);
	}
	@Test 
	/**
	Test to check Manager's parseOrder(), checks behaviour when a non existant adjective is passed
	*/
	public void TestParseOrder1(){
		Manager Dan = new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
		double down=Dan.parseOrder("meh","chair",1);
		assertTrue("expected -2", down==-2);
	}
	@Test 
	/**
	Test to check Manager's parseOrder(), checks behaviour when a non existant noun is passed
	*/
	public void TestParseOrder2(){
		Manager Dan = new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
		double down=Dan.parseOrder("mesh","air",1);
		assertTrue("expected -2", down==-2);
	}
	@Test
	/**
	Test to check Manager's parseOrder(), checks behaviour when an impossible amount is passed
	*/
	public void TestParseOrder3(){
		Manager Dan = new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
		double down=Dan.parseOrder("mesh","chair",200);
		assertTrue("expected -1", down==-1);
	}
	  @Rule
  // Handle System.exit() status
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();
}