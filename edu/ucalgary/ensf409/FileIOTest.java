/**
 * @author Liana Goodman
 * @author Ethan Sengsavang
 * @author Amir Abdrakmanov
 * @version 1.1
 * @since 1.0
 */

package edu.ucalgary.ensf409;

import static org.junit.Assert.*;
import org.junit.*;
import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
Test class for FileIO.java
*/
public class FileIOTest {
	
	private String file1="";
	private String file2="";
	private String file3="";
    @Test
	/**
	Basic functionality test of FileIO constructor and write(),
	checks file outputs line by line, MAY BE PROBLEMATIC ON SLOWER MACHINES 
	since the file names are created based on the time, a slower machine may not be able to properly run this test
	ideally machines should run this test in under 1 second
	*/
    public void testConstructorAndWrite () throws IOException {
        FileIO test = new FileIO ("L093");
		LocalDateTime orderTime=LocalDateTime.now();
		DateTimeFormatter properTime = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
		String output="orderL093"+orderTime.format(properTime)+".txt";
		this.file1=output;
        String [] arr = {"C9890", "C0942"};
        test.write(arr, "mesh chair, 1", 150);
        File orig = new File("orderform.text");
        File written = new File(output);
        boolean match = true;

        Scanner origScan = new Scanner (orig);
        Scanner writScan = new Scanner (written);

        while (origScan.hasNextLine() && writScan.hasNextLine() && match) {
            String origLine = origScan.nextLine();
            String writLine = writScan.nextLine();

            if (!origLine.equals(writLine)) {
                match = false;
            }
        }
		
		origScan.close();
		writScan.close();
        assertTrue ("Files do not match", match);
    }
	@Test
	/**
	Testing for empty file name inputs to FileIO, FileIO should still create the file and write to it
	*/
	public void TestEmptyFileName(){
		FileIO test = new FileIO("");
		String [] arr= new String[2];
		arr[0]="Hello";
		arr[1]="everybody";
		test.write(arr, "t",0.0);
		LocalDateTime orderTime=LocalDateTime.now();
		DateTimeFormatter properTime = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
		String out="order"+orderTime.format(properTime)+".txt";
		this.file2=out;
		File temst = new File(out);
		assertTrue("File not created", temst.exists());
	}
	
	@Test
	/**
	Simple test to check FileIO's response to null input, an exception should be thrown
	*/
	public void TestNullFileName(){
		assertThrows(NullPointerException.class,()->{
		FileIO test=new FileIO(null);
		});
	}
	@Test
	/**
	Simple test to check write() response to null String, it should still create a file, it'll simply be null
	*/
	public void TestNullWrite(){
		FileIO test = new FileIO("dl2");
		LocalDateTime orderTime=LocalDateTime.now();
		DateTimeFormatter properTime = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
		String out="orderDL2"+orderTime.format(properTime)+".txt";
		this.file3=out;
		String [] arr = new String[2];
		test.write(arr,null,0.0);
		File check = new File(out);
		assertTrue("File not properly created", check.exists());
	}
	
	@Test
	/**
	Basic test for simply checking if FileIO's constructor does not create a null object
	*/
	public void TestNullConstructor(){
		FileIO test = new FileIO("EN22");
		assertTrue("Constructor failed", test!=null);
	}
	
	@After
	/**
	Simple function to delete files after use
	*/
	public void Destruct(){
		File out1= new File(this.file1);
		File out2= new File(this.file2);
		File out3= new File(this.file3);
		if(out1.exists()){
			out1.delete();
		}
		if(out2.exists()){
			out2.delete();
		}
		if(out3.exists()){
			out3.delete();
		}
	}
}