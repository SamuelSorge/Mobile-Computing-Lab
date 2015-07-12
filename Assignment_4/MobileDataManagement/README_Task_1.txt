### Task 1: Mobile Data Management - The Chirp Service

### HOW TO
1.) Adjust app.properties depending on how many Client/Server you want. Each Client needs a server with the same index (and adjust the number of total participants)
2.) Adjust run.bat depending on how many Client/Server participants you have defined in app.properties and add the Clients and Server with its index, 
e.g. if you add a 4th Client/Server then add "java -jar Server.jar 4" and "java -jar Client.jar 4". 
Note: First start the server, then the Client!