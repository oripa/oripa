package oripa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import oripa.file.Exporter;
import oripa.file.ExporterCP;
import oripa.file.ExporterDXF;
import oripa.file.ExporterOBJ;
import oripa.file.ExporterSVG;
import oripa.file.ExporterXML;
import oripa.file.FileFilterEx;
import oripa.file.LoaderCP;
import oripa.file.LoaderDXF;
import oripa.file.LoaderPDF;
import oripa.file.LoaderXML;
import oripa.file.LoadingAction;
import oripa.file.SavingAction;


/**
 * 
 * @author OUCHI Koji
 *
 */
public class FilterDB {

	private HashMap<String, FileFilterEx> filters = new HashMap<>();

	private static FilterDB instance = null;

	public static FilterDB getInstance(){
		if(instance == null){
			instance = new FilterDB();
		}
		return instance;
	}
	

	
	private FilterDB(){
		
		FileFilterEx filter;
		
		filter = new FileFilterEx(
				new String[]{".opx", ".xml"}, 
				"(*.opx, *.xml) " + ORIPA.res.getString("ORIPA_File"), new SavingDoc(new ExporterXML())
				);	
		filter.setLoadingAction(new LoadingDoc(new LoaderXML()));
		this.putFilter("opx", filter);

		filter = new FileFilterEx(
	    		new String[]{".png", ".jpg"}, 
        		"(*.png, *.jpg) " + ORIPA.res.getString("Picture_File")					
				);
		this.putFilter("pict", filter);


	
		String key = "dxf";
		filter = new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ") " + key + ORIPA.res.getString("File"), new SavingDoc(new ExporterDXF()) );
		filter.setLoadingAction(new LoadingDoc(new LoaderDXF()));
		this.putFilter(key, filter);

		key = "obj";
		filter = new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ") " + key + ORIPA.res.getString("File"), new SavingDoc( new ExporterOBJ()) );
		this.putFilter(key, filter);

		key = "cp";
		filter = new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ") " + key + ORIPA.res.getString("File"), new SavingDoc( new ExporterCP()) );
		filter.setLoadingAction(new LoadingDoc(new LoaderCP()));
		this.putFilter(key,filter);
		
		
		key = "svg";
		filter = new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ") " + key + ORIPA.res.getString("File"), new SavingDoc(new ExporterSVG()) );
		this.putFilter(key, filter);
		
		key = "pdf";
		filter = new FileFilterEx(new String[]{".pdf"}, "(*.pdf) PDF file");
		filter.setLoadingAction(new LoadingDoc(new LoaderPDF()));
		this.putFilter(key, filter);

	}
	
    public FileFilterEx getFilter(String key){
    	return filters.get(key);
    }
    
    public FileFilterEx putFilter(String key, FileFilterEx filter){
    	return filters.put(key, filter);
    }
    
    public FileFilterEx[] toArray(){
    	FileFilterEx[] array = new FileFilterEx[filters.size()];
    	
    	int i = 0;
    	for (String key : filters.keySet()) {
			array[i] = filters.get(key);
			i++;
		}
    	
    	return array;
    }
    
    public FileFilterEx[] getLoadables(){
    	ArrayList<FileFilterEx> loadables = new ArrayList<>();
    	
    	for (String key : filters.keySet()) {
    		FileFilterEx filter = filters.get(key);
    		if(filter.getLoadingAction() != null){
    			loadables.add(filter);
    		}
    	}
  
    	FileFilterEx[] array = new FileFilterEx[loadables.size()];
    	
    	return loadables.toArray(array);
    }

    /**
     * returns null if any filter cannot load the file.
     * @param path
     * @return a filter which can load the file at the path.
     */
    public FileFilterEx getLoadableFilterOf(String path){
    	File file = new File(path);
    	if(file.isDirectory()){
    		return null;
    	}
    	
		for(FileFilterEx filter : this.toArray()){
			if(filter.accept(file)){
				return filter;
			}
		}
		
		return null;
    }
    
    public FileFilterEx[] getSavables(){
    	ArrayList<FileFilterEx> savables = new ArrayList<>();
    	
    	for (String key : filters.keySet()) {
    		FileFilterEx filter = filters.get(key);
    		if(filter.getSavingAction() != null){
    			savables.add(filter);
    		}
    	}
    	
    	return (FileFilterEx[]) savables.toArray();
    }
    
    
}
