import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;


public class client
{
	public class threadm implements Runnable
	{
	Socket s;	
	InputStreamReader input;
	BufferedReader reader;
	public threadm(Socket soc)
	{
	s=soc;
	try
	{
	input = new InputStreamReader(s.getInputStream());
	reader = new BufferedReader(input);
	}
	catch(Exception ex) { System.out.println("Server has Closed"); }
	}
	public void run()
	{
		String message;
		String filename;
		try
		{
		while((message=reader.readLine())!=null)
		{
		if(message.substring(0, 4).equals("file"))
		{
		System.out.println(message.substring(5));		
		filename = findfile(message);
		try
		{
			InputStream inpustream = s.getInputStream();
    		FileOutputStream file = new FileOutputStream(filename);
    		BufferedOutputStream bufferoutput = new BufferedOutputStream(file);
    		byte[] bytes = new byte[16];
    		int count;
    		while((count=inpustream.read(bytes))>0)
    		{
    			bufferoutput.write(bytes,0,count);
    			bufferoutput.flush();
    			if(count<16) break;
    		}
    		
		}
		catch(Exception ex) {System.out.println("Problem in last step");}
		}
		else if(!message.equals("message")) System.out.println(message);
		}
		}
		catch(Exception ex) {ex.printStackTrace();}
	}
	public String findfile(String st)
	{
		String[] sts = st.split("\\s+");
		return sts[2];
	}
	}
	public void go()
    {
		int check=0,port;
		Socket socket=null;	
		String message="",string="",name;
		InputStreamReader input=null;
		BufferedReader reader=null;
		PrintWriter writer=null;
		Scanner scan = new Scanner(System.in);
		while(check==0 && scan.hasNextLine()) { message = scan.nextLine(); check=1;}
		port = portnumber(message);
		name=clientname(message);
        try
        {
        socket = new Socket("127.0.0.1",port);
        writer = new PrintWriter(socket.getOutputStream());
        writer.println(name);
        writer.flush();
        input = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(input);
        System.out.println(reader.readLine());
        Thread t = new Thread(new threadm(socket));
        t.start();
        }
		catch(Exception ex) {System.out.println("clientside mistake"); }
        while((message=scan.nextLine())!=null)
        {
        	if(containswordmessage(message))
        	 {
        		message = modifymessage(message);
        		writer.println(message);
        		writer.flush();
        	 }
        	 else if(containswordfile(message))
        	 {
        		 string = modifymessage(message);
                 writer.println(string);
        		 writer.flush();
                 File myFile = new File(findfilename(message));
                 byte[] bytes = new byte[16];
                 try
                 {
                 BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                 OutputStream os = socket.getOutputStream();
                 int count;
                 while((count=bis.read(bytes))>0) 
                 {
                	 os.write(bytes, 0, count);
                	 os.flush();
                	 if(count<16) break;
                 }
                 }
                 catch(Exception ex) { System.out.println("problem in file writing"); }

        	 }
        	 else System.out.println("you entered a Invalid string");
        }
    }
	public String findfilename2(String message)
	{
		String sts[]=message.split("\\s+");
		return sts[1];
	}
	public String findfilename(String message)
	{
		String sts[]=message.split("\\s+");
		return sts[2];
	}
	public String modifymessage(String st)
	{
		String ans="";
		String[] sts=st.split("\\s+");
		if(sts.length<2) System.out.println("Invalid String, Try Again");
		ans = ans + sts[1];
		ans = ans +" "+sts[0];
		for(int i=2;i<sts.length;i++) ans = ans+" "+sts[i];
	    return ans;	
	}
	public String clientname(String st)
	{
		String[] sts=st.split("\\s+");
		return sts[0];
	}
	public int portnumber(String st)
	{
		String[] ans = st.split("\\s+");
		return Integer.parseInt(ans[1]);
	}
    public boolean containswordmessage(String st)
	{
	String[] sts=st.split("\\s+");
	for(String s:sts) if(s.equals("message")) return true;
	return false;
	}
    public boolean containswordfile(String st)
	{
	String[] sts=st.split("\\s+");
	for(String s:sts) if(s.equals("file")) return true;
	return false;
	}
    public static void main(String args[])
    {
    	new client().go();
    }
}