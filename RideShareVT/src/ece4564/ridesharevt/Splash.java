package ece4564.ridesharevt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        startAnimating();
    }

    /**
     * Helper method to start the animation on the splash screen
     */
    private void startAnimating() {
        // Fade in top title
        ImageView logo1 = (ImageView) findViewById(R.id.imageView2);
        Animation fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo1.startAnimation(fade1);
        // Fade in bottom title after a built-in delay.
        ImageView logo2 = (ImageView) findViewById(R.id.imageView3);
        Animation fade2 = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        logo2.startAnimation(fade2);
        // Transition to Main Menu when bottom title finishes animating
        fade2.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                // The animation has ended, transition to the login screen
                startActivity(new Intent(Splash.this, Login.class));
                Splash.this.finish();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the animation
        ImageView logo1 = (ImageView) findViewById(R.id.imageView2);
        logo1.clearAnimation();
        ImageView logo2 = (ImageView) findViewById(R.id.imageView3);
        logo2.clearAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start animating at the beginning so we get the full splash screen experience
        startAnimating();
    }
}

