# Group 11 - ENSF 409 Stream

## Authors
Liana Goodman
Ethan Seng
Amir Abdrakmanov

### SQLAccess Class
The SQLAccess class should not need to be accesed by users. 
It's purpose is solely to retrieve infomration from the data base.

TESTING:
- Before running the tests, modify the USERNAME, PASSWORD, and DBURL variables to ensure that it connects to the right data base.
- The provided inventory.sql file should be uploaded before each run.
- Values in tests are hard coded to match the provided data base, though the class itself is very flexible.
- If you want to avoid this, comment out the removeFurnitureExists () test case and you will on ly need to load the .sql file once. This is because this test case removes a specific instance in the table and the test will fail if the instance no longer exists.

