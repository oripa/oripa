package oripa.paint.segment;

import oripa.paint.MouseContext;
import oripa.paint.PickingVertex;


public class SelectingFirstVertexForSegment extends PickingVertex{


	
	
	public SelectingFirstVertexForSegment(){
		super();
	}
	
	@Override
	public void undoAction(MouseContext context) {
		context.clear(false);
	}

	@Override
	public void onResult(MouseContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingSecondVertexForSegment.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
