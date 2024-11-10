package oripa.persistence.filetool;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SavingAction<Data> {
	private final Exporter<Data> exporter;

	private Supplier<Object> configSupplier;
	private BiConsumer<Data, String> beforeSave = (data, filePath) -> {
	};
	private BiConsumer<Data, String> afterSave = (data, filePath) -> {
	};

	public SavingAction(final Exporter<Data> exporter) {
		this.exporter = exporter;
	}

	public final boolean save(final Data data, final String path)
			throws IOException, IllegalArgumentException {
		beforeSave.accept(data, path);
		var saved = exporter.export(data, path, configSupplier == null ? null : configSupplier.get());
		if (saved) {
			afterSave.accept(data, path);
		}
		return saved;
	}

	public SavingAction<Data> setConfig(final Supplier<Object> configSupplier) {
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
	public SavingAction<Data> setBeforeSave(final BiConsumer<Data, String> beforeSave)
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
	public SavingAction<Data> setAfterSave(final BiConsumer<Data, String> afterSave)
			throws IllegalArgumentException {
		if (afterSave == null) {
			throw new IllegalArgumentException("null argument is not allowed.");
		}
		this.afterSave = afterSave;
		return this;
	}

}