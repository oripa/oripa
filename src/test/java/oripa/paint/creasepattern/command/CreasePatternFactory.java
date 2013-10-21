package oripa.paint.creasepattern.command;

import java.util.Collection;
import java.util.LinkedList;

import oripa.value.LineType;
import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * a factory for creating simple test data
 * @author Koji
 *
 */
public class CreasePatternFactory {

	/**
	 * creates a X shape composed by four lines.
	 * @param size
	 * @param center
	 * @return
	 */
	public Collection<OriLine> createCrossedLines(double size, OriPoint center){
		LinkedList<OriLine> creasePattern = new LinkedList<>();
		
		double partSize = size / 2;
		OriLine slash = new OriLine(
				new OriPoint(center.x - partSize, center.y - partSize),
				new OriPoint(center.x + partSize, center.y + partSize),
				LineType.RIDGE.toInt());

		OriLine backSlash = new OriLine(
				new OriPoint(center.x + partSize, center.y + partSize),
				new OriPoint(center.x - partSize, center.y - partSize),
				LineType.RIDGE.toInt());

		
		//FIXME lazy implementation.
		// we should add four small lines into the collection
		// in order to ensure correctness of this method.
		LineAdder adder = new LineAdder();
		
		adder.addLine(slash, creasePattern);
		adder.addLine(backSlash, creasePattern);
		
		
		return creasePattern;
	}
}
