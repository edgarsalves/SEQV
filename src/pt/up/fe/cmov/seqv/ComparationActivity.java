package pt.up.fe.cmov.seqv;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

public class ComparationActivity extends Activity {
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comparation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comparation, menu);
		return false;
	}

}
