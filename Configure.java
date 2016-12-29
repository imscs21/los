import java.util.*;
import java.io.*;
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
		public final static String file_version = "1.3";
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
