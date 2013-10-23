package oripa.paint.line;

import oripa.paint.PaintContextInterface;
import oripa.paint.segment.TwoPointSegmentAction;

public class TwoPointLineAction extends TwoPointSegmentAction {


	public TwoPointLineAction(){
		setActionState(new SelectingFirstVertexForLine());
	}

	
	
	@Override
	public void destroy(PaintContextInterface context) {
		// TODO Auto-generated method stub
		super.destroy(context);
	}



	@Override
	public void recover(PaintContextInterface context) {
		setActionState(new SelectingFirstVertexForLine());
		
	}
	
	

}
