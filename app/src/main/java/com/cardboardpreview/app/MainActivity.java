package com.cardboardpreview.app;

import android.os.Bundle;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.CardboardAndroidApplication;
import com.badlogic.gdx.backends.android.CardboardApplicationListener;
import com.badlogic.gdx.backends.android.CardboardCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.google.vrtoolkit.cardboard.*;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends CardboardAndroidApplication implements CardboardApplicationListener {

    private static final String TAG = "MainActivity";

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 1000.0f;
    private static final float CAMERA_Z = 0.01f;

    private static final boolean VR_MODE = true; // Set VR_MODE to false to select monocular mode.
    private VrStereoRenderer mStereoRenderer;

    private CardboardCamera mCamera;
    private Model mModel;
    private ModelInstance mModelInstance;
    private ModelBatch mBatch;
    private Environment mEnvironment;

    private final Matrix4 mEyeViewAdjustMatrix = new Matrix4();
    private final Matrix4 mPerspective = new Matrix4();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(this, config);

        final CardboardView cardboardView = getCardboardView();
        cardboardView.setVRModeEnabled(VR_MODE);
        cardboardView.setSettingsButtonEnabled(VR_MODE);

        mStereoRenderer = new VrStereoRenderer(this, cardboardView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mStereoRenderer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mStereoRenderer.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged(hasFocus=" + hasFocus + ")");
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onNewFrame(HeadTransform paramHeadTransform) {
        mModelInstance.transform.rotate(0, 1, 0, Gdx.graphics.getDeltaTime() * 30);
        mStereoRenderer.onNewFrame(paramHeadTransform);
    }

    @Override
    public void onDrawEye(Eye eye) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        mStereoRenderer.onDrawEye(eye);

        mEyeViewAdjustMatrix.set(eye.getEyeView());
        mCamera.setEyeViewAdjustMatrix(mEyeViewAdjustMatrix);

        mPerspective.set(eye.getPerspective(Z_NEAR, Z_FAR));
        mCamera.setEyeProjection(mPerspective);
        mCamera.update();

        mBatch.begin(mCamera);
        mBatch.render(mModelInstance, mEnvironment);
        mBatch.end();
    }

    @Override
    public void onFinishFrame(Viewport paramViewport) {
        mStereoRenderer.onFinishFrame(paramViewport);
    }

    @Override
    public void onRendererShutdown() {
        mStereoRenderer.onRendererShutdown();
    }

    @Override
    public void onCardboardTrigger() {

    }

    @Override
    public void onSurfaceCreated(EGLConfig config) {
        mStereoRenderer.onSurfaceCreated(config);
    }

    @Override
    public void create() {
        mCamera = new CardboardCamera();
        mCamera.position.set(0f, 0f, CAMERA_Z);
        mCamera.lookAt(0,0,0);
        mCamera.near = Z_NEAR;
        mCamera.far = Z_FAR;

        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        ModelBuilder modelBuilder = new ModelBuilder();
        mModel = modelBuilder.createBox(20f, 20f, 20f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        mModelInstance = new ModelInstance(mModel);
        mModelInstance.transform.translate(-30, 0, 0);

        mBatch = new ModelBatch();
    }

    @Override
    public void resize(int width, int height) {
        mStereoRenderer.onSurfaceChanged(width, height);
    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        if (mBatch != null) {
            mBatch.dispose();
        }

        if (mModel != null) {
            mModel.dispose();
        }
    }
}
