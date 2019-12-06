package nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks;

import android.support.v4.app.Fragment;

import java.util.List;

import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;


public interface GalleryLoadindSubcriber {

	public abstract void updateDisplay(List<DA_GalleryImageInfo> items);

	public abstract Fragment getFragment();

	public abstract int getFragmentLabel();

}
