package oripa.file;


public interface LoadingAction {

	public abstract boolean load(String path) throws FileVersionError;

}