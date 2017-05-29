package edu.cpp.cs.cs445.danger_doge;
/**
 * File: Chunk.java
 * Authors: Tom Lundeberg, Daniel Gamboa, Kevin Grossi, & Isolde Alfaro
 * Class: CS 445 - Computer Graphics
 * 
 * Assignment: Final Project - Checkpoint 2
 * Date last modified: May 18, 2017
 * 
 * Purpose: The chunk method generates N numbers of cubes to be rendered 
 *          onto the screen simultaneously in one call to the graphics 
 *          card to ensure efficient graphics rendering
 */

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;      // Add this jar to your Libraries:
import org.newdawn.slick.util.ResourceLoader;       // http://slick.ninjacave.com/slick-util/

public class Chunk {
    static final int CHUNK_SIZE = 60;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    
    // Texture variables
    private int VBOTextureHandle;
    private Texture texture;
    
    // Noise variables
    private SimplexNoise noise;
    
    /**
     * Chunk Constructor, creates a chunk of blocks based on our global params
     * 
     * @param startX: starting x coord for chunk
     * @param startY: starting y coord for chunk
     * @param startZ: starting z coord for chunk
     */
    public Chunk (int startX, int startY, int startZ) {
        try { // Try loading in the texture map
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.out.print("Error loading texture");
        }
        
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    if (r.nextFloat() > 0.8f) { // Generate block types randomly based on probability
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    } else if (r.nextFloat() > 0.6f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    } else if (r.nextFloat() > 0.4f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);    
                    } else if (r.nextFloat() > 0.2f) { 
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    } else if (r.nextFloat() > 0.1f) { 
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    } else {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);   // Treating bedrock as default
                    }
                }
            }
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }

    /**
     * render: Renders our blocks onto the screen and applies their respective
     *         textures
     * 
     */
    public void render() {
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);            // Applying texture
            glTexCoordPointer(2, GL_FLOAT, 0, 0L);      // Applying texture
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    /**
     * rebuildMesh: organizes where the blocks will actually be placed within the chunk
     * 
     * @param startX: starting x coord for chunk
     * @param startY: starting y coord for chunk
     * @param startZ: starting z coord for chunk
     */
    public void rebuildMesh(float startX, float startY, float startZ) {
        noise = new SimplexNoise(20, 0.2, r.nextInt());   // using next int to generate random seed
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();  // Grab textures
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE)* 6 * 12);
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                float noiseHeight = ((int)startY + (int)(15 * noise.getNoise((int) x, (int) startY, (int) z)) * CUBE_LENGTH) + 10;
                if (noiseHeight > 30) noiseHeight = 30; // hard ceil
                System.out.println(noiseHeight);
                
                for (float y = 0; y < noiseHeight; y++) {     // randomized height
                    if (y == 0) { // bottom layer handling
                        if (r.nextFloat() < 0.4f) { // 30% stone at bottom
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Stone);                            
                        }
                        
                        else if (r.nextFloat() < 0.1f) { // 10% dirt at bottom
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Dirt);                            
                        }
                        
                        else {  // rest bedrock
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Bedrock);
                        }
                    }
                    
                    else if (y == noiseHeight - 1) {    // top layer handling
                        if (r.nextFloat() < 0.4f) { // 30% sand
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Sand);                            
                        }
                        
                        else if (r.nextFloat() < 0.1f) { // 10% water
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Water);                            
                        }
                        
                        else {  // rest grass
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Grass);
                        }
                    }
                  
                    else {  // everything inbetween
                        if (r.nextFloat() < 0.5) {  // half stone
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Stone);  
                        }
                        
                        else {  // half dirt
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Dirt);   
                        }                        
                    }
                    
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + (int)(CHUNK_SIZE * .8)),
                                                      (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int)(x)][(int) (y)][(int) (z)]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        System.out.println("finished generating terrain");
    }
    
    public void rebuildWithDiamonds(float startX, float startY, float startZ) {
        noise = new SimplexNoise(20, 0.2, r.nextInt());   // using next int to generate random seed
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();  // Grab textures
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE)* 6 * 12);
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                float noiseHeight = ((int)startY + (int)(15 * noise.getNoise((int) x, (int) startY, (int) z)) * CUBE_LENGTH) + 10;
                if (noiseHeight > CHUNK_SIZE) noiseHeight = CHUNK_SIZE; // hard ceil
                System.out.println(noiseHeight);
                
                for (float y = 0; y < noiseHeight; y++) {     // randomized height
                    Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Diamond); 
                    
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + (int)(CHUNK_SIZE * .8)),
                                                      (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int)(x)][(int) (y)][(int) (z)]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    public void rebuildWithCaverns(float startX, float startY, float startZ) {
        SimplexNoise baseNoise = new SimplexNoise(10, 0.15, r.nextInt());   // generating the base terrain for the cavern
        SimplexNoise cavNoise = new SimplexNoise(10, 0.2, r.nextInt());   // for generating empty space for the cavern
        SimplexNoise surfaceNoise = new SimplexNoise(10, 0.2, r.nextInt());   // for generating the surface terrain after the cavern
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();  // Grab textures
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE)* 6 * 12);
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                float baseHeight = ((int)startY + (int)(15 * baseNoise.getNoise((int) x, (int) startY, (int) z)) * CUBE_LENGTH) + 10;
                float cavernHeight = ((int)startY + (int)(15 * cavNoise.getNoise((int) x, (int) startY, (int) z)) * CUBE_LENGTH) + 20;
                float surfaceHeight = ((int)startY + (int)(15 * surfaceNoise.getNoise((int) x, (int) startY, (int) z)) * CUBE_LENGTH) + 30;
                System.out.println("Base: " + baseHeight);
                System.out.println("Cav: " + cavernHeight);
                System.out.println("Surf: " + surfaceHeight);
                
                if (surfaceHeight >= 60) surfaceHeight = 59;
                
                for (float y = 0; y < baseHeight; y++) {     // randomized height
                    if (y == 0) { // bottom layer handling
                        if (r.nextFloat() < 0.4f) { // 30% stone at bottom
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Stone);                            
                        }
                        
                        else if (r.nextFloat() < 0.1f) { // 10% dirt at bottom
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Dirt);                            
                        }
                        
                        else {  // rest bedrock
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Bedrock);
                        }
                    }
                   
                    else {  // everything inbetween
                        if (r.nextFloat() < 0.8) {  // 80% stone
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Stone);  
                        }
                        
                        else {  // rest dirt
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Dirt);   
                        }                        
                    }
                    
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + (int)(CHUNK_SIZE * .8)),
                                                      (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int)(x)][(int) (y)][(int) (z)]));
                }
                    
                for (float y = cavernHeight; y < surfaceHeight; y++) { 
                    if (y == surfaceHeight - 1) {    // top layer handling
                        if (r.nextFloat() < 0.4f) { // 30% sand
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Sand);                            
                        }

                        else if (r.nextFloat() < 0.1f) { // 10% water
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Water);                            
                        }

                        else {  // rest grass
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Grass);
                        }
                    }

                    else {  // everything inbetween
                        if (r.nextFloat() < 0.5) {  // half stone
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Stone);  
                        }

                        else {  // half dirt
                            Blocks[(int) x][(int) y][(int) z].setType(Block.BlockType.BlockType_Dirt);   
                        }                        
                    }
                
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + (int)(CHUNK_SIZE * .8)),
                                                      (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int)(x)][(int) (y)][(int) (z)]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        System.out.println("finished generating terrain");
        
    }
    
    /**
     * createCubeVertexCol: generates colors for our cubes, not used currently as
     *                      our cubes will have textures
     * 
     */
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    /**
     * createCube: Creates a cube at the specified coordinate
     * 
     * @param x: x location for cube
     * @param y: y location for cube
     * @param z: z location for cube
     * @return: cube in float[] form
     */
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z };
    }
    
    /**
     * getColorCube: returns a color for the cube, since we are using textures
     *               this just always returns white
     * 
     */
    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
    }
    
    /**
     * createTexCube: creates a textured cube which grabs the textures from the 
     *                texture map based on the provided offset
     * 
     */
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f / 16) / 1024f;    // width of texture map by number of textures per row
        switch (block.GetID()) {
            case 0: // Grass
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // TOP!
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1 };
            
            case 1: // Sand
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // TOP!
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // FRONT QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // LEFT QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                // RIGHT QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2 };
                
                case 2: // Water
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*15, y + offset*14,
                x + offset*14, y + offset*14,
                x + offset*14, y + offset*13,
                x + offset*15, y + offset*13,
                // TOP!
                x + offset*15, y + offset*14,
                x + offset*14, y + offset*14,
                x + offset*14, y + offset*13,
                x + offset*15, y + offset*13,
                // FRONT QUAD
                x + offset*14, y + offset*13,
                x + offset*15, y + offset*13,
                x + offset*15, y + offset*14,
                x + offset*14, y + offset*14,
                // BACK QUAD
                x + offset*15, y + offset*14,
                x + offset*14, y + offset*14,
                x + offset*14, y + offset*13,
                x + offset*15, y + offset*13,
                // LEFT QUAD
                x + offset*14, y + offset*13,
                x + offset*15, y + offset*13,
                x + offset*15, y + offset*14,
                x + offset*14, y + offset*14,
                // RIGHT QUAD
                x + offset*14, y + offset*13,
                x + offset*15, y + offset*13,
                x + offset*15, y + offset*14,
                x + offset*14, y + offset*14 };
                
                case 3: // Dirt
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // TOP!
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // BACK QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // LEFT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // RIGHT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1 };
                
                case 4: // Stone
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // TOP!
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // FRONT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                // BACK QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // LEFT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                // RIGHT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1 };
                
                case 5: // Bedrock/default
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // TOP!
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // BACK QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // LEFT QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2,
                // RIGHT QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*2, y + offset*2  };
                
                case 9: // DIAMONDS!!
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4,
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                // TOP!
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4,
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                // FRONT QUAD
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4,
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                // BACK QUAD
                x + offset*3, y + offset*4,
                x + offset*2, y + offset*4,
                x + offset*2, y + offset*3,
                x + offset*3, y + offset*3,
                // LEFT QUAD
                x + offset*3, y + offset*3,
                x + offset*2, y + offset*3,
                x + offset*2, y + offset*4,
                x + offset*3, y + offset*4,
                // RIGHT QUAD
                x + offset*3, y + offset*3,
                x + offset*2, y + offset*3,
                x + offset*2, y + offset*4,
                x + offset*3, y + offset*4 };
        }
        
        return new float[] { 1, 1, 1 };
    }
}
