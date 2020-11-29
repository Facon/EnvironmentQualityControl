package es.deusto.redes.data.dao;

import java.util.Calendar;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable(detachable="true")
public class Measure {
	private Date date = Calendar.getInstance().getTime();
	private String coordinate = null;
	private int value = 0;
//	@Persistent(defaultFetchGroup="true")
//	private Sensor sensor = null;
	
	public Measure(Date date, String coordinates, int value) {
		super();
		this.date = date;
		this.coordinate = coordinates;
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

//	public Sensor getSensor() {
//		return sensor;
//	}
//
//	public void setSensor(Sensor sensor) {
//		this.sensor = sensor;
//	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
