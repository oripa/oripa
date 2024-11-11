package oripa.persistence.doc;

import oripa.persistence.dao.AbstractFileAccessSupportSelector;
import oripa.persistence.dao.FileDAO;

/**
 *
 * load and save {@link Doc} to/from file
 *
 * @author OUCHI Koji
 *
 *
 */
public class DocDAO extends FileDAO<Doc> {
	public DocDAO(final AbstractFileAccessSupportSelector<Doc> selector) {
		super(selector);
	}
}
