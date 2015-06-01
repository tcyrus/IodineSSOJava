package edu.tjhsst.tcyrus.iodine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;

import org.json.JSONException;
import org.json.JSONObject;

/** Iodine SSO API
 *
 * @author Timothy Cyrus
 * @see <a href="https://github.com/tcyrus/IodineSSOJava">Github</a>
 */
public class SSO {
	/** Creates A Single Sign On URL
	 * 
	 * @param name			Name of Application
	 * @param callback		Callback URL or Intent URI
	 * @return URL			URL to launch in Web Browser
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public static URL SSOUrl(String name, URL callback) throws MalformedURLException, UnsupportedEncodingException {
		return SSOUrl(name,callback,120);
	}
	/** Creates A Single Sign On URL
	 * 
	 * @param name			Name of Application
	 * @param callback		Callback URL or Intent URI
	 * @param exp			Custom Time for Expiration of Token (SSO Token in POST)
	 * @return URL			URL to launch in Web Browser
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public static URL SSOUrl(String name, URL callback, int exp) throws MalformedURLException, UnsupportedEncodingException {
		int time=(int)(System.currentTimeMillis()/1000L);
		String data="time="+URLEncoder.encode(Integer.toString(time),"UTF-8")+"&method=GET";
		data+="&title="+URLEncoder.encode(name,"UTF-8");
	    data+="&return="+URLEncoder.encode(callback.toString(),"UTF-8");
	    data+="&exp="+URLEncoder.encode(Integer.toString(time+exp),"UTF-8");
	    String data64=new String(Base64.encodeBase64(data.getBytes()));
	    return new URL("https://iodine.tjhsst.edu/sso?req="+data64);
	}
	/** Verifies SSO Token
	 * 
	 * @param sso			SSO Token
	 * @return JSONObject	JSON returned from Verification
	 * @throws IOException
	 */
	public static JSONObject verifySSO(String sso) throws IOException {
		URL verifyurl=new URL("https://iodine.tjhsst.edu/ajax/sso/valid_key?sso="+sso);
		HttpsURLConnection urlConnection=(HttpsURLConnection)verifyurl.openConnection();
		urlConnection.connect();
		JSONObject result=inputStreamToJSON(urlConnection.getInputStream());
		urlConnection.disconnect();
		return result;
	}
	/** Logs In to Iodine using SSO Token
	 * 
	 * @param sso			SSO Token
	 * @return List<String>	List of Cookies
	 * @throws IOException
	 */
	public static List<String> login(String sso) throws IOException {
		URL loginurl=new URL("https://iodine.tjhsst.edu/login?sso="+sso);
		HttpsURLConnection urlConnection=(HttpsURLConnection)loginurl.openConnection();
		urlConnection.connect();
		List<String> cookieList=urlConnection.getHeaderFields().get("Set-Cookie");
		urlConnection.disconnect();
		return cookieList;
	}
	/** Converts Input Stream to JSONObject
	 * 
	 * @param inputStream
	 * @return JSONObject
	 * @throws JSONException
	 * @throws IOException
	 */
	private static JSONObject inputStreamToJSON(InputStream inputStream) throws JSONException, IOException {
		final BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"),8);
		final StringBuilder sb=new StringBuilder();
		String line;
		while ((line=reader.readLine())!=null) {sb.append(line).append("\n");}
		return new JSONObject(sb.toString());
	}
}