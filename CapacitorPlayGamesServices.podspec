
  Pod::Spec.new do |s|
    s.name = 'CapacitorPlayGamesServices'
    s.version = '0.0.1'
    s.summary = 'Capacitor Play Games Services is a native Google Play Games Services implementation for Android.'
    s.license = 'MIT'
    s.homepage = 'https://github.com/gammafp/capacitor-play-games-services'
    s.author = 'Francisco Pereira Alvarado'
    s.source = { :git => 'https://github.com/gammafp/capacitor-play-games-services', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end