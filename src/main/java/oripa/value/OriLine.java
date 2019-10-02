/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.value;

import javax.vecmath.Vector2d;

import oripa.geom.Line;
import oripa.geom.Segment;


public class OriLine implements Comparable<OriLine> {

	final public static int TYPE_NONE = 0;
    final public static int TYPE_CUT = 1;
    final public static int TYPE_RIDGE = 2;
    final public static int TYPE_VALLEY = 3;

    // currently switching to use enum...
    public enum Type{
		
		NONE(TYPE_NONE), CUT(TYPE_CUT), RIDGE(TYPE_RIDGE), VALLEY(TYPE_VALLEY);

		private final int val;
		
		Type(int val){
			this.val = val;
		}
		
		public int toInt(){
			return val;
		}
	
		public static Type fromInt(int val){
			Type type;
			switch(val){
			
			case TYPE_CUT:
				type = CUT;
				
				break;
				
			case TYPE_RIDGE:
				type = RIDGE;
				break;
					
			case TYPE_VALLEY:
				type = VALLEY;
				break;
				
			case TYPE_NONE:
			default:
				type = NONE;
				break;
				
			}
			
			return type;
		}
	};

	private Type type = Type.NONE;
	
    public boolean selected;
    public int typeVal = TYPE_NONE;  // eventually unneeded
    public final OriPoint p0 = new OriPoint();
    public final OriPoint p1 = new OriPoint();

    public OriLine() {
    }

    public void setTypeValue(int type) { // eventually unneeded
    	this.type = Type.fromInt(type);
        this.typeVal = type;
    }
    
    public void setType(Type type){
    	this.type = type;
    	this.typeVal = type.toInt(); // eventually unneeded
    }

    public int getTypeValue() { // eventually unneeded
        return typeVal;
    }

    public OriLine(OriLine l) {
        selected = l.selected;
        p0.set(l.p0);
        p1.set(l.p1);
        typeVal = l.typeVal;
    }

    public OriLine(Vector2d p0, Vector2d p1, int type) {
        this.typeVal = type;
        this.p0.set(p0);
        this.p1.set(p1);
    }

    public OriLine(double x0, double y0, double x1, double y1, int type) {
        this.typeVal = type;
        this.p0.set(x0, y0);
        this.p1.set(x1, y1);
    }

    @Override
    public String toString() {
        return "" + p0 + "" + p1;
    }

    public void changeToNextType() {
        switch (typeVal) {
            case 3:
                typeVal = 0;
                break;
            case 2:
                typeVal = 3;
                break;
            case 1:
                typeVal = 2;
                break;
            case 0:
                typeVal = 2;
                break;
        }
    }

    public Segment getSegment() {
        return new Segment(p0, p1);
    }

    public Line getLine() {
        return new Line(p0, new Vector2d(p1.x - p0.x, p1.y - p0.y));
    }


    /**
     * gives order to this class's object.
     *
     * line type is not in comparison because
     * there is only one line in the real world if 
     * the two ends of the line are determined.
	 *
     * @param oline
     * @return
     */
    @Override
    public int compareTo(OriLine oline) {

    	int comparison00 = this.p0.compareTo(oline.p0);
    	int comparison11 = this.p1.compareTo(oline.p1);

    	if (comparison00 == 0) {
    		return comparison11;
    	}

    	return comparison00;
    }

    /**
     * 
     * line type is not compared because
     * there is only one line in the real world if 
     * the two ends of the line are determined.
     */
	@Override
	public boolean equals(Object obj) {

		// same class?
    	if (!(obj instanceof OriLine)) {
    		return false;
    	}
		
		OriLine oline = (OriLine)obj;
    	int comparison00 = this.p0.compareTo(oline.p0);
    	int comparison01 = this.p0.compareTo(oline.p1);
    	int comparison10 = this.p1.compareTo(oline.p0);
    	int comparison11 = this.p1.compareTo(oline.p1);

    	// same direction?
    	if (comparison00 == 0 && comparison11 == 0) {
    		return true;
    	}

    	// reversed direction?
    	if (comparison01 == 0 && comparison10 == 0) {
    		return true;
    	}

    	// differs
    	return false;
	}


    
}
