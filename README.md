# Client App for Data Transfer with Server App (Android IPC Mechanisms)

## Introduction

The Client App is a part of a client-server system designed for sending data between client app and a server app. This app utilizes foreground services to implement three different Inter-Process Communication (IPC) methods: AIDL (Android Interface Definition Language), Messenger, and Broadcast. The app also fetches data from an API every minute and sends data from the client app to the server app periodically when triggered by user.

## Features

- **Foreground Service:** The app uses foreground services to perform long-running operations, ensuring data transfer and communication continue even when the app is in the background.

- **AIDL Communication:** The app implements AIDL-based IPC to enable secure and efficient communication between the client app and the server.

- **Messenger Communication:** Another IPC method used is Messenger, providing a two way message-based communication mechanism between the client and server.

- **Broadcast Communication:** The app also leverages the Broadcast mechanism to send and receive data between the client app and the foreground server.

- **Data Fetching:** The app fetches data from a designated API every minute, keeping the data up-to-date.

- **Data Sending:** Users can trigger the data sending process by tapping the "Connect" button. The app sends data to the server every minute through the chosen IPC method.

- **Background Compatibility:** The app is optimized for Android's background execution limitations, ensuring it runs efficiently without negatively impacting device performance.

## Installation

The Android Client App can be installed on Android devices running Android 8.0 or above.

### Build and Install

1. Clone the repository from GitHub:

```bash
git https://github.com/atakanakin/ClientApp.git
cd ClientApp
```

2. Open the project in Android Studio.

3. Build the project and install it on your Android device.

## Usage

1. Launch the Client App on your device.

2. The app will display the main interface with a "Connect" button.

3. Choose one of the IPC methods offered (AIDL, Messenger, or Broadcast) through the bottom navigation bar.

4. Tap the "Connect" button to trigger data sending to the server using the selected IPC method (AIDL, Messenger, or Broadcast).

5. Observe the status and logs to ensure the data transfer process is successful.

## Dependencies

The Android Client App relies on the following libraries and components:

- [AndroidX](https://developer.android.com/jetpack/androidx): AndroidX support libraries for compatibility across different Android versions.

- [Retrofit](https://square.github.io/retrofit/): A type-safe HTTP client for Android to fetch data from the API.

- [AIDL](https://developer.android.com/guide/components/aidl): Android Interface Definition Language for AIDL-based IPC.

- [BroadcastReceiver](https://developer.android.com/guide/components/broadcasts): Broadcast mechanism for IPC.

- [Messenger](https://developer.android.com/reference/android/os/Messenger): Messenger for message-based communication between components.


## Contributing

Contributions to the Client App are welcome! If you encounter any bugs, want to add new features, or improve existing ones, please follow the guidelines below:

1. Fork the repository.

2. Create a new branch for your changes:

```bash
git checkout -b feature/your-feature-name
```

3. Make your changes and test thoroughly.

4. Commit your changes:

```bash
git commit -m "Add your commit message here"
```

5. Push the changes to your forked repository:

```bash
git push origin feature/your-feature-name
```

6. Create a pull request against the `main` branch of this repository.

## License

This project is licensed under the [MIT License](LICENSE).

## Contact

For any questions or inquiries, feel free to reach out to me at atakanakink@gmail.com.

## Thanks

Special thanks to [Perihan Mirkelam](https://proandroiddev.com/ipc-techniques-for-android-45d815ac59be) for their inspiring article on IPC techniques for Android.

Thank you for using the Client App!
