package nahuy.fithcmus.magiccam.presentation.uis.customs.recycler_animations;

import androidx.recyclerview.widget.RecyclerView;
import android.view.animation.OvershootInterpolator;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.BaseItemAnimator;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by huy on 6/24/2017.
 */

public class AnimationMaker {
    public enum MyAnimation{
        SCALE_IN,
        FADE_IN,
        SLIDE_IN
    }

    public static void setAnimation(RecyclerView recyclerView, RecyclerView.Adapter adapter,
                                    MyAnimation animation){

        BaseItemAnimator recycleAnimator = new LandingAnimator(new OvershootInterpolator(2f));
        AnimationAdapter adapterAnimator = new AlphaInAnimationAdapter(adapter);

        switch (animation){
            case SCALE_IN:
                recycleAnimator = new ScaleInAnimator(new OvershootInterpolator(2f));
                adapterAnimator = new ScaleInAnimationAdapter(adapter);
                break;
            case FADE_IN:
                recycleAnimator = new FadeInAnimator(new OvershootInterpolator(2f));
                break;
            case SLIDE_IN:
                recycleAnimator = new SlideInLeftAnimator(new OvershootInterpolator(2f));
                adapterAnimator = new SlideInLeftAnimationAdapter(adapter);
                break;
        }

        adapterAnimator.setInterpolator(new OvershootInterpolator());
        recyclerView.setItemAnimator(recycleAnimator);
        recyclerView.getItemAnimator().setAddDuration(400);
        recyclerView.setAdapter(adapterAnimator);
    }
}
