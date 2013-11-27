package pt.up.fe.cmov.seqv;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MySharesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_shares);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_shares, menu);
		return false;
	}

}
