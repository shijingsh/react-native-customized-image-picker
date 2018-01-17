# react-native-customized-image-picker
iOS/Android image picker with support for camera, video compression, multiple images and cropping

## Result

<p align="left">
<img width=200 title="Multiple Pick" src="https://github.com/liukefu2050/react-native-customized-image-picker/blob/master/images/pic.png">
<img width=200 title="video Pick" src="https://github.com/liukefu2050/react-native-customized-image-picker/blob/master/images/pic2.png">
</p>

## Usage
use version
```
version >= 0.0.20
```

Import library
```javascript
import ImagePicker from 'react-native-customized-image-picker';
```

#### Select from gallery

Call single image picker 
```javascript
ImagePicker.openPicker({
  
}).then(image => {
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

### Optional cleanup
Module is creating tmp images which are going to be cleaned up automatically somewhere in the future. If you want to force cleanup, you can use `clean` to clean all tmp files.
Delete the cut, compression, and photographed pictures.
```javascript
ImagePicker.clean().then(() => {
  console.log('removed all tmp images from tmp directory');
}).catch(e => {
  console.log(e);
});
```

#### Request Object

| Property        | Type           | Description  |
| ------------- |:-------------:| :-----|
| cropping | bool (default false)      | Enable or disable cropping |
| width       | number(default 200) | Width of result image when used with `cropping` option |
| height      | number(default 200) | Height of result image when used with `cropping` option |
| multiple | bool (default false) | Enable or disable multiple image selection |
| isCamera | bool (default false) | Enable or disable camera selection |
| openCameraOnStart | bool (default false) | Enable or disable turn on the camera when it starts |
| returnAfterShot | bool (default false) | Enable or disable pictures taken directly |
| multipleShot | bool (default false) | Enable or disable Capture image multiple time |
| maxSize  | number (default 9) | set image count |
| includeBase64 | bool (default false) | Enable or disable includeBase64 |
| compressQuality  | number([0-100]) | Picture compression ratio |
| minCompressSize  | number | Setting the minimum size of the compressed file(kb) |
| title  | string | Sets the title of the page |
| isVideo | bool (default false)      | Enable or disable video only | 
| isHidePreview | bool (default false)      | Enable or disable hidden preview button | 
| isHideVideoPreview | bool (default false)      | Enable or disable hidden video preview button | 
| isPlayGif | bool (default false)      | Enable or disable play gif | 
| imageLoader | string (default "GLIDE")      | Sets the imageLoader of the page,enum(PICASSO,GLIDE,FRESCO,UNIVERSAL) | 
#### Response Object

| Property        | Type           | Description  |
| ------------- |:-------------:| :-----|
| path          | string | Selected image location |
| width      | number      | Selected image width |
| height | number      | Selected image height |
| mime | string | Selected image MIME type (image/jpeg, image/png) |
| size | number | Selected image size in bytes |
| data | base64 | Optional base64 selected file representation |

## Install

```
npm i react-native-customized-image-picker --save
react-native link react-native-customized-image-picker
```

#### Post-install steps

##### android

in AndroidManifest.xml add follow:

```html
        <uses-permission android:name="android.permission.CAMERA" />
        <uses-feature android:name="android.hardware.camera" android:required="true"/>
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```
                
### iOS
  - thinks to : https://github.com/ivpusic/react-native-image-crop-picker
#### Step 1:

In Xcode open Info.plist and add string key `NSPhotoLibraryUsageDescription` with value that describes why you need access to user photos. More info here https://forums.developer.apple.com/thread/62229. Depending on what features you use, you also may need `NSCameraUsageDescription` and `NSMicrophoneUsageDescription` keys.

#### Step 2:

##### Cocoapods (Highly recommended)

```
cd ios
pod init
```

After this you have to add pod dependencies to `Podfile`. Open `Podfile` with your editor, and add or adjust example configuration:

```
platform :ios, '8.0'

target '<your_project_name>' do
    pod 'RSKImageCropper'
    pod 'QBImagePickerController'
end
```

After this run:

```
pod install
```

##### Manual

- Drag and drop the ios/ImageCropPickerSDK folder to your xcode project. (Make sure Copy items if needed IS ticked)
- Click on project General tab
  - Under `Deployment Info` set `Deployment Target` to `8.0`
  - Under `Embedded Binaries` click `+` and add `RSKImageCropper.framework` and `QBImagePicker.framework`



In Xcode open Info.plist and add string key `NSPhotoLibraryUsageDescription` with value that describes why do you need access to user photos. More info here https://forums.developer.apple.com/thread/62229. Depending on what features you use, you also may need `NSCameraUsageDescription` and `NSMicrophoneUsageDescription` keys.

###### cocoapods users

- Add `platform :ios, '8.0'` to Podfile (!important)
- Add `pod 'RSKImageCropper'` and `pod 'QBImagePickerController'` to Podfile

###### non-cocoapods users

- Drag and drop the ios/ImageCropPickerSDK folder to your xcode project. (Make sure Copy items if needed IS ticked)
- Click on project General tab
  - Under `Deployment Info` set `Deployment Target` to `8.0`
  - Under `Embedded Binaries` click `+` and add `RSKImageCropper.framework` and `QBImagePicker.framework`

## How it works?

It is basically wrapper around few libraries

#### Android
- RxGalleryFinal: https://github.com/liukefu2050/RxGalleryFinal  
                  forked from  https://github.com/FinalTeam/RxGalleryFinal
#### iOS
- QBImagePickerController
- RSKImageCropper

## Update log

#### 0.0.39
- add optional multipleShot

#### 0.0.36
- add optional minCompressSize

#### 0.0.33
- add optional cleanup

#### 0.0.30
- RxGalleryFinal  upgrade
- add param
- fixed bug

#### 0.0.29
- add param "returnAfterShot" and "title"

## License
*MIT*
