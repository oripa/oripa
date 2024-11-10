package oripa.persistence.filetool;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class AbstractSavingAction<Data> {
	private String path;
	private Supplier<Object> configSupplier;
	private BiConsumer<Data, String> beforeSave = (data, filePath) -> {
	};
	private BiConsumer<Data, String> afterSave = (data, filePath) -> {
	};

	public final AbstractSavingAction<Data> setPath(final String path) {
		this.path = path;

		return this;
	}

	public final String getPath() {
		return path;
	}

	public final boolean save(final Data data) throws IOException, IllegalArgumentException {
		beforeSave.accept(data, path);
		var saved = saveImpl(data, configSupplier == null ? null : configSupplier.get());
		if (saved) {
			afterSave.accept(data, path);
		}
		return saved;
	}

	public AbstractSavingAction<Data> setConfig(final Supplier<Object> configSupplier) {
		this.configSupplier = configSupplier;
		return this;
	}

	public AbstractSavingAction<Data> setBeforeSave(final BiConsumer<Data, String> beforeSave)
			throws IllegalArgumentException {
		if (beforeSave == null) {
			throw new IllegalArgumentException("null argument is not allowed.");
		}
		this.beforeSave = beforeSave;
		return this;
	}

	public AbstractSavingAction<Data> setAfterSave(final BiConsumer<Data, String> afterSave)
			throws IllegalArgumentException {
		if (afterSave == null) {
			throw new IllegalArgumentException("null argument is not allowed.");
		}
		this.afterSave = afterSave;
		return this;
	}

	protected abstract boolean saveImpl(Data data, Object configObj) throws IOException, IllegalArgumentException;

}