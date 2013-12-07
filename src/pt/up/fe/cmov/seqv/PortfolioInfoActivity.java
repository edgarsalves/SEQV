package pt.up.fe.cmov.seqv;

import java.util.ArrayList;
import java.util.Calendar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PortfolioInfoActivity extends Activity {
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portfolio_info);

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
	
	public void build_graph(String type, String a, String b, String c, String d, String e, String f, String g){
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
		infoText.setText( graph_data[ graph_data.length-1 ].getY() + "" );

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

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph_holder_info);  
		layout.removeAllViews();
		layout.addView(graphView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.portfolio_info, menu);
		return false;
	}

}
