# react-native-customized-image-picker

iOS/Android image picker with support for camera, video compression, multiple images and cropping

## Result

<p align="left">
<img width=200 height=356 title="Multiple Pick" src="https://github.com/samad324/react-native-customized-image-picker/blob/master/images/pic.png">
<img width=200 height=356 title="video Pick" src="https://github.com/samad324/react-native-customized-image-picker/blob/master/images/pic2.png">
<img width=200 height=356 title="iOS multiple Pick" src="https://github.com/samad324/react-native-customized-image-picker/blob/master/images/pic3.png">
</p>

## Usage

<a href="https://nodei.co/npm/react-native-customized-image-picker/">
  <img src="https://nodei.co/npm/react-native-customized-image-picker.svg?downloads=true&downloadRank=true&stars=true">
</a>
<p>
  <a href="https://badge.fury.io/js/react-native-customized-image-picker">
    <img src="https://badge.fury.io/js/react-native-customized-image-picker.svg" alt="npm version" height="18">
  </a>
  <a href="https://npmjs.org/react-native-customized-image-picker">
    <img src="https://img.shields.io/npm/dm/react-native-customized-image-picker.svg" alt="npm downloads" height="18">
  </a>
  <a href="https://travis-ci.org/aws/react-native-customized-image-picker">
    <img src="https://travis-ci.org/aws/react-native-customized-image-picker.svg?branch=master" alt="build:started">
  </a>
  <a href="https://codecov.io/gh/aws/react-native-customized-image-picker">
    <img src="https://codecov.io/gh/aws/react-native-customized-image-picker/branch/master/graph/badge.svg" />
  </a>
</p>


#### Import library

```javascript
import ImagePicker from "react-native-customized-image-picker";
```

#### Select from gallery

Call single image picker

```javascript
ImagePicker.openPicker({}).then(image => {
  console.log(image);
});
```

Call multiple image picker

```javascript
ImagePicker.openPicker({
  multiple: true
}).then(images => {
  console.log(images);
});
```

### Select from camera

```javascript
ImagePicker.openCamera({
  width: 300,
  height: 400,
  cropping: true
}).then(image => {
  console.log(image);
});
```

Select video

```javascript
ImagePicker.openCamera({
  width: 300,
  height: 400,
  isVideo: true
}).then(image => {
  console.log(image);
});
```

### Optional cleanup

Module is creating tmp images which are going to be cleaned up automatically somewhere in the future. If you want to force cleanup, you can use `clean` to clean all tmp files.
Delete the cut, compression, and photographed pictures.

```javascript
ImagePicker.clean()
  .then(() => {
    console.log("removed all tmp images from tmp directory");
  })
  .catch(e => {
    console.log(e);
  });
```

#### Request Object

| Property               |           Type           | Description                                                           |
| ---------------------- | :----------------------: | :-------------------------------------------------------------------- |
| cropping               |   bool (default false)   | Enable or disable cropping                                            |
| width                  |   number(default 200)    | Width of result image when used with `cropping` option                |
| height                 |   number(default 200)    | Height of result image when used with `cropping` option               |
| multiple               |   bool (default false)   | Enable or disable multiple image selection                            |
| isCamera               |   bool (default false)   | Enable or disable camera selection                                    |
| openCameraOnStart      |   bool (default false)   | Enable or disable turn on the camera when it starts                   |
| returnAfterShot        |   bool (default false)   | Enable or disable pictures taken directly                             |
| multipleShot           |   bool (default false)   | Enable or disable Capture image multiple time                         |
| maxSize                |    number (default 9)    | set image count                                                       |
| spanCount              |    number (default 3)    | Set the number of pictures displayed in a row                         |
| includeBase64          |   bool (default false)   | Enable or disable includeBase64                                       |
| compressQuality        |     number([0-100])      | Picture compression ratio                                             |
| minCompressSize        |          number          | Setting the minimum size of the compressed file(kb)                   |
| title                  |          string          | Sets the title of the page                                            |
| isVideo                |   bool (default false)   | Enable or disable video only                                          |
| isSelectBoth           |   bool (default false)   | Enable or disable select both images and videos                       |
| isHidePreview          |   bool (default false)   | Enable or disable hidden preview button                               |
| isHideVideoPreview     |   bool (default false)   | Enable or disable hidden video preview button                         |
| isPlayGif              |   bool (default false)   | Enable or disable play gif                                            |
| hideCropBottomControls |   bool (default true)    | Enable or disable crop controls                                       |
| aspectRatioX           |    number (default 1)    | Set an aspect ratio X for crop bounds.                                |
| aspectRatioY           |    number (default 1)    | Set an aspect ratio Y for crop bounds.                                |
| videoQuality           |    number (default 1)    | 1: high 0: low.                                                       |
| imageLoader            | string (default "GLIDE") | Sets the imageLoader of the page,enum(PICASSO,GLIDE,FRESCO,UNIVERSAL) |

#### Response Object

| Property |  Type  | Description                                      |
| -------- | :----: | :----------------------------------------------- |
| path     | string | Selected image location                          |
| width    | number | Selected image width                             |
| height   | number | Selected image height                            |
| mime     | string | Selected image MIME type (image/jpeg, image/png) |
| size     | number | Selected image size in bytes                     |
| data     | base64 | Optional base64 selected file representation     |

## Install

```
npm i react-native-customized-image-picker --save
yarn add react-native-customized-image-picker
```

#### add permission <pickerExample>/android/app/src/main/AndroidManifest.xml

```xml
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

#### Post-install steps

##### android

auto linked

### iOS

- thinks to : https://github.com/ivpusic/react-native-image-crop-picker

auto linked

```bash
cd ios
pod install
```

## Setting themes

#### Setting language

- Add file gallery_strings.xml under the directory "yourProject\android\app\src\main\res\values".

```xml
<resources>
    <string name="gallery_loading_view_click_loading_more">Load more</string>
    <string name="gallery_loading_view_no_more">No more</string>
    <string name="gallery_loading_view_loading">Loading…</string>

    <string name="gallery_over_button_text">Complete</string>
    <string name="gallery_over_button_text_checked">Complete(%1$d/%2$d)</string>
    <string name="gallery_image_max_size_tip">You can only choose %1$d photos</string>
    <string name="gallery_page_title">%1$d/%2$d</string>
    <string name="gallery_media_grid_image_title">photos</string>
    <string name="gallery_media_grid_video_title">video</string>
    <string name="gallery_default_request_storage_access_permission_tips">App request to read your album</string>
    <string name="gallery_default_camera_access_permission_tips">>App request to Camera</string>
    <string name="gallery_default_media_empty_tips">Absolutely empty</string>
    <string name="gallery_device_no_camera_tips">The device has no camera</string>
    <string name="gallery_device_camera_unable">Camera not available</string>
    <string name="gallery_preview_title">preview</string>
    <string name="gallery_all_image">All pictures</string>
    <string name="gallery_all_video">All video</string>
    <string name="gallery_take_image">Photograph</string>
    <string name="gallery_image_selected">Selected</string>
    <string name="gallery_image_unit">pictures</string>
    <string name="gallery_title_cut">cut</string>
    <string name="gallery_video">record video</string>
</resources>
```

#### Setting style

- modify file styles.xml under the directory "yourProject\android\app\src\main\res\values".

```xml
<resources>
   <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">

   </style>
   <style name="Theme_Light.Default">
       <item name="gallery_toolbar_bg">#233</item>
       <item name="gallery_toolbar_close_image">@mipmap/ic_launcher</item>
       <item name="gallery_toolbar_close_color">#223</item>
       <item name="gallery_toolbar_widget_color">#2A2A2F</item>
       <item name="gallery_toolbar_text_color">#fff</item>
       <item name="gallery_toolbar_text_size">16dp</item>
       <item name="gallery_toolbar_divider_height">16dp</item>
       <item name="gallery_toolbar_divider_bg">#1976D2</item>
       <item name="gallery_toolbar_bottom_margin">10dp</item>
       <item name="gallery_toolbar_text_gravity">right</item>
       <item name="gallery_toolbar_height">16dp</item>
       <item name="gallery_toolbar_over_button_bg">@mipmap/ic_launcher</item>
       <item name="gallery_toolbar_over_button_text_size">16dp</item>
       <item name="gallery_toolbar_over_button_text_color">#446</item>
       <item name="gallery_toolbar_over_button_pressed_color">#1976D2</item>
       <item name="gallery_toolbar_over_button_normal_color">#1976D2</item>
       <item name="gallery_color_statusbar">#1976D2</item>
       <item name="gallery_color_active_widget">#1976D2</item>
       <item name="gallery_checkbox_button_tint_color">#1976D2</item>
       <item name="gallery_checkbox_text_color">#1976D2</item>
       <item name="gallery_page_bg">#FFFFFF</item>
       <item name="gallery_default_image">@mipmap/ic_launcher</item>
       <item name="gallery_camera_image">@mipmap/ic_launcher</item>
       <item name="gallery_camera_bg">#1976D2</item>
       <item name="gallery_take_image_text_color">#1976D2</item>
       <item name="gallery_ucrop_status_bar_color">#1976D2</item>
       <item name="gallery_ucrop_toolbar_color">#1976D2</item>
       <item name="gallery_ucrop_toolbar_widget_color">#1976D2</item>
       <item name="gallery_ucrop_activity_widget_color">#1976D2</item>
       <item name="gallery_ucrop_toolbar_title">@string/app_name</item>
   </style>

   <style name="gallery_checkBox" parent="@android:style/Widget.CompoundButton.CheckBox">
       <item name="android:scaleX">1.5</item>
       <item name="android:scaleY">1.5</item>
   </style>
</resources>
```

- modify file AndroidManifest.xml .

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.example"
    android:versionCode="1"
    android:versionName="1.0">

  <application
    android:name=".MainApplication"
    android:allowBackup="true"
    android:label="@string/app_name"
    android:icon="@mipmap/ic_launcher"
    tools:replace="android:theme"
    android:theme="@style/AppTheme">

      <activity
          android:name="cn.finalteam.rxgalleryfinal.ui.activity.MediaActivity"
          android:exported="true"
          android:theme="@style/Theme_Light.Default" />
  </application>
</manifest>
```

##### Important content

- xmlns:tools="http://schemas.android.com/tools"
- tools:replace="android:theme"
- android:theme="@style/AppTheme"
- cn.finalteam.rxgalleryfinal.ui.activity.MediaActivity Theme_Light.Default

## How it works?

It is basically wrapper around few libraries

#### Android

- RxGalleryFinal: https://github.com/liukefu2050/RxGalleryFinal  
   forked from https://github.com/FinalTeam/RxGalleryFinal

#### iOS

- QBImagePickerController
- RSKImageCropper

## License

_MIT_
