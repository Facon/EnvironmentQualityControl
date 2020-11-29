package es.deusto.redes.data.dao;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable="true")
public class Cell {
	@PrimaryKey
	private int cell_id = 0;
	private int size = 1;
	private String coordinate = null;
	
	public Cell(int cell_id, int size, String coordinate) {
		super();
		this.cell_id = cell_id;
		this.size = size;
		this.coordinate = coordinate;
	}

	public int getCell_id() {
		return cell_id;
	}

	public void setCell_id(int cell_id) {
		this.cell_id = cell_id;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
