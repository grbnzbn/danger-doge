package edu.cpp.cs.cs445.danger_doge;

/**
 * File: Block.java
 * Authors: Tom Lundeberg, Daniel Gamboa, Kevin Grossi, & Isolde Alfaro
 * Class: CS 445 - Computer Graphics
 * 
 * Assignment: Final Project - Checkpoint 3
 * Date last modified: May 29, 2017
 * 
 * Purpose: This program displays a 3D blocks with various texture mapping for
 * different areas of the terrain
 */

import static org.lwjgl.opengl.GL11.*;

public class Block {
    
    private boolean IsActive;
    private BlockType Type;
    private float x, y, z;
    private static final float BLOCK_SIZE = 2.0f; // Used in our constructor for checkpoint1
    
    /**
     * BlockType: Various block types for terrain with ID getter and setter 
     * methods
     */
    public enum BlockType {
        BlockType_Grass(0), BlockType_Sand(1), 
        BlockType_Water(2), BlockType_Dirt(3),
        BlockType_Stone(4), BlockType_Bedrock(5),
        BlockType_Diamond(9);
        private int BlockID;
        
        BlockType(int i) {
            BlockID = i;
        }
        
        public int GetID() {
            return BlockID;
        }
        
        public void SetID(int i) {
            BlockID = i;
        }
    }
    
    /**
     * Block: constructor class
     * @param type 
     */
    public Block(BlockType type) {
        Type = type;
    }
    
    /**
     * setType: block type setter method
     * 
     * @param type 
     */
    public void setType(BlockType type) {
        Type = type;
    }
    
    /**
     * setCoords: coordinate setter method
     * @param x
     * @param y
     * @param z 
     */
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * IsActive: returns boolean for active blocks
     * @return 
     */
    public boolean IsActive() {
        return IsActive;
    }
    
    /**
     * SetActive: active boolean setter method
     * @param active 
     */
    public void SetActive(boolean active) {
        IsActive = active;
    }
    
    /**
     * GetID: ID setter method
     * @return 
     */
    public int GetID() {
        return Type.GetID();
    }
    
    /**
     * Block constructor: Creates a block with fixed colors and size
     *                    per the requirements of checkpoint 1
     */
    public Block(String ckpt) {
        
        if (ckpt == "ckpt1") {
            glEnable(GL_DEPTH_TEST);
            glBegin(GL_QUADS);

            //Top = Red
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f( BLOCK_SIZE, BLOCK_SIZE,-BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE, BLOCK_SIZE,-BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            glVertex3f( BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

            //Bottom = Green
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f( BLOCK_SIZE,-BLOCK_SIZE, BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE,-BLOCK_SIZE, BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE,-BLOCK_SIZE,-BLOCK_SIZE);
            glVertex3f( BLOCK_SIZE,-BLOCK_SIZE,-BLOCK_SIZE);

            //Front = Blue
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f( BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE,-BLOCK_SIZE, BLOCK_SIZE);
            glVertex3f( BLOCK_SIZE,-BLOCK_SIZE, BLOCK_SIZE);

            //Left = Cyan
            glColor3f(0.0f, 1.0f, 1.0f);
            glVertex3f(-BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE, BLOCK_SIZE,-BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE,-BLOCK_SIZE,-BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE,-BLOCK_SIZE, BLOCK_SIZE);

            //Back = Yellow
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f( BLOCK_SIZE,-BLOCK_SIZE,-BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE,-BLOCK_SIZE,-BLOCK_SIZE);
            glVertex3f(-BLOCK_SIZE, BLOCK_SIZE,-BLOCK_SIZE);
            glVertex3f( BLOCK_SIZE, BLOCK_SIZE,-BLOCK_SIZE);

            //Right = Purple
            glColor3f(1.0f, 0.0f, 1.0f);
            glVertex3f( BLOCK_SIZE, BLOCK_SIZE,-BLOCK_SIZE);
            glVertex3f( BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            glVertex3f( BLOCK_SIZE,-BLOCK_SIZE, BLOCK_SIZE);
            glVertex3f( BLOCK_SIZE,-BLOCK_SIZE,-BLOCK_SIZE);

            glEnd();
            glFlush();
        }
    }
}