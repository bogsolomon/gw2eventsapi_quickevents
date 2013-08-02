package ca.bsolomon.gw2events.quick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2events.quick.dao.RepeatingEvent;
import ca.bsolomon.gw2events.quick.util.EventData;


@ManagedBean(name="quickEventBean")
@SessionScoped
public class QuickEventBean {
	
	private int lowLevelBound = 0;
    private int highLevelBound = 80;
    
    private GW2EventsAPI api = new GW2EventsAPI();

	public List<EventData> getQuickEvents() {
		List<EventData> shortPeriodEvents = new ArrayList<>();
		
		for (String eventId: QuickEventJob.events.keySet()) {
			RepeatingEvent repEvent = QuickEventJob.events.get(eventId);
			
			if (lowLevelBound <= QuickEventJob.eventDetails.get(eventId).getLevel() &&
					QuickEventJob.eventDetails.get(eventId).getLevel() <= highLevelBound  && repEvent.getDataCountInactive() != 0) {
				long avgDurationBetween = repEvent.getInactivePeriodSum()/repEvent.getDataCountInactive();
				
				String mapId = QuickEventJob.eventDetails.get(eventId).getMapId();
				
				EventData event = new EventData(eventId, GW2EventsAPI.eventIdToName.get(eventId), GW2EventsAPI.mapIdToName.get(mapId), 
						new Duration(avgDurationBetween));
				
				shortPeriodEvents.add(event);
			}
		}
		
		Collections.sort(shortPeriodEvents);
		
		return shortPeriodEvents.subList(0, Math.min(25, shortPeriodEvents.size()));
	}
	
	public List<EventData> getSoonEvents() {
		List<EventData> events = new ArrayList<>();
		
		DateTime now = new DateTime();
		
		for (String eventId: QuickEventJob.events.keySet()) {
			RepeatingEvent repEvent = QuickEventJob.events.get(eventId);
			
			if (lowLevelBound <= QuickEventJob.eventDetails.get(eventId).getLevel() &&
					QuickEventJob.eventDetails.get(eventId).getLevel() <= highLevelBound) {
				
				if (repEvent.isLastFailSucc() && repEvent.getDataCountInactive() != 0) {
					long avgDurationBetween = repEvent.getInactivePeriodSum()/repEvent.getDataCountInactive();
					
					DateTime lastEnded = repEvent.getLastSuccFailTime();
					
					Duration duration = new Duration(lastEnded, now);
					
					long timeTo = avgDurationBetween - duration.getMillis();
					
					String mapId = QuickEventJob.eventDetails.get(eventId).getMapId();
					
					EventData event = new EventData(eventId, GW2EventsAPI.eventIdToName.get(eventId), GW2EventsAPI.mapIdToName.get(mapId), 
							new Duration(timeTo));
					
					events.add(event);
				}
			}
		}
		
		Collections.sort(events);
		
		return events.subList(0, Math.min(25, events.size()));
	}
	
	public int getLowLevelBound() {
		return lowLevelBound;
	}

	public void setLowLevelBound(int lowLevelBound) {
		this.lowLevelBound = lowLevelBound;
	}

	public int getHighLevelBound() {
		return highLevelBound;
	}

	public void setHighLevelBound(int highLevelBound) {
		this.highLevelBound = highLevelBound;
	}
	
	public void handleLevelRangeChange() {  
		
	}
}
