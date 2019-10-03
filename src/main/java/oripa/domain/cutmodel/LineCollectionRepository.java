package oripa.domain.cutmodel;

import java.util.Collection;

import oripa.value.OriLine;

public interface LineCollectionRepository {

	/**
	 * 
	 * @return a line collection
	 */
	public abstract Collection<OriLine> getLines();

	/**
	 * This method should sets a copy of given collection.
	 * @param lines
	 * @throws RepositorySetterError
	 */
	public abstract void setLines(Collection<OriLine> lines) throws RepositorySetterError;

}