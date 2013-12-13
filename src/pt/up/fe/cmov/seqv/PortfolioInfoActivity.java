package pt.up.fe.cmov.seqv;

import java.util.ArrayList;
import java.util.Calendar;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PortfolioInfoActivity extends Activity {
	private Context context = this;
	LinearLayout layout;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portfolio_info);

		layout = (LinearLayout) findViewById(R.id.graph_holder_info); 

		final Dialog optionsDialog = new Dialog(context);
		optionsDialog.setContentView(R.layout.dialog_portfolio_info_options);
		optionsDialog.setTitle("Options");
		optionsDialog.setCanceledOnTouchOutside(false);


		Button confirm_btn = (Button) optionsDialog.findViewById(R.id.confirm_option); 
		confirm_btn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				RadioGroup rg = (RadioGroup) optionsDialog.findViewById(R.id.info_options_radio_group);

				int id = rg.getCheckedRadioButtonId();
				if (id == -1){
					//no item selected
				}
				else{
					//Portfolio Value
					if (id == R.id.radio0){
						build_portfolio_value();
					}
					//Stocks by value
					else if(id == R.id.radio1){
						build_portfolio_stocks_value();
					}
					//Stocks by number
					else{
						build_portfolio_stocks_number();
					}
				}

				optionsDialog.dismiss();
			}
		});

		Button btn = (Button) findViewById(R.id.options_button); 
		btn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				optionsDialog.show();
			}
		});

		build_portfolio_value();
	}

	private void build_portfolio_value(){
		Calendar cal = Calendar.getInstance();
		String year = Integer.toString( cal.get(Calendar.YEAR) );
		String month = Integer.toString( cal.get(Calendar.MONTH) );
		String day = Integer.toString( cal.get(Calendar.DAY_OF_MONTH) );

		int month_bef = cal.get(Calendar.MONTH)-1;
		int year_bef = cal.get(Calendar.YEAR);

		if(month_bef < 0){
			year_bef--;
			month_bef = 11 + month_bef;
		}

		try{
			build_graph(context.getString(R.string.last_month), Integer.toString(month_bef), day, Integer.toString(year_bef), month, day, year, "d");
		} catch (Exception e) {
			Toast.makeText(context, "Could not make the graph!", Toast.LENGTH_SHORT).show();
		}
	}

	private void build_portfolio_stocks_value(){

		//All company symbols
		ArrayList<String> symbols = new ArrayList<String>(MainActivity.myPortfolio.keySet());

		ArrayList<Quote> quotes = YahooCalls.getQuotes( symbols );

		int num_columns = symbols.size() + 1;
		GraphViewData[] graph_data = new GraphViewData[ num_columns ];
		String[] labels = new String[ num_columns ];

		double max = 0;

		//For each company that the user has action in
		for(int k=0 ; k < symbols.size() ; k++){
			if(k >= quotes.size())
				break;

			Quote quote = quotes.get(k);

			//Company 
			String key = symbols.get(k);

			double value = quote.price * MainActivity.myPortfolio.get(key);
			if(value > max)
				max = value;

			//Add Data to graph
			graph_data[k] = new GraphViewData(k, value);

			//Label
			labels[k] = quote.symbol+"";
		}

		labels[num_columns-1] = "";
		graph_data[num_columns-1] = new GraphViewData(num_columns-1, 0.0);;

		GraphViewDataInterface[] gvData = graph_data;

		GraphViewSeries exampleSeries = new GraphViewSeries(gvData);  
		BarGraphView graphView = new MyBarGraphView(this, "Portfolio - Shares by value");  
		graphView.addSeries(exampleSeries); 

		graphView.setHorizontalLabels(labels);  

		graphView.getGraphViewStyle().setGridColor(Color.BLACK);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setVerticalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.text_size));
		graphView.getGraphViewStyle().setNumHorizontalLabels( num_columns );
		graphView.getGraphViewStyle().setNumVerticalLabels( graph_data.length );

		layout.removeAllViews();
		layout.addView(graphView);
	}

	private void build_portfolio_stocks_number(){
		//All company symbols
		ArrayList<String> symbols = new ArrayList<String>(MainActivity.myPortfolio.keySet());

		ArrayList<Quote> quotes = YahooCalls.getQuotes( symbols );

		int num_columns = symbols.size() + 1;
		GraphViewData[] graph_data = new GraphViewData[ num_columns ];
		String[] labels = new String[ num_columns ];

		//For each company that the user has action in
		for(int k=0 ; k < symbols.size() ; k++){
			if(k >= quotes.size())
				break;

			Quote quote = quotes.get(k);

			//Company 
			String key = symbols.get(k);

			//Add Data to graph
			graph_data[k] = new GraphViewData(k, MainActivity.myPortfolio.get(key));

			//Label
			labels[k] = quote.symbol+"";
		}

		labels[num_columns-1] = "";
		graph_data[num_columns-1] = new GraphViewData(num_columns-1, 0.0);;

		GraphViewDataInterface[] gvData = graph_data;

		GraphViewSeries exampleSeries = new GraphViewSeries(gvData);  
		BarGraphView graphView = new MyBarGraphView(this, "Portfolio - Shares by value");  
		graphView.addSeries(exampleSeries); 

		graphView.setHorizontalLabels( labels );  

		graphView.getGraphViewStyle().setGridColor(Color.BLACK);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setVerticalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.text_size));
		graphView.getGraphViewStyle().setNumHorizontalLabels( num_columns );
		graphView.getGraphViewStyle().setNumVerticalLabels( graph_data.length );

		layout.removeAllViews();
		layout.addView(graphView);
	}

	private void build_graph(String type, String a, String b, String c, String d, String e, String f, String g){ 
		//All company symbols
		ArrayList<String> symbols = new ArrayList<String>(MainActivity.myPortfolio.keySet());

		GraphViewData[] graph_data = null;
		String[] labels = null;
		int num_columns = 0;

		//For each company that the user has action in
		for(int k=0 ; k < symbols.size() ; k++){
			//Company 
			String key = symbols.get(k);

			//Get info from yahoo
			ArrayList<QuoteEvolution> ret = YahooCalls.getQuoteEvolution(a, b, c, d, e, f, g, key);

			//Initial Sets
			if( k == 0){
				num_columns = ret.size();
				graph_data = new GraphViewData[ num_columns ];
				labels = new String[num_columns];
			}

			int iteration = 0;
			for(int i=ret.size()-1 ; i >=0 ; i--){
				QuoteEvolution quote = ret.get(i);

				Calendar quote_calendar = Calendar.getInstance();
				quote_calendar.setTime(quote.date);

				double value = quote.close * MainActivity.myPortfolio.get(key);

				if(k > 0)
					value += graph_data[iteration].getY();

				//Add Data to graph
				graph_data[iteration] = new GraphViewData(num_columns-iteration, value);

				labels[iteration++] = quote_calendar.get( Calendar.DAY_OF_MONTH )+"";
			}
		}
		TextView infoText = (TextView) findViewById(R.id.infoText);
		infoText.setText( (Math.round(graph_data[ graph_data.length-1 ].getY() * 100)/100.0) + "" );

		GraphViewDataInterface[] gvData = graph_data;

		GraphViewSeries exampleSeries = new GraphViewSeries(gvData);  
		GraphView graphView = new LineGraphView(this, "Portfolio - " + type);  
		graphView.addSeries(exampleSeries); 

		graphView.setHorizontalLabels( labels );  

		graphView.getGraphViewStyle().setGridColor(Color.BLACK);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setVerticalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.text_size));
		graphView.getGraphViewStyle().setNumHorizontalLabels( num_columns );
		graphView.getGraphViewStyle().setNumVerticalLabels( graph_data.length );

		layout.removeAllViews();
		layout.addView(graphView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.portfolio_info, menu);
		return false;
	}


	class MyBarGraphView extends BarGraphView{

		public MyBarGraphView(Context context, String title) {
			super(context, title);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void drawSeries(Canvas canvas, GraphViewDataInterface[] values, float graphwidth, float graphheight,
				float border, double minX, double minY, double diffX, double diffY,
				float horstart, GraphViewSeriesStyle style) {
			float colwidth = graphwidth / (values.length);

			paint.setStrokeWidth(style.thickness);

			float offset = 0;

			// draw data
			for (int i = 0; i < values.length; i++) {
				float valY = (float) (values[i].getY() - minY);
				float ratY = (float) (valY / diffY);
				float y = graphheight * ratY;

				// hook for value dependent color
				if (style.getValueDependentColor() != null) {
					paint.setColor(style.getValueDependentColor().get(values[i]));
				} else {
					paint.setColor(style.color);
				}

				float left = (i * colwidth) + horstart -offset;
				float top = (border - y) + graphheight;
				float right = ((i * colwidth) + horstart) + (colwidth - 1) -offset;
				canvas.drawRect(left, top, right, graphheight + border - 1, paint);

				// -----Set values on top of graph---------
				if (true && values[i].getY()!=0) {
					top -= 4;
					if (top<=border) top+=border+4;
					paint.setTextAlign(Align.CENTER);
					paint.setColor(getResources().getColor(R.color.orange0));
					canvas.drawText(formatLabel(values[i].getY(), false), (left+right)/2, top, paint);
				}
			}
		}
	}
}
