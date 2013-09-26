package oripa.paint.segment;

import oripa.paint.core.PaintContext;
import oripa.paint.core.PickingVertex;


public class SelectingFirstVertexForSegment extends PickingVertex{


	
	
	public SelectingFirstVertexForSegment(){
		super();
	}
	
	@Override
	public void undoAction(PaintContext context) {
		context.clear(false);
	}

	@Override
	public void onResult(PaintContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingSecondVertexForSegment.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
