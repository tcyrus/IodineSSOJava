package edu.tjhsst.tcyrus.IodineSSOAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class SSO {
	public static void main(String[] args) throws IOException {
		System.out.println(SSOUrl("App Name",new URL("http://localhost/")).toString());
		Scanner scan=new Scanner(System.in);
		System.out.println("Enter SSO Key");
		String input=scan.next();
		scan.close();
		System.out.println(verifySSO(input).toString());
		System.out.println(login(input));
	}
	public static URL SSOUrl(String title, URL callback) throws MalformedURLException, UnsupportedEncodingException {
		return SSOUrl(title,callback,120);
	}
	public static URL SSOUrl(String title, URL callback, int exp) throws MalformedURLException, UnsupportedEncodingException {
		int time=(int)(System.currentTimeMillis()/1000L);
		String data="time="+URLEncoder.encode(Integer.toString(time),"UTF-8")+"&method=GET";
		data+="&title="+URLEncoder.encode(title,"UTF-8");
	    data+="&return="+URLEncoder.encode(callback.toString(),"UTF-8");
	    data+="&exp="+URLEncoder.encode(Integer.toString(time+exp),"UTF-8");
	    String data64=new String(Base64.encodeBase64(data.getBytes()));
	    return new URL("https://iodine.tjhsst.edu/sso?req="+data64);
	}
	public static JSONObject verifySSO(String sso) throws IOException {
		URL verifyurl=new URL("https://iodine.tjhsst.edu/ajax/sso/valid_key?sso="+sso);
		HttpsURLConnection urlConnection=(HttpsURLConnection)verifyurl.openConnection();
		urlConnection.connect();
		JSONObject result=inputStreamToJSON(urlConnection.getInputStream());
		urlConnection.disconnect();
		return result;
	}
	public static List<String> login(String sso) throws IOException {
		URL loginurl=new URL("https://iodine.tjhsst.edu/login?sso="+sso);
		HttpsURLConnection urlConnection=(HttpsURLConnection)loginurl.openConnection();
		urlConnection.connect();
		List<String> cookieList=urlConnection.getHeaderFields().get("Set-Cookie");
		urlConnection.disconnect();
		return cookieList;
	}
	private static JSONObject inputStreamToJSON(InputStream inputStream) throws JSONException, IOException {
		final BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"),8);
		final StringBuilder sb=new StringBuilder();
		String line;
		while ((line=reader.readLine())!=null) {sb.append(line).append("\n");}
		return new JSONObject(sb.toString());
	}
}
