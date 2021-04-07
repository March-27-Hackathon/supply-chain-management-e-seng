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
    @Test
	/**
	Basic functionality test of FileIO constructor and write(),
	checks file outputs line by line, MAY BE PROBLEMATIC ON SLOWER MACHINES 
	since the file names are created based on the time, a slower machine may not be able to properly run this test
	ideally machines should run this test in under 1 second
	*/
    public void testConstructorAndWrite () throws IOException {
        FileIO test = new FileIO ("L093");
        String [] arr = {"C9890", "C0942"};
        test.write(arr, "mesh chair, 1", 150);
		LocalDateTime orderTime=LocalDateTime.now();
		DateTimeFormatter properTime = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
		String output="orderL093"+orderTime.format(properTime)+".txt";
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

        assertTrue ("Files do not match", match);
    }
	@Test
	/**
	Testing for Null inputs to FileIO, FileIO should still create the file and write to it,
	it'll be blank however
	*/
	public void TestNullFile(){
		FileIO test = new FileIO("");
		String [] arr= new String[2];
		arr[0]="Hello";
		arr[1]="everybody";
		test.write(arr, "t",0.0);
		LocalDateTime orderTime=LocalDateTime.now();
		DateTimeFormatter properTime = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
		String out="order"+orderTime.format(properTime)+".txt";
		File temst = new File(out);
		assertTrue("File not created", temst.exists());
	}
	@Test
	/**
	Basic test for simply checking if FileIO's constructor does not create a null object
	*/
	public void NullConstructor(){
		FileIO test = new FileIO("EN22");
		assertTrue("Constructor failed", test!=null);
	}
//	@After
//	public void Afterwards(){
//	File first= new File("order.txt");
//		File second = new File("orderL093(1).txt");
//		File third = new File("orderL093.txt");
//		if(first.exists()){
//		first.delete();
//		}
//		if(second.exists()){
//			second.delete();
//		}
//		if(third.exists()){
//			third.delete();
//		}
//	}
	
	
	
}