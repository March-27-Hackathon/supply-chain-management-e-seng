/**
 * @author Amir Abdrakmanov, Ethan Sengsavang, Liana Goodman
 *
 * @version 1.1
 * @since 1.0
 */

package edu.ucalgary.ensf409;

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
		 while(hold==-1){
		 System.out.println("Please enter your desired furniture type or type 'Q' to quit:");
		 this.request = System.console().readLine();
		 if(this.request.equalsIgnoreCase("Q")){
			 System.exit(1);
		 }
		 System.out.println("Please enter quantity:");
		quantity=System.console().readLine();
		 this.request=this.request.trim();
		String [] items=this.request.split(" ");
		 quantity=quantity.trim();
		 try{
		 quan = Integer.parseInt(quantity);
		 if(quan<=0){
			 throw new IllegalArgumentException();
		 }
		 if(items.length>2){
			 throw new Exception();
		 }
		 if(items[0]==null||items[1]==null){
			 continue;
		 }
		 }catch(IllegalArgumentException ex){
			 System.out.println("Invalid quantity");
			 continue;
		 }catch(Exception e){
			 System.out.println("Invalid furniture type");
			 continue;
		 }
		 code+=items[0].charAt(0);
		 code+=items[1].charAt(0);
		 code+=quantity;
		 hold=manage.parseOrder(items[0].toLowerCase(),items[1].toLowerCase(),quan);
		 if(hold==-1){
			 System.out.println("Invalid furniture type");
		 }else if(hold==-2){
			 System.out.println("Order cannot be fulfilled based on current inventory");
			 String[] manufaclist= this.manage.getManufacturersList(items[0].toLowerCase(),items[1].toLowerCase());
			 String temp="";
			 for(int i=0; i<manufaclist.length-1; i++){
				 temp+=manufaclist[i];
				 temp+=", ";
			 }
			 temp+="and ";
			 temp+=manufaclist[manufaclist.length-1];
			 System.out.println("Suggested manufacturers are "+temp);
			 System.exit(1);
		 }
		 }
		 this.request+=" ";
		 this.request+=quantity;
		 this.manage.setFileName(code);
		 this.confirm(hold);
	 }
	 /**
	 Private helper function whose job is to initialize the instance of Manager
	 prompts the user for the required database credentials
	 */
	 private void initializeManage(){
		 System.out.println("Please Enter username:");
		 String user=System.console().readLine();
		 System.out.println("Please Enter password");
		 String pass=System.console().readLine();
		 System.out.println("Please Enter DataBase URL in the form (jdbc:mysql://localhost/INVENTORY)");
		 String dburl=System.console().readLine();
		 this.manage= new Manager(user,pass,dburl);
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
			 this.manage.confirmOrder(this.request);
			 System.out.println("Order confirmed successfully, Please see order form");
			 break;
		 }else if(response.equalsIgnoreCase("n")){
			 System.out.println("Order not confirmed. Goodbye");
			 break;
		 }else{
			 System.out.println("Invalid response");
		 }
	 }
 }
 }
