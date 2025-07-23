package co.za.kasi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingRecurrence implements Serializable {
	
	private String period;
	
	private String fromDate;
	
	private String toDate;

	public String getPeriod() {return period;}

	public void setPeriod(String period) {this.period = period;}

	public String getFromDate() {return fromDate;}

	public void setFromDate(String fromDate) {this.fromDate = fromDate;}

	public String getToDate() {return toDate;}

	public void setToDate(String toDate) {this.toDate = toDate;}
}
