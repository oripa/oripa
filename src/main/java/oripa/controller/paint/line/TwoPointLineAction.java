package oripa.controller.paint.line;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.segment.TwoPointSegmentAction;

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
