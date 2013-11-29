package pt.up.fe.cmov.seqv;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyPortfolioActivity extends Activity {
	private Context context = this;
	private ListView lvSearch;
	private ArrayList<String> symbols;
	private TextView tvNCompanys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_portfolio);
		
		symbols = new ArrayList<String>(MainActivity.myPortfolio.keySet());
		
		lvSearch = (ListView) findViewById(R.id.mpList);
		lvSearch.setAdapter(new MyPortfolioAdapter(context));
		
		tvNCompanys = (TextView) findViewById(R.id.mpText);
		tvNCompanys.setText("Companys in my portfolio: " + Integer.toString(MainActivity.getNCompanys()));
		
		Button btnAdd = (Button) findViewById(R.id.mpAdd);
		btnAdd.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, AddShareActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.my_portfolio, menu);
		return false;
	}
	
	class MyPortfolioAdapter extends BaseAdapter {
		Context context;

		public MyPortfolioAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return symbols.size();
		}

		@Override
		public Object getItem(int position) {
			return symbols.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View vi = convertView;
			if (vi == null)
				vi = inflater.inflate(R.layout.row_layout, null);

			TextView title = (TextView)vi.findViewById(R.id.rowTitle);
			//TextView description = (TextView)vi.findViewById(R.id.rowDescription);
			TextView price = (TextView)vi.findViewById(R.id.rowRightDescription);
			//ImageView image = (ImageView)vi.findViewById(R.id.rowImage);
			
			String symbol = symbols.get(position);
			Integer number = MainActivity.myPortfolio.get(symbol);
			
			title.setText(symbol);
			price.setText(Integer.toString(number));
			
			return vi;
		}
	}
	
	@Override
	protected void onResume() {
		symbols = new ArrayList<String>(MainActivity.myPortfolio.keySet());
		lvSearch.setAdapter(new MyPortfolioAdapter(context));
		tvNCompanys.setText("Companys in my portfolio: " + Integer.toString(MainActivity.getNCompanys()));
		super.onResume();
	}
}
