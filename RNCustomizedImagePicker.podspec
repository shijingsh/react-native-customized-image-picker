  
package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name             = "RNCustomizedImagePicker"
  s.version          = package['version']
  s.summary          = package["description"]
  s.requires_arc = true
  s.license      = 'MIT'
  s.homepage     = 'n/a'
  s.authors      = { "Ivan Pusic,liukefu" => "" }
  s.source       = { :git => "https://github.com/liukefu2050/react-native-customized-image-picker", :tag => "v#{s.version}"}
  s.source_files = 'ios/*.{h,m}'
  s.platform     = :ios, "8.0"
  s.dependency 'RSKImageCropper'
  s.dependency 'QBImagePickerController'
  s.dependency 'React-Core'
  s.dependency 'React-RCTImage'
end