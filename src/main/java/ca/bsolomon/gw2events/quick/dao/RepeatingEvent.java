package ca.bsolomon.gw2events.quick.dao;

import org.joda.time.DateTime;

public class RepeatingEvent {

	private final String eventId;
	
	private DateTime lastActiveTime;
	private DateTime lastSuccFailTime;
	
	private boolean lastActive = false;
	private boolean lastFailSucc = false;
	
	private int dataCountActive = 0;
	private int dataCountInactive = 0;
	
	private long activePeriodSum = 0;
	private long inactivePeriodSum = 0;
	
	public RepeatingEvent(String eventId) {
		this.eventId = eventId;
	}

	public DateTime getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(DateTime lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	public DateTime getLastSuccFailTime() {
		return lastSuccFailTime;
	}

	public void setLastSuccFailTime(DateTime lastSuccFailTime) {
		this.lastSuccFailTime = lastSuccFailTime;
	}

	public boolean isLastActive() {
		return lastActive;
	}

	public void setLastActive(boolean lastActive) {
		this.lastActive = lastActive;
	}

	public long getActivePeriodSum() {
		return activePeriodSum;
	}

	public void setActivePeriodSum(long activePeriodSum) {
		this.activePeriodSum = activePeriodSum;
	}

	public long getInactivePeriodSum() {
		return inactivePeriodSum;
	}

	public void setInactivePeriodSum(long inactivePeriodSum) {
		this.inactivePeriodSum = inactivePeriodSum;
	}

	public String getEventId() {
		return eventId;
	}

	public boolean isLastFailSucc() {
		return lastFailSucc;
	}

	public void setLastFailSucc(boolean lastFailSucc) {
		this.lastFailSucc = lastFailSucc;
	}

	public int getDataCountActive() {
		return dataCountActive;
	}

	public void setDataCountActive(int dataCountActive) {
		this.dataCountActive = dataCountActive;
	}

	public int getDataCountInactive() {
		return dataCountInactive;
	}

	public void setDataCountInactive(int dataCountInactive) {
		this.dataCountInactive = dataCountInactive;
	}
}
