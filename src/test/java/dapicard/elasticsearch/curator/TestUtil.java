package dapicard.elasticsearch.curator;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

public class TestUtil {
	static PeriodFormatter p = PeriodFormat.wordBased(Locale.ENGLISH);

	/**
	 * 
	 * @param period
	 *          The JodaTime Period
	 * @return
	 */
	public static DateTime nowMinusPeriod(String period) {
		return new DateTime(new Instant().minus(p.parsePeriod(period).toStandardDuration()));
	}
}
