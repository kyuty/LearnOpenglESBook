package com.learnopengles.android.lesson1;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangdong on 16-7-25.
 */
public class WaveDemo implements GLSurfaceView.Renderer
{
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    /** Store our model data in a float buffer. */
    private FloatBuffer mWaveVertices;
    private ShortBuffer mWaveIndices;
    private FloatBuffer mWaveColors;

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** How many elements per vertex. */
    private final int mStrideBytes = 7 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Offset of the color data. */
    private final int mColorOffset = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;

    /**
     * Initialize the model data.
     */
    public WaveDemo()
    {
        // Define points for equilateral triangles.

//        // This triangle is red, green, and blue.
//        final float[] triangle1VerticesData = {
//                // X, Y, Z,
//                // R, G, B, A
//                -0.5f, -0.25f, 0.0f,
//                1.0f, 0.0f, 0.0f, 1.0f,
//
//                0.5f, -0.25f, 0.0f,
//                0.0f, 0.0f, 1.0f, 1.0f,
//
//                0.0f, 0.559016994f, 0.0f,
//                0.0f, 1.0f, 0.0f, 1.0f};
//
//        // Initialize the buffers.
//        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//
//        mTriangle1Vertices.put(triangle1VerticesData).position(0);

        initData();
    }

    private int n = 50; // 份数
    private float width = 1.0f;
    private float height = 1.0f;
    private float z = 0;
    private int numsVertices = (4 + (n -1) * 2);
    private float[] waveVertices = new float[numsVertices * 3];
    private short waveIndices[] = new short[n*2*3];
    private float[] waveColors = new float[numsVertices* 4];

    private void updateData(float t){
        // y = A * sin(k * x - w * t - theta) + D
        // A 为波幅（纵轴）， ω 为角频率  t 为时间（横轴）  theta 为相偏移（横轴左右） k 为波数（周期密度）  D 为（直流）偏移量（y轴高低）。
        float a = 0.05f;
        float w = 0.005f;
        float theta = 0;
        float k = 1;
        float d = 0;

        for(int i = 0; i < numsVertices; i++){
            int index = -1;
            if(i % 2 == 0){
                index = i;
            }
            if(index == -1){
                continue;
            }
            float x = waveVertices[index * 3 + 1];
            waveVertices[index * 3 + 1] = (float)(a * Math.sin(k*x-w*t-theta)+d);
            theta+=0.1f;
        }

        mWaveVertices.put(waveVertices).position(0);
    }

    int n_wave = 2;

    private void initData() {

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


        float[] temp = new float[numsVertices* 4];
        indexColors = 0;
        r = 0; g = 0; b = 1; a = 1;
        for (int i = 0; i <= n; i++){
            temp[indexColors] = r;
            indexColors++;
            temp[indexColors] = g;
            indexColors++;
            temp[indexColors] = b;
            indexColors++;
            temp[indexColors] = a;
            indexColors++;

            temp[indexColors] = r;
            indexColors++;
            temp[indexColors] = g;
            indexColors++;
            temp[indexColors] = b;
            indexColors++;
            temp[indexColors] = a;
            indexColors++;
        }

        if(n_wave > 1){
            float[] waveVertices_nWave = new float[waveVertices.length * n_wave];
            float[] waveTextures_nWave = new float[waveColors.length * n_wave];
            short[] waveIndices_nWave = new short[waveIndices.length * n_wave];
            for(int i_wave = 0; i_wave < n_wave; i_wave++){
                for(int i = 0; i < waveVertices.length; i++){
                    waveVertices_nWave[i_wave * waveVertices.length + i] = waveVertices[i];
                }
                if(i_wave == 0){
                    for(int i = 0; i < waveColors.length; i++){
                        waveTextures_nWave[i_wave * waveColors.length + i] = waveColors[i];
                    }
                } else {
                    for(int i = 0; i < waveColors.length; i++){
                        waveTextures_nWave[i_wave * waveColors.length + i] = temp[i];
                    }
                }

                for(int i = 0; i < waveIndices.length; i++){
                    waveIndices_nWave[i_wave * waveIndices.length + i] = (short) (waveIndices[i] + i_wave * numsVertices);
                }
            }
            numsVertices *= n_wave;
            waveVertices = new float[waveVertices_nWave.length];
            waveIndices = new short[waveIndices_nWave.length];
            waveColors = new float[waveTextures_nWave.length];
            for(int i = 0; i < waveVertices_nWave.length; i++){
                waveVertices[i] = waveVertices_nWave[i];
            }
            for(int i = 0; i < waveIndices_nWave.length; i++){
                waveIndices[i] = waveIndices_nWave[i];
            }
            for(int i = 0; i < waveTextures_nWave.length; i++){
                waveColors[i] = waveTextures_nWave[i];
            }

        }

        // Initialize the buffers.
        mWaveVertices = ByteBuffer.allocateDirect(waveVertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mWaveVertices.put(waveVertices).position(0);

        mWaveIndices = ByteBuffer.allocateDirect(waveIndices.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mWaveIndices.put(waveIndices).position(0);

        mWaveColors = ByteBuffer.allocateDirect(waveColors.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mWaveColors.put(waveColors).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        // Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.

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

        // Load in the vertex shader.
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            // Compile the shader.
            GLES20.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }

        // Load in the fragment shader shader.
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);

            // Compile the shader.
            GLES20.glCompileShader(fragmentShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if (fragmentShaderHandle == 0)
        {
            throw new RuntimeException("Error creating fragment shader.");
        }

        // Create a program object and store the handle to it.
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        // Set program handles. These will later be used to pass in values to the program.

        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

        // Pass in the position information
        mWaveVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                3 * 4, mWaveVertices);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        mWaveColors.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
                4 * 4, mWaveColors);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        updateData(time);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, waveIndices.length, GLES20.GL_UNSIGNED_SHORT, mWaveIndices);

//        // Draw one translated a bit down and rotated to be flat on the ground.
//        Matrix.setIdentityM(mModelMatrix, 0);
//        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, 0.0f);
//        Matrix.rotateM(mModelMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
//        drawTriangle(mTriangle2Vertices);
//
//        // Draw one translated a bit to the right and rotated to be facing to the left.
//        Matrix.setIdentityM(mModelMatrix, 0);
//        Matrix.translateM(mModelMatrix, 0, 1.0f, 0.0f, 0.0f);
//        Matrix.rotateM(mModelMatrix, 0, 90.0f, 0.0f, 1.0f, 0.0f);
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
//        drawTriangle(mTriangle3Vertices);
    }

    /**
     * Draws a triangle from the given vertex data.
     *
     * @param aTriangleBuffer The buffer containing the vertex data.
     */
    private void drawTriangle(final FloatBuffer aTriangleBuffer)
    {
        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        aTriangleBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
