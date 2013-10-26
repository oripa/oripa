package oripa.paint.cptool;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import oripa.paint.util.RectangleDomain;
import oripa.value.OriLine;

public class LinePaster {

	/**
	 * 
	 * @param toBePasted	lines to be added into current lines.
	 * @param currentLines	it will be affected as 
	 * 						new lines are added and unnecessary lines are deleted.
	 */
	public void paste(
			Collection<OriLine> toBePasted, Collection<OriLine> currentLines){

		//----------------------------------------------------------
		// Find lines possible to cross to new lines.
		// found lines are removed from the current list.
		//----------------------------------------------------------
		
		RectangleDomain domain = new RectangleDomain(toBePasted);
		LinkedList<OriLine> crossables = new LinkedList<>();
				
		for(Iterator<OriLine> itrator = currentLines.iterator();
				itrator.hasNext();){
			OriLine line = itrator.next();

			// skip lines with no intersection
			if(line.p0.x < domain.getLeft() && line.p1.x < domain.getLeft() ||
					line.p0.x > domain.getRight() && line.p1.x > domain.getRight()  ||
					line.p0.y < domain.getTop() && line.p1.y < domain.getTop() ||
					line.p0.y > domain.getBottom() && line.p1.y > domain.getBottom()){
				continue;
			}
			
			crossables.add(line);
			itrator.remove();
		}

		//-----------------------------------------------
		// make them crossed
		//-----------------------------------------------
		
		LineAdder adder = new LineAdder();
		
		for(OriLine line : toBePasted){
			// the result is stored into crossables.
			adder.addLine(line, crossables);
		}
		
		//-----------------------------------------------
		// set the result to the current list
		//-----------------------------------------------
		for(OriLine line : crossables){
			currentLines.add(line);
		}

		
	}
	
}
