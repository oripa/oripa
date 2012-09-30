package oripa.doc;

import oripa.resource.Constants;

public class DocHolder {
	private static DocHolder instance = null;
	
	public static DocHolder getInstance(){
		if(instance == null){
			instance = new DocHolder();
		}
		
		return instance;
	}
//-----------------------------------------------------
	
	private Doc doc;
	
	private DocHolder(){
		doc = new Doc(Constants.DEFAULT_PAPER_SIZE);
	}
	
	public Doc getDoc(){
		return doc;
	}
}
