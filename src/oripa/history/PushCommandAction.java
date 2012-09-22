package oripa.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import oripa.paint.Globals;

public class PushCommandAction implements ActionListener{
	CommandHistory history = CommandHistory.getInstance();
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e == null){
			return;
		}
		AbstractButton source = (AbstractButton)e.getSource();
		history.push(new HistoryInfo(
				Globals.mouseAction.getEditMode(), source.getActionListeners()));			
					
		
		
	}
}

