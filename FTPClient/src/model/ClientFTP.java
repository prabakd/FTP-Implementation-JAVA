package model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author dhanush
 */
// FTP Client

import ftpclient.MainFrame;
import java.net.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.SwingUtilities;


public class ClientFTP
{
    
    BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
   public static Socket soc;
    public static DataInputStream din = null;
    public static DataOutputStream dout = null;
    public static DataInputStream datadin = null;
    public static DataOutputStream dataout = null;
    public static Socket datasoc;
    public static int PortNo,dataPort;
    public static String Host;
    public String connect(String args[]) throws Exception
    {
     	
        String username="";
        String pass="";
        
        String msg="Failure";
	 	//System.out.println(args[0]);
	 	if (args.length == 4){
	 		//System.out.println(args[1]);
	 		Host=args[0];
                        PortNo=Integer.parseInt(args[1]);
                        
                        username=args[2];
                        pass=args[3];
	 		
	 	}
	 	else {
	 		PortNo=21;
	 	}
        soc=new Socket(Host,PortNo);
        dataPort=PortNo-1;
        //datasoc = new Socket (Host,PortNo-1);
//        System.out.println(datasoc.getPort());
        System.out.println(soc.getPort());
        din=new DataInputStream(soc.getInputStream());
        dout=new DataOutputStream(soc.getOutputStream());
        //datadin= new DataInputStream(datasoc.getInputStream());
        //dataout=new DataOutputStream(datasoc.getOutputStream());
        dout.writeUTF(username);
    	System.out.println("Etner the password:");
    	//String pass=br.readLine();
    	dout.writeUTF(pass);
        System.out.println("after psass");
    	return din.readUTF();
        //System.out.println("msgs");
        //return msg;
        //transferfileClient t=new transferfileClient(soc,username,pass);
        //t.displayMenu();
        
    }

 /*public String getPwd() throws Exception{
 transferfileClient t=new transferfileClient(soc);
 String pwd=t.getPWD();
 return pwd;
 }   
}*/
    
    public void SendFile(File file) throws Exception
    {        
        dout.writeUTF("SEND");
        String filename=file.getName();
        //System.out.print("Enter File Name :");
        //filename=br.readLine();
        System.out.println(file);
        System.out.println(file.getName());
        /*File f=new File(filename);
        if(!f.exists())
        {
            System.out.println("File not Exists...");
            dout.writeUTF("File not found");
            return;
        }*/
        
        dout.writeUTF(filename);
        
        String msgFromServer=din.readUTF();
        if(msgFromServer.compareTo("File Already Exists")==0)
        {
            String Option;
            System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
            //Option=br.readLine();   
            MainFrame obj_main=new MainFrame();
            Option=obj_main.getChoice();
            if(Option=="Y")    
            {
                dout.writeUTF("Y");
            }
            else
            {
                dout.writeUTF("N");
                return;
            }
        }
        
        System.out.println("Sending File ...");
        FileInputStream fin=new FileInputStream(file);
        double filelength=file.length();
        double updatelength=filelength/1000;
        MainFrame obj=new MainFrame();
        int ch,count=0;
        do
        {
            if (count > updatelength){
                //System.out.println("in client" + updatelength);
//                obj.updateProgress(updatelength,filelength);
                //Thread.sleep(100);
                //obj.jProgressBar1.setValue(100);
            updatelength+=updatelength;
            
            }
            count++;
            ch=fin.read();
            //System.out.println(ch);
            dataout.writeUTF(String.valueOf(ch));
        }
        while(ch!=-1);
        fin.close();
        System.out.println(din.readUTF());
        //return true;
    }
    
    public void ReceiveFile(String fileName) throws Exception
    {
        //String fileName;
        //System.out.print("Enter File Name :");
        //fileName=br.readLine();
        dout.writeUTF("GET");
        dout.writeUTF(fileName);
        String msgFromServer=din.readUTF();
        
        if(msgFromServer.compareTo("File Not Found")==0)
        {
            System.out.println("File not found on Server ...");
            return;
        }
        else if(msgFromServer.compareTo("READY")==0)
        {
            System.out.println("Receiving File ...");
            File f=new File(fileName);
            if(f.exists())
            {
                String Option;
                System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
                //Option=br.readLine();  
                MainFrame obj_main=new MainFrame();
                Option=obj_main.getChoice();
                if(Option=="N")    
                {
                    dout.flush();
                    return;    
                }                
            }
            FileOutputStream fout=new FileOutputStream(f);
            int ch;
            String temp;
            do
            {
                temp=din.readUTF();
                ch=Integer.parseInt(temp);
                if(ch!=-1)
                {
                    fout.write(ch);                    
                }
            }while(ch!=-1);
            fout.close();
            System.out.println(din.readUTF());
                
        }
        
        
    }
    public String getPWD() throws Exception{
    
    dout.writeUTF("PWD");
    datasoc=new Socket(Host,dataPort);
    datadin=new DataInputStream(datasoc.getInputStream());
    dataout=new DataOutputStream(datasoc.getOutputStream());
    System.out.println("before read in pwd");
    String pwd=datadin.readUTF();
    System.out.println("getpwd");
    return pwd;
    }
    
    public ArrayList<String> getFiles() throws IOException{
    
        ArrayList<String> fileList = new ArrayList<String>();
        dout.writeUTF("getFiles");
        datasoc=new Socket(Host,dataPort);
    datadin=new DataInputStream(datasoc.getInputStream());
    dataout=new DataOutputStream(datasoc.getOutputStream());
        int length=din.readInt();
        for (int i=0; i < length; i++){
            fileList.add(din.readUTF());
        }
        System.out.println("in get files");
        
        return fileList;
    }
    
    public ArrayList<String> getList() throws IOException{
    
    ArrayList<String> fileList = new ArrayList<String>();
        dout.writeUTF("getList");
    datasoc=new Socket(Host,dataPort);
    datadin=new DataInputStream(datasoc.getInputStream());
    dataout=new DataOutputStream(datasoc.getOutputStream()); 
        System.out.println("Testin list");
        int length=Integer.parseInt(din.readUTF());
        for (int i=0; i < length; i++){
            fileList.add(din.readUTF());
        }
               
        return fileList;
    }
    
    public void disconnect() throws IOException{
    
        dout.writeUTF("Disconnect");
        datasoc=new Socket(Host,dataPort);
    datadin=new DataInputStream(datasoc.getInputStream());
    dataout=new DataOutputStream(datasoc.getOutputStream());
        dout.flush();
        //soc.close();
    
    }
    public void deleteFile(String filename) throws IOException{
    
        dout.writeUTF("Delete");
        datasoc=new Socket(Host,dataPort);
    datadin=new DataInputStream(datasoc.getInputStream());
    dataout=new DataOutputStream(datasoc.getOutputStream());
        dout.writeUTF(filename);
        
    }
    public String setCD(String path) throws IOException {
       
         dout.writeUTF("CD");
             datasoc=new Socket(Host,dataPort);
    datadin=new DataInputStream(datasoc.getInputStream());
    dataout=new DataOutputStream(datasoc.getOutputStream());
         dout.writeUTF(path);
         String status=din.readUTF();
         return status;
       
    }
    public String setNewDir(String dir) throws IOException{
       
        dout.writeUTF("mkdir");
         datasoc=new Socket(Host,dataPort);
    datadin=new DataInputStream(datasoc.getInputStream());
    dataout=new DataOutputStream(datasoc.getOutputStream());
        dout.writeUTF(dir);
        String status=din.readUTF();
        return status;
    }
   public ArrayList<String> getDir() throws IOException{
       
        ArrayList<String> fileList = new ArrayList<String>();
        dout.writeUTF("getDir");
        datasoc=new Socket(Host,dataPort);
    datadin=new DataInputStream(datasoc.getInputStream());
    dataout=new DataOutputStream(datasoc.getOutputStream());
        int length=din.readInt();
        for (int i=0; i < length; i++){
            fileList.add(din.readUTF());
        }
        //System.out.println("in get files");
        
        return fileList;
    }
   public String deleteFolder(String dirName) throws IOException{
    
   dout.writeUTF("rmdir");
      datasoc=new Socket(Host,dataPort);
    datadin=new DataInputStream(datasoc.getInputStream());
    dataout=new DataOutputStream(datasoc.getOutputStream());
   dout.writeUTF(dirName);
   String status=din.readUTF();
        return status;
   }
   

}
 