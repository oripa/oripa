package oripa.bind.state.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.ORIPA;
import oripa.paint.core.Globals;
import oripa.paint.core.GraphicMouseAction;
import oripa.paint.core.PaintContext;
import oripa.viewsetting.main.ScreenUpdater;


/**
 * Add this listener to Button object or something for selecting paint action.
 * 
 * @author koji
 *
 */
public class PaintActionSetter implements ActionListener{
	
	private GraphicMouseAction mouseAction;
	
	public PaintActionSetter(GraphicMouseAction mouseAction) {
		this.mouseAction = mouseAction;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		PaintContext context = PaintContext.getInstance();

		Globals.getMouseAction().destroy(context);
		mouseAction.recover(context);
		
		Globals.setMouseAction(mouseAction);

		if(mouseAction.needSelect() == false){
			ORIPA.doc.resetSelectedOriLines();
		}
				
		ScreenUpdater screenUpdater = ScreenUpdater.getInstance();
		screenUpdater.updateScreen();
	}
	

}	

