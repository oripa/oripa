package oripa.bind.state.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.ORIPA;
import oripa.paint.GraphicMouseActionInterface;
import oripa.paint.ScreenUpdaterInterface;
import oripa.paint.core.PaintConfig;
import oripa.paint.core.PaintContext;
import oripa.viewsetting.main.ScreenUpdater;


/**
 * Add this listener to Button object or something for selecting paint action.
 * 
 * @author koji
 *
 */
public class PaintActionSetter implements ActionListener{
	
	private GraphicMouseActionInterface mouseAction;
	
	public PaintActionSetter(GraphicMouseActionInterface mouseAction) {
		this.mouseAction = mouseAction;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		PaintContext context = PaintContext.getInstance();

		PaintConfig.getMouseAction().destroy(context);
		mouseAction.recover(context);
		
		PaintConfig.setMouseAction(mouseAction);

		if(mouseAction.needSelect() == false){
			ORIPA.doc.resetSelectedOriLines();
		}
				
		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();
		screenUpdater.updateScreen();
	}
	

}	

