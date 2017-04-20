# react-native-customized-image-picker
iOS/Android image picker with support for camera, video compression, multiple images and cropping

## Result
<img width=200 title="Multiple Pick" src="https://github.com/liukefu2050/react-native-customized-image-picker/blob/master/images/pic.png">


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

#### Request Object

| Property        | Type           | Description  |
| ------------- |:-------------:| :-----|
| cropping | bool (default false)      | Enable or disable cropping |
| width       | number(default 200) | Width of result image when used with `cropping` option |
| height      | number(default 200) | Height of result image when used with `cropping` option |
| multiple | bool (default false) | Enable or disable multiple image selection |
| isCamera | bool (default false) | Enable or disable camera selection |
| openCameraOnStart | bool (default false) | Enable or disable turn on the camera when it starts |
| maxSize  | number (default 9) | set image count |
| includeBase64 | bool (default false) | Enable or disable includeBase64 |
| compressQuality  | number([0-100]) | Picture compression ratio |
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

##### iOS
native for ios not modified; 
please see : https://github.com/ivpusic/react-native-image-crop-picker

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

## License
*MIT*
