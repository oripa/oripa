package oripa.doc;

import java.awt.Component;

import oripa.file.AbstractSavingAction;
import oripa.file.FileAccessSupportFilter;
import oripa.file.FileChooser;
import oripa.file.FileChooserFactory;
import oripa.file.FileVersionError;

public class DocDAO {

	// -----------------------------------------------------

	public Doc load(String path) throws FileVersionError {
		DocFilterSelector selecter = new DocFilterSelector();

		Object loaded = selecter.getLoadableFilterOf(path).getLoadingAction()
				.load();

		if (loaded instanceof Doc) {
			return (Doc) loaded;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public void save(Doc doc, String path, FileTypeKey type) {
		DocFilterSelector selecter = new DocFilterSelector();

		selecter.getFilter(type).getSavingAction().setPath(path).save(doc);
	}

	/**
	 * 
	 * @param doc
	 * @param homePath
	 * @param filters
	 * @param parent
	 * @return chosen path
	 */
	public String saveWithGUI(Doc doc, String homePath,
			FileAccessSupportFilter<Doc>[] filters,
			Component parent) {
		FileChooserFactory<Doc> chooserFactory = new FileChooserFactory<>();
		FileChooser<Doc> chooser = chooserFactory.createChooser(homePath,
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

	public Doc loadWithGUI(String homePath,
			FileAccessSupportFilter<Doc>[] filters, Component parent)
			throws FileVersionError {
		FileChooserFactory<Doc> factory = new FileChooserFactory<>();
		FileChooser<Doc> fileChooser = factory.createChooser(
				homePath, filters);

		// set opx as the default filter
		fileChooser.setFileFilter(findDefaultFilter(filters));

		return fileChooser.getActionForLoadingFile(parent).load();

	}

	private FileAccessSupportFilter<Doc> findDefaultFilter(
			FileAccessSupportFilter<Doc>[] filters) {

		for (FileAccessSupportFilter<Doc> filter : filters) {
			if (filter.getTargetType() == FileTypeKey.OPX) {
				return filter;
			}
		}

		return filters[0];
	}

}
