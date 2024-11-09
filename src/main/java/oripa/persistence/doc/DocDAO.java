package oripa.persistence.doc;

import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.dao.AbstractFileDAO;

/**
 *
 * load and save {@link oripa.doc.Doc} to/from file
 *
 * @author OUCHI Koji
 *
 *
 */
public class DocDAO extends AbstractFileDAO<DocEntity> {
	private final AbstractFileAccessSupportSelector<DocEntity> selector;

	public DocDAO(final AbstractFileAccessSupportSelector<DocEntity> selector) {
		this.selector = selector;
	}

	@Override
	public AbstractFileAccessSupportSelector<DocEntity> getFileAccessSupportSelector() {
		return selector;
	}
}
