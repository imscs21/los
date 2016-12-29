import java.io.*;
import java.util.*;
public class BaseClass{
public HttpURLConnection conn=null;
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

	public String queryContent(Configure conf,String cookie_content,String sub_query) throws Exception{
		conn =(HttpURLConnection) new URL(conf.getURL()+sub_query).openConnection();
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
	public boolean isSuccess(String resp,String[] acclst){
		boolean rst = false;
		for(String acc:acclst){
			rst = rst||resp.contains("<h2>Hello "+acc);
			if(rst){
				break;
			}
		}
		return rst;
	}
	public boolean isSuccess(String resp){
		return isSuccess(resp,new String[]{"guest","Guest","Admin","admin"});
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
		
	}

}
