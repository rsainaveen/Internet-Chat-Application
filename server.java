
import java.net.*;
import java.io.*;
import java.util.*;


public class server
{
    HashMap<String,PrintWriter> messagemap = new HashMap<String,PrintWriter>();
    HashMap<String,Socket> filemap = new HashMap<String,Socket>();
	public class threadm implements Runnable
    {
    	String name;
    	Socket socket;
    	BufferedReader reader;
    	public threadm(Socket socket,String name)
    	{
    	this.name=name;
    	this.socket=socket;
    	try
    	{
    		InputStreamReader input = new InputStreamReader(socket.getInputStream());
    		reader = new BufferedReader(input);
    	}
    	catch(Exception ex) { System.out.println("Problem in thread Constructor");}
    	}
    	public void run()
    	{
    	String message;	
    	try
    	{
    	while((message=reader.readLine())!=null)
    	{	
    	if(message.substring(0, 7).equals("message"))
        {
    	evaluate(message);	
        }
    	if(message.substring(0, 4).equals("file"))
    	{
    	    String filename = find(message);
    		InputStream inputstream = socket.getInputStream();
    	    FileOutputStream file = new FileOutputStream(filename);
    		BufferedOutputStream bufferoutput = new BufferedOutputStream(file);
    		byte[] bytes = new byte[16];
    		int count;
    		while((count=inputstream.read(bytes))>0) 
    		{
    			bufferoutput.write(bytes,0,count);
    			bufferoutput.flush();
    			if(count<16) break;
    		}
    		
    	    evaluatef(message,filename);
    	    
    	}
    	}
    	}
    	catch(Exception ex) { System.out.println(name+" has disconnected");}
    	}
    	public void evaluatef(String message,String filename)
    	{
    		String otherclient;
	    	 String sendmessage;
	    	 if(findcasttype(message).equals("broadcast"))
    	    	{
	    		    sendmessage = "file "+"File: "+filename+" sent by "+name;
    	    		broadcastf(filename,sendmessage,name);
    	    		System.out.println(name+" "+findcasttype(message)+"ed file");
    	    		messagemap.get(name).println("File sent");
    	    		messagemap.get(name).flush();
    	    	}
    	    	else if(findcasttype(message).equals("blockcast"))
    	    	{
    	    		sendmessage = "file "+"File: "+filename+" sent by "+name;
    	    		otherclient = findotherclient(message);
    	    		blockcastf(filename,sendmessage,name,otherclient);
    	    		System.out.println(name+" "+findcasttype(message)+"ed file excluding ["+otherclient+"]");
    	    		messagemap.get(name).println("File sent");
    	    		messagemap.get(name).flush();
    	    	}
    	    	else if(findcasttype(message).equals("unicast"))
    	    	{
    	    		sendmessage = "file "+"File: "+filename+" sent by "+name;
    	    		otherclient = findotherclient(message);
    	    		unicastf(filename,sendmessage,otherclient);
    	    		System.out.println(name+" "+findcasttype(message)+"ed file to ["+otherclient+"]");
    	    		messagemap.get(name).println("File sent");
    	    		messagemap.get(name).flush();
    	    	}
    	    	else
    	    	{
    	    		System.out.println("first Invalid command by "+name);
    	    		messagemap.get(name).println("You entered an Invalid String");	
    	    		messagemap.get(name).flush();
    	    	}
	    }
    public void broadcastf(String filename,String message,String curr)
	{
    	for(Map.Entry<String, PrintWriter> en:messagemap.entrySet())
    	{
    		        if(!en.getKey().equals(curr))
				    {
		        	 en.getValue().println(message);
					 en.getValue().flush();
		        	 try
				     {
				     Socket sock = filemap.get(en.getKey());
				     File myFile = new File(filename);	
				     byte[] bytes = new byte[16];
					 BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
	                 OutputStream os = sock.getOutputStream();
	                 int count;
	                 while((count=bis.read(bytes))>0) 
	                 {
	                 os.write(bytes, 0, count);
	                 os.flush();
	                 if(count<16) break;
	                 }
				     }
				     catch(Exception ex) { System.out.println("Problem in last before step");} 
				    
				    }
		}		
	}
    public void blockcastf(String filename,String message,String curr,String other)
	{
    	for(Map.Entry<String, PrintWriter> en:messagemap.entrySet())
    	{
    		        if(!en.getKey().equals(curr) && !en.getKey().equals(other))
				    {
		        	 en.getValue().println(message);
					 en.getValue().flush();
		        	 try
				     {
				     Socket sock = filemap.get(en.getKey());
				     File myFile = new File(filename);	
				     byte[] bytes = new byte[16];
					 BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
	                 OutputStream os = sock.getOutputStream();
	                 int count;
	                 while((count=bis.read(bytes))>0) 
	                 {
	                	 os.write(bytes, 0, count);
	                     os.flush();
	                     if(count<16) break;
	                 }
				     }
				     catch(Exception ex) { System.out.println("Problem in last before step");} 
				    
				    }
		}		
	}
    public void unicastf(String filename,String message,String other)
	{
  
    		       
		        	 messagemap.get(other).println(message);
					 messagemap.get(other).flush();
		        	 try
				     {
				     Socket sock = filemap.get(other);
				     File myFile = new File(filename);	
				     byte[] bytes = new byte[16];
					 BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
	                 OutputStream os = sock.getOutputStream();
	                 int count;
	                 while((count=bis.read(bytes))>0) 
	                 {
	                 os.write(bytes, 0, count);
	                 os.flush();
	                 if(count<16) break;
	                 }
				     }
				     catch(Exception ex) { System.out.println("Problem in last before step");} 
				    
				   
	}
    public void evaluate(String message)
    {
    		    String otherclient;
    	    	String sendmessage;
    	    	if(findcasttype(message).equals("broadcast"))
    	    	{
    	    		sendmessage = "@"+""+name+":"+message.substring(18);
    	    		broadcast(sendmessage,name);
    	    		System.out.println(name+" "+findcasttype(message)+"ed message");
    	    		messagemap.get(name).println("Message sent");
    	    		messagemap.get(name).flush();
    	    	}
    	    	else if(findcasttype(message).equals("blockcast"))
    	    	{
    	    		sendmessage = "@"+""+name+":"+findsendmessage(message);
    	    		otherclient = findotherclient(message);
    	    		blockcast(sendmessage,name,otherclient);
    	    		System.out.println(name+" "+findcasttype(message)+"ed message excluding ["+otherclient+"]");
    	    		messagemap.get(name).println("Message sent");
    	    		messagemap.get(name).flush();
    	    	}
    	    	else if(findcasttype(message).equals("unicast"))
    	    	{
    	    		sendmessage = "@"+""+name+":"+findsendmessage(message);
    	    		otherclient = findotherclient(message);
    	    		unicast(sendmessage,otherclient);
    	    		System.out.println(name+" "+findcasttype(message)+"ed message to ["+otherclient+"]");
    	    		messagemap.get(name).println("Message sent");
    	    		messagemap.get(name).flush();
    	    	}
    	    	else
    	    	{
    	    		System.out.println("first Invalid command by "+name);
    	    		messagemap.get(name).println("You entered an Invalid String");	
    	    		messagemap.get(name).flush();
    	    	}
    	}
    public String findfilename(String message)
    {
    	String[] sts=message.split("\\s+");
    	return sts[2];
    }
    public void broadcast(String message,String curr)
	{
		 for(Map.Entry<String, PrintWriter> en:messagemap.entrySet())
			{
				    if(!en.getKey().equals(curr))
					 {
				     en.getValue().println("message");
				     en.getValue().flush();
					 en.getValue().println(message);
					 en.getValue().flush();
					 }
			}
	}
	public void blockcast(String message,String curr,String other)
	{
		 for(Map.Entry<String, PrintWriter> en:messagemap.entrySet())
			{
				 if(!en.getKey().equals(curr) && !en.getKey().equals(other))
					 {
					 en.getValue().println("message");
				     en.getValue().flush();
					 en.getValue().println(message);
					 en.getValue().flush();
					 }
			}	
	}
	public void unicast(String message,String other)
		{
		    messagemap.get(other).println("message");
		    messagemap.get(other).flush();
		    messagemap.get(other).println(message);
			messagemap.get(other).flush();
		}
	public String findotherclient(String message)
	{
		String[] sts = message.split("\\s+");
		return sts[sts.length-1];
	}
	public String findsendmessage(String message)
	{
		String[] sts = message.split("\\s+");
		String ans="";
		for(int i=2;i<sts.length-1;i++) ans = ans+" "+sts[i];
		return ans;
			
	}
	public String findcasttype(String message)
	{
		String[] sts = message.split("\\s+");
		return sts[1];
	}
	public String modify(String path,String filename)
	{
	String ans="";
	String temp="\\";
	int i,j;
	i=path.length()-2;
	while(i>=0 && path.charAt(i)!='/') i--;
	for(j=1;j<=i;j++)
	{
	if(path.charAt(j)=='/') ans = ans+temp+temp;
	else ans = ans + path.charAt(j);
	}
	ans = ans +filename;
	return ans;
	}
	public String find(String s)
	{
		String[] sts=s.split("\\s+");
		return sts[2];
	}
    }
	public void go()
    {
	Socket clientsocket;
	String clientname;
	String message,string="";
	InputStreamReader input;
	BufferedReader reader;
	PrintWriter writer;	
	try
	{
	ServerSocket serversocket = new ServerSocket(7000);	
	while(true)
	{
    clientsocket = serversocket.accept();
    input = new InputStreamReader(clientsocket.getInputStream());
    reader = new BufferedReader(input);
    clientname = reader.readLine();
    filemap.put(clientname, clientsocket);
    System.out.println(clientname +" is connected");
    writer = new PrintWriter(clientsocket.getOutputStream());
    writer.println(clientname +",you are connected");
    writer.flush();
    messagemap.put(clientname, writer);
    Thread tm = new Thread(new threadm(clientsocket,clientname));
    tm.start();
	}
    }
	catch(Exception ex) { System.out.println("server side misteake"); }
    }
	public static void main(String[] args)
	{
		new server().go();
	}
}