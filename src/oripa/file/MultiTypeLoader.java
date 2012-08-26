package oripa.file;

import oripa.Doc;

public class MultiTypeLoader implements Loader {

	@Override
	public Doc load(String filePath) throws FileVersionError {
		Loader loader = null;

		String extension = filePath.substring(filePath.lastIndexOf('.')); 
		switch(extension) {
		case ".dxf":
			loader = new LoaderDXF();
			break;
		case ".pdf":
			loader = new LoaderPDF();
			break;
		case ".cp":
			loader = new LoaderCP();
			break;
		case ".opx":
		case ".xml":
			loader = new LoaderXML();
		} 
		
		return loader.load(filePath);
	}
}
