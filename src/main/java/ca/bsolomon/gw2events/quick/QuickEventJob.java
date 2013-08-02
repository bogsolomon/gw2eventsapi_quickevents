package ca.bsolomon.gw2events.quick;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.chrono.GJChronology;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2event.api.dao.Event;
import ca.bsolomon.gw2event.api.dao.EventDetails;
import ca.bsolomon.gw2events.quick.dao.RepeatingEvent;
import ca.bsolomon.gw2events.quick.util.EventStatusValues;

public class QuickEventJob implements Job {

	private static final String SOR_SERVID = "1013";
	private GW2EventsAPI api = new GW2EventsAPI();
	
	public static Map<String, EventDetails> eventDetails;
	public static Map<String, RepeatingEvent> events = new ConcurrentHashMap<>(2000, 0.9f, 1);
	
	DateTimeZone zone = DateTimeZone.forID("America/New_York");
	Chronology gregorianJuian = GJChronology.getInstance(zone);
		
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		if (GW2EventsAPI.eventIdToName.size() == 0) {
			GW2EventsAPI.generateEventIds();
			GW2EventsAPI.generateMapIds();
			
			eventDetails = api.queryEventDetails().getEvents().get(0);
		}
		
		getServerData();
	}

	private void getServerData() {
		List<Event> result = api.queryServerEventStatus(SOR_SERVID);
		
		DateTime now = new DateTime(gregorianJuian);
		
		for (int i=0; i<result.size(); i++) {
			Event obj = result.get(i);
			
			String status = obj.getState();
			String eventId = obj.getEventId();
			
			if (!events.containsKey(eventId)) {
				RepeatingEvent repEvent = new RepeatingEvent(eventId);
				
				events.put(eventId, repEvent);
			}
			
			RepeatingEvent repEvent = events.get(eventId);
			
			if (status.equals(EventStatusValues.ACTIVE.toString()) && !repEvent.isLastActive()
					&& repEvent.getLastSuccFailTime() != null) {
				DateTime lastEnd = repEvent.getLastSuccFailTime();
				
				repEvent.setLastActiveTime(now);
				repEvent.setLastFailSucc(false);
				repEvent.setLastActive(true);
				
				Duration duration = new Duration(lastEnd, now);
				long durationMilli = duration.getMillis();
				
				repEvent.setInactivePeriodSum(repEvent.getInactivePeriodSum()+durationMilli);
				repEvent.setDataCountInactive(repEvent.getDataCountInactive()+1);
			} else if (status.equals(EventStatusValues.FAIL.toString()) ||
					status.equals(EventStatusValues.SUCCESS.toString())  && !repEvent.isLastFailSucc()
					&& repEvent.getLastActiveTime() != null) {
				DateTime lastStart = repEvent.getLastActiveTime();
				
				repEvent.setLastSuccFailTime(now);
				repEvent.setLastFailSucc(true);
				repEvent.setLastActive(false);
				
				Duration duration = new Duration(lastStart, now);
				long durationMilli = duration.getMillis();
				
				repEvent.setActivePeriodSum(repEvent.getActivePeriodSum()+durationMilli);
				repEvent.setDataCountActive(repEvent.getDataCountActive()+1);
			}
		}
	}
}
