#! /bin/bash


# Path Hack for MinGW

MY_PATH="$1"
# echo "$MY_PATH"


# replace \ by / and change top directory format
MY_PATH=$(echo "$MY_PATH" | sed -e 's/\\/\//g' | sed -e 's/C:/\/c/')
#echo "slashes converted:";echo "$MY_PATH"

# escape spaces
#MY_PATH=$(echo "$MY_PATH" | sed -e 's/ /\\ /g')
# echo "spaces escaped:";
echo "$MY_PATH"



