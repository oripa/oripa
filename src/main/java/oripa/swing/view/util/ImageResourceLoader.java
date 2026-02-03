package oripa.swing.view.util;

import java.net.URL;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageResourceLoader {
    private static final Logger logger = LoggerFactory.getLogger(ImageResourceLoader.class);

    public ImageIcon loadAsIcon(final String name) {
        return this.loadAsIcon(name, getClass());
    }

    public ImageIcon loadAsIcon(final String name, final Class<?> c) {
        logger.debug("icon name: {}", name);

        ClassLoader classLoader = c.getClassLoader();
        URL url = classLoader.getResource(name);

        ImageIcon icon = new ImageIcon(url);

        return icon;

    }
}
