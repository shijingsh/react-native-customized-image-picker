package com.zfdang.multiple_images_selector;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zfdang.multiple_images_selector.models.FolderItem;
import com.zfdang.multiple_images_selector.models.FolderListContent;
import com.zfdang.multiple_images_selector.models.ImageItem;
import com.zfdang.multiple_images_selector.models.ImageListContent;
import com.zfdang.multiple_images_selector.utilities.FileUtils;
import com.zfdang.multiple_images_selector.utilities.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class ImagesSelectorActivity extends AppCompatActivity
        implements OnImageRecyclerViewInteractionListener, OnFolderRecyclerViewInteractionListener, View.OnClickListener{

    private static final String TAG = "ImageSelector";
    private static final String ARG_COLUMN_COUNT = "column-count";

    private static final int MY_PERMISSIONS_REQUEST_STORAGE_CODE = 197;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA_CODE = 341;

    private int mColumnCount = 3;

    // custom action bars
    private ImageView mButtonBack;
    private Button mButtonConfirm;

    private RecyclerView recyclerView;

    // folder selecting related
    private View mPopupAnchorView;
    private TextView mFolderSelectButton;
    private FolderPopupWindow mFolderPopupWindow;

    private String currentFolderPath;
    private ContentResolver contentResolver;

    private File mTempImageFile;
    private static final int CAMERA_REQUEST_CODE = 694;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_selector);

        // hide actionbar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        // get parameters from bundle
        Intent intent = getIntent();
        SelectorSettings.mMaxImageNumber = intent.getIntExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, SelectorSettings.mMaxImageNumber);
        SelectorSettings.isShowCamera = intent.getBooleanExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, SelectorSettings.isShowCamera);
        SelectorSettings.mMinImageSize = intent.getIntExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, SelectorSettings.mMinImageSize);

        ArrayList<String> selected = intent.getStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST);
        ImageListContent.SELECTED_IMAGES.clear();
        if(selected != null && selected.size() > 0) {
            ImageListContent.SELECTED_IMAGES.addAll(selected);
        }

        // initialize widgets in custom actionbar
        mButtonBack = (ImageView) findViewById(R.id.selector_button_back);
        mButtonBack.setOnClickListener(this);

        mButtonConfirm = (Button) findViewById(R.id.selector_button_confirm);
        mButtonConfirm.setOnClickListener(this);

        // initialize recyclerview
        View rview = findViewById(R.id.image_recycerview);
        // Set the adapter
        if (rview instanceof RecyclerView) {
            Context context = rview.getContext();
            recyclerView = (RecyclerView) rview;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ImageRecyclerViewAdapter(ImageListContent.IMAGES, this));

            VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.recyclerview_fast_scroller);
            // Connect the recycler to the scroller (to let the scroller scroll the list)
            fastScroller.setRecyclerView(recyclerView);
            // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
            recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        }

        // popup windows will be anchored to this view
        mPopupAnchorView = findViewById(R.id.selector_footer);

        // initialize buttons in footer
        mFolderSelectButton = (TextView) findViewById(R.id.selector_image_folder_button);
        mFolderSelectButton.setText(R.string.selector_folder_all);
        mFolderSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (mFolderPopupWindow == null) {
                    mFolderPopupWindow = new FolderPopupWindow();
                    mFolderPopupWindow.initPopupWindow(ImagesSelectorActivity.this);
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.showAtLocation(mPopupAnchorView, Gravity.BOTTOM, 10, 150);
                }
            }
        });

        currentFolderPath = "";
        FolderListContent.clear();
        ImageListContent.clear();

        updateDoneButton();

        requestReadStorageRuntimePermission();
    }

    public void requestReadStorageRuntimePermission() {
        if (ContextCompat.checkSelfPermission(ImagesSelectorActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImagesSelectorActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE_CODE);
        } else {
            LoadFolderAndImages();
        }
    }


    public void requestCameraRuntimePermissions() {
        if (ContextCompat.checkSelfPermission(ImagesSelectorActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(ImagesSelectorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ImagesSelectorActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA_CODE);
        } else {
            launchCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    LoadFolderAndImages();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ImagesSelectorActivity.this, getString(R.string.selector_permission_error), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchCamera();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ImagesSelectorActivity.this, getString(R.string.selector_permission_error), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private final String[] projections = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};

    // this method is to load images and folders for all
    public void LoadFolderAndImages() {
        Log.d(TAG, "Load Folder And Images...");
        Observable.just("")
                .flatMap(new Func1<String, Observable<ImageItem>>() {
                    @Override
                    public Observable<ImageItem> call(String folder) {
                        List<ImageItem> results = new ArrayList<>();

                        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        String where = MediaStore.Images.Media.SIZE + " > " + SelectorSettings.mMinImageSize;
                        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

                        contentResolver = getContentResolver();
                        Cursor cursor = contentResolver.query(contentUri, projections, where, null, sortOrder);
                        if (cursor == null) {
                            Log.d(TAG, "call: " + "Empty images");
                        } else if (cursor.moveToFirst()) {
                            FolderItem allImagesFolderItem = null;
                            int pathCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            int nameCol = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                            int DateCol = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                            do {
                                String path = cursor.getString(pathCol);
                                String name = cursor.getString(nameCol);
                                long dateTime = cursor.getLong(DateCol);

                                ImageItem item = new ImageItem(name, path, dateTime);

                                // if FolderListContent is still empty, add "All Images" option
                                if(FolderListContent.FOLDERS.size() == 0) {
                                    // add folder for all image
                                    FolderListContent.selectedFolderIndex = 0;

                                    // use first image's path as cover image path
                                    allImagesFolderItem = new FolderItem(getString(R.string.selector_folder_all), "", path);
                                    FolderListContent.addItem(allImagesFolderItem);

                                    // show camera icon ?
                                    if(SelectorSettings.isShowCamera) {
                                        results.add(ImageListContent.cameraItem);
                                        allImagesFolderItem.addImageItem(ImageListContent.cameraItem);
                                    }
                                }

                                // add image item here, make sure it appears after the camera icon
                                results.add(item);

                                // add current image item to all
                                allImagesFolderItem.addImageItem(item);

                                // find the parent folder for this image, and add path to folderList if not existed
                                String folderPath = new File(path).getParentFile().getAbsolutePath();
                                FolderItem folderItem = FolderListContent.getItem(folderPath);
                                if (folderItem == null) {
                                    // does not exist, create it
                                    folderItem = new FolderItem(StringUtils.getLastPathSegment(folderPath), folderPath, path);
                                    FolderListContent.addItem(folderItem);
                                }
                                folderItem.addImageItem(item);
                            } while (cursor.moveToNext());
                            cursor.close();
                        } // } else if (cursor.moveToFirst()) {
                        return Observable.from(results);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImageItem>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + Log.getStackTraceString(e));
                    }

                    @Override
                    public void onNext(ImageItem imageItem) {
                        // Log.d(TAG, "onNext: " + imageItem.toString());
                        ImageListContent.addItem(imageItem);
                        recyclerView.getAdapter().notifyItemChanged(ImageListContent.IMAGES.size()-1);
                    }
                });
    }

    public void updateDoneButton() {
        if(ImageListContent.SELECTED_IMAGES.size() == 0) {
            mButtonConfirm.setEnabled(false);
        } else {
            mButtonConfirm.setEnabled(true);
        }

        String caption = getResources().getString(R.string.selector_action_done, ImageListContent.SELECTED_IMAGES.size(), SelectorSettings.mMaxImageNumber);
        mButtonConfirm.setText(caption);
    }

    public void OnFolderChange() {
        mFolderPopupWindow.dismiss();

        FolderItem folder = FolderListContent.getSelectedFolder();
        if( !TextUtils.equals(folder.path, this.currentFolderPath) ) {
            this.currentFolderPath = folder.path;
            mFolderSelectButton.setText(folder.name);

            ImageListContent.IMAGES.clear();
            ImageListContent.IMAGES.addAll(folder.mImages);
            recyclerView.getAdapter().notifyDataSetChanged();
        } else {
            Log.d(TAG, "OnFolderChange: " + "Same folder selected, skip loading.");
        }
    }


    @Override
    public void onFolderItemInteraction(FolderItem item) {
        // dismiss popup, and update image list if necessary
        OnFolderChange();
    }

    @Override
    public void onImageItemInteraction(ImageItem item) {
        if(ImageListContent.bReachMaxNumber) {
            String hint = getResources().getString(R.string.selector_reach_max_image_hint, SelectorSettings.mMaxImageNumber);
            Toast.makeText(ImagesSelectorActivity.this, hint, Toast.LENGTH_SHORT).show();
            ImageListContent.bReachMaxNumber = false;
        }

        if(item.isCamera()) {
            requestCameraRuntimePermissions();
        }

        updateDoneButton();
    }


    public void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // set the output file of camera
            try {
                mTempImageFile = FileUtils.createTmpFile(this);
            } catch (IOException e) {
                Log.e(TAG, "launchCamera: ", e);
            }
            if (mTempImageFile != null && mTempImageFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempImageFile));
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(this, R.string.camera_temp_file_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // after capturing image, return the image path as selected result
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTempImageFile != null) {
                    // notify system
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTempImageFile)));

                    Intent resultIntent = new Intent();
                    ImageListContent.clear();
                    ImageListContent.SELECTED_IMAGES.add(mTempImageFile.getAbsolutePath());
                    resultIntent.putStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS, ImageListContent.SELECTED_IMAGES);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            } else {
                // if user click cancel, delete the temp file
                while (mTempImageFile != null && mTempImageFile.exists()) {
                    boolean success = mTempImageFile.delete();
                    if (success) {
                        mTempImageFile = null;
                    }
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        if( v == mButtonBack) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if(v == mButtonConfirm) {
            Intent data = new Intent();
            data.putStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS, ImageListContent.SELECTED_IMAGES);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}
