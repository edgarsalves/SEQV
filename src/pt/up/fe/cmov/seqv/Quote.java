package pt.up.fe.cmov.seqv;

import java.util.StringTokenizer;

public class Quote {
	String symbol;
	Double price;
	String date;
	String time;
	Double volume;

	public Quote(String line) {
		StringTokenizer stElement = new StringTokenizer(line, ",");
		
		symbol = stElement.nextElement().toString();
		symbol = symbol.substring(1,symbol.length()-1);
		
		price= Double.parseDouble(stElement.nextElement().toString());
		
		date = stElement.nextElement().toString();
		date = date.substring(1,date.length()-1);

		time = stElement.nextElement().toString();
		time = time.substring(1,time.length()-1);
		
		volume= Double.parseDouble(stElement.nextElement().toString());
	}

}
