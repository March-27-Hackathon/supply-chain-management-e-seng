# Group 11 - ENSF 409 Stream

## Authors
Liana Goodman
Ethan Seng
Amir Abdrakmanov

## Usage
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

this program will use the terminal to prompt the user for their database credentials, username first,
then password and finally database URL, if this fails to login into the database, it will contiune to prompt the user until it can connect

once the user successfully logins into the database the user will be prompted to enter their desired furniture type and then quantity, 
i.e. "mesh chair" and "1" or "desk lamp" and "2"

the user may enter whatever they want, if such an item exists in the data base the program will try to find 
the cheapest combination of pieces to assemble a whole if possible. 
if the user does not enter a valid furniture i.e. "banana man" the program will prompt the user to re-enter the furniture type and quantity

if the user enters a valid furniture type, and the program can assemble the pieces to make a whole, the program asks the user if they want to 
confirm the piece at the desired price, if yes an order form is created if no the program ends

if the user enters a valid furniture type but the program cannot assemble the pieces to make a whole then the program outputs in the terminal
possible manufacturers to buy from and quits. 

### SQLAccess Class
The SQLAccess class should not need to be accesed by users. 
It's purpose is solely to retrieve infomration from the data base.

### TESTING:
- Before running the tests, modify the USERNAME, PASSWORD, and DBURL variables to ensure that it connects to the right data base.
- The provided inventory.sql file should be uploaded before each run.
- Values in tests are hard coded to match the provided data base, though the class itself is very flexible.
- If you want to avoid this, comment out the removeFurnitureExists () test case and you will on ly need to load the .sql file once. This is because this test case removes a specific instance in the table and the test will fail if the instance no longer exists.
