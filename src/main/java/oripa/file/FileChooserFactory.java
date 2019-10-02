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

		for (FileFilterEx filter : filters)
		{
			fileChooser.addChoosableFileFilter(filter);
		}

		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(filters[0]);
		
		return  fileChooser;
	}



}
