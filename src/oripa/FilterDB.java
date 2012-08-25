package oripa;

import java.util.HashMap;

import oripa.file.ExporterCP;
import oripa.file.ExporterDXF;
import oripa.file.ExporterOBJ;
import oripa.file.ExporterSVG;
import oripa.file.ExporterXML;
import oripa.file.FileFilterEx;


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
		
		this.putFilter("opx", 
	    		new FileFilterEx(
	    				new String[]{".opx"}, 
	    				"(*.opx) " + ORIPA.res.getString("ORIPA_File"), new ExporterXML()
	    				));
	                    
		this.putFilter("pict",
				new FileFilterEx(
			    		new String[]{".png", ".jpg"}, 
	            		"(*.png, *.jpg) " + ORIPA.res.getString("Picture_File")					
	    				));

	
		String key = "dxf";
		this.putFilter(key,
		new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ")" + key + ORIPA.res.getString("File"), new ExporterDXF()));

		key = "obj";
		this.putFilter(key,
		new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ")" + key + ORIPA.res.getString("File"), new ExporterOBJ()));

		key = "cp";
		this.putFilter(key,
		new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ")" + key + ORIPA.res.getString("File"), new ExporterCP()));
		
		key = "svg";
		this.putFilter(key,
		new FileFilterEx(new String[]{"." + key }, 
				"(*." + key + ")" + key + ORIPA.res.getString("File"), new ExporterSVG()));
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
