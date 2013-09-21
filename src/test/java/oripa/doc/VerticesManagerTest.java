package oripa.doc;

import java.util.Collection;

import javax.vecmath.Vector2d;

import junit.framework.TestCase;

import org.junit.Test;

import oripa.doc.core.VerticesManager;
import oripa.doc.value.OriPoint;

public class VerticesManagerTest extends TestCase{

	// not to be here
	
//	Doc doc = new Doc(paperSize);
//	double interval = doc.getCreasePattern().getVerticesManager().interval;
//
//	@Test
//	protected void setUp() throws Exception {
//		/**
//		 * 0__________
//		 *  _|_|______
//		 *  _|_|______
//		 *   | |
//		 *   | |
//		 *  
//		 */
//		// horizontal line
//		doc.addLine(new OriLine(0, 0, paperSize, 0, OriLine.TYPE_RIDGE));		
//		doc.addLine(new OriLine(0, interval, paperSize, interval, OriLine.TYPE_RIDGE));
//		doc.addLine(new OriLine(0, interval * 2, paperSize, interval * 2, OriLine.TYPE_RIDGE));
//
//		// vertical
//		doc.addLine(new OriLine(interval, 0, interval, paperSize, OriLine.TYPE_RIDGE));
//		doc.addLine(new OriLine(interval * 2, 0, interval * 2, paperSize, OriLine.TYPE_RIDGE));
//		
//
//		ORIPA.doc = doc;
//		
//	}
//
//	@Test
//	public void testNearest(){
//		PaintContext context =  PaintContext.getInstance();
//		
//		Point.Double mousePoint = new Point.Double(0, 0);
//		context.setLogicalMousePoint(mousePoint);
//
//		VerticesManager manager = doc.getCreasePattern().getVerticesManager();
//
//		final double distance = 10;
//		Collection<Collection<Vector2d>> area = manager.getArea(
//				mousePoint.x, mousePoint.y, distance);
//		
//		
//		Vector2d mouseVector = new Vector2d(mousePoint.x, mousePoint.y);
//		assertEquals(mouseVector, 
//				NearestVertexFinder.findAround(context, distance).point);
//	}
//	
	@Test
	public void testAddVertex() {
		final double paperSize = 400;
		
		VerticesManager manager = new VerticesManager(paperSize);

		double interval = manager.interval;
		addAndCheckContains(manager, new Vector2d(0, 0));
		addAndCheckContains(manager, new Vector2d(interval, 0));
		addAndCheckContains(manager, new Vector2d(interval, interval));


	}

	private boolean managerContains(VerticesManager manager, Vector2d vertex) {
		Collection<Vector2d> vertices;
		vertices = manager.getAround(vertex);
		return vertices.contains(vertex);
		
	}
	private void addAndCheckContains(VerticesManager manager, Vector2d target){
		manager.add(target);

		Collection<Vector2d> vertices;
		vertices = manager.getAround(target);

		System.out.println("target: " + target);
		for(Vector2d v: vertices){
			System.out.println(v);
		}
		
		assertTrue(managerContains(manager, target));
		
	}
	
	@Test
	public void testDuplicationManagement() {
		final double paperSize = 400;
		
		VerticesManager manager = new VerticesManager(paperSize);

		OriPoint p = new OriPoint(10,10);

		manager.add(p);
		manager.add(p);

		manager.remove(p);
		assertTrue(managerContains(manager, p));
		
		manager.remove(p);
		assertFalse(managerContains(manager, p));
	}
	
}
