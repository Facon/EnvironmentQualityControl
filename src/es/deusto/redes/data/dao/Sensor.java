package es.deusto.redes.data.dao;

import java.util.Iterator;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable="true")
public class Sensor {
	private String description = null;
	private boolean state = false;
	@Persistent(defaultFetchGroup="true")
	private Set<Measure> measures = null;
//	@Persistent(defaultFetchGroup="true")
//	private Set<Vehicle> vehicles = null;
	
	public Sensor(String description, boolean state, Set<Measure> measures) {
		super();
		this.description = description;
		this.state = state;
		this.measures = measures;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}
	
	public Set<Measure> getMeasures() {
		return measures;
	}
	
	public Measure getLastMeasure() {
		Iterator <Measure> iter = measures.iterator();
		Measure measure = null;
		
		while (iter.hasNext()) {
			measure = iter.next();
		}
		
		return measure;
	}

	public void setMeasures(Set<Measure> measures) {
		this.measures = measures;
	}
	
	public void addMeasure(Measure measure) {
		if (measures != null) {
			measures.add(measure);
		}
	}

//	public Set<Vehicle> getVehicles() {
//		return vehicles;
//	}
//
//	public void setVehicles(Set<Vehicle> vehicles) {
//		this.vehicles = vehicles;
//	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
