package nahuy.fithcmus.magiccam.presentation.uis.activities;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import nahuy.fithcmus.magiccam.R;

/**
 * Created by huy on 6/27/2017.
 */

public class SplashScreenActivity extends AwesomeSplash {

    private static final int LONG_DURATION = 2000;
    private static final int NORMAL_DURATION = 100;
    private static final int TEXT_SIZE = 36;

    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.setAnimCircularRevealDuration(LONG_DURATION);
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);
        configSplash.setRevealFlagY(Flags.REVEAL_TOP);
        configSplash.setBackgroundColor(R.color.splashBackground);

        configSplash.setLogoSplash(R.drawable.ic_launcher);
        configSplash.setAnimLogoSplashDuration(NORMAL_DURATION);
        configSplash.setAnimLogoSplashTechnique(Techniques.Tada);

        String titleText = getResources().getString(R.string.app_name);
        configSplash.setTitleSplash(titleText);
        configSplash.setAnimTitleDuration(NORMAL_DURATION);
        configSplash.setAnimTitleTechnique(Techniques.Wave);
        configSplash.setTitleTextSize(TEXT_SIZE);
        configSplash.setTitleTextColor(R.color.splashTextColor);
        configSplash.setTitleFont("fonts/cherry.ttf");
    }

    @Override
    public void animationsFinished() {
        final Activity a = this;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // mp.stop();
                startActivity(new Intent(SplashScreenActivity.this, NavigateActivity.class));
                Toast.makeText(a, "Finish loading", Toast.LENGTH_LONG).show();
            }
        }, LONG_DURATION);
    }
}
