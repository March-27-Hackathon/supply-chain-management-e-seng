# Group 11 - ENSF 409 Stream

## Authors
Liana Goodman
Ethan Seng
Amir Abdrakmanov

## Usage
### Preparations
To initalize the database, run (in MySQL client)
```SQL
SOURCE <absolute path>/inventory.sql;
```
to initialize the database, and
```
javac -cp .;lib\lib\mysql-connector-java-8.0.23.jar edu\ucalgary\ensf409\*.java
```
to compile the program, then
```
java -cp .;lib\mysql-connector-java-8.0.23.jar;. edu.ucalgary.ensf409.Runner
```
to run the program.

### Making an order
This program will use the terminal to prompt the user for their database credentials, username first,
then password and finally database URL, if this fails to login into the database, it will contiune to prompt the user until it can connect

*Note: for readability, all user input will be prepended by "> ". This does not actually show when running the program*

```
Please Enter username:
> user
Please Enter password:
> 
Please Enter database URL in the form (jdbc:mysql://localhost/INVENTORY):
> jdbc:mysql://localhost/INVENTORY
```
*Note: Characters typed as the password will not be printed to the terminal for security.*

Once the user successfully logins into the database the user will be prompted to enter their desired furniture type and then quantity.
```
Please enter your desired furniture type (e.g. 'desk lamp') or type 'Q' to quit:
> mesh chair
Please enter quantity:
> 1
```

The user may enter whatever they want, if such an item exists in the data base the program will try to find 
the cheapest combination of pieces to assemble a whole if possible. 
If the user does not enter a valid furniture, the program will prompt the user to re-enter the furniture type and quantity.
```
Please enter your desired furniture type (e.g. 'desk lamp') or type 'Q' to quit:
> banana man
Invalid furniture type
```

If the user enters a valid furniture type, and the program can assemble the pieces to make a whole, the program asks the user if they want to 
confirm the piece at the desired price. 

If the user enters 'y' for yes, an order form is created.
```
About to purchase:
  - C9890
  - C0942
Cheapest price found was: $150.0
Confirm order?: (y/n)
> y
Order will be saved as: orderMC108042021-163109.txt
```

If 'n' for no, the program ends.
```
About to purchase:
  - C9890
  - C0942
Cheapest price found was: $150.0
Confirm order?: (y/n)
> n
Order not confirmed
```

If the user enters a valid furniture type but the program cannot assemble the pieces to make a whole then the program outputs in the terminal
possible manufacturers to buy from and quits. 
```
Please enter your desired furniture type (e.g. 'desk lamp') or type 'Q' to quit:
> executive chair
Please enter quantity:
> 2
Order cannot be fulfilled based on current inventory
Suggested manufacturers are Office Furnishings, Fine Office Supplies, Chairs R Us, and Furniture Goods
```

## TESTING:
### SQLAccess Testing
Tests the SQLAccess class and sees if the class has proper access to the database.
- Before running the tests, modify the USERNAME, PASSWORD, and DBURL variables to ensure that it connects to the right data base.
- The provided inventory.sql file should be uploaded before each run.
- Values in tests are hard coded to match the provided data base, though the class itself is very flexible.
- If you want to avoid this, comment out the removeFurnitureExists () test case and you will on ly need to load the .sql file once. This is because this test case removes a specific instance in the table and the test will fail if the instance no longer exists.
### FileIO Testing
Tests the FileIO class with a simple functionality test of the only two functions, as  well as tests passing null and blank data to both
- No special actions need to be done for testing this class.
### Manager Testing
Test the manager class, has broad reaching tests ranging from simple functionality tests to complex but valid data input and bad data input tests for different methods
- Before running the tests, modify the USERNAME, PASSWORD AND DBURL vairables to ensure that it connects to the correct database.

*Note: There are no test cases to be used for UserIO, as it only handles standard input and the user interface.
All methods that handle input checking are found within Manager, which are tested with Manager tests.*
