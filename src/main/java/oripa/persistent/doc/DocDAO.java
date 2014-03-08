package oripa.persistent.doc;

import java.awt.Component;
import java.io.IOException;

import oripa.persistent.filetool.AbstractSavingAction;
import oripa.persistent.filetool.FileAccessActionProvider;
import oripa.persistent.filetool.FileAccessSupportFilter;
import oripa.persistent.filetool.FileChooser;
import oripa.persistent.filetool.FileChooserCanceledException;
import oripa.persistent.filetool.FileChooserFactory;
import oripa.persistent.filetool.FileVersionError;

public class DocDAO {

	// -----------------------------------------------------

	public Doc load(final String path) throws FileVersionError, IOException {
		DocFilterSelector selecter = new DocFilterSelector();

		Object loaded = selecter.getLoadableFilterOf(path).getLoadingAction()
				.load();

		if (loaded instanceof Doc) {
			return (Doc) loaded;
		}

		throw new IOException("Wrong file");

	}

	@SuppressWarnings("unchecked")
	public void save(final Doc doc, final String path, final FileTypeKey type) {
		DocFilterSelector selecter = new DocFilterSelector();

		selecter.getFilter(type).getSavingAction().setPath(path).save(doc);
	}

	/**
	 * 
	 * @param doc
	 * @param homePath
	 * @param parent
	 * @param filters
	 * @return chosen path
	 */
	public String saveUsingGUI(final Doc doc, final String homePath,
			final Component parent,
			final FileAccessSupportFilter<Doc>... filters)
			throws FileChooserCanceledException {
		FileChooserFactory<Doc> chooserFactory = new FileChooserFactory<>();
		FileAccessActionProvider<Doc> chooser = chooserFactory.createChooser(homePath,
				filters);

		AbstractSavingAction<Doc> saver = chooser
				.getActionForSavingFile(parent);

		if (saver.getPath() != null) {
			// if(path.endsWith(".opx")){
			// ORIPA.doc.setDataFilePath(path);
			// ORIPA.doc.clearChanged();
			//
			// updateMenu(path);
			// }
		} else {
			saver.setPath(homePath);
		}

		saver.save(doc);

		return saver.getPath();

	}

	public Doc loadUsingGUI(final String homePath,
			final FileAccessSupportFilter<Doc>[] filters, final Component parent)
			throws FileVersionError, FileChooserCanceledException {
		FileChooserFactory<Doc> factory = new FileChooserFactory<>();
		FileChooser<Doc> fileChooser = factory.createChooser(
				homePath, filters);

		// set opx as the default filter
		// fileChooser.setFileFilter(findDefaultFilter(filters));

		return fileChooser.getActionForLoadingFile(parent).load();

	}

	private FileAccessSupportFilter<Doc> findDefaultFilter(
			final FileAccessSupportFilter<Doc>[] filters) {

		for (FileAccessSupportFilter<Doc> filter : filters) {
			if (filter.getTargetType() == FileTypeKey.OPX) {
				return filter;
			}
		}

		return filters[0];
	}

}
