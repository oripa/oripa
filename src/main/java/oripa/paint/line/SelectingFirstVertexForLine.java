package oripa.paint.line;

import oripa.paint.PaintContext;
import oripa.paint.PickingVertex;

public class SelectingFirstVertexForLine extends PickingVertex{
	
	public SelectingFirstVertexForLine(){
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
		setNextClass(SelectingSecondVertexForLine.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
