package oripa.paint.line;

import oripa.paint.MouseContext;
import oripa.paint.segment.TwoPointSegmentAction;

public class TwoPointLineAction extends TwoPointSegmentAction {


	public TwoPointLineAction(){
		setActionState(new SelectingFirstVertexForLine());
	}

	
	
	@Override
	public void destroy(MouseContext context) {
		// TODO Auto-generated method stub
		super.destroy(context);
		setActionState(new SelectingFirstVertexForLine());
	}



	@Override
	public void recover(MouseContext context) {
		
	}
	
	

}
