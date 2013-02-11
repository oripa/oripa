ORIPA S
======

A great drawer ORIPA is originally composed by MITANI Jun for origami crease pattern.
"ORIPA S" fixes some bugs and provides function of saving as image.

Executable file: https://sourceforge.net/projects/oripas/files/


Known bugs
--------
* Getting slow for a massive data. (It is a  problem of implementation and computational time rather than a bug.)

What going on
---------
<!-- You can try the following(s) with unstable version. -->
* Erasing vertex automatically after deleting selected lines.
* Accept [Enter] input of division number.

Next challenge(s)
--------
* Drawing copyright on saved picture.
* Screenshot of folded model.

History of updates
-------

* __2012/10/07 Bug fix:__
   * Speed up: finding the nearest vertex, pasting lines and adding line. Maybe it won't hang up now. 
   (but slow in a test with 100*100 grid data.)
   * Bug fixed. The last update was incomplete :P
   * Correct origin position of second copy(cut)-and-paste.
<p></p>

* __2012/09/29 Bug fix:__
   * Cut-and-copy mode by Ctrl+x. (Actually the crash was not a bug. Closing window was a correct action defined by MITANI Jun.)
   * Behavior of right click on copy-and-paste mode becomes the same as original ORIPA. 
     It will finish copy & paste then get back to last selection mode ("Select" or mirror copy).
<p></p>

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
