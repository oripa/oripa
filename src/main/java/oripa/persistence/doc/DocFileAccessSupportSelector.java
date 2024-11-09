package oripa.persistence.doc;

import java.util.SortedMap;
import java.util.TreeMap;

import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.project.Project;
import oripa.resource.StringID;

/**
 * Manages available filters for {@link Project} file access. Limited to crease
 * pattern input/output.
 *
 * @author OUCHI Koji
 *
 */
public class DocFileAccessSupportSelector extends AbstractFileAccessSupportSelector<Doc> {

	private final SortedMap<FileTypeProperty<Doc>, FileAccessSupport<Doc>> filters = new TreeMap<>();

	/**
	 * A constructor that puts default filters into this instance.
	 */
	public DocFileAccessSupportSelector() {

		CreasePatternFileTypeKey key = CreasePatternFileTypeKey.OPX;
		putFileAccessSupport(key, createDescription(key, StringID.Main.ORIPA_FILE_ID));

		key = CreasePatternFileTypeKey.PICT;
		putFileAccessSupport(key, createDescription(key, StringID.Main.PICTURE_FILE_ID));

		key = CreasePatternFileTypeKey.FOLD;
		putFileAccessSupport(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.DXF;
		putFileAccessSupport(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.CP;
		putFileAccessSupport(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.SVG;
		putFileAccessSupport(key, createDescription(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.PDF;
		putFileAccessSupport(key, createDescription(key, StringID.Main.FILE_ID));

	}

	@Override
	protected SortedMap<FileTypeProperty<Doc>, FileAccessSupport<Doc>> getFileAccessSupports() {
		return filters;
	}
}
