package oripa.persistence.doc;

import java.util.TreeMap;

import oripa.persistence.dao.FileAccessSupportFactory;
import oripa.persistence.dao.FileAccessSupportSelector;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.resource.StringID;
import oripa.util.file.FileFactory;

/**
 * Creates available file access supports for {@link Doc} file access.
 *
 * @author OUCHI Koji
 *
 */
public class DocFileAccessSupportSelectorFactory {

	public FileAccessSupportSelector<Doc> create(final FileFactory fileFactory) {
		var supports = new TreeMap<FileTypeProperty<Doc>, FileAccessSupport<Doc>>();
		var supportFactory = new FileAccessSupportFactory<Doc>();

		CreasePatternFileTypeKey key = CreasePatternFileTypeKey.OPX;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.Main.ORIPA_FILE_ID));

		key = CreasePatternFileTypeKey.PICT;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.Main.PICTURE_FILE_ID));

		key = CreasePatternFileTypeKey.FOLD;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.DXF;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.CP;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.SVG;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.PDF;
		supports.put(key, supportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		return new FileAccessSupportSelector<Doc>(supports, fileFactory);
	}
}