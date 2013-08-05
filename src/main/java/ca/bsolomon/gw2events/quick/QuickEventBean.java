package ca.bsolomon.gw2events.quick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.joda.time.Duration;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2events.quick.dao.RepeatingEvent;
import ca.bsolomon.gw2events.quick.util.EventData;

@ManagedBean(name="quickEventBean")
@ViewScoped
public class QuickEventBean {
	
	@ManagedProperty(value="#{userValues}")
	private UserValuesBean userValues;
	
	List<EventData> shortPeriodEvents;
	
	public List<EventData> getQuickEvents() {
		return shortPeriodEvents;
	}
	
	public EventData getEvent() {
		 if (userValues.getEventId() != null && userValues.getEventId().length() > 0) {
			RepeatingEvent repEvent = QuickEventJob.events.get(userValues.getEventId());
				
			if (repEvent.getDataCountInactive() != 0) {
				long avgDurationBetween = repEvent.getInactivePeriodSum()/repEvent.getDataCountInactive();
				
				String mapId = QuickEventJob.eventDetails.get(userValues.getEventId()).getMapId();
				
				return new EventData(userValues.getEventId(), GW2EventsAPI.eventIdToName.get(userValues.getEventId()), GW2EventsAPI.mapIdToName.get(mapId), 
						new Duration(avgDurationBetween));
			} else {
				String mapId = QuickEventJob.eventDetails.get(userValues.getEventId()).getMapId();
				
				return new EventData(userValues.getEventId(), GW2EventsAPI.eventIdToName.get(userValues.getEventId()), GW2EventsAPI.mapIdToName.get(mapId), 
						new Duration(0));
			}
		 } else {
			 return new EventData("", "", "", new Duration(0));
		 }
	 }
	
	@PostConstruct
	public void queryData() {
		shortPeriodEvents = new ArrayList<>();
		
		for (String eventId: QuickEventJob.events.keySet()) {
			RepeatingEvent repEvent = QuickEventJob.events.get(eventId);
			
			if (userValues.getLowLevelBound() <= QuickEventJob.eventDetails.get(eventId).getLevel() &&
					QuickEventJob.eventDetails.get(eventId).getLevel() <= userValues.getHighLevelBound()  && repEvent.getDataCountInactive() != 0) {
				long avgDurationBetween = repEvent.getInactivePeriodSum()/repEvent.getDataCountInactive();
				
				String mapId = QuickEventJob.eventDetails.get(eventId).getMapId();
				
				EventData event = new EventData(eventId, GW2EventsAPI.eventIdToName.get(eventId), GW2EventsAPI.mapIdToName.get(mapId), 
						new Duration(avgDurationBetween));
				
				shortPeriodEvents.add(event);
			}
		}
		
		Collections.sort(shortPeriodEvents);
		
		shortPeriodEvents = shortPeriodEvents.subList(0, Math.min(50, shortPeriodEvents.size()));
	}

	public UserValuesBean getUserValues() {
		return userValues;
	}

	public void setUserValues(UserValuesBean userValues) {
		this.userValues = userValues;
		userValues.setEventBean(this);
	}

	public void reloadData() {
		List<EventData> events = new ArrayList<>();
		
		for (String eventId: QuickEventJob.events.keySet()) {
			RepeatingEvent repEvent = QuickEventJob.events.get(eventId);
			
			if (userValues.getLowLevelBound() <= QuickEventJob.eventDetails.get(eventId).getLevel() &&
					QuickEventJob.eventDetails.get(eventId).getLevel() <= userValues.getHighLevelBound()  && repEvent.getDataCountInactive() != 0) {
				long avgDurationBetween = repEvent.getInactivePeriodSum()/repEvent.getDataCountInactive();
				
				String mapId = QuickEventJob.eventDetails.get(eventId).getMapId();
				
				EventData event = new EventData(eventId, GW2EventsAPI.eventIdToName.get(eventId), GW2EventsAPI.mapIdToName.get(mapId), 
						new Duration(avgDurationBetween));
				
				events.add(event);
			}
		}
		
		Collections.sort(events);
		
		shortPeriodEvents = events.subList(0, Math.min(50, events.size()));
	}
}
