package ca.bsolomon.gw2events.quick;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.joda.time.Duration;

import ca.bsolomon.gw2event.api.GW2EventsAPI;
import ca.bsolomon.gw2events.quick.util.EventData;


@ManagedBean(name="quickEventBean")
@ViewScoped
public class QuickEventBean {
	
	private GW2EventsAPI api = new GW2EventsAPI();

	public List<EventData> getQuickEvents() {
		List<EventData> events = new ArrayList<>();
		
		int count = 0;
		
outer:	for (Duration d :QuickEventJob.shortPeriodEvents.keySet()) {
			for (String eventId:QuickEventJob.shortPeriodEvents.get(d)) {
				String eventName = GW2EventsAPI.eventIdToName.get(eventId);
				String mapName = api.getEventMap(eventId);
				
				EventData data = new EventData(eventId, eventName, mapName, d.toString());
				events.add(data);
	
				count++;
				
				if (count >= 25) {
					break outer;
				}
			}
		}
		
		return events;
	}
	
	public List<EventData> getSoonEvents() {
		List<EventData> events = new ArrayList<>();
		
		int count = 0;
		
outer:	for (Duration d :QuickEventJob.soonEvents.keySet()) {
			for (String eventId:QuickEventJob.soonEvents.get(d)) {
				String eventName = GW2EventsAPI.eventIdToName.get(eventId);
				String mapName = api.getEventMap(eventId);
				
				EventData data = new EventData(eventId, eventName, mapName, d.toString());
				events.add(data);
	
				count++;
				
				if (count >= 25) {
					break outer;
				}
			}
		}
		
		return events;
	}
}
