package oripa.doc.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.LinkedList;

import javax.vecmath.Vector2d;

import oripa.geom.OriLine;


/**
 * For a fast access to vertex
 * @author koji
 *
 */
public class VerticesManager {

	/*
	 * divides paper equally to localize accessing to vertices
	 */
	static public final int divNum = 32;

	public final double interval;
	public final double paperCenter;

	
	public class AreaPosition{
		public int x, y;
		
		public AreaPosition(Vector2d v) {
			x = toDiv(v.x);
			y = toDiv(v.y);
		}
		
		public AreaPosition(double x, double y){
			this.x = toDiv(x);
			this.y = toDiv(y);
		}
		

	}

	int toDiv(double p){
		int div = (int) ((p + paperCenter) / interval);
					
		if(div < 0){
			return 0;
		}
		
		if(div >= divNum){
			return divNum-1;
		}
		
		return div;
	}
	
	//[div_x][div_y]
	private HashSet<Vector2d>[][] vertices = new HashSet[divNum][divNum];

	
	public VerticesManager(double paperSize) {
		interval = paperSize / divNum;
		paperCenter = paperSize/2;
		
		for(int x = 0; x < divNum; x++){
			for(int y = 0; y < divNum; y++){
				vertices[x][y] = new HashSet<Vector2d>();
			}
		}
		
	}
	
	public void clear(){
		for(int x = 0; x < divNum; x++){
			for(int y = 0; y < divNum; y++){
				vertices[x][y].clear();
			}
		}		
	}
	
	private HashSet<Vector2d> getVertices(AreaPosition ap){
		return vertices[ap.x][ap.y];
	}
	
	public void add(Vector2d v){
		HashSet<Vector2d> vertices = getVertices(new AreaPosition(v));

		vertices.add(v);
				
	}
	
	public Collection<Vector2d> getAround(Vector2d v){
		AreaPosition ap = new AreaPosition(v);
		return getVertices(ap);
	}
	
	public void remove(Vector2d v){
		AreaPosition ap = new AreaPosition(v);
		getVertices(ap).remove(v);
	}
	
	public Collection<Collection<Vector2d>> getArea(
			double x, double y, double distance){

		Collection<Collection<Vector2d>> result = new LinkedList<>();		
		
		int leftDiv   = toDiv(x - distance);
		int rightDiv  = toDiv(x + distance);
		int topDiv    = toDiv(y - distance);
		int bottomDiv = toDiv(y + distance);
		
		for(int xDiv = leftDiv; xDiv <= rightDiv; xDiv++){
			for(int yDiv = topDiv; yDiv <= bottomDiv; yDiv++){
				result.add(vertices[xDiv][yDiv]);
			}
		}
		
		
		return result;
	}
	
	public void load(Collection<OriLine> lines){
		this.clear();
		for(OriLine line : lines){
			add(line.p0);
			add(line.p1);
		}
		
	}
	
	
}
