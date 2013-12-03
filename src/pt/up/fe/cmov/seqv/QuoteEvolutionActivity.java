package pt.up.fe.cmov.seqv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;

public class QuoteEvolutionActivity extends Activity {
	private Context context = this;
	private Calendar cal;
	private String year;
	private String month;
	private String day;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_quote_evolution);
		
		cal = Calendar.getInstance();
		year = Integer.toString( cal.get(Calendar.YEAR) );
		month = Integer.toString( cal.get(Calendar.MONTH) );
		day = Integer.toString( cal.get(Calendar.DAY_OF_MONTH) );

		Button btn = (Button) findViewById(R.id.time_frame); 
		btn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				final Dialog timeFrameDialog = new Dialog(context);
				timeFrameDialog.setContentView(R.layout.dialog_time_frame);
				timeFrameDialog.setTitle("Time Frame");
				
				final Button last_year = (Button) timeFrameDialog.findViewById(R.id.last_year);
				final Button last_months = (Button) timeFrameDialog.findViewById(R.id.last_months);
				final Button last_month = (Button) timeFrameDialog.findViewById(R.id.last_month);
				final Button last_week = (Button) timeFrameDialog.findViewById(R.id.last_week);

				//Last Year
				last_year.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						int year_bef = cal.get(Calendar.YEAR)-1;
						
						build_graph(month, day, Integer.toString(year_bef), month, day, year, "m");
						timeFrameDialog.dismiss();
					}	
				});
				//Last Months
				last_months.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						int month_bef = cal.get(Calendar.MONTH) - 3;
						int year_bef = cal.get(Calendar.YEAR);
						if(month_bef <= 0){
							year_bef--;
							month_bef = 12 + month_bef;
						}
							
						
						build_graph(Integer.toString(month_bef), "1", Integer.toString(year_bef), month, "31", year, "w");
						timeFrameDialog.dismiss();
					}	
				});
				//Last Month
				last_month.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						int month_bef = cal.get(Calendar.MONTH)-1;
						int year_bef = cal.get(Calendar.YEAR);
						if(month_bef <= 0){
							year_bef--;
							month_bef = 12 + month_bef;
						}
						
						build_graph(Integer.toString(month_bef), "1", Integer.toString(year_bef), month, "31", year, "d");
						timeFrameDialog.dismiss();
					}	
				});
				//Last Week
				last_week.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Calendar last_week_calendar = Calendar.getInstance();
						last_week_calendar.add(Calendar.DAY_OF_YEAR, -7);
						
						build_graph(Integer.toString( last_week_calendar.get(Calendar.MONTH) ), 
								Integer.toString( last_week_calendar.get(Calendar.DAY_OF_MONTH) ), 
								Integer.toString( last_week_calendar.get(Calendar.YEAR) ), 
								month, day, year, "d");
						
						timeFrameDialog.dismiss();
					}	
				});
				
				
				timeFrameDialog.show();
			}
		});

		int year_bef = cal.get(Calendar.YEAR)-1;
		build_graph(month, day, Integer.toString(year_bef), month, day, year, "m");
	}
	
	public void build_graph(String a, String b, String c, String d, String e, String f, String g){
		Double max = 0.0;
		
		System.out.println(c);
		
		//a – initial month 
		//b – initial day 
		//c – initial year 
		//d – final month 
		//e – final day 
		//f – final year 
		//g – periodicity (d - daily; w - weekly; m - monthly)
		String s = "MSFT";
		ArrayList<QuoteEvolution> ret = YahooCalls.getQuoteEvolution(a, b, c, d, e, f, g, s);
		
		int num_columns = ret.size();
		GraphViewData[] graph_data = new GraphViewData[ num_columns ];
		
		String[] labels = new String[num_columns];

		int iteration = 0;
		for(int i=ret.size()-1 ; i >=0 ; i--){
			QuoteEvolution quote = ret.get(i);
			//quote_calendar.setTime( new Date() )
			try {
				Date date = new SimpleDateFormat("yyyy-M-d", Locale.ENGLISH).parse( quote.date );
				
				Calendar quote_calendar = Calendar.getInstance();
				quote_calendar.setTime(date);
				
				//Add Data to graph
				int column = g == "m" ? quote_calendar.get( Calendar.MONTH )+1 : (g == "w" ? quote_calendar.get( Calendar.WEEK_OF_MONTH ) : quote_calendar.get( Calendar.DAY_OF_MONTH ) );
				graph_data[iteration] = new GraphViewData(num_columns-iteration, quote.close);
				
				if( g == "m"){
					String year_str = Integer.toString(quote_calendar.get( Calendar.YEAR ));
					String year_short = year_str.substring(2);
					labels[iteration++] = column+"-"+ year_short;
				}
				else {
					labels[iteration++] = column+"-"+ (quote_calendar.get( Calendar.MONTH )+1);
				}

				Log.i("debugger", "Date "+date.toString());
				Log.i("debugger", "Column "+column);
				
				if(max < quote.close){
					max = quote.close;
				}
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		//TODO
		GraphViewDataInterface[] gvData = graph_data;
		//TODO
		
		GraphViewSeries exampleSeries = new GraphViewSeries(gvData);  
		GraphView graphView = new LineGraphView(this, MyPortfolioActivity.symbol);  
		graphView.addSeries(exampleSeries); 

		graphView.setHorizontalLabels( labels );  

		graphView.getGraphViewStyle().setGridColor(Color.BLACK);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setVerticalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.text_size));
		graphView.getGraphViewStyle().setNumHorizontalLabels( num_columns );
		graphView.getGraphViewStyle().setNumVerticalLabels( graph_data.length );
		graphView.setScrollable(true);
		graphView.setScalable(true);
		//graphView.getGraphViewStyle().setVerticalLabelsWidth(20);

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph_holder);  
		layout.removeAllViews();
		layout.addView(graphView);
	}
}
