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

/**
Test class for FileIO.java
*/
public class FileIOTest {
    @Test
	/**
	Basic functionality test of FileIO constructor and write(),
	checks file outputs line by line
	*/
    public void testConstructorAndWrite () throws IOException {
        FileIO test = new FileIO ("L093");
        String [] arr = {"C9890", "C0942"};
        test.write(arr, "mesh chair, 1", 150);

        File orig = new File("orderform.text");
        File written = new File("orderL093.txt");
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
	Basic Test to check if file of second name was successfully created,
	similiar operation to testConstructorAndWrite()
	*/
    public void testSecondFileofSameName () throws IOException {
		FileIO test1 = new FileIO ("L093");
        String [] arr1 = {"C9890", "C0942"};
        test1.write(arr1, "mesh chair, 1", 150);
		
        FileIO test = new FileIO ("L093");
        String [] arr = {"C9890", "C0942"};
        test.write(arr, "mesh chair, 1", 150);

        File orig = new File("orderform.text");
        File written = new File("orderL093(1).txt");
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
		File temst = new File("order.txt");
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
	@After
	public void Afterwards(){
		File first= new File("order.txt");
		File second = new File("orderL093(1).txt");
		File third = new File("orderL093.txt");
		if(first.exists()){
			first.delete();
		}
		if(second.exists()){
			second.delete();
		}
		if(third.exists()){
			third.delete();
		}
	}
	
	
	
}