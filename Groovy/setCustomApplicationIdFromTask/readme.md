# Demonstrate how to set a variant applicationId from a Task.

This sample shows how to create a Task which will output a file containing a single String. The
produced file will then be used to set the variant's applicationId using the file content.
Please note, the applicationId will only be known at execution time.

## To Run
./gradlew assembleDebug