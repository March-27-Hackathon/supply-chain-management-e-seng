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
Test Class for Manager.java
*/
public class ManagerTest{
	@Test
	/**
	Basic test to check if Manager's constructor fails
	*/
	public void TestConstructorBasic(){
		try{
		Manager film= new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
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
			Manager film = new Manager("amit","ensf409","jdbc:mysql://localhost/INVENTORY");
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
		Manager Dan=new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
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
		Manager Dan = new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
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
		Manager Dan = new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
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
		Manager Dan = new Manager("amir", "ensf409", "jdbc:mysql://localhost/INVENTORY");
		double down=Dan.parseOrder("mesh","chair",200);
		assertTrue("parseOrder returned wrong value"+down, down==-2);
		}catch(Exception rpe){
			System.err.println("Unexpected error");
		}
	}
	  @Rule
  // Handle System.exit() status
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();
}