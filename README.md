# srv

Http web server to manipulate files.

## Start the project

To start the project with the make file.  
You must first build the image with `make image` then run it with `make ctn-run`.

## Get the executable

You also can build the project in a container to get an executable with `make srv`.  
It will do the same thing as `./gradlew build` but it'll do that in the container.

The executable file will be in `./app/build/distributions/` au usual with gradle.
