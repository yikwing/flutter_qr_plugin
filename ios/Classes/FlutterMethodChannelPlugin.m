#import "FlutterMethodChannelPlugin.h"
#import <flutter_method_channel/flutter_method_channel-Swift.h>

@implementation FlutterMethodChannelPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterMethodChannelPlugin registerWithRegistrar:registrar];
}
@end
