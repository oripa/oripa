package oripa.persistence.filetool;

import java.io.IOException;
import java.util.function.Supplier;

public class SavingAction<Data> {
    private final Exporter<Data> exporter;

    private Supplier<Object> configSupplier;

    public SavingAction(final Exporter<Data> exporter) {
        this.exporter = exporter;
    }

    public final boolean save(final Data data, final String path)
            throws IOException, IllegalArgumentException {
        var saved = exporter.export(data, path, configSupplier == null ? null : configSupplier.get());

        return saved;
    }

    public SavingAction<Data> setConfig(final Supplier<Object> configSupplier) {
        this.configSupplier = configSupplier;
        return this;
    }

}