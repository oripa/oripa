#! /bin/bash


GROUP="java3d"
VERSION="1.5.2"

mvn_install_jar(){
	
	
	ARTIFACT=$(basename "$1" .jar)
	echo "installing $1 as $ARTIFACT"


	mvn -e  install:install-file -Dfile=$1 "-DgroupId=$2"\
	"-DartifactId=$ARTIFACT" "-Dversion=$3" -Dpackaging=jar -DgeneratePom=true
}

# assuming that all java softwares are installed at default path
# (on Windows)

# path is for MinGW
MY_JAVA_HOME=$(bash ./path-win2linux.sh "$JAVA_HOME")
echo "Java home path: $MY_JAVA_HOME"


JAVA3D_HOME=$(echo "$MY_JAVA_HOME" | sed -e 's/\/jdk[^/]*$//g')/Java3D/$VERSION
JAVA3D_EXT="$JAVA3D_HOME/lib/ext"

if [ ! -e "$JAVA3D_EXT" ] ; then\
	echo "NOT EXIST: $JAVA3D_EXT"
	exit 1
fi

echo "=== start ==="

pushd "$JAVA3D_EXT"

J3DCORE=j3dcore
mvn_install_jar "$J3DCORE.jar" "$GROUP" $VERSION

J3DUTILS=j3dutils
mvn_install_jar "$J3DUTILS.jar" "$GROUP" $VERSION

VECMATH=vecmath
mvn_install_jar "$VECMATH.jar" "$GROUP" $VERSION

popd
