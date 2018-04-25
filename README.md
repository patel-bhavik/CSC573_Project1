Peer-To-Peer(P2P) System with Centralized Index (CI) Server
----------------------------------

Following document describes the second project P2P-CI for CSC-573 Internet Protocols. This document contains all the necessary information to set-up and execute the project. It also has the section where I have explained all assumptions and deviations that I have made from the project requirement document.

1. Setup and Execution

    1. Please extract the folder named `P2P_CI` from the rar file that I have submitted.

    2. Open the command prompt and go to the `P2P_CI/src` directory, using cd command.
        
		`cd <<path of P2P_CI directory>>/P2P_CI/src`
    
    3. Now execute the following command in given sequence to compile the java code.
        
        ```
        javac constants\*.java
        javac -classpath . utility\*.java
        javac -classpath . communication\protocol\*.java
        javac -classpath . server\*.java
        javac -classpath . client\*.java        
        ```
    
    4. If you are going to run a client on this machine create one directory of your choice and at any location that you want and create few RFC files with `.txt` extension. Keep a note of this directory path, we will need this when we run client program. You should create separate directory for each client you plan to run.

    5. Running the Server Program

        1. Opean a terminal window and go to the `P2P_CI/src` directory, using cd command.
            
			`cd <<path of P2P_CI directory>>/P2P_CI/src`

        2. Execute following command and it will start server on the current machine and on 7734 port.
            
			`java -classpath . server.ServerInitialization`

        3. You should see message like `Centralized Index server started on <<ip of the machine>>:7734 successfully.`

    6. Running Client Program

        1. Opean a terminal window and go to the `P2P_CI/src` directory, using cd command.
            
			`cd <<path of P2P_CI directory>>/P2P_CI/src`

        2. Execute following command and it will start client on a random port on this machine.
            
			`java -classpath . client.PeerClients`

        3. Provide required information
            
			It will ask for the IP address of the server to connect. Please provide the IP of the server which we started earlier. It will also ask for the path of the RFC directory. Give the absolute path of the directory which we created earlier while set up. Please give path only till the directory name. Don't include file name with the path. For eg, if your directory name is RFC give path as `<<absolute path of directory>>\RFC`. Please don't include `\` at the end. After giving this information client program will connect to server and you can follow steps as directed through the simple menu driven program.

        4. You should be able to see following message on the console.
            
			```
            Enter IP Address of server to connect: <<server ip address>>
            Enter RFC directory path: <<RFC directory path>>
            Upload server started on <<client ip address>>:<<client upload port>> successfully.
            Connection established with server running at <<server ip address>>.1:7734 successfully.
            Menu
            1. Add RFC
            2. Lookup RFC
            3. List All RFCs
            4. Download RFC
            5. Exit
            Enter your choice:
            ```
    
    7. Now follow the menu driven program and you will see the request response on both client and server console.

2. Assumptions

    1. Name of the RFC File must be same as the RFC Number. For e.g. the RFC file corresponding to RFC number 123 will be `123.txt`.

    2. When the peer connects to the system, we assume that it provides correct RFC directory path. Path validation is not performed.

    3. When the peer adds an RFC to the system, it is assumed that that peer has a corresponding RFC file in the RFC directory. The file name must be as stated above.

    4. When the peer downloads the RFC File, it gets saved to the same RFC directory which user gave while connecting. So, if this path is wrong even after getting ok response and corresponding file content from respective server, file will not be saved in the client.

    5. When RFC file is successfully downloaded, ADD request for the same is sent to CI Server and not input of confirmation is asked from user.

    6. If any client has added wrong RFC info to CI server and if another client tries to download that RFC file, NOT FOUND response is given back. Or if client has that RFC file but it provided wrong RFC directory path while connecting with server then also NOT FOUND response is given back.

3. Deviation from project report

    1. Data Structure Used

        We have used one hashtable instead of two linked list as mentioned in project document. Key of hashtable is RFC, which contains RFC details like RFC Number and RFC Title. Value of the hashtable is list of peers that have the corresponding RFC file. Each peer has info regarding Host Name, IP and Upload Port information of the client. Reason behind using this data structure is that, our frequent operation is add and lookup. Hashtable will be more efficient in such cases where we can lookup RFCs based on keys easily instead of traversing RFC list and then for that RFC traversing Peer list.

    2. Using TAB instead of Space in request

        In project document, for each request between different part space is used. However, I created rquest with TAB as separator. Title of a RFC may also contain spaces in between. To avoid confusion between the different request parts and different word of a rfc title we used TAB as a separator.

    3. Added IP Address in response

        In reponse to LOOKUP and LIST request, I have added IP address as well in response. This is to make sure that client has all required information to contact peer to download RFC.