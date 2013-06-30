package oripa.file;

import oripa.doc.Doc;

public class FileFilterEx extends javax.swing.filechooser.FileFilter {

	/**
	 * 
	 * @author OUCHI Koji
	 *
	 */

    
    
    private String extensions[];
    private String msg;
    
    private SavingAction savingAction = null;

    private LoadingAction loadingAction = null;
    
    public FileFilterEx(String[] extensions, String msg) {
        this.extensions = extensions;
        this.msg = msg;
    }

    public FileFilterEx(String[] extensions, String msg, SavingAction savingAction) {
        this.extensions = extensions;
        this.msg = msg;
        this.savingAction = savingAction;
    }

//    public FileFilterEx(String[] extensions, String msg, Exporter exporter) {
//        this.extensions = extensions;
//        this.msg = msg;
//        this.exporter = exporter;
//        
////        this.savingAction = new SavingAction() {
////			
////			@Override
////			public boolean save(String path) {
////				boolean success = false;
////				try {
////					success = FileFilterEx.this.exporter.export(ORIPA.doc, path);
////				} catch (Exception e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				return success;
////			}
////		};
//        
//    }


	public boolean save(Doc doc, String path) throws Exception{

    	boolean success = false;
    	
    	if(savingAction != null){
    		success = savingAction.save(path);
    	}
//    	else if(exporter != null){
//    		success = exporter.export(doc, path);
//    	}
    	
    	
    	return success;
    }
    
	

	public boolean load(String path) throws Exception{

    	boolean success = false;
    	
    	if(loadingAction != null){
    		success = loadingAction.load(path);
    	} 	
    	
    	return success;
    }
	
	
    public LoadingAction getLoadingAction() {
		return loadingAction;
	}

	public void setLoadingAction(LoadingAction loadingAction) {
		this.loadingAction = loadingAction;
	}

//	public Loader getLoader() {
//		return loader;
//	}
//
//	public void setLoader(Loader loader) {
//		this.loader = loader;
//	}

    
    public void setSavingAction(SavingAction s) {
    	savingAction = s;
    }
    
    public SavingAction getSavingAction(){
    	return savingAction;
    }

    
//    public Exporter getExporter() {
//		return exporter;
//	}
//
//	public void setExporter(Exporter exporter) {
//		this.exporter = exporter;
//	}

	public String[] getExtensions(){
    	return extensions;
    }
    
    
    @Override
    public boolean accept(java.io.File f) {
        if (f.isDirectory()) {
            return true;
        }
        for (int i = 0; i < extensions.length; i++) {

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
