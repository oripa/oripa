package oripa.persistence.doc;

import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.dao.AbstractFileDAO;

/**
 *
 * load and save {@link Doc} to/from file
 *
 * @author OUCHI Koji
 *
 *
 */
public class DocDAO extends AbstractFileDAO<Doc> {
	private final AbstractFileAccessSupportSelector<Doc> selector;

	public DocDAO(final AbstractFileAccessSupportSelector<Doc> selector) {
		this.selector = selector;
	}

	@Override
	public AbstractFileAccessSupportSelector<Doc> getFileAccessSupportSelector() {
		return selector;
	}
}
