package oripa.paint.copypaste;

import javax.vecmath.Vector2d;

import oripa.paint.PaintContextInterface;

class OriginHolder {

//--------------------------------------------------------------
	private static OriginHolder holder = null;
	
	private OriginHolder(){}
	
	public static OriginHolder getInstance(){
		if(holder == null){
			holder = new OriginHolder();
		}
		
		return holder;
	}
//--------------------------------------------------------------

	private Vector2d origin = null;
	
	public void setOrigin(Vector2d p){
		origin = p;
	}
	
	public void resetOrigin(PaintContextInterface context){
    	if(origin == null){
    		if(context.getLineCount() > 0){
    			origin = context.getLine(0).p0;
    		}
		}	
	}
	
	public Vector2d getOrigin(PaintContextInterface context){
		resetOrigin(context);
		
		return origin;
	}
	
}
