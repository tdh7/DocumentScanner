package nahuy.fithcmus.magiccam.presentation.presenters;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.EditNavItem;
import nahuy.fithcmus.magiccam.presentation.entities.ShareItem;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.edit.EditPhotoBottomCropFragment;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.edit.EditPhotoBottomFilterFragment;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.edit.EditPhotoBottomFrameFragment;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.edit.EditPhotoBottomStickerFragment;
import nahuy.fithcmus.magiccam.presentation.uis.fragments.edit.EditPhotoBottomStickerTextFragment;

/**
 * Created by huy on 6/20/2017.
 */

public class EditItemAccessObject {

    private static ArrayList<EditNavItem> lstOfItems = new ArrayList<>();

    static{
        lstOfItems.add(new EditNavItem("Filter", R.drawable.ic_filter_trap, new EditPhotoBottomFilterFragment()));
        lstOfItems.add(new EditNavItem("Crop", R.drawable.ic_crop_trap, new EditPhotoBottomCropFragment()));
        lstOfItems.add(new EditNavItem("Frame", R.drawable.ic_frame_trap, new EditPhotoBottomFrameFragment()));
        lstOfItems.add(new EditNavItem("Sticker", R.drawable.ic_sticker_trap, new EditPhotoBottomStickerFragment()));
        lstOfItems.add(new EditNavItem("Sticker Text", R.drawable.ic_sticker_text_trap, new EditPhotoBottomStickerTextFragment()));
    }

    public static ArrayList<EditNavItem> getLstOfItems() {
        return lstOfItems;
    }

}
