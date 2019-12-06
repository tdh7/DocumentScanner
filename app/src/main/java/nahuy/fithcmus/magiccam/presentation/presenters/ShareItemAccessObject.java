package nahuy.fithcmus.magiccam.presentation.presenters;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.R;
import nahuy.fithcmus.magiccam.presentation.entities.ShareItem;


/**
 * Created by huy on 2/6/2017.
 */

public class ShareItemAccessObject {

    private static ArrayList<ShareItem> lstOfItems = new ArrayList<>();

    static{
        lstOfItems.add(new ShareItem(R.drawable.ic_facebook, "Facebook", "com.facebook.katana"));
        lstOfItems.add(new ShareItem(R.drawable.ic_twitter, "Twitter", "com.twitter.android"));
        lstOfItems.add(new ShareItem(R.drawable.ic_instagram, "Instagram", "com.instagram.android"));
    }

    public static ArrayList<ShareItem> getLstOfItems() {
        return lstOfItems;
    }
}
