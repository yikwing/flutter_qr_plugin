import 'dart:async';

import 'package:flutter/services.dart';

class FlutterMethodChannel {
  static const MethodChannel _channel =
      const MethodChannel('flutter_method_channel');

  static Future<String> qrcode() async {
    return await _channel.invokeMethod('qrcode');
  }
}
