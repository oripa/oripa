package oripa.domain.cutmodel;

import java.util.ArrayList;
import java.util.Collection;

import oripa.util.collection.CollectionCloneFactory;
import oripa.value.OriLine;

/**
 * This class holds the cut model outline.
 * The state of the outline in this class is never changed by others.
 * @author Koji
 *
 */
public class CutModelOutlineRepository implements LineCollectionRepository {

	private Collection<OriLine> lines = new ArrayList<>();

	static private CutModelOutlineFactory repository = new CutModelOutlineFactory();
	
	public static CutModelOutlineFactory getRepository() {
		return repository;
	}
	
	
	private CutModelOutlineRepository() {}
	
	/* (non Javadoc)
	 * @see oripa.sheetcut.LineCollectionRepository#getLines()
	 */
	@Override
	public Collection<OriLine> getLines() {
		return (Collection<OriLine>) lines;
	}


	/* (non Javadoc)
	 * @see oripa.sheetcut.LineCollectionRepository#setLines(java.util.Collection)
	 */
	@Override
	public void setLines(
			Collection<OriLine> lines) throws RepositorySetterError {

		CollectionCloneFactory<OriLine> cloneFactory =
				new CollectionCloneFactory<>();
	
		try {
			this.lines = cloneFactory.createCloneOf(lines, ArrayList.class);
		} catch (Exception e) {
			throw new RepositorySetterError(e);
		}
	}
}
