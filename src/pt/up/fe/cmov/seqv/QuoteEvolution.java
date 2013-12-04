package pt.up.fe.cmov.seqv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class QuoteEvolution {
	Date date;
	Double open;
	Double high;
	Double low;
	Double close;
	Double volume;
	Double adjClose;
	
	public QuoteEvolution(String line) {
		StringTokenizer stElement = new StringTokenizer(line, ",");
		
		try {
			String dateStr = stElement.nextElement().toString();
			date = new SimpleDateFormat("yyyy-M-d", Locale.ENGLISH).parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		open= Double.parseDouble(stElement.nextElement().toString());
		high= Double.parseDouble(stElement.nextElement().toString());
		low= Double.parseDouble(stElement.nextElement().toString());
		close= Double.parseDouble(stElement.nextElement().toString());
		volume= Double.parseDouble(stElement.nextElement().toString());
		adjClose= Double.parseDouble(stElement.nextElement().toString());
	}

	
}
