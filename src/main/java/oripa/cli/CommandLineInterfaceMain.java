/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.cli;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import oripa.geom.GeomUtil;

/**
 * @author OUCHI Koji
 *
 */
public class CommandLineInterfaceMain {

	private static final String CONVERT = "convert";
	private static final String IMAGE = "image";
	private static final String INDEX = "index";
	private static final String REVERSE = "reverse";
	private static final String SPLIT = "split";
	private static final String FOLD = "fold";
	private static final String HELP = "help";

	private static final String CP_FILE = "cp-file";
	private static final String IMAGE_FILE = "image-file";
	private static final String FOLD_FILE = "fold-file";
	private static final String FRAME_INDEX = "frame-index";

	public void run(final String[] args) {
		Options options = new Options();

		var convertOption = Option.builder("c")
				.longOpt(CONVERT)
				.hasArg()
				.argName(CP_FILE)
				.desc("Convert crease pattern file format (opx, fold, cp) to other crease pattern format "
						+ "or crease pattern image (png or jpg). The argument is output file path.")
				.build();
		options.addOption(convertOption);

		var imageOption = Option.builder("i")
				.longOpt(IMAGE)
				.hasArg()
				.argName(IMAGE_FILE)
				.desc("Output image file (svg) of folded forms in multiple frame FOLD format. "
						+ "The argument is output file path."
						+ "This option requires --" + INDEX + " option.")
				.build();
		options.addOption(imageOption);

		var indexOption = Option.builder("n")
				.longOpt(INDEX)
				.hasArg()
				.argName(FRAME_INDEX)
				.desc("0-start Index of face order matrices. This option is to be used with --" + IMAGE + " option.")
				.build();
		options.addOption(indexOption);

		var reverseOption = Option.builder("r")
				.longOpt(REVERSE)
				.desc("Put this option if face order of the output image should be reversed.")
				.build();
		options.addOption(reverseOption);

		var splitOption = Option.builder("s")
				.longOpt(SPLIT)
				.desc("Put this option if the output of --" + FOLD + " should be single frame FOLD files.")
				.build();
		options.addOption(splitOption);

		var foldOption = Option.builder("f")
				.longOpt(FOLD)
				.hasArg()
				.argName(FOLD_FILE)
				.desc("Fold crease pattern file (opx, fold, cp) and save as a multipule frame FOLD format. "
						+ "The argument is output file path. If you specify --" + SPLIT + " option, "
						+ "the output will be single frame FOLD files and index will be inserted into file name as \"givenName.123.fold\".")
				.build();
		options.addOption(foldOption);

		var helpOption = Option.builder("h")
				.longOpt(HELP)
				.desc("Show help.")
				.build();
		options.addOption(helpOption);

		try {
			var parser = new DefaultParser();
			var line = parser.parse(options, args);

			// TODO read from option
			var pointEps = GeomUtil.pointEps();

			if (line.hasOption(HELP)) {
				var formatter = new HelpFormatter();
				formatter.printHelp("ORIPA command line tool.", options);
				return;
			}

			var pathArgList = line.getArgList();
			if (pathArgList.isEmpty()) {
				throw new IllegalArgumentException("No input file path.");
			}

			var inputFilePath = pathArgList.get(0);

			if (line.hasOption(convertOption)) {
				var outputFilePath = line.getOptionValue(convertOption);
				var converter = new CreasePatternFileConverter();
				converter.convert(inputFilePath, outputFilePath, pointEps);

			} else if (line.hasOption(imageOption)) {
				if (!line.hasOption(indexOption)) {
					throw new IllegalArgumentException("Need --" + indexOption.getLongOpt() + " option.");
				}
				var outputFilePath = line.getOptionValue(imageOption);
				var index = Integer.parseInt(line.getOptionValue(indexOption));
				var reverse = line.hasOption(reverseOption);
				var converter = new FoldedModelImageExporter();
				converter.export(inputFilePath, index, reverse, outputFilePath);

			} else if (line.hasOption(foldOption)) {
				var outputFilePath = line.getOptionValue(foldOption);
				var folder = new CommandLineFolder();
				var split = line.hasOption(splitOption);
				folder.fold(inputFilePath, split, outputFilePath, pointEps);

			} else if (line.getOptions().length == 0) {
				throw new IllegalArgumentException("No option is given. Hint: see help by -" + helpOption.getOpt());
			}

		} catch (Exception ex) {
			System.err.println("command line error: " + ex);
		}
	}
}
