package oripa.doc.exporter;

import oripa.doc.Doc;

public interface Exporter {
	boolean export(Doc doc, String filepath) throws Exception;
}
