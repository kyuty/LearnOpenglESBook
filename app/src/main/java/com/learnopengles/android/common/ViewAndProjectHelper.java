package com.learnopengles.android.common;

import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangdong on 16-8-2.
 */
public class ViewAndProjectHelper {


    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    public static float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    public static float[] mProjectionMatrix = new float[16];

    // Position the eye behind the origin.
    public static float mEyeX = 0.0f;
    public static float mEyeY = 0.0f;
    public static float mEyeZ = 1.5f;

    // We are looking toward the distance
    public static float mLookX = 0.0f;
    public static float mLookY = 0.0f;
    public static float mLookZ = -5.0f;

    // Set our up vector. This is where our head would be pointing were we holding the camera.
    public static float mUpX = 0.0f;
    public static float mUpY = 1.0f;
    public static float mUpZ = 0.0f;

    public static float mBottom = -1.0f;
    public static float mTop = 1.0f;
    public static float mNear = 1.0f;
    public static float mFar = 10.0f;

    public static void setCameraViewStates(float eyeX, float eyeY, float eyeZ, float lookX, float lookY, float lookZ, float upX, float upY, float upZ) {
        mEyeX = eyeX; mEyeY = eyeY; mEyeZ = eyeZ;
        mLookX = lookX; mLookY = lookY; mLookZ = lookZ;
        mUpX = upX; mUpY = upY; mUpZ = upZ;
    }

    public static void setCameraViewStates(float[] viewMatrix) {
        mViewMatrix = viewMatrix;
    }

    public static void setDefault(int width, int height) {
        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, mEyeX, mEyeY, mEyeZ, mLookX, mLookY, mLookZ, mUpX, mUpY, mUpZ);
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, mBottom, mTop, mNear, mFar);
    }

    public static void setProject(float bottom, float top, float near, float far) {
        mBottom = bottom;
        mTop = top;
        mNear = near;
        mFar = far;
    }

    public static void setProject(float[] projectionMatrix) {
        mProjectionMatrix = projectionMatrix;
    }

}
