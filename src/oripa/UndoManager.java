package oripa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import oripa.geom.OriLine;

public class UndoManager<Backup> {

	private Stack<Backup> undoStack = new Stack<Backup>();
	private Backup cache;
	

	public void push(Backup uinfo){
		undoStack.push(uinfo);
	}

	public Backup pop() {
		if (undoStack.isEmpty()) {
			return null;
		}

		return undoStack.pop();
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
