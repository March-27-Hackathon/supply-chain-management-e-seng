/**
 * @author Amir Abdrakmanov, Ethan Sengsavang, Liana Goodman
 *
 * @version 1.1
 * @since 1.0
 */

package edu.ucalgary.ensf409;
import java.sql.*;
import java.io.*;
 /**
 Dedicated class for User interfacing, currently uses the terminal, this may change
 */
 public class UserIO{
	private String request;
	private Manager manage;
	
	/**
	Default Constructor, propmts the user for the data base credentials first and foremost
	if those are invalid, the program ends, else it begins asking the user for their desired
	furniture order, if the user inputs invalid furniture types or negative quantites the program
	will ask the user again for that data until it is able to proceed with it
	calls both initalizeManage() and confirm()
	*/
	 public UserIO(){
		 this.initializeManage();
		 int quan=0;
		 double hold=-1;
		 String code="";
		 String quantity="";
		 String adjective="";
		 String noun="";
		 while(true){
		 System.out.println("Please enter your desired furniture type (e.g. 'desk lamp') or type 'Q' to quit:");
		 this.request = System.console().readLine();
		  this.request=this.request.trim();
		 if(this.request.equalsIgnoreCase("Q")){
			 System.exit(1);
		 }
		String [] items=this.request.split(" ");
		try{
			if(items.length>2){
				throw new IllegalArgumentException();
			}
			if(items[0]==null||items[1]==null){
			 throw new IllegalArgumentException();
		 }
		 adjective= items[0].substring(0,1).toUpperCase()+items[0].substring(1).toLowerCase();
		noun= items[1].substring(0,1).toUpperCase()+items[1].substring(1).toLowerCase();
		 if(!this.manage.verify(adjective,noun)){
			 throw new IllegalArgumentException();
		 }
		}catch(IllegalArgumentException e){
			System.out.println("Invalid furniture type");
			continue;
		}
		catch(SQLException sql){
			System.out.println("Invalid furniture type");
			continue;
		}catch(Exception l){
			System.out.println("Invalid furniture type");
			continue;
		}
		
		 System.out.println("Please enter quantity:");
		quantity=System.console().readLine();
		 quantity=quantity.trim();
		 try{
		 quan = Integer.parseInt(quantity);
		 if(quan<=0){
			 throw new IllegalArgumentException();
		 }
		 }catch(IllegalArgumentException ex){
			 System.out.println("Invalid quantity");
			 continue;
		 }
		 try{
		 hold=manage.parseOrder(adjective,noun,quan);
		 }catch(SQLException ex){
			 System.out.println("Invalid furniture type");
			 continue;
		 }catch(Exception exc){
			 System.out.println("Unexpected Error");
			 exc.printStackTrace();
			 System.exit(1);
		 }
		 if(hold==-1){
			 System.out.println("Invalid furniture type");
		 }else if(hold==-2){
			 System.out.println("Order cannot be fulfilled based on current inventory");

             String[] manufaclist = null;
             try{
			    manufaclist= this.manage.getManufacturersList(noun);
             }catch(SQLException e){
                System.out.println("Manufacturer list could not be retrieved");
                e.printStackTrace();
                System.exit(1);
             }catch(Exception e){
                System.out.println("Unexpected error reached");
                e.printStackTrace();
                System.exit(1);
             }

             if(manufaclist == null){
                System.out.println("Unexpected error reached");
                System.exit(1);
             }

			 String temp="";
			 for(int i=0; i<manufaclist.length-1; i++){
				 temp+=manufaclist[i];
				 temp+=", ";
			 }
			 temp+="and ";
			 temp+=manufaclist[manufaclist.length-1];
			 System.out.println("Suggested manufacturers are "+temp);
			 continue;
		 }
		 
		 code="";
		 code+=items[0].charAt(0);
		 code+=items[1].charAt(0);
		 code+=quantity;
		 this.request+=" ";
		 this.request+=quantity;
		 this.manage.setFileName(code);
		 this.confirm(hold);
		 try{
		 this.manage.reset();
		 }catch(SQLException qwe){
			 System.err.println("Data base error");
			 System.exit(1);
		 }
		 catch(Exception q){
			 System.err.println("Unexpected error");
			 System.exit(1);
		 }
		 }
	 }
	 /**
	 Private helper function whose job is to initialize the instance of Manager
	 prompts the user for the required database credentials
	 */
	 private void initializeManage(){
		 while(true){
		 System.out.println("Please Enter username:");
		 String user=System.console().readLine();
		 System.out.println("Please Enter password:");
		Console con=System.console();
		char [] pas=con.readPassword();
		 String pass= new String(pas);
		 System.out.println("Please Enter database URL in the form (jdbc:mysql://localhost/INVENTORY):");
		 String dburl=System.console().readLine();
		 try{
		 this.manage= new Manager(user,pass,dburl);
		 }catch(SQLException exp){
			 System.out.println("Error connecting to database");
			 continue;
		 }catch(Exception pez){
			 System.out.println("Unexpected Error");
			 pez.printStackTrace();
			 System.exit(1);
		 }
		 break;
		 }
	 }
	 /**
	 @param value double parameter that respresents total price
	 Confirm function asks the user if they want to confirm the order 
	 after the price was determined, if they respond with 'y' the order form is printed
	 and the data base is updated if 'n' then the program ends without creating the orderform 
	 and does not update the database otherwise it will continue asking the user
	 */
	 private void confirm(double value){
		 System.out.println("Cheapest price found was: " + value);
		 while(true){
		 System.out.println("Confirm order?: (y/n)");
		 String response=System.console().readLine();
		 response=response.trim();
		 if(response.equalsIgnoreCase("y")){
			 try{
			 this.manage.confirmOrder(this.request);
			 }catch(SQLException ekc){
				 System.out.println("Error saving data base");
				 ekc.printStackTrace();
				 System.exit(1);
			 }catch(Exception loss){
				 System.out.println("Unexpected error");
				 loss.printStackTrace();
				 System.exit(1);
			 }
			 System.out.println("Order confirmed successfully, Please see order form");
			 break;
		 }else if(response.equalsIgnoreCase("n")){
			 System.out.println("Order not confirmed");
			 break;
		 }else{
			 System.out.println("Invalid response");
		 }
	 }
 }
 }
