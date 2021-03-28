/**
 * @author Amir Abdrakmanov, Ethan Sengsavang, Liana Goodman
 *
 * @version 1.0
 * @since 1.0
 */
package edu.ucalgary.ensf409;
import java.io.*;
/**
Dedicated class for handling the writing to the output file
*/
public class FileIO{ // Start of FileIO class
	private final String FILENAME;
	private File fileOut=null;
	
	/**
	@param order String parameter that is defined as two chars followed by a number
	Constructor that takes in a String related to the order, if such a file name already exists, 
	adds the most recently available number to the end of the file name e.g. orderEC2(1).txt, 
	sets FILENAME and fileOut
	*/
	public FileIO(String order){
		String temp = "order";
		temp+=order.toUpperCase();
		temp+=".txt";
		File check= new File(temp);
		int b=1;
		while(check.exists()){
			String other="("+String.valueOf(b)+")";
			int i= temp.indexOf('.');
			temp=temp.substring(0,i);
			int c=temp.indexOf('(');
			if(c!=-1){
				temp=temp.substring(0,c);
			}
			temp+=other;
			temp+=".txt";
			check=new File(temp);
			b++;
		}
		System.out.println(temp);
		this.FILENAME=new String(temp);
		this.fileOut=new File(this.FILENAME);
	}
	/**
	@param id String array parameter representing the ids of the chosen furniture
	@param origReq String parameter for the user's original request
	@param price double parameter for the total price of furniture
	function used for formatting and writing to the output text file, catches any IOExceptions
	*/
	public void write(String [] id, String origReq, double price){
		String out="Furniture Order Form\n\n";
		out+="Faculty Name:\nContact:\nDate:\n\n";
		out+="Original Request: ";
		out+=origReq;
		out+="\n\n";
		out+="Items Ordered\n";
		for(int i=0; i<id.length;i++){
			out+="ID: ";
			out+=id[i];
			out+="\n";
		}
		out+="\n";
		out+="Total Price: $";
		out+=String.valueOf((int)price);
		FileWriter writer = null;
		try{
			writer= new FileWriter(this.fileOut);
			writer.write(out);
			writer.flush();
			writer.close();
		}catch(IOException e){
			System.err.println("IO error occured");
			e.printStackTrace();
			System.exit(1);
		}
		
	}
		
}
