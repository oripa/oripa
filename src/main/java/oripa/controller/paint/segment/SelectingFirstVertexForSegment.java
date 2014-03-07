package oripa.controller.paint.segment;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;


public class SelectingFirstVertexForSegment extends PickingVertex{


	
	
	public SelectingFirstVertexForSegment(){
		super();
	}
	
	@Override
	public void undoAction(PaintContextInterface context) {
		context.clear(false);
	}

	@Override
	public void onResult(PaintContextInterface context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingSecondVertexForSegment.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
