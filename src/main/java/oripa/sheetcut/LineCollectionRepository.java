package oripa.sheetcut;

import java.util.Collection;

import oripa.value.OriLine;

public interface LineCollectionRepository {

	public abstract Collection<OriLine> getLines();

	public abstract void setLines(Collection<OriLine> lines) throws RepositorySetterError;

}