package oripa.bind.state.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.ORIPA;
import oripa.paint.GraphicMouseActionInterface;
import oripa.paint.PaintContextInterface;
import oripa.paint.ScreenUpdaterInterface;
import oripa.paint.core.PaintConfig;
import oripa.paint.core.PaintContext;
import oripa.paint.creasepattern.CreasePattern;
import oripa.paint.creasepattern.Painter;
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
		PaintContextInterface context = PaintContext.getInstance();

		PaintConfig.getMouseAction().destroy(context);
		mouseAction.recover(context);
		
		PaintConfig.setMouseAction(mouseAction);

		if(mouseAction.needSelect() == false){
			CreasePattern creasePattern = ORIPA.doc.getCreasePattern();
			Painter painter = new Painter();
			painter.resetSelectedOriLines(creasePattern);
		}
				
		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();
		screenUpdater.updateScreen();
	}
	

}	

