ORIPA S
======

A great drawer ORIPA is originally composed by MITANI Jun for origami crease pattern.
"ORIPA S" fixes some bugs and provides function of saving as image.

Executable file(stable): https://dl.dropbox.com/u/45296904/oripaS1_21.jar

For developer: By my mistake, the master branch is now including bug on right click action.

History of updates
-------
The latest is of stable version.

* __2012/09/17 Bug fix:__
    * Behavior of right click on copy-and-paste mode becomes similar to original ORIPA. It will get back to "Select" mode.
<p></p>


* __2012/09/08 Improve usability:__
    * On copy-and-paste mode, you can select the origin vertex by pressing Ctrl key.
<p></p>

* __2012/08/28 Bug fix, improve usability:__
    * Undo of changing line type is fixed.
    * Ctrl + z acts as undo command the same as right mouse button.
    * On mirror-copy mode, you can select multi-lines by Dragging.
    * On delete-line mode, you can delete multi-lines by Dragging.	
    * It comfirms to save on closing window if you have edited.
    * Codes for inputting line are refactored (just partially).
<p></p>
 	

* __2012/07/15 Bug fix:__
    * Codes for inputting line are refactored (just partially).
    * Undo of mirror copy is available.
    * It corrects a problem that the color of selected line remains green.
<p></p>
 
 
* __2012/06/23 Saving as JPG or PNG is available.__
<p></p>


Known bugs
--------
* crashing when you input Ctrl + X.
* behavior of editing contour is different from the original ORIPA.

Next challenge(s)
--------
* drawing copyright on saved picture.

What going on
---------
You can try the followings with unstable version.
* Cut-and-copy mode by Ctrl+x. 
* Emulating the original behavior of right click on copy-and-paste mode and editing contour.


The original read-me text by MITANI Jun:
----
ORIPA v0.35 (2012/06/17) by Jun Mitani (jmitani@gmail.com)

Distribution version

#### Contents:
- source code folder (file encoding: UTF-8);
- ORIPA jar file;
- ORIPA User Manual;

Source code is written in Java for JRE1.7.
Java3D is required.

#### ORIPA - Origami Pattern Editor 

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



#### Changes from the last version:

- Added icon for the Main Window;
- Added export SGV functionality for crease pattern;
- Added export SGV functionality for folded origami;


#### Acknowledgments

I appreciate the help received from Hugo Alves Akitaya for releasing the source codes.
