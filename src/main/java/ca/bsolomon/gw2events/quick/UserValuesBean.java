package ca.bsolomon.gw2events.quick;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.omnifaces.util.Ajax;
import org.primefaces.component.datatable.DataTable;

@ManagedBean(name="userValues")
@SessionScoped
public class UserValuesBean {

	private QuickEventBean eventBean;
	
	private int lowLevelBound = 0;
    private int highLevelBound = 80;
    private String eventId="";
    
	
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
		eventBean.reloadData();
	}
	
	public void updateEvents(DataTable table) {  
		eventBean.reloadData();
		Ajax.update(table.getClientId());
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public QuickEventBean getEventBean() {
		return eventBean;
	}

	public void setEventBean(QuickEventBean eventBean) {
		this.eventBean = eventBean;
	}
}
