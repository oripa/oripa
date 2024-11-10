package oripa.persistence.filetool;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class AbstractSavingAction<Data> {
	private Supplier<Object> configSupplier;
	private BiConsumer<Data, String> beforeSave = (data, filePath) -> {
	};
	private BiConsumer<Data, String> afterSave = (data, filePath) -> {
	};

	public final boolean save(final Data data, final String path)
			throws IOException, IllegalArgumentException {
		beforeSave.accept(data, path);
		var saved = saveImpl(data, path, configSupplier == null ? null : configSupplier.get());
		if (saved) {
			afterSave.accept(data, path);
		}
		return saved;
	}

	public AbstractSavingAction<Data> setConfig(final Supplier<Object> configSupplier) {
		this.configSupplier = configSupplier;
		return this;
	}

	/**
	 *
	 * @param beforeSave
	 *            a consumer whose parameters are data and file path.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public AbstractSavingAction<Data> setBeforeSave(final BiConsumer<Data, String> beforeSave)
			throws IllegalArgumentException {
		if (beforeSave == null) {
			throw new IllegalArgumentException("null argument is not allowed.");
		}
		this.beforeSave = beforeSave;
		return this;
	}

	/**
	 *
	 * @param afterSave
	 *            a consumer whose parameters are data and file path.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public AbstractSavingAction<Data> setAfterSave(final BiConsumer<Data, String> afterSave)
			throws IllegalArgumentException {
		if (afterSave == null) {
			throw new IllegalArgumentException("null argument is not allowed.");
		}
		this.afterSave = afterSave;
		return this;
	}

	protected abstract boolean saveImpl(Data data, String path, Object configObj)
			throws IOException, IllegalArgumentException;

}