package oripa.paint.line;

import oripa.paint.MouseContext;
import oripa.paint.PickingVertex;

public class SelectingFirstVertexForLine extends PickingVertex{
	
	public SelectingFirstVertexForLine(){
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
		setNextClass(SelectingSecondVertexForLine.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
