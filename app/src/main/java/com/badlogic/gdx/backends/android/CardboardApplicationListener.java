package com.badlogic.gdx.backends.android;

import com.badlogic.gdx.ApplicationListener;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

public interface CardboardApplicationListener extends ApplicationListener {

    void onNewFrame(HeadTransform paramHeadTransform);

    void onDrawEye(Eye eye);

    void onFinishFrame(Viewport paramViewport);

    void onRendererShutdown();

    void onCardboardTrigger();

    void onSurfaceCreated(EGLConfig config);
}
