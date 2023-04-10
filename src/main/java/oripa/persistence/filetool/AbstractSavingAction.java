package oripa.persistence.filetool;

import java.io.IOException;
import java.util.function.Supplier;

public abstract class AbstractSavingAction<Data> {
	private String path;
	private Supplier<Object> configSupplier;

	public final AbstractSavingAction<Data> setPath(final String path) {
		this.path = path;
		return this;
	}

	public final String getPath() {
		return path;
	}

	public final boolean save(final Data data) throws IOException, IllegalArgumentException {
		beforeSave(data);
		var result = saveImpl(data, configSupplier == null ? null : configSupplier.get());
		afterSave(data);
		return result;
	}

	public void setConfig(final Supplier<Object> configSupplier) {
		this.configSupplier = configSupplier;
	}

	protected abstract void beforeSave(Data data);

	protected abstract void afterSave(Data data);

	protected abstract boolean saveImpl(Data data, Object confgObj) throws IOException, IllegalArgumentException;

}