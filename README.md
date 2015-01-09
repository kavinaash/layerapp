# Layer QuickStart for Android

**Example code for integrating [LayerKit](https://layer.com/).**

QuickStart is a sample application highlighting how to integrate LayerKit, the Android SDK for the Layer communications platform. It presents a very simple example of a chat app using Layer.

## Requirements

The QuickStart application requires Android Studio and the Layer SDK. Dependencies are managed via [Maven](https://developer.layer.com/docs/quick-start/android) to simplify installation.

## Usage

1. Clone the project from Github: `$ git clone https://github.com/layerhq/quick-start-android.git`
2. Open the workspace in Android Studio
3. Replace LAYER_APP_ID in MainActivity.java (line 11) with your App ID from http://developer.layer.com.  If you skip this step you will get an error on app launch.
4. (Optional) Replace GCM_ID with your own Google Cloud Messaging ID (instructions can be found here: https://developer.layer.com/docs/guides#push-notification)
5. Build and run the QuickStart application on an Emulator and a physical Device to start a 1:1 conversation between them.

## Highlights

* Demonstrates how to implement authentication, typing indicators, and metadata
* Provides a reference implementation for driving Message UI

## Configuration

In order to populate the sample app with content, you must configure the following variable inside Constants.h:

* `LAYER_APP_ID`: The Layer application identifier for you application.

The authentication process requires that you provide a sandbox app identifier that has been configured to use the Layer Identity Provider.

## Credits

QuickStart was crafted in San Francisco by Neil Mehta during his work on [Layer](http://layer.com). At Layer, we are building the Communications Layer for the Internet. We value, support, and create works of Open Source engineering excellence.

## License

QuickStart is available under the Apache 2 License. See the LICENSE file for more info.
