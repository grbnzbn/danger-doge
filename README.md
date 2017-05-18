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
Your program should still be able to do all from the above checkpoint. In addition to the above
requirements your program should now be able to draw multiple cubes using our chunks method
(creating a world at least 30 cubes x 30 cubes large), with each cube textured and then randomly placed
using the simplex noise classes provided (Your terrain should be randomly placed each time you run the
program but still appear to smoothly rise and fall as opposed to sudden mountains and valleys
appearing). Finally, your program should have a minimum of 6 cube types defined with a different
texture for each one as follows: Grass, sand, water, dirt, stone, and bedrock.

### Final Check Point:
Your program should still be able to do all from the above checkpoints. In addition to the above
requirements your program should now be able to correctly place only grass, sand, or water at the
topmost level of terrain, dirt, or stone at levels below the top, and bedrock at the very bottom of the
generated terrain. A light source should be created that will leave half the world brightly lit and the other
half dimly illuminated.
