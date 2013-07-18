package ca.bsolomon.gw2events.quick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.chrono.GJChronology;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ca.bsolomon.gw2event.api.GW2EventsAPI;

public class QuickEventJob implements Job {

	private static final String SOR_SERVID = "1013";
	private GW2EventsAPI api = new GW2EventsAPI();
	
	private Map<String, DateTime> lastSuccess = new HashMap<>();
	private Map<String, DateTime> lastActive = new HashMap<>();
	private Map<String, String> lastState = new HashMap<>();
	
	public static Map<Duration, List<String>> shortPeriodEvents = new HashMap<>();
	private Map<String, Duration> shortPeriodEventsByID = new HashMap<>();
	
	public static Map<Duration, List<String>> soonEvents = new HashMap<>();
	private Map<String, Duration> soonEventsByID = new HashMap<>();
	
	DateTimeZone zone = DateTimeZone.forID("America/New_York");
	Chronology gregorianJuian = GJChronology.getInstance(zone);
	
	private static Period MAX_TIME = new Period(0, 5, 0, 0);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		if (GW2EventsAPI.eventIdToName.size() == 0) {
			GW2EventsAPI.generateEventIds();
			GW2EventsAPI.generateMapIds();
		}
		
		getServerData();
		
		computeQuickestEvents();
		
		computeNextEvents();
	}

	private void computeNextEvents() {
		DateTime now = new DateTime(gregorianJuian);
		
		for (String eventId:lastActive.keySet()) {
			if (lastSuccess.containsKey(eventId)) {
				DateTime lastSuc = lastSuccess.get(eventId);
				
				Period per = new Period(lastSuc, lastActive.get(eventId));
				
				Duration duration = per.toDurationFrom(lastSuc);
				
				Period timePassed = new Period(lastSuc, now);
				
				Duration timePassedDuration = timePassed.toDurationFrom(lastSuc);
				
				Duration remaining = duration.minus(timePassedDuration);
				
				Duration maxDuration = MAX_TIME.toDurationFrom(lastSuc);
				
				if (remaining.isShorterThan(maxDuration)) {
					if (!soonEvents.containsKey(remaining)) {
						soonEvents.put(remaining, new ArrayList<String>());
					}
					if (!soonEvents.get(remaining).contains(eventId)) {
						Duration oldDuration = soonEventsByID.put(eventId, remaining);
						
						if (oldDuration != null) {
							shortPeriodEvents.get(oldDuration).remove(eventId);
						}
						
						shortPeriodEvents.get(remaining).add(eventId);
					}
				}
			}
		}
	}

	private void computeQuickestEvents() {
		for (String eventId:lastActive.keySet()) {
			if (lastSuccess.containsKey(eventId)) {
				DateTime lastSuc = lastSuccess.get(eventId);
				
				Period per = new Period(lastSuc, lastActive.get(eventId));
				
				Duration duration = per.toDurationFrom(lastSuc);
				Duration maxDuration = MAX_TIME.toDurationFrom(lastSuc);
				
				if (duration.isShorterThan(maxDuration)) {
					if (!shortPeriodEvents.containsKey(duration)) {
						shortPeriodEvents.put(duration, new ArrayList<String>());
					}
					if (!shortPeriodEvents.get(duration).contains(eventId)) {
						Duration oldDuration = shortPeriodEventsByID.put(eventId, duration);
						
						if (oldDuration != null) {
							shortPeriodEvents.get(oldDuration).remove(eventId);
						}
						
						shortPeriodEvents.get(duration).add(eventId);
					}
				}
			}
		}
	}

	private void getServerData() {
		JSONArray result = api.queryServerEventStatus(SOR_SERVID);
		
		DateTime date = new DateTime(gregorianJuian);
		
		for (int i=0; i<result.size(); i++) {
			JSONObject obj = result.getJSONObject(i);
			
			String status = obj.getString("state");
			String eventId = obj.getString("event_Id");
			
			if (!lastState.get(eventId).equals(status)) {
				if (status.equals("Active")) {
					lastActive.put(eventId, date);
				} else if (status.equals("Success") || status.equals("Fail")) {
					lastSuccess.put(eventId, date);
				}
			}
		}
	}

}
