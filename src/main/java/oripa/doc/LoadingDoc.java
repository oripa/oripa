package oripa.doc;

import oripa.ORIPA;
import oripa.doc.loader.Loader;
import oripa.file.FileVersionError;

public class LoadingDoc implements oripa.file.LoadingAction{

	private Loader loader;
	
	public LoadingDoc(Loader loader){
		this.loader = loader;
		
	}
	
	@Override
	public boolean load(String path) throws FileVersionError {
		boolean success = false;
		Doc doc = null;

		doc = loader.load(path);
		if (doc != null) {
			ORIPA.doc = doc;
			if(path != ""){
				ORIPA.doc.setDataFilePath(path);
			}
		}
		success = (doc != null);
				
		return success;
	}

}
