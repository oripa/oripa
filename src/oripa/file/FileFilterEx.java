package oripa.file;

public class FileFilterEx extends javax.swing.filechooser.FileFilter {

    public interface SavingAction {
    	public boolean save(String path);
    }

    
    private String extensions[];
    private String msg;
    private SavingAction savingAction;
   
    public FileFilterEx(String[] extensions, String msg) {
        this.extensions = extensions;
        this.msg = msg;
    }

    public FileFilterEx(String[] extensions, String msg, SavingAction action) {
        this.extensions = extensions;
        this.msg = msg;
        this.savingAction = action;
    }

    public void setSavingAction(SavingAction s) {
    	savingAction = s;
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
