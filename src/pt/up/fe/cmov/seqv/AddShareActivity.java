package pt.up.fe.cmov.seqv;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AddShareActivity extends Activity {
	HashMap<String, String> results;
	ArrayList<String> symbols;
	ListView lvSearch;
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_share);
		
		results = new HashMap<String, String>();
		symbols = new ArrayList<String>(results.keySet());
		
		final TextView tvQuery = (TextView) findViewById(R.id.asQuery);
		
		lvSearch = (ListView) findViewById(R.id.asList);
		lvSearch.setAdapter(new SearchResultsAdapter(context));
		
		Button btnSearch = (Button) findViewById(R.id.asSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String query = tvQuery.getText().toString();
				results = YahooCalls.searchCompanyQuery(query);
				symbols  = new ArrayList<String>(results.keySet());
				lvSearch.setAdapter(new SearchResultsAdapter(context));
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_share, menu);
		return false;
	}

	class SearchResultsAdapter extends BaseAdapter {
		Context context;

		public SearchResultsAdapter(Context context) {
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
			TextView description = (TextView)vi.findViewById(R.id.rowDescription);
			//TextView price = (TextView)vi.findViewById(R.id.rowRightDescription);
			//ImageView image = (ImageView)vi.findViewById(R.id.rowImage);
			
			String symbol = symbols.get(position);
			String name = results.get(symbol);
			
			title.setText(symbol);
			description.setText(name);
			
			return vi;
		}
	}
}
