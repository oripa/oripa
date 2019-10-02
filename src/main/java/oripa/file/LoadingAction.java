package oripa.file;


public interface LoadingAction {

	boolean load(String path) throws FileVersionError;

}