package pt.up.fe.cmov.seqv;

import java.util.ArrayList;
import java.util.Calendar;

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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class ComparationActivity extends Activity {
	private Context context = this;
	
	private Calendar cal;
	private String year;
	private String month;
	private String day;

	private boolean noGraphFlag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comparation);
		
		//Today's date
		cal = Calendar.getInstance();
		year = Integer.toString( cal.get(Calendar.YEAR) );
		month = Integer.toString( cal.get(Calendar.MONTH) );
		day = Integer.toString( cal.get(Calendar.DAY_OF_MONTH) );
		
		final Dialog timeFrameDialog = new Dialog(context);
		timeFrameDialog.setContentView(R.layout.dialog_time_frame);
		timeFrameDialog.setTitle("Time Frame");
		timeFrameDialog.setCanceledOnTouchOutside(false);
		timeFrameDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if(noGraphFlag)
					finish();
			}
		});
		timeFrameDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				noGraphFlag = false;
			}
		});
		
		final Button last_year = (Button) timeFrameDialog.findViewById(R.id.last_year);
		final Button last_months = (Button) timeFrameDialog.findViewById(R.id.last_months);
		final Button last_month = (Button) timeFrameDialog.findViewById(R.id.last_month);
		final Button last_week = (Button) timeFrameDialog.findViewById(R.id.last_week);
		
		//Last Year
		last_year.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int year_bef = cal.get(Calendar.YEAR)-1;

				build_graph(context.getString(R.string.last_year), month, day, Integer.toString(year_bef), month, day, year, "m");
				timeFrameDialog.dismiss();
			}	
		});
		//Last Months
		last_months.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				int month_bef = cal.get(Calendar.MONTH) - 3;
				int year_bef = cal.get(Calendar.YEAR);

				if(month_bef < 0){
					year_bef--;
					month_bef = 11 + month_bef;
				}

				build_graph(context.getString(R.string.last_3months), Integer.toString(month_bef), "1", Integer.toString(year_bef), month, "31", year, "w");
				timeFrameDialog.dismiss();
			}	
		});
		//Last Month
		last_month.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				int month_bef = cal.get(Calendar.MONTH)-1;
				int year_bef = cal.get(Calendar.YEAR);

				if(month_bef < 0){
					year_bef--;
					month_bef = 11 + month_bef;
				}

				build_graph(context.getString(R.string.last_month), Integer.toString(month_bef), "1", Integer.toString(year_bef), month, "31", year, "d");
				timeFrameDialog.dismiss();
			}	
		});
		//Last Week
		last_week.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Calendar last_week_calendar = Calendar.getInstance();
				last_week_calendar.add(Calendar.DAY_OF_YEAR, -7);

				build_graph(context.getString(R.string.last_7days),
						Integer.toString( last_week_calendar.get(Calendar.MONTH) ), 
						Integer.toString( last_week_calendar.get(Calendar.DAY_OF_MONTH) ), 
						Integer.toString( last_week_calendar.get(Calendar.YEAR) ), 
						month, day, year, "d");

				timeFrameDialog.dismiss();
			}	
		});

		Button btn = (Button) findViewById(R.id.time_frame_comparation); 
		btn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				timeFrameDialog.show();
			}
		});

		timeFrameDialog.show();
	}
	
	public void build_graph(String type, String a, String b, String c, String d, String e, String f, String g){
		System.out.println(c);
		
		String symb1 = MyPortfolioActivity.symbols.get( MyPortfolioActivity.symbol1 );
		String symb2 = MyPortfolioActivity.symbols.get( MyPortfolioActivity.symbol2 );

		//First Company
		ArrayList<QuoteEvolution> evo1 = YahooCalls.getQuoteEvolution(a, b, c, d, e, f, g, symb1);
		
		//Second Company
		ArrayList<QuoteEvolution> evo2 = YahooCalls.getQuoteEvolution(a, b, c, d, e, f, g, symb2);

		int num_columns = evo1.size();
		int num_datas = evo1.size() + evo2.size();
		GraphViewData[] graph_data1 = new GraphViewData[ evo1.size() ];
		GraphViewData[] graph_data2 = new GraphViewData[ evo2.size() ];

		String[] labels = new String[num_columns];

		int iteration = 0;
		for(int i= evo1.size()-1 ; i >= 0 ; i--){
			//Get quote info
			QuoteEvolution quote = evo1.get(i);
			
			//Calendar
			Calendar quote_calendar = Calendar.getInstance();
			quote_calendar.setTime(quote.date);

			//Add Data to graph
			int column = g == "m" ? quote_calendar.get( Calendar.MONTH )+1 : (g == "w" ? quote_calendar.get( Calendar.WEEK_OF_MONTH ) : quote_calendar.get( Calendar.DAY_OF_MONTH ) );
			graph_data1[iteration] = new GraphViewData(num_columns-iteration, quote.close);

			if( g == "m"){
				labels[iteration++] = column+"";
			}
			else {
				labels[iteration++] = column+"/"+ (quote_calendar.get( Calendar.MONTH )+1);
			}
		}
		iteration = 0;
		for(int i= evo2.size()-1 ; i >= 0 ; i--){
			//Get quote info
			QuoteEvolution quote = evo2.get(i);
			
			//Calendar
			Calendar quote_calendar = Calendar.getInstance();
			quote_calendar.setTime(quote.date);

			//Add Data to graph
			graph_data2[iteration++] = new GraphViewData(num_columns-iteration, quote.close);
		}

		GraphViewDataInterface[] gvData1 = graph_data1;
		GraphViewDataInterface[] gvData2 = graph_data2;

		GraphViewSeries series1 = new GraphViewSeries(symb1, new GraphViewSeriesStyle(getResources().getColor(R.color.blue2), 1), gvData1);  
		GraphViewSeries series2 = new GraphViewSeries(symb2, new GraphViewSeriesStyle(getResources().getColor(R.color.orange0), 1), gvData2);  
		
		GraphView graphView = new LineGraphView(this, symb1 +" and "+ symb2 + " - " + type); 
		
		graphView.addSeries(series1); 
		graphView.addSeries(series2); 

		graphView.setHorizontalLabels( labels );  

		graphView.getGraphViewStyle().setGridColor(Color.BLACK);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setVerticalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.text_size));
		graphView.getGraphViewStyle().setNumHorizontalLabels( num_columns );
		graphView.getGraphViewStyle().setNumVerticalLabels( Math.max(graph_data1.length, graph_data2.length) );
		graphView.setShowLegend(true); 
		graphView.setScrollable(true);
		graphView.setScalable(true);
		//graphView.getGraphViewStyle().setVerticalLabelsWidth(20);

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph_holder_comparation);  
		layout.removeAllViews();
		layout.addView(graphView);
	}


}
