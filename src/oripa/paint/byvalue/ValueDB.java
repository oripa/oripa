package oripa.paint.byvalue;

import java.util.Observable;

public class ValueDB extends Observable{

	private double length = 0;
	private double angle = 0;
	
	private static ValueDB instance = null;
	
	private ValueDB(){}
	
	public static ValueDB getInstance(){
		if(instance == null){
			instance = new ValueDB();
		}
		
		return instance;
	}
	
	
	public void set(double length, double angle){
		this.length = length;
		this.angle = angle;

		this.setChanged();
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;

		this.setChanged();
	}

	/**
	 * 
	 * @return angle [degree]
	 */
	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;

		this.setChanged();
	}

	/**
	 * @return full-path class name
	 */
	@Override
	public String toString() {
		return this.getClass().getName();
	}
	
	
	
}
