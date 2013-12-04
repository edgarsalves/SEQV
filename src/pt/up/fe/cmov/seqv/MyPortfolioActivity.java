package pt.up.fe.cmov.seqv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyPortfolioActivity extends Activity {
	private Context context = this;
	private ListView lvSearch;
	private ArrayList<String> symbols;
	private TextView tvNCompanys;

	private int counter;
	private int n;
	public static String symbol;
	public static String name;
	private Quote q;

	private Spinner s1, s2;
	private int symbol1, symbol2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_portfolio);

		symbols = new ArrayList<String>(MainActivity.myPortfolio.keySet());

		lvSearch = (ListView) findViewById(R.id.mpList);
		lvSearch.setAdapter(new MyPortfolioAdapter(context));

		lvSearch.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				if(MainActivity.isOnline(context)){
					symbol = symbols.get(position);
					name = YahooCalls.getCompanyName(symbol);
					counter = MainActivity.myPortfolio.get(symbol);
					q = YahooCalls.getQuotes(new ArrayList<String>(Arrays.asList(symbol))).get(0);

					final Dialog addShareDialog = new Dialog(context);
					addShareDialog.setContentView(R.layout.dialog_my_portfolio);
					addShareDialog.setTitle("Set " + symbol);
					addShareDialog.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							refreshLayout();
						}
					});

					final TextView tvSymbol = (TextView) addShareDialog.findViewById(R.id.dmpSymbol);
					tvSymbol.setText("Symbol: " + symbol);

					final TextView tvName = (TextView) addShareDialog.findViewById(R.id.dmpName);
					tvName.setText("Name: " + name);

					final TextView tvPrice = (TextView) addShareDialog.findViewById(R.id.dmpPrice);
					tvPrice.setText("Price: " + q.price);

					final ImageButton btnEvolution = (ImageButton) addShareDialog.findViewById(R.id.dmpEvolution);
					btnEvolution.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if(MainActivity.isOnline(context)){
								Intent i = new Intent(context, QuoteEvolutionActivity.class);
								startActivity(i);					
							}
						}
					});

					final TextView tvNumber = (TextView) addShareDialog.findViewById(R.id.dmpNumber);
					tvNumber.setText(Integer.toString(counter));

					final ImageButton btnMinus = (ImageButton) addShareDialog.findViewById(R.id.dmpMinus);
					btnMinus.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(counter>0){
								counter--;
								tvNumber.setText(Integer.toString(counter));
							}
						}
					});

					final ImageButton btnPlus = (ImageButton) addShareDialog.findViewById(R.id.dmpPlus);
					btnPlus.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							counter++;
							tvNumber.setText(Integer.toString(counter));
						}
					});

					final Button btnConfirm = (Button) addShareDialog.findViewById(R.id.dmpConfirm);
					btnConfirm.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							n = counter - MainActivity.myPortfolio.get(symbol);

							if(n==0)
								addShareDialog.dismiss();
							else{
								Builder confirmationDialog = new AlertDialog.Builder(context);
								confirmationDialog.setTitle("");
								confirmationDialog.setMessage("Do you really want to confirm these changes?");
								confirmationDialog.setIcon(android.R.drawable.ic_dialog_alert);
								confirmationDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										if(n!=0)
											MainActivity.updateMyPortfolio(symbol, n, context);
										Toast.makeText(context, "Changes saved!", Toast.LENGTH_SHORT).show();
										addShareDialog.dismiss();
									}});
								confirmationDialog.setNegativeButton(R.string.no, null);
								confirmationDialog.show();
							}
						}
					});

					addShareDialog.show();
				}
			}
		});

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

		Button btnCompare = (Button) findViewById(R.id.mpCompare);
		btnCompare.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(MainActivity.getNCompanys()>1){
					final Dialog compareDialog = new Dialog(context);
					compareDialog.setContentView(R.layout.dialog_compare);
					compareDialog.setTitle("Comparation");

					s1 = (Spinner) compareDialog.findViewById(R.id.dcCombobox1);
					s2 = (Spinner) compareDialog.findViewById(R.id.dcCombobox2);
					addItemsOnSpinners();
					setSpinnersListeners();

					Button btnCompare = (Button) compareDialog.findViewById(R.id.dcCompare);
					btnCompare.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent i = new Intent(context, ComparationActivity.class);
							startActivity(i);
							compareDialog.dismiss();
						}
					});
					compareDialog.show();
				}
			}
		});
	}

	private void setSpinnersListeners(){
		s1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String selected = parentView.getItemAtPosition(position).toString();

				if(selected.equals(symbols.get(symbol2))){
					Toast.makeText(context, "Already selected!", Toast.LENGTH_SHORT).show();
					@SuppressWarnings("unchecked")
					int spinnerPosition = ((ArrayAdapter<String>) s1.getAdapter()).getPosition(symbols.get(symbol1));
					s1.setSelection(spinnerPosition);
				}
				else
					symbol1 = symbols.indexOf(selected);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});

		s2.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String selected = parentView.getItemAtPosition(position).toString();

				if(selected.equals(symbols.get(symbol1))){
					Toast.makeText(context, "Already selected!", Toast.LENGTH_SHORT).show();
					@SuppressWarnings("unchecked")
					int spinnerPosition = ((ArrayAdapter<String>) s2.getAdapter()).getPosition(symbols.get(symbol2));
					s2.setSelection(spinnerPosition);
				}
				else
					symbol2 = symbols.indexOf(selected);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
	}

	private void addItemsOnSpinners() {
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < symbols.size(); i++){
			list.add(symbols.get(i));
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		s1.setAdapter(dataAdapter);
		symbol1 = 0;
		int s1pos = dataAdapter.getPosition(symbols.get(symbol1));
		s1.setSelection(s1pos);

		s2.setAdapter(dataAdapter);
		symbol2 = 1;
		int s2pos = dataAdapter.getPosition(symbols.get(symbol2));
		s2.setSelection(s2pos);
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
		refreshLayout();
		super.onResume();
	}

	private void refreshLayout(){
		symbols = new ArrayList<String>(MainActivity.myPortfolio.keySet());
		lvSearch.setAdapter(new MyPortfolioAdapter(context));
		tvNCompanys.setText("Companys in my portfolio: " + Integer.toString(MainActivity.getNCompanys()));
	}


}
