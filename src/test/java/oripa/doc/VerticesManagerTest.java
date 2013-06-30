package oripa.doc;

import java.awt.Point;
import java.util.Collection;

import javax.vecmath.Vector2d;

import junit.framework.TestCase;

import org.junit.Test;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.doc.core.VerticesManager;
import oripa.geom.OriLine;
import oripa.paint.PaintContext;
import oripa.paint.geometry.NearestVertexFinder;

public class VerticesManagerTest extends TestCase{

	final double paperSize = 400;
	Doc doc = new Doc(paperSize);
	double interval = doc.getCreasePattern().getVerticesManager().interval;

	@Test
	protected void setUp() throws Exception {
		/**
		 * 0__________
		 *  _|_|______
		 *  _|_|______
		 *   | |
		 *   | |
		 *  
		 */
		// horizontal line
		doc.addLine(new OriLine(0, 0, paperSize, 0, OriLine.TYPE_RIDGE));		
		doc.addLine(new OriLine(0, interval, paperSize, interval, OriLine.TYPE_RIDGE));
		doc.addLine(new OriLine(0, interval * 2, paperSize, interval * 2, OriLine.TYPE_RIDGE));

		// vertical
		doc.addLine(new OriLine(interval, 0, interval, paperSize, OriLine.TYPE_RIDGE));
		doc.addLine(new OriLine(interval * 2, 0, interval * 2, paperSize, OriLine.TYPE_RIDGE));
		

		ORIPA.doc = doc;
		
	}

	@Test
	public void testNearest(){
		PaintContext context =  PaintContext.getInstance();
		
		Point.Double mousePoint = new Point.Double(0, 0);
		context.setLogicalMousePoint(mousePoint);

		VerticesManager manager = doc.getCreasePattern().getVerticesManager();

		final double distance = 10;
		Collection<Collection<Vector2d>> area = manager.getArea(
				mousePoint.x, mousePoint.y, distance);
		
		
		Vector2d mouseVector = new Vector2d(mousePoint.x, mousePoint.y);
		assertEquals(mouseVector, 
				NearestVertexFinder.findAround(context, distance).point);
	}
	
	@Test
	public void testManager() {
		
		VerticesManager manager = doc.getCreasePattern().getVerticesManager();
		


		
				
		assertContains(manager, new Vector2d(0, 0));
		assertContains(manager, new Vector2d(interval, 0));
		assertContains(manager, new Vector2d(interval, interval));


	}

	
	private void assertContains(VerticesManager manager, Vector2d target){
		Collection<Vector2d> vertices;
		vertices = manager.getAround(target);

		System.out.println("target: " + target);
		for(Vector2d v: vertices){
			System.out.println(v);
		}
		
		assertTrue(vertices.contains(target));
		
	}
	
}
