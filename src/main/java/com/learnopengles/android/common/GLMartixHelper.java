package com.learnopengles.android.common;

import android.opengl.GLES20;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangdong on 16-8-2.
 */
public class GLMartixHelper {

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    public float[] mModelMatrix;

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    public float[] mViewMatrix;

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    public float[] mProjectionMatrix;

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    public float[] mMVPMatrix;

    public boolean useMatrix = true;

    public void setMatrixStates(boolean useMatrix) {
        this.useMatrix = useMatrix;
        if(useMatrix) {
            mModelMatrix = new float[16];
//            mViewMatrix = new float[16];
//            mProjectionMatrix = new float[16];
            mMVPMatrix = new float[16];
        }
    }

    // Position the eye behind the origin.
    public float mEyeX = 0.0f;
    public float mEyeY = 0.0f;
    public float mEyeZ = 1.5f;

    // We are looking toward the distance
    public float mLookX = 0.0f;
    public float mLookY = 0.0f;
    public float mLookZ = -5.0f;

    // Set our up vector. This is where our head would be pointing were we holding the camera.
    public float mUpX = 0.0f;
    public float mUpY = 1.0f;
    public float mUpZ = 0.0f;

    public void setCameraViewStates(float eyeX, float eyeY, float eyeZ, float lookX, float lookY, float lookZ, float upX, float upY, float upZ) {
        this.mEyeX = eyeX; this.mEyeY = eyeY; this.mEyeZ = eyeZ;
        this.mLookX = lookX; this.mLookY = lookY; this.mLookZ = lookZ;
        this.mUpX = upX; this.mUpY = upY; this.mUpZ = upZ;
    }

    public void setCameraViewStates(float[] viewMatrix) {
        mViewMatrix = viewMatrix;
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        if(useMatrix) {
            // Set the view matrix. This matrix can be said to represent the camera position.
            // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
            // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
            Matrix.setLookAtM(mViewMatrix, 0, mEyeX, mEyeY, mEyeZ, mLookX, mLookY, mLookZ, mUpX, mUpY, mUpZ);
        }
    }


    public float mBottom = -1.0f;
    public float mTop = 1.0f;
    public float mNear = 1.0f;
    public float mFar = 10.0f;

    public void setProject(float bottom, float top, float near, float far) {
        this.mBottom = bottom;
        this.mTop = top;
        this.mNear = near;
        this.mFar = far;
    }

    public void setProject(float[] projectionMatrix) {
        mProjectionMatrix = projectionMatrix;
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        if(useMatrix) {
            // Create a new perspective projection matrix. The height will stay the same
            // while the width will vary as per aspect ratio.
            final float ratio = (float) width / height;
            final float left = -ratio;
            final float right = ratio;
            Matrix.frustumM(mProjectionMatrix, 0, left, right, mBottom, mTop, mNear, mFar);
        }
    }

    public void onDrawFrame(GL10 glUnused) {
        if(useMatrix) {
            // Draw the triangle facing straight on.
            Matrix.setIdentityM(mModelMatrix, 0);
//            Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

            if(mViewMatrix != null){
                // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
                // (which currently contains model * view).
                Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
            }

            if(mProjectionMatrix != null){
                // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
                // (which now contains model * view * projection).
                Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
            }
        }
    }

    public GLMartixHelper(){
//        setMatrixStates();
//        setCameraViewStates();
//        setProject();
    }
}
