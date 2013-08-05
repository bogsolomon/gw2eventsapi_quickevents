package ca.bsolomon.gw2events.quick.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

@FacesConverter(value="durationConverter")
public class DurationConverter implements Converter{

	private static PeriodFormatter formatter = new PeriodFormatterBuilder()
			.appendHours()
			.appendSuffix("h")
			.appendMinutes()
			.appendSuffix("m")
			.appendSeconds()
			.appendSuffix("s")
			.toFormatter();
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		return new Duration(formatter.parsePeriod(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return formatter.print(((Duration)value).toPeriod());
	}

}
