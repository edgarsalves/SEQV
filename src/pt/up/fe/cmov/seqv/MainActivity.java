package pt.up.fe.cmov.seqv;

import java.util.ArrayList;
import java.util.Arrays;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
		private Context context = this;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			
			Button btnShares = (Button) findViewById(R.id.mMyShares);
			btnShares.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(isOnline(context)){
						Intent i = new Intent(context, AddShareActivity.class);
						//TODO
						//Intent i = new Intent(context, MySharesActivity.class);
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
			
			//TODO
			String s = YahooCalls.getQuotes(new ArrayList<String>(Arrays.asList("dell", "msft")));
			YahooCalls.quoteToObject(s);
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

}
