package oripa.persistence.doc;

import java.util.HashMap;
import java.util.Map;

import oripa.persistence.dao.FileSelectionSupport;
import oripa.persistence.dao.FileSelectionSupportFactory;
import oripa.persistence.dao.FileSelectionSupportSelector;
import oripa.persistence.dao.FileType;
import oripa.persistence.filetool.FileAccessSupport;
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
public class DocFileSelectionSupportSelectorFactory {
	private final FileSelectionSupportFactory selectionSupportFactory = new FileSelectionSupportFactory();

	public FileSelectionSupportSelector<Doc> create(final FileFactory fileFactory) {
		var supports = new HashMap<FileType<Doc>, FileSelectionSupport<Doc>>();
		var accessSupportFactory = new FileAccessSupportFactory();

		CreasePatternFileTypeKey key = CreasePatternFileTypeKey.OPX;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.ORIPA_FILE_ID));

		key = CreasePatternFileTypeKey.PICT;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.PICTURE_FILE_ID));

		key = CreasePatternFileTypeKey.FOLD;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.DXF;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.CP;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.SVG;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		key = CreasePatternFileTypeKey.PDF;
		put(
				supports,
				key,
				accessSupportFactory.createFileAccessSupport(key, StringID.Main.FILE_ID));

		return new FileSelectionSupportSelector<Doc>(supports, selectionSupportFactory, accessSupportFactory,
				fileFactory);
	}

	private void put(final Map<FileType<Doc>, FileSelectionSupport<Doc>> supports,
			final FileTypeProperty<Doc> key, final FileAccessSupport<Doc> accessSupport) {
		supports.put(
				new FileType<>(key),
				selectionSupportFactory.create(accessSupport));

	}
}
