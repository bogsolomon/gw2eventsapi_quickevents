package ca.bsolomon.gw2events.quick;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
	
	DateTimeZone zone = DateTimeZone.forID("America/New_York");
	Chronology gregorianJuian = GJChronology.getInstance(zone);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		if (GW2EventsAPI.eventIdToName.size() == 0) {
			GW2EventsAPI.generateEventIds();
		}
		
		getServerData();
		
		computeQuickestEvents();
	}

	private void computeQuickestEvents() {
		for (String eventId:lastActive.keySet()) {
			if (lastSuccess.containsKey(eventId)) {
				
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
