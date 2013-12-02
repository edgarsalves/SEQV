package pt.up.fe.cmov.seqv;

import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.app.Activity;
import android.graphics.Color;

public class QuoteEvolutionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quote_evolution);
		Double max = 0.0;
		
		
		//TODO
		GraphViewDataInterface[] gvData = new GraphViewData[] {  
				new GraphViewData(1, 40)  
				, new GraphViewData(2, 12)  
				, new GraphViewData(3, 7)
				, new GraphViewData(4, 8)
				, new GraphViewData(5, 10)
				, new GraphViewData(6, 26)
				, new GraphViewData(7, 37)
				, new GraphViewData(8, 53)
		};
		max = 53.0;
		//TODO


		GraphViewSeries exampleSeries = new GraphViewSeries(gvData);  
		GraphView graphView = new LineGraphView(this, MyPortfolioActivity.symbol);  
		graphView.addSeries(exampleSeries); 

		graphView.getGraphViewStyle().setGridColor(Color.BLACK);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setVerticalLabelsColor(getResources().getColor(R.color.orange0));
		graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.text_size));
		graphView.getGraphViewStyle().setNumHorizontalLabels(gvData.length);
		graphView.getGraphViewStyle().setNumVerticalLabels((int) Math.round(max/gvData.length));
		graphView.setScrollable(true);
		graphView.setScalable(true);
		//graphView.getGraphViewStyle().setVerticalLabelsWidth(20);

		LinearLayout layout = (LinearLayout) findViewById(R.id.qeLayout);  
		layout.addView(graphView);  
	}
}
