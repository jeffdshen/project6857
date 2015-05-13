# project6857

You need to install maven before compiling

To compile:
Navigate to the directory and compile with 
mvn -Pdesktop package

To run:
Navigate to desktop/target/ and run
java -jar project6857-desktop-1.0-SNAPSHOT-jar-with-dependencies.jar

There needs to be a client and a server. The client connects to the server using the server's IP address.

You might need to move everything in assets/fairplay to desktop/target/assets/fairplay. You might also need to change permissions to administrator for run_alice and run_bob. Search for those files. This project only works with Unix based systems.
