
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.util.*;
import static java.nio.file.StandardCopyOption.*;


public class FTPServer {
	static int PortNo;
	//public ServerSocket datasoc;
	 public static void main(String args[]) throws Exception
	 
	    {
		 	
		 	//System.out.println(args[0]);
		 	if (args.length == 1){
		 		//System.out.println(args[0]);
		 		PortNo=Integer.parseInt(args[0]);
		 		
		 	}
		 	else {
		 		PortNo=5217;
		 	}
	        ServerSocket soc=new ServerSocket(PortNo);
	        ServerSocket datasoc=new ServerSocket(PortNo-1);
	        System.out.println("FTP Server Started on Port Number "+ PortNo);
	        while(true)
	        {
	            System.out.println("Waiting for Connection ...");
	            //System.out.println(123);
	            transferfile t=new transferfile(soc.accept(),datasoc);
            
	        }
	    }
	}



class transferfile extends Thread
{
    static Socket ClientSoc,dataSoc;
    static ServerSocket DataSoc;
    DataInputStream dinput;
    DataOutputStream doutput;
    
    transferfile(Socket soc,ServerSocket datasoc)
    {
        try
        {
            ClientSoc=soc;  
            DataSoc=datasoc;
            dinput=new DataInputStream(ClientSoc.getInputStream());
            doutput=new DataOutputStream(ClientSoc.getOutputStream());
            System.out.println("FTP Client Connected ...");
        	String use=dinput.readUTF();
        	if (use.compareTo("test")==0){
            	String pass=dinput.readUTF();
            	if (pass.compareTo("test")==0){
            		doutput.writeUTF("Success");
            		System.out.println("User logged in successfully");
            		
            	}
            	else{
            		doutput.writeUTF("Failure");
            		}
            
            }
        	else{
        		doutput.writeUTF("Failure");
        		}
            start();
            
        }
        catch(Exception ex)
        {
        }        
    }
    void SendFile() throws Exception
    {   
    	DataInputStream datain;
        DataOutputStream dataout;
    	dataSoc=DataSoc.accept();
	 	datain=new DataInputStream(dataSoc.getInputStream());
    	dataout=new DataOutputStream(dataSoc.getOutputStream());
    	
    	String filename=dinput.readUTF();
        File f=new File(filename);
        if(!f.exists())
        {
        	doutput.writeUTF("File Not Found");
            return;
        }
        else
        {
        	String path=f.getAbsolutePath();
        	String temppath="/tmp/";
        	//System.setProperty("user.dir","/tmp/");
        	File tmpfile = new File ("/tmp/"+filename);
        	Path target = Paths.get(temppath + f.getName());
            Path source = Paths.get(path);
            //System.out.println(source);
            //System.out.println("hi");
            //System.out.println(target);
            try {
            	 	
            	Files.copy(source, target, REPLACE_EXISTING);
            	 
            	  } catch (IOException e) {
            	 
            	   e.printStackTrace();
            	  }
            	 
        	doutput.writeUTF("READY");
            FileInputStream fin=new FileInputStream(tmpfile);
            doutput.writeDouble(f.length());
            int ch;
            do
            {
                ch=fin.read();
                dataout.writeUTF(String.valueOf(ch));
            }
            while(ch!=-1);    
            fin.close();    
            doutput.writeUTF("File Receive Successfully");                            
        }
    }
    
    void ReceiveFile() throws Exception
    {
    	DataInputStream datain;
        DataOutputStream dataout;
    	dataSoc=DataSoc.accept();
	 	datain=new DataInputStream(dataSoc.getInputStream());
    	dataout=new DataOutputStream(dataSoc.getOutputStream());
    	
        String filename=dinput.readUTF();
        if(filename.compareTo("File not found")==0)
        {
            return;
        }
        File f=new File(filename);
        String option;
        
        if(f.exists())
        {
        	doutput.writeUTF("File Already Exists");
            option=dinput.readUTF();
        }
        else
        {
        	doutput.writeUTF("SendFile");
            option="Y";
        }
            
            if(option.compareTo("Y")==0)
            {
            	
            	String path=f.getAbsolutePath();
            	String temppath="/tmp/";
            	//System.setProperty("user.dir","/tmp/");
            	File tmpfile = new File ("/tmp/"+filename);
            	
                FileOutputStream fout=new FileOutputStream(tmpfile);
                int ch;
                String temp;
                do
                {
                    temp=datain.readUTF();
                    ch=Integer.parseInt(temp);
                    if(ch!=-1)
                    {
                        fout.write(ch);                    
                    }
                }while(ch!=-1);
                fout.close();
                Path source = Paths.get(temppath + f.getName());
                Path target = Paths.get(path);
                //System.out.println(source);
                //System.out.println("hi");
                //System.out.println(target);
                try {
                	 	
                	Files.move(source, target, REPLACE_EXISTING);
                	 
                	  } catch (IOException e) {
                	 
                	   e.printStackTrace();
                	  }
                	 
        		File delfile = new File(temppath, filename);
        		delfile.delete();
                doutput.writeUTF("File Send Successfully");
            }
            else
            {
                return;
            }
            
    }

    void Pwd() throws Exception{
    	DataInputStream datain;
        DataOutputStream dataout;
    	dataSoc=DataSoc.accept();
	 	datain=new DataInputStream(dataSoc.getInputStream());
    	dataout=new DataOutputStream(dataSoc.getOutputStream());
    	//String dir=System.getProperty("user.dir");
    	File file = new File(".");
    	String dir = file.getAbsolutePath();
    	System.out.println(dir);
    	dataout.writeUTF(dir);
    	//datain.close();
    	//dataout.close();
    }
    void getFiles() throws Exception{
    	DataInputStream datain;
        DataOutputStream dataout;
    	dataSoc=DataSoc.accept();
	 	datain=new DataInputStream(dataSoc.getInputStream());
    	dataout=new DataOutputStream(dataSoc.getOutputStream());
    	String dir = System.getProperty("user.dir");
    	File folder= new File(dir);
    	File[] listofFiles = folder.listFiles();
    	
    	int count = 0;
    	for (int i=0;i<listofFiles.length;i++){
    		if(listofFiles[i].isFile()){
    			count++;
    		}
    	}
    	doutput.writeInt(count);
    	//System.out.println(listofFiles.length);
    	//System.out.println(count);
    	//int length=listofFiles.length;
    	for (File file : listofFiles) {
    	      if (file.isFile()) {
    	        //System.out.println("File " + file.getName());
    	        doutput.writeUTF(file.getName());
    	        
    	      } 
    	      //else if (listofFiles[i].isDirectory()) {
    	        //System.out.println("Directory " + listofFiles[i].getName());
    	      //}
    	    }
    	
    }
    void getDir() throws Exception{
    	DataInputStream datain;
        DataOutputStream dataout;
    	dataSoc=DataSoc.accept();
	 	datain=new DataInputStream(dataSoc.getInputStream());
    	dataout=new DataOutputStream(dataSoc.getOutputStream());
    	String dir = System.getProperty("user.dir");
    	File folder= new File(dir);
    	File[] listofFiles = folder.listFiles();
    	
    	int count = 0;
    	for (int i=0;i<listofFiles.length;i++){
    		if(!listofFiles[i].isFile()){
    			count++;
    		}
    	}
    	doutput.writeInt(count);
    	//System.out.println(listofFiles.length);
    	//System.out.println(count);
    	//int length=listofFiles.length;
    	for (File file : listofFiles) {
    	      if (!file.isFile()) {
    	        //System.out.println("File " + file.getName());
    	        doutput.writeUTF(file.getName());
    	      } 
    	      //else if (listofFiles[i].isDirectory()) {
    	        //System.out.println("Directory " + listofFiles[i].getName());
    	      //}
    	    }
    	
    }
	 void getList() throws Exception {
		 DataInputStream datain;
	     DataOutputStream dataout;
		 dataSoc=DataSoc.accept();
		 datain=new DataInputStream(dataSoc.getInputStream());
	    dataout=new DataOutputStream(dataSoc.getOutputStream());
		// TODO Auto-generated method stub
		String dir = System.getProperty("user.dir");
	    File folder= new File(dir);
	    File[] listofFiles = folder.listFiles();
	    doutput.writeUTF(String.valueOf(listofFiles.length));
	   	for (int i = 0; i < listofFiles.length; i++) {
	   	      if (listofFiles[i].isFile()) {
	   	        //System.out.println("File " + listofFiles[i].getName());
	   	        doutput.writeUTF("File - " + listofFiles[i].getName());
	   	      } 
	    	    else if (listofFiles[i].isDirectory()) {
	    	    //System.out.println("Directory " + listofFiles[i].getName());
	    	    doutput.writeUTF("Dir -  "+ listofFiles[i].getName());
	    	    }
	    	    }		 
		
	}
	void setCD() throws Exception{
		DataInputStream datain;
        DataOutputStream dataout;
		dataSoc=DataSoc.accept();
	 	datain=new DataInputStream(dataSoc.getInputStream());
    	dataout=new DataOutputStream(dataSoc.getOutputStream());
		String path=dinput.readUTF();
		File newdir = new File(path);
		if(newdir.exists()){
		System.setProperty("user.dir",path);
		doutput.writeUTF("true");}
		else{
			//System.out.println("No path");
			doutput.writeUTF("false");
		}
	}
	void deleteFile() throws Exception{
		DataInputStream datain;
        DataOutputStream dataout;
		dataSoc=DataSoc.accept();
	 	datain=new DataInputStream(dataSoc.getInputStream());
    	dataout=new DataOutputStream(dataSoc.getOutputStream());
		String filename=dinput.readUTF();
		String dir=System.getProperty("user.dir");
		File delfile = new File(dir, filename);
		delfile.delete();
	}
	void setNewDir() throws Exception{
		DataInputStream datain;
        DataOutputStream dataout;
		dataSoc=DataSoc.accept();
	 	datain=new DataInputStream(dataSoc.getInputStream());
    	dataout=new DataOutputStream(dataSoc.getOutputStream());
		String dir = dinput.readUTF();
		File newdir = new File(dir);
		if (!newdir.exists()){
			newdir.mkdir();
			doutput.writeUTF("true");
		}
	else {
			//System.out.println("Error while creating directory");
			doutput.writeUTF("false");
		}
	}
	void deleteDir() throws Exception{
		DataInputStream datain;
        DataOutputStream dataout;
		dataSoc=DataSoc.accept();
	 	datain=new DataInputStream(dataSoc.getInputStream());
    	dataout=new DataOutputStream(dataSoc.getOutputStream());
		String filename=dinput.readUTF();
		String dir=System.getProperty("user.dir");
		File deldir = new File(dir, filename);
		System.out.println(deldir.length());
		File[] listofFiles = deldir.listFiles();
		
		if (listofFiles.length>0){
			doutput.writeUTF("false");
		}
		else{
			
			deldir.delete();
			doutput.writeUTF("true");
		}
	}

    public void run()
    {
    
        while(true)
        {
            try
            {
            
            //System.out.println("Waiting for Command ...");
            String Command=dinput.readUTF();
            
            
            if(Command.compareTo("GET")==0)
            {
                System.out.println("\tGET Command Received ...");
                SendFile();
                continue;
            }
            else if(Command.compareTo("SEND")==0)
            {
                System.out.println("\tSEND Command Receiced ...");                
                ReceiveFile();
                continue;
            }
            else if(Command.compareTo("DISCONNECT")==0)
            {
                System.out.println("\tDisconnect Command Received ...");
                doutput.flush();
                ClientSoc.close();
                //System.exit(1);
            }
            else if (Command.compareTo("PWD")==0){
            	System.out.println("\tPWD Command Received ...");
            	//datain=new DataInputStream(dataSoc.getInputStream());
            	//dataout=new DataOutputStream(dataSoc.getOutputStream());
            	Pwd();
            	continue;
            }
            else if (Command.compareTo("getFiles")==0){
            	System.out.println("\tgetFiles Command Received ...");
            	getFiles();
            	continue;
            }
            else if (Command.compareTo("getList")==0){
            	System.out.println("\tgetList command Reveived ...");
            	getList();
            	continue;
            }
            else if (Command.compareTo("CD")==0){
            	System.out.println("\tCD Command Received ...");
            	setCD();
            	continue;
            }
            else if (Command.compareTo("Delete")==0){
            	System.out.println("\tDelete Command Received ...");
            	deleteFile();
            	continue;
            }
            else if (Command.compareTo("mkdir")==0){
            	System.out.println("\tMkdir Command Received ...");
            	setNewDir();
            	continue;
            }
            else if (Command.compareTo("getDir")==0){
            	System.out.println("\tgetdir Command Received ...");
            	getDir();
            	continue;
            }
            else if (Command.compareTo("rmdir")==0){
            	System.out.println("\tRmdir Command Received ...");
            	deleteDir();
            	continue;
            }
            }
            catch(Exception ex)
            {
            }
        }
    }

}