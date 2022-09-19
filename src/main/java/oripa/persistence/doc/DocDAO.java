package oripa.persistence.doc;

import oripa.doc.Doc;
import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.dao.AbstractFileDAO;
import oripa.persistence.filetool.FileTypeProperty;

/**
 *
 * load and save {@link oripa.doc.Doc} to/from file
 *
 * @author OUCHI Koji
 *
 *
 */
public class DocDAO extends AbstractFileDAO<Doc> {
	private final AbstractFileAccessSupportSelector<Doc> selector;

	/**
	 * Constructor
	 *
	 * @param selector
	 *            If you don't use {@link #load(String)} and
	 *            {@link AbstractFileDAO#save(Doc, String, FileTypeProperty)},
	 *            {@code selector} can be null.
	 */
	public DocDAO(final AbstractFileAccessSupportSelector<Doc> selector) {
		this.selector = selector;
	}

	@Override
	public AbstractFileAccessSupportSelector<Doc> getFileAccessSupportSelector() {
		return selector;
	}
}
