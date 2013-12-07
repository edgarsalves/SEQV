package pt.up.fe.cmov.seqv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddShareActivity extends Activity {
	private Context context = this;
	private ListView lvSearch;
	private HashMap<String, String> results;
	private ArrayList<String> symbols;
	private int counter = 1;
	private String symbol;
	private Quote q;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_share);

		results = new HashMap<String, String>();
		symbols = new ArrayList<String>(results.keySet());

		final TextView tvQuery = (TextView) findViewById(R.id.asQuery);

		lvSearch = (ListView) findViewById(R.id.asList);
		lvSearch.setAdapter(new SearchResultsAdapter(context));

		lvSearch.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id){
				if(MainActivity.isOnline(context)){
					symbol = symbols.get(position);
					q = YahooCalls.getQuotes(new ArrayList<String>(Arrays.asList(symbol))).get(0);
					counter = 1;

					final Dialog addShareDialog = new Dialog(context);
					addShareDialog.setContentView(R.layout.dialog_add_share);
					addShareDialog.setTitle("Add share of " + symbol);

					final TextView tvSymbol = (TextView) addShareDialog.findViewById(R.id.dasSymbol);
					tvSymbol.setText("Symbol: " + symbol);

					final TextView tvName = (TextView) addShareDialog.findViewById(R.id.dasName);
					tvName.setText("Name: " + results.get(symbol));

					final TextView tvPrice = (TextView) addShareDialog.findViewById(R.id.dasPrice);
					tvPrice.setText("Price: " + q.price);

					final TextView tvNumber = (TextView) addShareDialog.findViewById(R.id.dasNumber);
					tvNumber.setText(Integer.toString(counter));

					final ImageButton btnMinus = (ImageButton) addShareDialog.findViewById(R.id.dasMinus);
					btnMinus.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(counter>1){
								counter--;
								tvNumber.setText(Integer.toString(counter));
							}
						}
					});

					final ImageButton btnPlus = (ImageButton) addShareDialog.findViewById(R.id.dasPlus);
					btnPlus.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							counter++;
							tvNumber.setText(Integer.toString(counter));
						}
					});

					final Button btnConfirm = (Button) addShareDialog.findViewById(R.id.dasConfirm);
					btnConfirm.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Builder confirmationDialog = new AlertDialog.Builder(context);
							confirmationDialog.setTitle("");
							confirmationDialog.setMessage("Do you really want to add these shares?");
							confirmationDialog.setIcon(android.R.drawable.ic_dialog_alert);
							confirmationDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									MainActivity.updateMyPortfolio(symbol, counter, context);
									Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
									addShareDialog.dismiss();
								}});
							confirmationDialog.setNegativeButton(R.string.no, null);
							confirmationDialog.show();
						}
					});

					addShareDialog.show();
				}
			}
		});

		Button btnSearch = (Button) findViewById(R.id.asSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(MainActivity.isOnline(context)){
					String query = tvQuery.getText().toString();
					results = YahooCalls.searchCompanyQuery(query);
					symbols  = new ArrayList<String>(results.keySet());
					lvSearch.setAdapter(new SearchResultsAdapter(context));
				}
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
