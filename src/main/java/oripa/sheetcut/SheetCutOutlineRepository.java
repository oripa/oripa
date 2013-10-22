package oripa.sheetcut;

import java.util.ArrayList;
import java.util.Collection;

import oripa.value.OriLine;

public class SheetCutOutlineRepository implements LineCollectionRepository {

	private Collection<OriLine> lines = new ArrayList<>();

	static private SheetCutOutlineFactory repository = new SheetCutOutlineFactory();
	
	public static SheetCutOutlineFactory getRepository() {
		return repository;
	}
	
	
	private SheetCutOutlineRepository() {}
	
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
