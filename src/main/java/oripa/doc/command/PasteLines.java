package oripa.doc.command;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.vecmath.Vector2d;

import oripa.geom.OriLine;

public class PasteLines {

	/**
	 * A rectangle domain
	 */
	private class Domain{
		double left, right, top, bottom;

		/**
		 * reset variables by the most opposite value.
		 */
		public Domain() {
			initialize();
		}

		/**
		 * A rectangle domain fitting to given lines
		 * @param target
		 */
		public Domain(Collection<OriLine> target){

			initialize();
						
			for(OriLine line : target){
				enlargeDomain(this, line.p0);
				enlargeDomain(this, line.p1);
			}
			
		}

		private void initialize(){
			left = Double.MAX_VALUE;
			right = Double.MIN_VALUE;
			top = Double.MAX_VALUE;
			bottom = Double.MIN_VALUE;

		}
		
		public void enlargeDomain(Domain domain, Vector2d v){
			domain.left = Math.min(domain.left, v.x);
			domain.right = Math.max(domain.right, v.x);
			domain.top = Math.min(domain.top, v.y);
			domain.bottom = Math.max(domain.bottom, v.y);
			
		}
		
}
	
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
		
		Domain domain = new Domain(toBePasted);
		LinkedList<OriLine> crossables = new LinkedList<>();
				
		for(Iterator<OriLine> itrator = currentLines.iterator();
				itrator.hasNext();){
			OriLine line = itrator.next();

			// skip lines with no intersection
			if(line.p0.x < domain.left && line.p1.x < domain.left ||
					line.p0.x > domain.right && line.p1.x > domain.right  ||
					line.p0.y < domain.top && line.p1.y < domain.top ||
					line.p0.y > domain.bottom && line.p1.y > domain.bottom){
				continue;
			}
			
			crossables.add(line);
			itrator.remove();
		}

		//-----------------------------------------------
		// make them crossed
		//-----------------------------------------------
		
		AddLine adder = new AddLine();
		
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
