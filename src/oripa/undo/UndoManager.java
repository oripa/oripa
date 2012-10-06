package oripa.undo;

import java.util.Deque;
import java.util.LinkedList;

public class UndoManager<Backup> {

	private Deque<Backup> undoStack = new LinkedList<>();
	private Backup cache;
	
	private boolean changed = false;

	private int max = Integer.MAX_VALUE;

	public UndoManager() {
		
	}
	
	public UndoManager(int max){
		this.max = max;
	}

	public void push(Backup uinfo){
		undoStack.push(uinfo);
		
		if(undoStack.size() > max){
			undoStack.removeFirst();
		}
		
		changed = true;
	}

	public Backup pop() {
		if (undoStack.isEmpty()) {
			return null;
		}
		else {
			changed = true;
		}

		return undoStack.pop();
	}

	public Backup peek(){
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
	
	public void setCache(Backup info){
		cache = info;
	}
	
	public Backup getCache(){
		return cache;
	}
	
	public void pushCachedInfo(){
		this.push(cache);
	}
}
