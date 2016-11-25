package com.zfdang.multiple_images_selector;

import java.util.ArrayList;

/**
 * Created by zfdang on 2016-4-18.
 */
public class SelectorSettings {
    /**
     * max number of images to be selected
     */
    public static final String SELECTOR_MAX_IMAGE_NUMBER = "selector_max_image_number";
    public static int mMaxImageNumber = 9;

    /**
     * show camera
     */
    public static final String SELECTOR_SHOW_CAMERA = "selector_show_camera";
    public static boolean isShowCamera = true;
    public static final String CAMERA_ITEM_PATH = "/CAMERA/CAMERA";

    /**
     * initial selected images, full path of images
     */
    public static final String SELECTOR_INITIAL_SELECTED_LIST = "selector_initial_selected_list";
    public static ArrayList<String> resultList = new ArrayList<>();

    // results
    public static final String SELECTOR_RESULTS = "selector_results";

    // it can be used to filter very small images (mainly icons)
    public static int mMinImageSize = 50000;
    public static final String SELECTOR_MIN_IMAGE_SIZE = "selector_min_image_size";
}
