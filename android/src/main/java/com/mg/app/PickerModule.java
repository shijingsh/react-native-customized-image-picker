package com.mg.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.bean.ImageCropBean;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;

class PickerModule extends ReactContextBaseJavaModule {
    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";

    private Promise mPickerPromise;

    private boolean cropping = false;
    private boolean multiple = false;
    private boolean isCamera = false;
    private boolean includeBase64 = false;
    private boolean openCameraOnStart = false;
    private boolean isVideo = false;
    private boolean isHidePreview = false;
    private boolean isPlayGif = false;
    private boolean isHideVideoPreview = false;
    private String title = null;
    private String imageLoader = null;
    //Light Blue 500
    private int width = 200;
    private int height = 200;
    private int maxSize = 9;
    private int compressQuality = -1;
    private boolean returnAfterShot = false;
    private boolean multipleShot  = false;
    private final ReactApplicationContext mReactContext;

    private Compression compression = new Compression();
    private ReadableMap options;
    PickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "ImageCropPicker";
    }

    private void setConfiguration(final ReadableMap options) {
        multiple = options.hasKey("multiple") && options.getBoolean("multiple");
        isCamera = options.hasKey("isCamera") && options.getBoolean("isCamera");
        openCameraOnStart = options.hasKey("openCameraOnStart") && options.getBoolean("openCameraOnStart");
        width = options.hasKey("width") ? options.getInt("width") : width;
        height = options.hasKey("height") ? options.getInt("height") : height;
        maxSize = options.hasKey("maxSize") ? options.getInt("maxSize") : maxSize;
        cropping = options.hasKey("cropping") ? options.getBoolean("cropping") : cropping;
        includeBase64 = options.hasKey("includeBase64") && options.getBoolean("includeBase64");
        compressQuality = options.hasKey("compressQuality") ? options.getInt("compressQuality") : compressQuality;
        title = options.hasKey("title") ? options.getString("title") : title;
        returnAfterShot = options.hasKey("returnAfterShot") && options.getBoolean("returnAfterShot");
        multipleShot = options.hasKey("multipleShot") && options.getBoolean("multipleShot");
        isVideo = options.hasKey("isVideo") && options.getBoolean("isVideo");
        isHidePreview = options.hasKey("isHidePreview") && options.getBoolean("isHidePreview");
        isHideVideoPreview = options.hasKey("isHideVideoPreview") && options.getBoolean("isHideVideoPreview");
        isPlayGif = options.hasKey("isPlayGif") && options.getBoolean("isPlayGif");

        imageLoader = options.hasKey("imageLoader") ? options.getString("imageLoader") : imageLoader;
        this.options = options;
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return type;
    }

    private WritableMap getAsyncSelection(final Activity activity,String path) throws Exception {
        String mime = getMimeType(path);
        if (mime != null && mime.startsWith("video/")) {
            return  getVideo(activity, path, mime);
        }

        return getImage(activity,path);
    }

    private WritableMap getAsyncSelection(final Activity activity, ImageCropBean result) throws Exception {

        String path = result.getOriginalPath();
        return getAsyncSelection(activity,path);
    }
    private WritableMap getAsyncSelection(final Activity activity,MediaBean result) throws Exception {

        String path = result.getOriginalPath();
        return getAsyncSelection(activity,path);
    }

    private WritableMap getVideo(Activity activity, String path, String mime) throws Exception {
        Bitmap bmp = validateVideo(path);

        WritableMap video = new WritableNativeMap();
        video.putInt("width", bmp.getWidth());
        video.putInt("height", bmp.getHeight());
        video.putString("mime", mime);
        video.putInt("size", (int) new File(path).length());
        video.putString("path", "file://" + path);

        return video;
    }

    private WritableMap getImage(final Activity activity,String path) throws Exception {
        WritableMap image = new WritableNativeMap();
        if (path.startsWith("http://") || path.startsWith("https://")) {
            throw new Exception("Cannot select remote files");
        }
        validateImage(path);

        // if compression options are provided image will be compressed. If none options is provided,
        // then original image will be returned
        File compressedImage = compression.compressImage(activity, options, path);
        String compressedImagePath = compressedImage.getPath();
        BitmapFactory.Options options = validateImage(compressedImagePath);

        image.putString("path", "file://" + compressedImagePath);
        image.putInt("width", options.outWidth);
        image.putInt("height", options.outHeight);
        image.putString("mime", options.outMimeType);
        image.putInt("size", (int) new File(compressedImagePath).length());

        if (includeBase64) {
            image.putString("data", getBase64StringFromFile(compressedImagePath));
        }

        return image;
    }

    private WritableMap getImage(final Activity activity, ImageCropBean result) throws Exception {

        String path = result.getOriginalPath();
        return getImage(activity,path);
    }
    private WritableMap getImage(final Activity activity,MediaBean result) throws Exception {

        String path = result.getOriginalPath();
        return getImage(activity,path);
    }
    private void initImageLoader(Activity activity) {

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(activity);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        ImageLoader.getInstance().init(config.build());
    }

    @ReactMethod
    public void openPicker(final ReadableMap options, final Promise promise) {
        final Activity activity = getCurrentActivity();

        if (activity == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }

        setConfiguration(options);
        initImageLoader(activity);
        mPickerPromise = promise;

        RxGalleryFinal rxGalleryFinal =  RxGalleryFinal.with(activity);
        if(openCameraOnStart){
            rxGalleryFinal.openCameraOnStart();
        }else if(!isCamera){
            rxGalleryFinal.hideCamera();
        }
        if(compressQuality>0){
            rxGalleryFinal.cropropCompressionQuality(compressQuality);
        }
        if(title != null){
            rxGalleryFinal.setTitle(title);
        }
        if(returnAfterShot){
            rxGalleryFinal.returnAfterShot();
        }
        if(multipleShot){
            rxGalleryFinal.multipleShot();
        }
        if(isVideo){
            rxGalleryFinal.video();
        }else {
            rxGalleryFinal.image();
        }
        if(isHidePreview){
            rxGalleryFinal.hidePreview();
        }
        if (isHideVideoPreview){
            rxGalleryFinal.videoPreview();
        }
        if(isPlayGif){
            rxGalleryFinal.gif();
        }
        if (imageLoader != null){
            switch (imageLoader){
                case "PICASSO":
                    rxGalleryFinal.imageLoader(ImageLoaderType.PICASSO);
                    break;
                case "GLIDE":
                    rxGalleryFinal.imageLoader(ImageLoaderType.GLIDE);
                    break;
                case "FRESCO":
                    rxGalleryFinal.imageLoader(ImageLoaderType.FRESCO);
                    break;
                case "UNIVERSAL":
                    rxGalleryFinal.imageLoader(ImageLoaderType.UNIVERSAL);
                    break;
                default:
                    break;
            }
        }else{
            rxGalleryFinal.imageLoader(ImageLoaderType.GLIDE);
        }
        if(!this.multiple) {
            if(cropping){
                rxGalleryFinal.crop();
                rxGalleryFinal.cropMaxResultSize(this.width,this.height);
                //裁剪图片的回调
                RxGalleryListener
                        .getInstance()
                        .setRadioImageCheckedListener(
                                new IRadioImageCheckedListener() {
                                    @Override
                                    public void cropAfter(Object t) {
                                        WritableArray resultArr = new WritableNativeArray();
                                        try {
                                            resultArr.pushMap(getAsyncSelection(activity,t.toString()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        mPickerPromise.resolve(resultArr);
                                    }

                                    @Override
                                    public boolean isActivityFinish() {
                                        return true;
                                    }
                                });
            }
            rxGalleryFinal
                    .radio()
                    .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                        @Override
                        protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                            if(!cropping){
                                ImageCropBean result = imageRadioResultEvent.getResult();
                                WritableArray resultArr = new WritableNativeArray();
                                resultArr.pushMap(getAsyncSelection(activity,result));
                                mPickerPromise.resolve(resultArr);
                            }
                        }
                    })
                    .openGallery();
        } else {
            rxGalleryFinal
                    .multiple()
                    .maxSize(maxSize)
                    .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {
                        @Override
                        protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                            List<MediaBean> list = imageMultipleResultEvent.getResult();
                            WritableArray resultArr = new WritableNativeArray();
                            for(MediaBean bean:list){
                                resultArr.pushMap(getAsyncSelection(activity,bean));
                            }
                            mPickerPromise.resolve(resultArr);
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                        }
                    })
                    .openGallery();
        }
    }

    @ReactMethod
    public void clean(final Promise promise) {
        String path = RxGalleryFinalApi.getImgSaveRxDirByStr();
        File file = new File(path);

        deleteRecursive(file);
        promise.resolve(null);
    }
    private String getBase64StringFromFile(String absoluteFilePath) {
        InputStream inputStream;

        try {
            inputStream = new FileInputStream(new File(absoluteFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        bytes = output.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }


    private BitmapFactory.Options validateImage(String path) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        BitmapFactory.decodeFile(path, options);

        if (options.outMimeType == null || options.outWidth == 0 || options.outHeight == 0) {
            throw new Exception("Invalid image selected");
        }

        return options;
    }

    private Bitmap validateVideo(String path) throws Exception {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        Bitmap bmp = retriever.getFrameAtTime();

        if (bmp == null) {
            throw new Exception("Cannot retrieve video data");
        }

        return bmp;
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }
}