package oripa.viewsetting.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopCommandAction implements ActionListener{
	CommandHistory history = CommandHistory.getInstance();
	@Override
	public void actionPerformed(ActionEvent e) {
		if(history.canUndo() == false){
			return;
		}
		ActionListener[] actions = history.pop();
		for(ActionListener action : actions){
			action.actionPerformed(null);
		}
	}
}
