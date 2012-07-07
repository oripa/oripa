package oripa.file;

import oripa.Doc;

public interface Exporter {
	public boolean export(Doc doc, String filepath) throws Exception;
}
