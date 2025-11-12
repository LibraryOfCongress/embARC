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
rm -fr "${release_directory}/embARC_GUI_${version}_Mac.dmg"

pushd "${release_directory}/.."
    ./gradlew clean
popd

#-----------------------------------------------------------------------
# Build
pushd "${release_directory}/.."
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
             --main-class com.portalmedia.embarc.gui.Launcher \
             --dest build/distributions/macos \
             --vendor "Library of Congress" \
             --description "embARC - metadata embedded for archival content" \
             --icon "icons/embARC.icns"
popd

#-----------------------------------------------------------------------
# Sign .app
pushd "${release_directory}/.."
    if [ -n "${MACOS_CODESIGN_IDENTITY}" ] ; then
        ./Project/Mac/sign-app-bundle.sh build/distributions/macos/embARC.app com.portalmedia.embarc.gui "${MACOS_CODESIGN_IDENTITY}"
    fi
popd

#-----------------------------------------------------------------------
# Package .dmg
pushd "${release_directory}/.."
    ./Project/Mac/create-dmg.sh embARC GUI ${version} ./build/distributions/macos/embARC.app
popd

#-----------------------------------------------------------------------
# Sign .dmg
pushd "${release_directory}/.."
    if [ -n "${MACOS_CODESIGN_IDENTITY}" ] ; then
        ./Project/Mac/sign-app-bundle.sh embARC_GUI_${version}_Mac.dmg com.portalmedia.embarc.gui "${MACOS_CODESIGN_IDENTITY}"
    fi
popd

#-----------------------------------------------------------------------
# Export artifacts
mv "${release_directory}/../embARC_GUI_${version}_Mac.dmg" "${release_directory}/"
