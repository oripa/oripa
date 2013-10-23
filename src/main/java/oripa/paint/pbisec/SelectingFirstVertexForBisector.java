package oripa.paint.pbisec;

import oripa.paint.PaintContextInterface;
import oripa.paint.core.PickingVertex;

public class SelectingFirstVertexForBisector extends PickingVertex{
	
	public SelectingFirstVertexForBisector(){
		super();
	}

	@Override
	public void onResult(PaintContextInterface context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingSecondVertexForBisector.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
