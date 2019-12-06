package nahuy.fithcmus.magiccam.presentation.uis.customs.view_callbacks;

import java.util.ArrayList;
import java.util.List;

import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;

public abstract class GalleryLoadingPublisher {

	private List<GalleryLoadindSubcriber> subcribers;

	{
		subcribers = new ArrayList<>();
	}

	public void subcribe(GalleryLoadindSubcriber subcriber){
		this.subcribers.add(subcriber);
	}


	public void notifyChange(List<DA_GalleryImageInfo> items) {
		for(GalleryLoadindSubcriber sub : subcribers){
			sub.updateDisplay(
					items
			);
		}
	}

	public void subcribeAll(List<GalleryLoadindSubcriber> subcribers) {
		if(this.subcribers != null)
			this.subcribers.clear();
		this.subcribers.addAll(subcribers);
	}

}
