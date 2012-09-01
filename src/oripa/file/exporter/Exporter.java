package oripa.file.exporter;

import oripa.Doc;

public interface Exporter {
	public boolean export(Doc doc, String filepath) throws Exception;
}
