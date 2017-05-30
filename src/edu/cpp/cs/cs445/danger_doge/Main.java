package edu.cpp.cs.cs445.danger_doge;

/**
 * File: Main.java
 * Authors: Tom Lundeberg, Daniel Gamboa, Kevin Grossi, & Isolde Alfaro
 * Class: CS 445 - Computer Graphics
 * 
 * Assignment: Final Project - Checkpoint 3
 * Date last modified: May 29, 2017
 * 
 * Purpose: The Main class initializes the window and OpenGL rendering and
 *          serves as the entry point for our program.
 */

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Main {

    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    private static final int DISPLAY_HEIGHT = 480;
    private static final int DISPLAY_WIDTH = 640;    
    private static final int BITS_PER_PIXEL = 32;    
    
    private DisplayMode displayMode;
    private Camera camera;
    
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    /**
     * Main: constructor
     */
    public Main() {
        //Constructor
    }
    
    static {
      try {
        LOGGER.addHandler(new FileHandler("errors.log",true));
      }
      catch(IOException ex) {
        LOGGER.log(Level.WARNING,ex.toString(),ex);
      }
    }    
    
    /**
     * main: Calls window and prints key for keyboard and directions
     * 
     * @param args 
     */
    public static void main(String[] args) {
        Main main = null;
        try {
            System.out.println("Keys:");
            System.out.println("WASD      - Move");
            System.out.println("Spacebar  - Up");
            System.out.println("LShift    - Down");
            System.out.println("Esc       - Exit");            
            main = new Main();
            main.createWindow();
            main.initGL();
            main.run();
        } 
        catch (Exception e) {
            LOGGER.log(Level.SEVERE,e.toString(),e);
        }
        finally {
            if (main != null) {
                main.destroy();
            }
            System.exit(0);
        }
    }
    
    /**
     * createWindow: Creates the window for our 3d viewport
     * 
     * @throws Exception 
     */
    public void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == DISPLAY_WIDTH && d[i].getHeight() == DISPLAY_HEIGHT && d[i].getBitsPerPixel() == BITS_PER_PIXEL) {
                displayMode = d[i];
                break;
            }
        }
        
        Display.setDisplayMode(displayMode);
        Display.setTitle("TEAM DANGER DOGE!!");
        Display.create();
    }
    
    /**
     * initLightArrays: initializes the light source's initial position
     * 
     */    
    private void initLightArrays(){
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
        
    /**
     * destroy: Helper method for destroying the display
     * 
     */
    public void destroy() {
        Keyboard.destroy();
        Display.destroy();
    }

    /**
     * initGL: Initializes our openGL components
     * 
     */
    public void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);   //Specify background color (R,G,B,Alpha).
        glMatrixMode(GL_PROJECTION);    //Load camera using projection to view scene.
        glLoadIdentity();   //Load identity matrix and reset any previous projection matrices
        
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW); //Setup scene to Model view and rendering hints.
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        // Initializing things for Chunk
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        
        // Initializing things for Textures
        glEnable(GL_TEXTURE_2D);
        glEnableClientState (GL_TEXTURE_COORD_ARRAY);
        
        //Initializing things for Lighting
        initLightArrays();
        glLight(GL_LIGHT1, GL_POSITION, lightPosition);
        glLight(GL_LIGHT1, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT1, GL_DIFFUSE, whiteLight);
        glLight(GL_LIGHT1, GL_AMBIENT, whiteLight);
        
        
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT1);
        
    }
    
    /**
     * Run: Initializes our camera and triggers the game loop
     */
    public void run() {     
        camera = camera.getInstance();
        camera.gameLoop();
    }
}

