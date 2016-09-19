package com.learnopengles.android.common;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangdong on 16-7-25.
 */
public class GLHelper
{
    /** Store our model data in a float buffer. */
    private FloatBuffer mVerticesBuffer;
    private ShortBuffer mIndicesBuffer;
    private FloatBuffer mColorsBuffer;

    private int numsVertices;
    private float[] mVertices;
    private short[] mIndices;
    private float[] mColors;
    public int mProgram;
    public Map<String, Integer> mAttributeMap;
    public Map<String, Integer> mUniformMap;
    public GLMartixHelper mGLMatrixHelper;
    public boolean mUseElements;

    public GLHelper(float[] vertices, short[] indices, float[] colors, String vertexShader, String fragmentShader, String[] attributes, String[] uniforms)
    {
        this.mVertices = vertices;
        this.mIndices = indices;
        this.mColors = colors;
        initData();
        this.mProgram = ShaderHelper.createProgram(vertexShader, fragmentShader);
        this.mAttributeMap = ShaderHelper.getAttributesMap(this.mProgram, attributes);
        this.mUniformMap = ShaderHelper.getUniformMap(this.mProgram, uniforms);
        //createGLMatrixHelper();
        //useElements();
        //setPositionNameInShader();
        //setColorNameInShader();
        //setMartixInShader();
    }

    public void createGLMatrixHelper(boolean useMatrix){
        mGLMatrixHelper = new GLMartixHelper();
        mGLMatrixHelper.setMatrixStates(useMatrix);
    }

    public void useElements(boolean use){
        mUseElements = use;
    }

    private void initData() {
        // Initialize the buffers.
        if(mVertices != null) {
            mVerticesBuffer = ByteBuffer.allocateDirect(mVertices.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mVerticesBuffer.put(mVertices).position(0);
        } else {
            throw new RuntimeException("mVertices == null");
        }

        if(mIndices != null){
            mIndicesBuffer = ByteBuffer.allocateDirect(mIndices.length * 2)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            mIndicesBuffer.put(mIndices).position(0);
        }

        if(mColors != null) {
            mColorsBuffer = ByteBuffer.allocateDirect(mColors.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mColorsBuffer.put(mColors).position(0);
        }
    }

    public String mPositionNameInShader;    // attribute
    public void setPositionNameInShader(String name){
        this.mPositionNameInShader = name;
    }

    public String mColorNameInShader;   // attribute
    public void setColorNameInShader(String name){
        this.mColorNameInShader = name;
    }

    public String mMartixInShader;  // uniform
    public void setMartixInShader(String name){
        this.mMartixInShader = name;
    }

    public void onDrawFrame(GL10 glUnused, float[] viewMatrix, float[] projectionMatrix)
    {
//        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(mProgram);

        if(mVerticesBuffer != null) {
            // Pass in the position information
            int mPositionHandle = -1;
            if(mAttributeMap.containsKey(mPositionNameInShader)) {
                mPositionHandle = mAttributeMap.get(mPositionNameInShader);
            } else {
                throw new RuntimeException("mAttributeMap not contain " + mPositionNameInShader +" key");
            }
            mVerticesBuffer.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    3 * 4, mVerticesBuffer);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
        }

        if(mColorsBuffer != null) {
            // Pass in the color information
            int mColorHandle = -1;
            if(mAttributeMap.containsKey(mColorNameInShader)) {
                mColorHandle = mAttributeMap.get(mColorNameInShader);
            } else {
                throw new RuntimeException("mAttributeMap not contain " + mColorNameInShader +" key");
            }
            mColorsBuffer.position(0);
            GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
                    4 * 4, mColorsBuffer);
            GLES20.glEnableVertexAttribArray(mColorHandle);
        }

        if(mGLMatrixHelper.useMatrix){

            mGLMatrixHelper.setCameraViewStates(viewMatrix);
            mGLMatrixHelper.setProject(projectionMatrix);
            mGLMatrixHelper.onDrawFrame(glUnused);

            if(mGLMatrixHelper.mMVPMatrix != null){
                int mMVPMatrixHandle = -1;
                if(mUniformMap.containsKey(mMartixInShader)) {
                    mMVPMatrixHandle = mUniformMap.get(mMartixInShader);
                } else {
                    throw new RuntimeException("mUniformMap not contain mMartixInShader key");
                }
                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mGLMatrixHelper.mMVPMatrix, 0);
            }
        }

//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        if(mUseElements){
            if(mIndicesBuffer != null && mIndices != null){
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length, GLES20.GL_UNSIGNED_SHORT, mIndicesBuffer);
            } else {
                throw new RuntimeException("mIndicesBuffer == null || mIndices == null");
            }
        }
    }
}
