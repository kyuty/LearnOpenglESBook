package com.learnopengles.android.lesson1;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.learnopengles.android.common.GLHelper;
import com.learnopengles.android.common.ViewAndProjectHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangdong on 16-8-2.
 */
public class WaveDemoWithGLHelper implements GLSurfaceView.Renderer {

    private GLHelper mGLHelper;

    public WaveDemoWithGLHelper(){

    }

    private void initData() {
        int n = 50; // 份数
        float width = 1.0f;
        float height = 1.0f;
        float z = 0;
        int numsVertices = (4 + (n -1) * 2);
        float[] waveVertices = new float[numsVertices * 3];
        short waveIndices[] = new short[n*2*3];
        float[] waveColors = new float[numsVertices* 4];

        float leftTopX = - width / 2, leftTopY = height / 2;
        //float rightTopX =  width / 2, rightTopY = height / 2;
        float leftBottomX = - width / 2, leftBottomY = - height / 2;
        //float rightBottomX = width / 2, rightBottomY = - height / 2;

        int indexVertex = 0;
        waveVertices[indexVertex] = leftBottomX;
        indexVertex++;
        waveVertices[indexVertex] = leftBottomY;
        indexVertex++;
        waveVertices[indexVertex] = z;
        indexVertex++;

        waveVertices[indexVertex] = leftTopX;
        indexVertex++;
        waveVertices[indexVertex] = leftTopY;
        indexVertex++;
        waveVertices[indexVertex] = z;
        indexVertex++;

        for (int i = 0; i < n; i++){
            float addDelta = width / n * (i + 1);

            float x2 = leftBottomX + addDelta;
            float y2 = leftBottomY;
            float z2 = z;

            float x3 = leftTopX + addDelta;
            float y3 = leftTopY;
            float z3 = z;

            waveVertices[indexVertex] = x2;
            indexVertex++;
            waveVertices[indexVertex] = y2;
            indexVertex++;
            waveVertices[indexVertex] = z2;
            indexVertex++;

            waveVertices[indexVertex] = x3;
            indexVertex++;
            waveVertices[indexVertex] = y3;
            indexVertex++;
            waveVertices[indexVertex] = z3;
            indexVertex++;
        }

        System.out.println("indexVertex = " + indexVertex + " waveVertices.size = " + waveVertices.length);

        int index_waveIndices = 0;
        for(int i = 0; i < n; i++){
            short x0,x1,x2,x3;
            x0 = (short) ((i - 1) * 2 + 3);
            x1 = (short) ((i - 1) * 2 + 2);
            x2 = (short) (i * 2 + 2);
            x3 = (short) (i * 2 + 3);

            waveIndices[index_waveIndices] = x0;
            index_waveIndices++;
            waveIndices[index_waveIndices] = x1;
            index_waveIndices++;
            waveIndices[index_waveIndices] = x2;
            index_waveIndices++;

            waveIndices[index_waveIndices] = x0;
            index_waveIndices++;
            waveIndices[index_waveIndices] = x2;
            index_waveIndices++;
            waveIndices[index_waveIndices] = x3;
            index_waveIndices++;
        }
        System.out.println("index_waveIndices = " + index_waveIndices + " waveIndices.size = " + waveIndices.length);

        int indexColors = 0;
        int r = 1, g = 0, b = 0, a = 1;
        for (int i = 0; i <= n; i++){
            waveColors[indexColors] = r;
            indexColors++;
            waveColors[indexColors] = g;
            indexColors++;
            waveColors[indexColors] = b;
            indexColors++;
            waveColors[indexColors] = a;
            indexColors++;

            waveColors[indexColors] = r;
            indexColors++;
            waveColors[indexColors] = g;
            indexColors++;
            waveColors[indexColors] = b;
            indexColors++;
            waveColors[indexColors] = a;
            indexColors++;
        }
        System.out.println("indexColors = " + indexColors + " waveIndices.size = " + waveColors.length);


        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.
//                        + "layout (location = 0) attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
//                        + "layout (location = 1) attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.

                        + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
                        + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.

                        + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

                        + "void main()                    \n"		// The entry point for our vertex shader.
                        + "{                              \n"
                        + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader.
                        // It will be interpolated across the triangle.
                        + "   gl_Position = u_MVPMatrix   \n" 	// gl_Position is a special variable used to store the final position.
                        + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                        + "}                              \n";    // normalized screen coordinates.

        final String fragmentShader =
                "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                        // precision in the fragment shader.
                        + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the
                        // triangle per fragment.
                        + "void main()                    \n"		// The entry point for our fragment shader.
                        + "{                              \n"
                        + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.
                        + "}                              \n";


        mGLHelper = new GLHelper(waveVertices,waveIndices,waveColors,vertexShader,fragmentShader,new String[]{"a_Position", "a_Color"},new String[]{"u_MVPMatrix"});
        //createGLMatrixHelper();
        //useElements();
        mGLHelper.createGLMatrixHelper(true);
        mGLHelper.useElements(true);
        //setPositionNameInShader();
        //setColorNameInShader();
        //setMartixInShader();
        mGLHelper.setPositionNameInShader("a_Position");
        mGLHelper.setColorNameInShader("a_Color");
        mGLHelper.setMartixInShader("u_MVPMatrix");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initData();
        // Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        ViewAndProjectHelper.setDefault(width,height);
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        mGLHelper.onDrawFrame(gl,ViewAndProjectHelper.mViewMatrix, ViewAndProjectHelper.mProjectionMatrix);
    }
}
