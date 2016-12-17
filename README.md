# react-native-customized-image-picker
iOS/Android image picker with support for camera, video compression, multiple images and cropping


## Usage
can user version
```
version >= 0.0.19
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
| width          | number | Width of result image when used with `cropping` option |
| height      | number      | Height of result image when used with `cropping` option |
| multiple | bool (default false) | Enable or disable multiple image selection |
| isCamera | bool (default false) | Enable or disable camera selection |
| maxSize  | number (default 9) | set image count |

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

## How it works?

It is basically wrapper around few libraries

#### Android
- RxGalleryFinal: https://github.com/FinalTeam/RxGalleryFinal

#### iOS
- QBImagePickerController
- RSKImageCropper

## License
*MIT*
