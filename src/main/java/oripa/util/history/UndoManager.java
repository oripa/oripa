package oripa.util.history;

import java.util.Deque;
import java.util.LinkedList;

public class UndoManager<Backup> {

	private Deque<UndoInfo<Backup>> undoStack = new LinkedList<>();
	private UndoInfo<Backup> cache;
	
	private boolean changed = false;

	private int max = 1000;

	public UndoManager() {
		
	}
	
	public UndoManager(int max){
		this.max = max;
	}

	public void push(UndoInfo<Backup> uinfo){
		undoStack.push(uinfo);
		
		if(undoStack.size() > max){
			undoStack.removeFirst();
		}
		
		changed = true;
	}

	public UndoInfo<Backup> pop() {
		if (undoStack.isEmpty()) {
			return null;
		}
		else {
			changed = true;
		}

		return undoStack.pop();
	}

	public UndoInfo<Backup> peek(){
		if (undoStack.isEmpty()) {
			return null;
		}

		return undoStack.peek();
	}
	
	public boolean isChanged(){
		return changed;
	}
	
	public void clearChanged(){
		changed = false;
	}
	
	public boolean canUndo(){
		return ! undoStack.isEmpty();
	}
	
	public void setCache(UndoInfo<Backup> info){
		cache = info;
	}
	
	public UndoInfo<Backup> getCache(){
		return cache;
	}
	
	public void pushCachedInfo(){
		this.push(cache);
	}
}
