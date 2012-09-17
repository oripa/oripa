package oripa.doc.exporter;

import oripa.doc.Doc;

public interface Exporter {
	public boolean export(Doc doc, String filepath) throws Exception;
}
