package oripa.file;


public class FileChooserFactory {

	

	public FileChooser 
	createChooser(String path, FileFilterEx[] filters){

		FileChooser fileChooser;
		
		if(path != null){
			fileChooser = new FileChooser(path);
		}
		else {
			fileChooser = new FileChooser();
		}
		
		for (int i = 0; i < filters.length; i++) {
			fileChooser.addChoosableFileFilter(filters[i]);			
		}

		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(filters[0]);
		
		return  fileChooser;
	}



}
