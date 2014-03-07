package oripa.controller.paint.vertical;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;

public class SelectingVertexForVertical extends PickingVertex{
	
	public SelectingVertexForVertical(){
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
		setNextClass(SelectingLineForVertical.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
