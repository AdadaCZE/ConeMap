When starting up, the program will ask for a .txt file in a default directory, otherwise you will be sent to default document directory.

Default directory is here -> Files(folder)

file format is made of two parts:

1. color part:
	here, you write IDs and assign colors to them as such:
	id = integer
	hue = hue value from color models such as hsv or hsb - decimal value between 0 and 1
	format:
	id/hue

	id 0 is reserved for white
	id -1 is reserved for black (background)

1. part and 2. part are separated by a line which only contains /

2. cone part:
	here, you write the properties of cones:
	x = x position of the cone - integer
	y = y position of the cone - integer
	rBottom = radius of base of the cone - integer
	rTip = radius of tip of the cone - integer
	z = height of the cone - decimal value between 0 and 1, between rBottom and rTip will decrease from given value to 0
	id = color id - cone will have thee color specified under this color id - integer
	format:
	x/y/rBottom/rTip/z/id

there are two example files provided.

By pressing the Import button, you will again be asked for a .txt file.

You can move your view by inputting x and y coordinates into designated text fields and pressing the Move button.

when two cones collide, the brighter(higher) value will win.

This program doesn't provide any way of creating new cones in the program, as it is designed to be a data visualizer, rather than a data editor.
This program is not robust, feeding it undesired data will simply stop it without any warning.
