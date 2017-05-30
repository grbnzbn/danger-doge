package edu.cpp.cs.cs445.danger_doge;

/**
 * File: Camera.java
 * Authors: Tom Lundeberg, Daniel Gamboa, Kevin Grossi, & Isolde Alfaro
 * Class: CS 445 - Computer Graphics
 * 
 * Assignment: Final Project - Checkpoint 3
 * Date last modified: May 29, 2017
 * 
 * Purpose: The camera class creates a single instance of the camera and 
 *          allows user to manipulate the camera's position.
 */

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Camera {
    private static Camera INSTANCE;
    private Chunk chunk = new Chunk(0, 0, 0);
    
    private Vector3f position = null;
    private Vector3f lPosition = null;
    
    private float yaw = 0.0f;  
    private float pitch = 0.0f;
    
    private FloatBuffer lightPosition= BufferUtils.createFloatBuffer(4);
    
    
    
    /**
     * Camera: constructor with (0, 0, 0) as start location
     * 
     */
    private Camera()
    {
        position = new Vector3f(0, 0, 0);
        lPosition = new Vector3f(0, 0, 0);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
    }
    
    /**
     * Camera: constructor with specified start location
     * Instantiates position of Vector3f to the x y x parameters
     * 
     * @param x: x coordinate
     * @param y: y coordinate
     * @param z: z coordinate
     */
    private Camera(float x, float y, float z)
    {
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
    }
    
    /**
     * Singleton for the camera
     * 
     * @return: singleton instance
     */
    public static synchronized Camera getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Camera();
        }
        
        return INSTANCE;
    }
     
    /**
     * yaw: Increments the camera's current yaw rotation by the amount parameter
     * @param amount 
     */
    public void yaw(float amount)
    {
        yaw += amount;
    }
    
    /**
     * pitch: Increments the camera's current pitch rotation by the amount parameter
     * @param amount 
     */
    public void pitch(float amount)
    {
        pitch -= amount;
    }
    
    /**
     * walkForward: moves the camera forward relative to its current rotation (yaw)
     * 
     * @param distance 
     */
    public void walkForward(float distance)
    {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
        
        //Lighting movement
        
        lightPosition.put(lPosition.x-=xOffset).put(lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);        
    }
    
    /**
     * walkBackwards: moves the camera backward relative to its current rotation (yaw)
     * @param distance 
     */
    public void walkBackwards(float distance)
    {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
        
        //Lighting movement
       
        lightPosition.put(lPosition.x+=xOffset).put(lPosition.y).put(lPosition.z-=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    /**
     * strafeLeft: strafes the camera left relative to its current rotation (yaw)
     * 
     * @param distance 
     */
    public void strafeLeft(float distance)
    {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
        
        //Lighting movement
        
        lightPosition.put(lPosition.x-=xOffset).put(lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    /**
     * strafeRight: strafes the camera right relative to its current rotation (yaw)
     * 
     * @param distance 
     */
    public void strafeRight(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
        
        //Lighting movement
        
        lightPosition.put(lPosition.x-=xOffset).put(lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    /**
     * moveUp: moves the camera up relative to its current rotation (yaw)
     * 
     * @param distance 
     */
    public void moveUp(float distance)
    {
        position.y -= distance;
    }

    /**
     * moveDown: moves the camera down relative to its current rotation (yaw)
     * 
     * @param distance 
     */
    public void moveDown(float distance)
    {
        position.y += distance;
    }
    
    /**
     * lookThrough: translates and rotate the matrix so that it looks through 
     * the camera
     * 
     * Rotates the pitch around the X axis
     * Rotates the yaw around the Y axis
     * Translates to the position vector's location
     */
    public void lookThrough()
    {
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);
        
        //Lighting code
        FloatBuffer lightPosition= BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    /**
     * gameLoop: Our loop for rendering graphics onto the screen
     * 
     */
    public void gameLoop()
    {
        Camera camera = new Camera(0, 0, 0);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f; //length of frame
        float lastTime; // when the last frame was
        long time;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;
        //hide the mouse
        Mouse.setGrabbed(true);
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            time = Sys.getTime();
            lastTime = time;
            
            dx = Mouse.getDX(); // distance in mouse movement from the last getDX() call.
            dy = Mouse.getDY(); // distance in mouse movement from the last getDY() call.
            
            camera.yaw(dx * mouseSensitivity); //control camera yaw from x movement fromt the mouse
            camera.pitch(dy * mouseSensitivity); //control camera pitch from y movement fromt the mouse
            processKeyboard(camera, movementSpeed); //set the modelview matrix back to the identity
            glLoadIdentity(); 
            camera.lookThrough(); // look through the camera before you draw anything
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //render();
            chunk.render();
            Display.update();
            Display.sync(60);
            }
        Display.destroy();
    }
    
    /**
     * processKeyboard: Keyboard handler method; recomputes camera position based on user input
     * Changes terrain to all diamonds or adds underground taverns to the terrain
     * 
     * @param camera: our camera
     * @param speed: movement speed of camera
     */
    private void processKeyboard(Camera camera, float speed) {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            camera.walkForward(speed);
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_A)){
            camera.strafeLeft(speed);
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_S)){
            camera.walkBackwards(speed);
        }   
        
        if (Keyboard.isKeyDown(Keyboard.KEY_D)){
            camera.strafeRight(speed);
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            camera.moveUp(speed);
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            camera.moveDown(speed);
        }  
        
        if (Keyboard.isKeyDown(Keyboard.KEY_F1)){
            chunk.rebuildWithDiamonds(0, 0, 0);
        }  
        
        if (Keyboard.isKeyDown(Keyboard.KEY_F2)){
            chunk.rebuildWithCaverns(0, 0, 0);
        } 
    }
    
    /**
     * Render:  Renders a single block onto the screen
     */
    private void render() {
        try {
            Block b = new Block("ckpt1");
        } catch(Exception e) { }
    }
}
