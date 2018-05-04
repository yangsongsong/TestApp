package com.dev.kit.testapp.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.testapp.R;

/**
 * 通过两个小球同时落地，演示属性动画基本使用
 * Author: cuiyan
 * Date:   18/5/2 22:39
 * Desc:
 */
public class BallsFallDownSimultaneously extends BaseActivity implements View.OnClickListener {

    private View freeFallView;
    private View horizontalProjectileMotionView;
    // 模拟自由落体
    private ObjectAnimator freeFallAnimator;
    // 模拟平抛
    private ValueAnimator horizontalProjectileAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balls_fall_down);
        init();
    }

    private void init() {
        setOnClickListener(R.id.tv_reset, this);
        setOnClickListener(R.id.tv_start, this);
        freeFallView = findViewById(R.id.view_free_fall);
        horizontalProjectileMotionView = findViewById(R.id.view_horizontal_projectile_motion);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            initAnimation();
        }
    }

    private void initAnimation() {
        int verticalDistance = DisplayUtil.getScreenHeight() - freeFallView.getBottom() - DisplayUtil.getStatusBarHeight(this);
        int horizontalDistance = freeFallView.getLeft() - horizontalProjectileMotionView.getRight();
        freeFallAnimator = ObjectAnimator.ofFloat(freeFallView, "translationY", 0, verticalDistance);
//        freeFallAnimator.setInterpolator(new AccelerateInterpolator());
        freeFallAnimator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                // 效果等同于第55行设置
                return input * input;
            }
        });
        horizontalProjectileAnimator = ValueAnimator.ofObject(new MyTypeEvaluator(), new Point(0, 0), new Point(horizontalDistance, verticalDistance));
        horizontalProjectileAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point point = (Point) animation.getAnimatedValue();
                horizontalProjectileMotionView.setTranslationX(point.getPointX());
                horizontalProjectileMotionView.setTranslationY(point.getPointY());
            }
        });
        horizontalProjectileAnimator.setInterpolator(new LinearInterpolator());
    }

    private void runAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(freeFallAnimator).with(horizontalProjectileAnimator);
        animatorSet.playTogether(freeFallAnimator, horizontalProjectileAnimator);
        animatorSet.setDuration(1500);
        animatorSet.start();
    }

    private void reset() {
        freeFallView.setTranslationY(0);
        horizontalProjectileMotionView.setTranslationX(0);
        horizontalProjectileMotionView.setTranslationY(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reset: {
                reset();
                break;
            }
            case R.id.tv_start: {
                runAnimation();
                break;
            }
        }
    }

    // 描述小球运动轨迹坐标
    private class Point {
        private float pointX;
        private float pointY;

        private Point(float pointX, float pointY) {
            this.pointX = pointX;
            this.pointY = pointY;
        }

        private float getPointX() {
            return pointX;
        }

        private void setPointX(int pointX) {
            this.pointX = pointX;
        }

        private float getPointY() {
            return pointY;
        }

        private void setPointY(int pointY) {
            this.pointY = pointY;
        }
    }

    // 模拟平抛运动轨迹的估值器
    private class MyTypeEvaluator implements TypeEvaluator<Point> {
        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            float pointX = fraction * (endValue.getPointX() - startValue.getPointX());
            float pointY = fraction * fraction * (endValue.getPointY() - startValue.getPointY());
            return new Point(pointX, pointY);
        }
    }
}