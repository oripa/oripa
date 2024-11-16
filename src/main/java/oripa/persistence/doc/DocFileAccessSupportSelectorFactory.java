package oripa.persistence.doc;

import java.util.TreeMap;

import oripa.persistence.dao.FileSelectionSupport;
import oripa.persistence.dao.FileSelectionSupportFactory;
import oripa.persistence.dao.FileSelectionSupportSelector;
import oripa.persistence.filetool.FileAccessSupportFactory;
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
	private final FileSelectionSupportFactory selectionSupportFactory = new FileSelectionSupportFactory();

	public FileSelectionSupportSelector<Doc> create(final FileFactory fileFactory) {
		var supports = new TreeMap<FileTypeProperty<Doc>, FileSelectionSupport<Doc>>();
		var accessSupportFactory = new FileAccessSupportFactory<Doc>();

		CreasePatternFileTypeKey key = CreasePatternFileTypeKey.OPX;
		supports.put(
				key,
				new FileSelectionSupport<Doc>(
						accessSupportFactory.createFileAccessSupport(key, StringID.Main.ORIPA_FILE_ID)));

		key = CreasePatternFileTypeKey.PICT;
		supports.put(
				key,
				new FileSelectionSupport<Doc>(
						accessSupportFactory.createFileAccessSupport(key, StringID.Main.PICTURE_FILE_ID)));

		key = CreasePatternFileTypeKey.FOLD;
		supports.put(key, new FileSelectionSupport<Doc>(
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID)));

		key = CreasePatternFileTypeKey.DXF;
		supports.put(key, new FileSelectionSupport<Doc>(
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID)));

		key = CreasePatternFileTypeKey.CP;
		supports.put(key, new FileSelectionSupport<Doc>(
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID)));

		key = CreasePatternFileTypeKey.SVG;
		supports.put(key, new FileSelectionSupport<Doc>(
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID)));

		key = CreasePatternFileTypeKey.PDF;
		supports.put(key, new FileSelectionSupport<Doc>(
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID)));

		return new FileSelectionSupportSelector<Doc>(supports, selectionSupportFactory, fileFactory);
	}
}
