
require 'json'
package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name            = "RNCustomizedImagePicker"
  s.version       = package["version"]
  s.summary       = package['description']
  s.homepage        = "https://github.com/liukefu2050/react-native-customized-image-picker"
  s.license         = "MIT"
  s.author          = { "author" => "liukefu2050@sina.com" }
  s.platform        = :ios, "9.0"
  s.source          = { :git => "https://github.com/liukefu2050/react-native-customized-image-picker.git", :tag => "master" }
  s.source_files    = "**/*.{h,m}"
  s.requires_arc    = true
  s.resource        = "TZImagePickerController/TZImagePickerController.bundle"

  s.dependency "React"
  s.dependency "TZImagePickerController"

end
