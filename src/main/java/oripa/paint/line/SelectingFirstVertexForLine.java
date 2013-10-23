package oripa.paint.line;

import oripa.paint.PaintContextInterface;
import oripa.paint.core.PickingVertex;

public class SelectingFirstVertexForLine extends PickingVertex{
	
	public SelectingFirstVertexForLine(){
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
		setNextClass(SelectingSecondVertexForLine.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
