package oripa.util.history;

public class BasicUndoManager<Backup> extends AbstractUndoManager<Backup> {

    private final UndoInfoFactory<Backup> factory;

    public BasicUndoManager(final UndoInfoFactory<Backup> factory) {
        this.factory = factory;
    }

    /*
     * (non Javadoc)
     *
     * @see oripa.util.history.AbstractUndoManager#createClone(java.lang.Object)
     */
    @Override
    protected UndoInfo<Backup> createUndoInfo(final Backup info) {
        return factory.create(info);
    }
}
