# ORIPA: Origami Pattern Editor

ORIPA is a drawing software dedicated to designing the crease patterns of origami. The unique feature of ORIPA is calculation of the folded shape from the pattern.

The first version of ORIPA was released in 2005. ORIPA was made open source in 2012, and was pushed to Github in 2013.

To find out more about using the software, visit the [ORIPA project's website](http://mitani.cs.tsukuba.ac.jp/oripa/).


## Functionalities

* Various methods to input lines.
* You can save your crease pattern and folded model as a JPG or PNG image file.
* Cut by Ctrl + x or copy by Ctrl + c, paste by clicking left mouse button.
  On pasting mode, you can select the origin vertex by pressing Ctrl key.
* Importing other crease pattern is available for any acceptable files such as .opx and .cp.
* Undo by pressing Ctrl + z or clicking right mouse button.
* Redo by pressing Ctrl + y.
* You can select multi-lines by Dragging.
* After selecting lines, you can scale the selected lines by scale mode.
* On delete-line mode, you can delete multi-lines by Dragging.	
* The folded shape can be saved as SVG. In the output, each face keeps the pre-creases (Auxiliary lines) on it.
* Multiple crease patterns are supported. However, note that you need to save as single crease pattern if you would like to export the data as other than .opx.

## Download

### Executable jar

You can download the executable jar file at the [release page](https://github.com/oripa/oripa/releases).

To run ORIPA, **you need install JDK 21 or above.** (ver. 1.69 or older can be run on JDK 11.)

Once downloaded, to run ORIPA in a terminal just run:

```sh
java -jar ./oripa-1.70.jar
```

### Executable binary

Ver. 1.67 or above are also compiled into executable binary file for each platform.
We offer installer files of those. In this case, you don't have to install JDK.

The installation directory is platform-specific:

* On Linux, it is /opt/oripa
* On macOS, it is /Applications/oripa
* On Windows, it is c:\Program Files\oripa


## Command line interface (CLI)

If you give some command line option to ORIPA ver.1.64 or above, ORIPA will run as a command line application.
The CLI mode is designed as a file I/O application and available on jar only. The order of command arguments is interchangeable.

Examples:

```sh
# Show help.
java -jar ./oripa-1.70.jar --help
```

```sh
# Crease pattern file format conversion.
java -jar ./oripa-1.70.jar --convert converted.fold cp.opx
# Short version with a different order of options.
java -jar ./oripa-1.70.jar cp.opx -c converted.fold 

# FOLD is also acceptable input. Image conversion is available.
java -jar ./oripa-1.70.jar --convert converted.png cp.fold
```

```sh
# Fold the crease pattern and output as a multiple frame FOLD.
java -jar ./oripa-1.70.jar --fold folded.fold cp.opx
```

```sh
# Output an image of the folded model at the given index.
java -jar ./oripa-1.70.jar --image folded.svg -n 0 folded.fold
```



## Plug-in system

ORIPA ver.1.63 or above provides plug-in system using Java's default `ServiceLoader`.
You can add a line-input functionality by:

* Implement `oripa.gui.presenter.plugin.GraphicMouseActionPlugin` in a project different from the ORIPA's main project. If you use Maven, you need install ORIPA into your Maven local repository by `mvn install` and add the dependency in your plug-in project's pom.xml.
* Add resources/META-INF/services/oripa.gui.presenter.plugin.GraphicMouseActionPlugin that contains
  the FQN of your implementation class(es) of the interface.
* Make a jar and add it to classpath. The command will be like: `java -cp oripa-1.70.jar;your-plugin.jar oripa.ORIPA`.

## Algorithms

See [here](https://github.com/oripa/oripa/blob/master/documents/algorithms.md).

## Next challenge(s)

* Dividing lines by an input circle.
* Drawing copyright on saved picture.

# The original read-me text by MITANI Jun:

ORIPA v0.35 (2012/06/17) by Jun Mitani (jmitani@gmail.com)

Distribution version

## Contents:  
* source code folder (file encoding: UTF-8);  
* ORIPA jar file;  
* ORIPA User Manual;

Source code is written in Java for JRE1.7.
Java3D is required.

Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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


## Acknowledgments

I appreciate the help received from Hugo Alves Akitaya for releasing the source codes.
