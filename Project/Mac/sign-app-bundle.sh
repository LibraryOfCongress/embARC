#!/bin/bash

set -e # fail on any error

if [ $# -ne 3 ]; then
    echo "Usage: $0 <path_to_app> <app_identifier> <certificate_name>"
    echo "Exemple: $0 /path/to/MyApp.app com.company.myapp \"Developer ID Application: Company Name\""
    exit 1
fi

src_path="$(realpath "$(dirname "${BASH_SOURCE[0]}")")"
app_path="$(realpath "$1")"
app_id="$2"
crt_id="$3"

if [ ! -e "$app_path" ]; then
    exit 1
fi

tmp_path="$(mktemp -d)"
trap "rm -rf $tmp_path" EXIT

sign_file() {
    codesign --force --options runtime --timestamp --entitlements "$src_path/default.entitlements" --sign "Developer ID Application: $crt_id" "$1"
}

if [[ "${app_path##*.}" = "dmg" ]] ; then # sign the application package or dmg
    codesign --force --options runtime --timestamp --sign "Developer ID Application: $crt_id" --identifier "$app_id" "$app_path"
elif [[ "${app_path##*.}" = "pkg" ]] ; then
    productsign --sign "Developer ID Installer: $crt_id" "$app_path" ${app_path/%.pkg/.signed.pkg}
else # sign the application bundle
    # find binaries and libraries inside .jar files
    find "$app_path" -name "*.jar" -type f | while read -r jar ; do
        jar_temp="$tmp_path/$(basename "$jar" .jar)"
        mkdir -p "$jar_temp"

        to_sign=$(jar --list --file="$jar" | grep -E '(\.dylib|\.jnilib|ffplay_mac)$' || true)

        if [ -z "$to_sign" ]; then
            continue
        fi

        pushd "$jar_temp"
            for lib in $to_sign; do
                jar --extract --file="$jar" "$lib"
            done

            find "$jar_temp" \( -name "*.dylib" -o -name "*.jnilib" -o -name "ffplay_mac" \) -type f | while read -r lib ; do
                sign_file "$lib"
            done

            for lib in $to_sign ; do
                jar --update --file="$jar" "$lib"
            done
        popd

        rm -rf "$jar_temp"
    done

    # find libraries
    find "$app_path" -name "*.dylib" -type f | while read -r dylib ; do
        sign_file "$dylib"
    done

    # sign helpers binaries
    sign_file "$app_path/Contents/runtime/Contents/Home/lib/jspawnhelper"

    # signe the jre bundle
    sign_file "$app_path/Contents/runtime"

    # sign main binary
    sign_file "$app_path/Contents/MacOS/embARC"

    # sign the whole application
    codesign --force --options runtime --timestamp --sign "Developer ID Application: $crt_id"  --entitlements "$src_path/default.entitlements" --identifier "$app_id" "$app_path"
fi
