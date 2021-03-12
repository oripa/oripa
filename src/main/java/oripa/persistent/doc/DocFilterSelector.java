package oripa.persistent.doc;

import java.util.SortedMap;
import java.util.TreeMap;

import oripa.doc.Doc;
import oripa.persistent.dao.AbstractFilterSelector;
import oripa.persistent.filetool.FileAccessSupportFilter;
import oripa.persistent.filetool.FileTypeProperty;
import oripa.resource.StringID;

/**
 * Manages available filters for file access.
 *
 * @author OUCHI Koji
 *
 */
public class DocFilterSelector extends AbstractFilterSelector<Doc> {

	private final SortedMap<FileTypeProperty<Doc>, FileAccessSupportFilter<Doc>> filters = new TreeMap<>();

	/**
	 *
	 * A constructor that puts default filters into this instance.
	 */
	public DocFilterSelector() {

		CreasePatternFileTypeKey key = CreasePatternFileTypeKey.OPX;
		putFilter(key, createDescription(key, StringID.Main.ORIPA_FILE_ID));

		key = CreasePatternFileTypeKey.PICT;
		putFilter(key, createDescription(key, StringID.Main.PICTURE_FILE_ID));

		key = CreasePatternFileTypeKey.FOLD;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.DXF;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.CP;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.SVG;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.PDF;
		putFilter(key, createDescription(key, StringID.Main.FILE_ID));

	}

	/* (non Javadoc)
	 * @see oripa.persistent.dao.FilterSelector#getFilters()
	 */
	@Override
	protected SortedMap<FileTypeProperty<Doc>, FileAccessSupportFilter<Doc>> getFilters() {
		return filters;
	}
}
