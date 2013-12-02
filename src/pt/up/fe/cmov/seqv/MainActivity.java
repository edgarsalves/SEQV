package pt.up.fe.cmov.seqv;

import java.util.Iterator;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Context context = this;
	public static TreeMap<String, Integer> myPortfolio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loadDatabase();

		Button btnShares = (Button) findViewById(R.id.mMyShares);
		btnShares.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(isOnline(context)){
					Intent i = new Intent(context, MyPortfolioActivity.class);
					startActivity(i);
				}
			}
		});

		Button btnExit = (Button) findViewById(R.id.mExit);
		btnExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});	
	}

	@SuppressWarnings("unchecked")
	public void loadDatabase(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String str = preferences.getString("myPortfolio","");

		myPortfolio = new TreeMap<String, Integer>();

		if(str.length()!=0){
			JSONObject json;
			try {
				json = new JSONObject(str);
				Iterator<String> keys = json.keys();
				while(keys.hasNext()){
					String key = keys.next();
					Integer value = json.getInt(key);
					myPortfolio.put(key, value);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void updateDatabase(Context context){
		JSONObject json = new JSONObject(myPortfolio);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("myPortfolio", json.toString());
		editor.commit();
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		Toast.makeText(context, "There is no internet connection!", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}

	//number=0 -> remove symbol
	public static void updateMyPortfolio(String symbol, Integer number, Context context){
		if(myPortfolio.containsKey(symbol)){
			if(number==0)
				myPortfolio.remove(symbol);
			else if(number<0){
				Integer result = myPortfolio.get(symbol) + number;
				if(result<=0)
					myPortfolio.remove(symbol);
				else
					myPortfolio.put(symbol, myPortfolio.get(symbol) + number);
			}
			else
				myPortfolio.put(symbol, myPortfolio.get(symbol) + number);
		}
		else if(number>0)
			myPortfolio.put(symbol, number);
		else
			Log.e("ERROR", "Update my portfolio failed!");
		
		updateDatabase(context);		
	}
	
	public static int getNCompanys(){
		return myPortfolio.size();
	}

}
