# FTP-Implementation-JAVA
FTP protocol implementation using JAVA AWT/Swing

1. Working Environment : Java 8
2. Program Requirement ( IDE, library, test tool) : 
	NetBeans IDE 8.1

Steps to Run the FTP project:
--------------------------------
	1) First you need to compile and run the server program (FTPServer.java).
		- javac FTPServer.java
		- java FTPServer <<PortNo>>
	2) After the successful execution of Server program, you can run the FTPClient.jar file by double clicking it. You need to provide the Host, Port no and username (test) and password (test) to connect to the server. 
	Please note that errors are not handled so if you give any wrong details it wont connect to the server. 
	3) Once the FTPClient connected to the server it will go to different window where you can execute the commands.
		For example: you can type "send" command and click on execute button. you can press help button to see the list of commands.


FTP command list (or how to execute command)
----------------------------------------------
	We have implemented the following commands:
	1) send --- File upload to the server.
	2) receive --- Download files from the server to Current working directory.
	3) cd --- Change the current working directory in the server.
	4) mkdir --- Create a new directory in the server.
	5) rmdir --- Removes a directory in the server (only if there is no files in the directory).
	6) delete --- deletes a file from the server.
	7) list --- Lists all the files and directories in the Current working directory.
		
sample scenario ( Server function/ Client function /upload / download)
------------------------------------------------------------------------
	Multiple clients can be connected to the server at a time. Each client can execute any command. 
	Scenario 1: Client1 and Client 2 are connected to the server, Client1 can upload a new file during this time client2 cannot delete or receive this file until the file upload completes.
	Scenario 2: Client1 and Client2 are connected to the server at a time, If client2 downloading a big file from the server and at the same time if the client1 gives a delete command to delete the same file which client2 downloading, there won't be any error. The file will be deleted but the client2 will also receive the full file.
	Scenario 3: if client uploads a big file and at the same time if the client clicks the disconnect button, the file won't be uploaded to the server.
	Scenario 4. Client1 uploading a big file, Client2 can also upload a file to the server.
