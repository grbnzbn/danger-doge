# danger-doge

### Compilation and Running Instructions:
- To run this program simply download and open this project on NetBeans and have the OpenGL LWJGL libraries 
previously downloaded. 
- Expand the project and right click on the Libraries folder and add the LWJGL libraries. 
- Next right click on the project's node and select Properties. Under the Run category adjust the following to your computer and paste it the VM Options text box: -Djava.library.path=<lwjgl-X.X path>/native/<linux|macosx|solaris|windows>
- Lastly simply compile and run the program

### Check Point 1:
For this checkpoint a 3D cube with a each face of a different color is rendered. A user can manipulate the camera
by using the W, A, S, and D keys to move forward, left, backward, and right respectively. The space bar moves up 
and the left shift key moves down, and the esc key closes the window.

### Check Point 2:
For this checkpoint we implemented chunks of multiple textured cubes displaying the various types of terrain. Types of blocks include grass, sand, water dirt, stone and bedrock. The terrain slopes up and down.


### Final Check Point:
For this checkpoint the terrain is ordered to have only grass, sand, and water on the topmost layers while dirt and stone remain at the bottom. The terrain can be moved to enter a brightly lit or dimly lit world.  More key functions re implemented with F1 creating a terrain of all diamonds and F2 generating a terrain with underground taverns.
