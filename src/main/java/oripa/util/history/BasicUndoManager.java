package oripa.util.history;


public class BasicUndoManager<Backup> extends AbstractUndoManager<Backup> {

	private UndoInfoFactory<Backup> factory;
	
	
	public BasicUndoManager(UndoInfoFactory<Backup> factory) {
		this.factory = factory;
	}
	
	public BasicUndoManager(UndoInfoFactory<Backup> factory, int max) {
		this.factory = factory;
		this.max = max;
	}

	/* (non Javadoc)
	 * @see oripa.util.history.AbstractUndoManager#createClone(java.lang.Object)
	 */
	@Override
	protected UndoInfo<Backup> createUndoInfo(Backup info) {
		return factory.create(info);
	}
}
