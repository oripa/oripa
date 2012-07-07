package oripa.file;

import oripa.Doc;
import oripa.ORIPA;

public class FileFilterEx extends javax.swing.filechooser.FileFilter {

	/**
	 * 
	 * @author OUCHI Koji
	 *
	 */
    public interface SavingAction {
    	public boolean save(String path);
    }

    
    private String extensions[];
    private String msg;
    private SavingAction savingAction = null;
    private Exporter exporter = null;
   
    public FileFilterEx(String[] extensions, String msg) {
        this.extensions = extensions;
        this.msg = msg;
    }

    public FileFilterEx(String[] extensions, String msg, SavingAction action) {
        this.extensions = extensions;
        this.msg = msg;
        this.savingAction = action;
    }

    public FileFilterEx(String[] extensions, String msg, Exporter exporter) {
        this.extensions = extensions;
        this.msg = msg;
        this.exporter = exporter;
        
        this.savingAction = new SavingAction() {
			
			@Override
			public boolean save(String path) {
				boolean success = false;
				try {
					success = FileFilterEx.this.exporter.export(ORIPA.doc, path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return success;
			}
		};
        
    }


    public boolean save(Doc doc, String path) throws Exception{

    	boolean success = false;
    	
    	if(exporter != null){
    		success = exporter.export(doc, path);
    	}
    	
    	else if(savingAction != null){
    		success = savingAction.save(path);
    	}
    	
    	return success;
    }
    
    
    public void setSavingAction(SavingAction s) {
    	savingAction = s;
    }
    
    public Exporter getExporter() {
		return exporter;
	}

	public void setExporter(Exporter exporter) {
		this.exporter = exporter;
	}

	public String[] getExtensions(){
    	return extensions;
    }
    
    public SavingAction getSavingAction(){
    	return savingAction;
    }
    
    @Override
    public boolean accept(java.io.File f) {
        for (int i = 0; i < extensions.length; i++) {
            if (f.isDirectory()) {
                return true;
            }
            if (f.getName().endsWith(extensions[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return msg;
    }
}
