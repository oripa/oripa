package oripa.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GetCachedCommandAction  implements ActionListener{
	CommandHistory history = CommandHistory.getInstance();
	@Override
	public void actionPerformed(ActionEvent e) {
		
		ActionListener[] actions = history.getCache().listeners;
		if(actions == null){
			return;
		}
		
		for(ActionListener action : actions){
			action.actionPerformed(null);
		}
	}
	
}
