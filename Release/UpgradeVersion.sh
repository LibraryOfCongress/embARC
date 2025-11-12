##  Copyright (c) MediaArea.net SARL. All Rights Reserved.
 #
 #  Use of this source code is governed by a BSD-style license that can
 #  be found in the License.html file in the root of the source tree.
 ##

#!/bin/bash

set -e # fail on any error

if [ "${#}" -ne 1 ]; then
    echo "Usage: ${0} version"
    exit 1
fi

#-----------------------------------------------------------------------
# Setup
release_directory="$(realpath "$(dirname "${BASH_SOURCE[0]}")")"
version="${1}"
version_old="$(<"${release_directory}/../Project/version.txt")"

#-----------------------------------------------------------------------
# Processing versions with dots

files_dot=(
    "${release_directory}/../Project/version.txt"
    "${release_directory}/../build.gradle"
)

for file in "${files_dot[@]}"; do
   sed -i.bak "s/${version_old}/${version}/g" "${file}"
done

#-----------------------------------------------------------------------
# Processing installer's VERSION4
sed -i.bak "s/${version_old}/${version}/g" "${release_directory}/../Project/Install/embARC.nsi"
if [ "$(echo $version | cut -d. -f4)" != "" ] ; then
    sed -i.bak "s/!define PRODUCT_VERSION4 \"\${PRODUCT_VERSION}[0-9.]*\"/!define PRODUCT_VERSION4 \"\${PRODUCT_VERSION}\"/g" "${release_directory}/../Project/Install/embARC.nsi"
elif [ "$(echo $version | cut -d. -f3)" != "" ] ; then
    sed -i.bak "s/!define PRODUCT_VERSION4 \"\${PRODUCT_VERSION}[0-9.]*\"/!define PRODUCT_VERSION4 \"\${PRODUCT_VERSION}.0\"/g" "${release_directory}/../Project/Install/embARC.nsi"
else
    sed -i.bak "s/!define PRODUCT_VERSION4 \"\${PRODUCT_VERSION}[0-9.]*\"/!define PRODUCT_VERSION4 \"\${PRODUCT_VERSION}.0.0\"/g" "${release_directory}/../Project/Install/embARC.nsi"
fi
