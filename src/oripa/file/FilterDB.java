package oripa.file;

import java.io.IOException;
import java.util.HashMap;

import oripa.ExporterCP;
import oripa.ExporterDXF;
import oripa.ExporterOBJ;
import oripa.ExporterSVG;
import oripa.ORIPA;
import oripa.file.FileFilterEx.SavingAction;

public class FilterDB {

	private HashMap<String, FileFilterEx> filters = new HashMap<>();
	
	public FilterDB(){
		
		this.putFilter("ORIPA_File", 
	    		new FileFilterEx(
	    				new String[]{".opx"}, 
	    				"(*.opx) " + ORIPA.res.getString("ORIPA_File")
	    				));
	                    
		this.putFilter("Picture_File",
				new FileFilterEx(
			    		new String[]{".jpg", ".png"}, 
	            		"(*.jpg, *.png) " + ORIPA.res.getString("Picture_File")					
	    				));

	
		String key = "dxf";
		this.putFilter(key,
		new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ")" + key + ORIPA.res.getString("File"), null));

		key = "obj";
		this.putFilter(key,
		new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ")" + key + ORIPA.res.getString("File"), null));

		key = "cp";
		this.putFilter(key,
		new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ")" + key + ORIPA.res.getString("File"), null));
		
		key = "svg";
		this.putFilter(key,
		new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ")" + key + ORIPA.res.getString("File"), null));
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
}
