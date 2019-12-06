package nahuy.fithcmus.magiccam.presentation.presenters;

import android.content.Context;
import android.provider.Settings;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.domain.callbacks.QueryKeyUseCaseCallback;
import nahuy.fithcmus.magiccam.domain.callbacks.StoreQueryCallback;
import nahuy.fithcmus.magiccam.domain.use_cases.store.StoreAccessUseCase;
import nahuy.fithcmus.magiccam.domain.use_cases.store.StoreFilterQueryUseCase;
import nahuy.fithcmus.magiccam.presentation.entities.shader_kit.effect_kit.MyGLEffectShader;

/**
 * Created by huy on 6/24/2017.
 */

public class StorePresenter implements QueryKeyUseCaseCallback, StoreQueryCallback{

    private static StorePresenter storePresenter = new StorePresenter();

    private static String deviceId;

    private StoreViewSurface storeViewSurface;
    private StoreFilterViewSurface storeFilterViewSurface;

    private StorePresenter(){}

    public static void queryKey(StoreViewSurface storeViewSurface, Context context){
        storePresenter.storeViewSurface = storeViewSurface;
        deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        StoreAccessUseCase storeAccessUseCase = new StoreAccessUseCase(storePresenter);
        storeAccessUseCase.queryDeviceKey(deviceId);
    }

    public static void queryFilter(StoreFilterViewSurface storeFilterViewSurface){
        storePresenter.storeFilterViewSurface = storeFilterViewSurface;
        StoreFilterQueryUseCase storeFilterQueryUseCase = new StoreFilterQueryUseCase(storePresenter);
        storeFilterQueryUseCase.onQuery(deviceId);
    }

    @Override
    public void onSuccess() {
        this.storeViewSurface.onQueryKeySuccess();
    }

    @Override
    public void onFail() {
        this.storeViewSurface.onQueryKeyFail();
    }

    @Override
    public void FilterResult(ArrayList<MyGLEffectShader> glEffectShaders) {
        this.storeFilterViewSurface.onFilterResult(glEffectShaders);
    }

    @Override
    public void FilterResultFail() {
        this.storeFilterViewSurface.onFilterFail();
    }

    public interface StoreViewSurface{
        void onQueryKeySuccess();
        void onQueryKeyFail();
    }

    public interface StoreFilterViewSurface{
        void onFilterResult(ArrayList<MyGLEffectShader> shaders);
        void onFilterFail();
    }
}
