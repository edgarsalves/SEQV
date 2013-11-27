package pt.up.fe.cmov.seqv;

import java.util.StringTokenizer;

public class QuoteEvolution {
	String date;
	Double open;
	Double high;
	Double low;
	Double close;
	Double volume;
	Double adjClose;
	
	public QuoteEvolution(String line) {
		StringTokenizer stElement = new StringTokenizer(line, ",");
		
		date = stElement.nextElement().toString();
		open= Double.parseDouble(stElement.nextElement().toString());
		high= Double.parseDouble(stElement.nextElement().toString());
		low= Double.parseDouble(stElement.nextElement().toString());
		close= Double.parseDouble(stElement.nextElement().toString());
		volume= Double.parseDouble(stElement.nextElement().toString());
		adjClose= Double.parseDouble(stElement.nextElement().toString());
	}

	
}
