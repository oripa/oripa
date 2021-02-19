package oripa.persistent.doc;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

import oripa.doc.Doc;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.fold.halfedge.OrigamiModelFactory;
import oripa.persistent.filetool.AbstractSavingAction;
import oripa.persistent.filetool.FileAccessActionProvider;
import oripa.persistent.filetool.FileAccessSupportFilter;
import oripa.persistent.filetool.FileChooser;
import oripa.persistent.filetool.FileChooserCanceledException;
import oripa.persistent.filetool.FileChooserFactory;
import oripa.persistent.filetool.FileVersionError;
import oripa.persistent.filetool.WrongDataFormatException;

/**
 *
 * load and save {@link oripa.doc.Doc} to/from file
 *
 * @author OUCHI Koji
 *
 *
 */
public class DocDAO {
	private final DocFilterSelector selector = new DocFilterSelector();

	/**
	 * try loading file {@code path}
	 *
	 * @param path
	 *            for the file to be loaded.
	 * @return loaded doc.
	 * @throws FileVersionError
	 * @throws IOException
	 *             file IO trouble.
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             {@code path} is not correct.
	 * @throws WrongDataFormatException
	 */
	public Doc load(final String path)
			throws FileVersionError, IOException, FileNotFoundException, IllegalArgumentException,
			WrongDataFormatException {

		var file = new File(path);

		if (!file.exists()) {
			throw new FileNotFoundException(path + " doesn't exist.");
		}

		var loadingAction = selector.getLoadableFilterOf(path).getLoadingAction();

		return loadingAction.setPath(path).load();
	}

	/**
	 * save without dialog
	 *
	 * @param doc
	 *            to be saved.
	 * @param path
	 *            for the place to save the {@code doc}.
	 * @param type
	 *            file type.
	 * @throws IOException
	 *             file IO trouble.
	 * @throws IllegalArgumentException
	 *             {@code doc} can't be saved as the suggested file type.
	 */
	public void save(final Doc doc, final String path, final CreasePatternFileTypeKey type)
			throws IOException, IllegalArgumentException {

		var savingAction = selector.getFilter(type).getSavingAction();

		savingAction.setPath(path).save(doc);
	}

	/**
	 * open save dialog for file types in {@code filters}
	 *
	 * @param doc
	 *            to be saved
	 * @param homePath
	 *            starting path to display
	 * @param parent
	 * @param filters
	 * @return chosen path
	 * @throws FileChooserCanceledException
	 * @throws IOException
	 *             file IO trouble.
	 * @throws IllegalArgumentException
	 *             the filter chosen from {@code filters} by user accepts the
	 *             selected file but is not for saving the file.
	 */
	public String saveUsingGUI(final Doc doc, final String homePath,
			final Component parent,
			final FileAccessSupportFilter<Doc>... filters)
			throws FileChooserCanceledException, IOException, IllegalArgumentException {
		FileChooserFactory<Doc> chooserFactory = new FileChooserFactory<>();
		FileAccessActionProvider<Doc> chooser = chooserFactory.createChooser(
				homePath, filters);

		try {
			AbstractSavingAction<Doc> saver = chooser.getActionForSavingFile(parent);
			saver.save(doc);
			return saver.getPath();
		} catch (IllegalStateException e) {
			throw new IllegalArgumentException("Wrong filter(s) is(are) given.", e);
		}
	}

	/**
	 * open save dialog and perform foldability check of the model
	 *
	 * @param doc
	 *            to be saved
	 * @param owner
	 * @param filter
	 * @throws FileChooserCanceledException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void saveUsingGUIWithModelCheck(final Doc doc, final Component owner,
			final FileAccessSupportFilter<Doc> filter)
			throws FileChooserCanceledException, IOException, IllegalArgumentException {
		CreasePatternInterface creasePattern = doc.getCreasePattern();

		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		var origamiModel = modelFactory.createOrigamiModel(
				creasePattern, creasePattern.getPaperSize());
		doc.setOrigamiModel(origamiModel);

		if (!origamiModel.isProbablyFoldable()) {

			var selection = JOptionPane.showConfirmDialog(null,
					"Warning: Building a set of polygons from crease pattern "
							+ "was failed.",
					"Warning", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);

			if (selection == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}

		saveUsingGUI(doc, null, owner, filter);
	}

	/**
	 * open dialog to load file
	 *
	 * @param homePath
	 *            starting path
	 * @param filters
	 *            supported file types
	 * @param parent
	 * @return loaded doc.
	 * @throws FileVersionError
	 * @throws FileChooserCanceledException
	 * @throws IllegalArgumentException
	 *             the filter chosen from {@code filters} by user accepts the
	 *             selected file but is not for loading the file.
	 * @throws IOException
	 *             file IO trouble.
	 * @throws FileNotFoundException
	 *             selected file doesn't exist.
	 * @throws WrongDataFormatException
	 *             loading failed because of data format problem.
	 */
	public Doc loadUsingGUI(final String homePath,
			final FileAccessSupportFilter<Doc>[] filters, final Component parent)
			throws FileVersionError, FileChooserCanceledException, IllegalArgumentException,
			IOException, FileNotFoundException, WrongDataFormatException {
		FileChooserFactory<Doc> factory = new FileChooserFactory<>();
		FileChooser<Doc> fileChooser = factory.createChooser(
				homePath, filters);

		try {
			return fileChooser.getActionForLoadingFile(parent).load();
		} catch (IllegalStateException e) {
			throw new IllegalArgumentException("Wrong filter(s) is(are) given.", e);
		}

	}
}
