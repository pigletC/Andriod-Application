GitHub link: https://github.com/abcd1244070534/GreenPass

# GreenPass
 An Android application for password management and secure file sharing
# How to run & debug
 1. First get an copy of this repo, then open the folder in Android Studio. 
 2. Wait for the Android Studio to finish the gradle building.
 3. Connect your Android device with computer using a data cable.
 4. Run & debug the application
# Current Process
 This project contains many functions that have not been explored in the field of password 
 managementand file sharing, such as the application of local database and the implementation
 of DH algorithm. Currently, the fundamental functions of password management and secure file
 sharing have been implemented.
# Limitation
 The autofill function is not perfect and needs follow-up development.
 Besides, we currently do not support breakpoint continuation. That is, if the Bluetooth
 transmission process is forcibly terminated, even if the user successfully reconnects, the 
 data cannot continue to be transmitted from the last interrupted place. However, if a file 
 transmission failed due to Bluetooth connection issue, the application will show a pop-up 
 box to indicate that the device has been disconnected, then the system will allow the user
 to reconnect to the same device and transmit that file again.
# Future Plan
 We want the developers from the future to further implement the aspects including:
 1. beautifing the user UI
 2. providing more encryption options
 3. visualizing the file transition progress
 4. adding the user experience feedback function.