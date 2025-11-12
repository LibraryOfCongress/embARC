##  Copyright (c) MediaArea.net SARL. All Rights Reserved.
 #
 #  Use of this source code is governed by a BSD-style license that can
 #  be found in the License.html file in the root of the source tree.
 ##

#!/bin/bash

set -e

#-----------------------------------------------------------------------
# Setup
release_directory="$(realpath "$(dirname "${BASH_SOURCE[0]}")")"
version="$(<"${release_directory}/../Project/version.txt")"

#-----------------------------------------------------------------------
# Cleanup
rm -fr "${release_directory}/embARC_CLI_${version}_Mac.dmg"

pushd "${release_directory}/.."
    ./gradlew clean
popd

#-----------------------------------------------------------------------
# Build
pushd "${release_directory}/.."
    sed -i.bak 's/com.portalmedia.embarc.gui.Launcher/com.portalmedia.embarc.cli.Main/g' build.gradle
    ./gradlew build
popd

#-----------------------------------------------------------------------
# Package .app
pushd "${release_directory}/.."
    mkdir -p "build/distributions/macos"
    jpackage --type app-image \
             --name embARC \
             --app-version ${version} \
             --input build/libs \
             --main-jar embARC-${version}.jar \
             --main-class com.portalmedia.embarc.cli.Main \
             --dest build/distributions/macos \
             --vendor "Library of Congress" \
             --description "embARC - metadata embedded for archival content" \
             --icon "icons/embARC.icns"
popd

#-----------------------------------------------------------------------
# Sign .app
pushd "${release_directory}/.."
    if [ -n "${MACOS_CODESIGN_IDENTITY}" ] ; then
        ./Project/Mac/sign-app-bundle.sh build/distributions/macos/embARC.app com.portalmedia.embarc.cli "${MACOS_CODESIGN_IDENTITY}"
    fi
popd

#-----------------------------------------------------------------------
# Package .pkg
pushd "${release_directory}/../build/distributions/macos"
    mkdir -p embARC-ROOT/usr/local/{bin,share/embARC}
    cp -a embARC.app embARC-ROOT/usr/local/share/embARC
    ln -s ../share/embARC/embARC.app/Contents/MacOS/embARC embARC-ROOT/usr/local/bin/embarc
    pkgbuild --root embARC-ROOT --identifier "com.portalmedia.embarc.cli" --version "${version}" "embARC.pkg"
popd

#-----------------------------------------------------------------------
# Sign .pkg
pushd "${release_directory}/../build/distributions/macos"
    if [ -n "${MACOS_CODESIGN_IDENTITY}" ] ; then
        ../../../Project/Mac/sign-app-bundle.sh embARC.pkg com.portalmedia.embarc.cli "${MACOS_CODESIGN_IDENTITY}"
        mv -f embARC.signed.pkg embARC.pkg
    fi
popd


#-----------------------------------------------------------------------
# Package .dmg
pushd "${release_directory}/.."
    ./Project/Mac/create-dmg.sh embARC CLI ${version} ./build/distributions/macos/embARC.pkg

popd

#-----------------------------------------------------------------------
# Sign .dmg
pushd "${release_directory}/.."
    if [ -n "${MACOS_CODESIGN_IDENTITY}" ] ; then
        ./Project/Mac/sign-app-bundle.sh embARC_CLI_${version}_Mac.dmg com.portalmedia.embarc.cli  "${MACOS_CODESIGN_IDENTITY}"
    fi
popd

#-----------------------------------------------------------------------
# Export artifacts
mv "${release_directory}/../embARC_CLI_${version}_Mac.dmg" "${release_directory}/"
