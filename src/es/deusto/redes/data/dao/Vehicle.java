package es.deusto.redes.data.dao;

import java.util.Set;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@PersistenceCapable(detachable="true")
public class Vehicle {
	private boolean state_gps = false;
	private String location = null;
	@Element
	@Persistent(defaultFetchGroup="true")
	private byte[] photo = null;
	@Persistent(defaultFetchGroup="true")
	private Cell cell = null;
	@Persistent(defaultFetchGroup="true")
	private Set<Sensor> sensors = null;

	public Vehicle(boolean state_gps, String location, byte[] photo, Cell cell, Set<Sensor> sensors) {
		super();
		this.state_gps = state_gps;
		this.location = location;
		this.photo = photo;
		this.cell = cell;
		this.sensors = sensors;
	}
	
	public boolean isState_gps() {
		return state_gps;
	}

	public void setState_gps(boolean state_gps) {
		this.state_gps = state_gps;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public Set<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(Set<Sensor> sensors) {
		this.sensors = sensors;
	}
	
	public Sensor getSensor(int id) {
		return (Sensor) this.sensors.toArray()[id];
	}

	public static void main(String[] args) {
		System.out.println("Hola");

	}

}
