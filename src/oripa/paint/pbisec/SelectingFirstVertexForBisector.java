package oripa.paint.pbisec;

import oripa.paint.MouseContext;
import oripa.paint.PickingVertex;

public class SelectingFirstVertexForBisector extends PickingVertex{
	
	public SelectingFirstVertexForBisector(){
		super();
	}
	
	@Override
	public void undoAction(MouseContext context) {
		context.clear();
	}

	@Override
	public void onResult(MouseContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingSecondVertexForBisector.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
