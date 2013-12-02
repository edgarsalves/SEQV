package pt.up.fe.cmov.seqv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

public class YahooCalls {
	static String getResponse = null;

	//a – initial month (0 denotes January, and so on) 
	//b – initial day 
	//c – initial year 
	//d – final month 
	//e – final day 
	//f – final year 
	//g – periodicity (d - daily; w - weekly; m - monthly)
	public static ArrayList<QuoteEvolution> getQuoteEvolution(String a, String b, String c, String d, String e, String f, String g, String s){
		String str = getStringQuoteEvolution(a, b, c, d, e, f, g, s);
		return stringToQuoteEvolution(str);
	}
	private static String getStringQuoteEvolution(String a, String b, String c, String d, String e, String f, String g, String s){
		String ret = null;
		String path = "http://ichart.finance.yahoo.com/table.txt?";
		path += "a=" + (Integer.valueOf(a)-1);
		path += "&b=" + b;
		path += "&c=" + c;
		path += "&d=" + d;
		path += "&e=" + e;
		path += "&f=" + f;
		path += "&g=" + g;
		path += "&s=" + s;
		try{
			ret = getHTTPAsync(path);
		}catch (Exception ex){
			return null;
		}
		return ret;
	}
	private static ArrayList<QuoteEvolution> stringToQuoteEvolution(String table){
		ArrayList<QuoteEvolution> ret = new ArrayList<QuoteEvolution>();

		StringTokenizer stLine = new StringTokenizer(table, "\n");
		String line = stLine.nextElement().toString();
		while (stLine.hasMoreElements()) {
			line = stLine.nextElement().toString();
			QuoteEvolution q = new QuoteEvolution(line);
			ret.add(q);
		}

		return ret;	
	}
	
	public static ArrayList<Quote> getQuotes(ArrayList<String> s){
		String str = getStringQuotes(s);
		return stringToQuote(str);
	}
	private static String getStringQuotes(ArrayList<String> s){
		String ret = null;
		String path = "http://finance.yahoo.com/d/quotes?f=sl1d1t1v&s=";
		for(int i = 0; i<s.size(); i++){
			if(i != 0)
				path += ",";	
			path += s.get(i);
		}
		try{
			if(s.size()!=0)
				ret = getHTTPAsync(path);
			else
				return null;
		}catch (Exception ex){
			return null;
		}
		return ret;
	}
	private static ArrayList<Quote> stringToQuote(String table){
		ArrayList<Quote> ret = new ArrayList<Quote>();

		StringTokenizer stLine = new StringTokenizer(table, "\n");
		while (stLine.hasMoreElements()) {
			String line = stLine.nextElement().toString();
			Quote q = new Quote(line);
			ret.add(q);
		}

		return ret;	
	}

	public static HashMap<String, String> searchCompanyQuery(String query){
		JSONObject json = null;
		HashMap<String, String> list = new HashMap<String, String>();
		String path = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=";
		path += query +"&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
		
		try{
			String ret = getHTTPAsync(path);
			String j = ret.substring(ret.indexOf("(")+1, ret.length()-2);
			json = new JSONObject(j);
			JSONObject resultSet = json.getJSONObject("ResultSet");
			JSONArray result = resultSet.getJSONArray("Result");
			for(int i = 0; i< result.length(); i++){
				JSONObject ji = result.getJSONObject(i);
				String symbol = ji.getString("symbol");
				String name = ji.getString("name");
				list.put(symbol, name);
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
				
		return list;
	}
	
	public static String getCompanyName(String symbol){
		HashMap<String, String> map = searchCompanyQuery(symbol);
		return map.get(symbol);
	}

	private static String getHTTPAsync(final String path){
		getResponse = null;
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					getResponse = getHTTP(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thread.start();

		try {
			thread.join();
		} catch (InterruptedException e) {
		}
		return getResponse;
	}

	private static String getHTTP(String path){
		String ret = null;

		HttpURLConnection connection = null;
		URL url = null;
		BufferedReader in = null;
		try {
			url = new URL(path);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line = "";
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine())!= null)
				sb.append(line+ "\n");

			ret = sb.toString();

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}
		return ret;
	}
}
