#!/bin/bash

set -e # fail on any error

if [ "${#}" -lt 3 ]; then
    echo "Usage: ${0} <app_name> <app_kind> <app_version> <path_to_app> [path_to_background]"
    echo "Exemple: ${0} /path/to/MyApp.app"
    exit 1
fi

app_name="${1}"
app_kind=${2}
app_version="${3}"
app_path="$(realpath "${4}")"
bg_path="$(realpath "${5}" 2>/dev/null || true)"

tmp_path="$(mktemp -d)"
trap "rm -rf ${tmp_path}" EXIT

tmp_files="tmp-${app_name}"
tmp_dmg="tmp-${app_name}.dmg"

dmg="${app_name}_${app_kind}_${app_version}_Mac.dmg"

mkdir -p "${tmp_path}/${tmp_files}"

if [[ -n "${bg_path}" ]]; then
    mkdir -p "${tmp_path}/${tmp_files}/.background"
    cp "${bg_path}" "${tmp_path}/${tmp_files}/.background/"
fi

cp -R "${app_path}" "${tmp_path}/${tmp_files}/"


device=$(hdiutil info | grep -B 1 "/Volumes/${app_name}" | grep -E '^/dev/' | sed 1q | awk '{print $1}')
if [[ -e "${device}" ]] ; then
    hdiutil detach -force "${device}"
fi

hdiutil create "${tmp_path}/${tmp_dmg}" -ov -fs HFS+ -format UDRW -volname "${app_name}" -srcfolder "${tmp_path}/${tmp_files}"
device=$(hdiutil attach -readwrite -noverify "${tmp_path}/${tmp_dmg}" | grep -E '^/dev/' | sed 1q | awk '{print $1}')

if [[ "${app_path##*.}" = "app" ]] ; then
    pushd "/Volumes/${app_name}"
        ln -s /Applications
    popd
fi

echo '
    tell application "Finder"
        tell disk "'${app_name}'"
            open
            set current view of container window to icon view
            set toolbar visible of container window to false
            set the bounds of container window to {400, 100, 950, 600}
            set viewOptions to the icon view options of container window
            set arrangement of viewOptions to not arranged
            set icon size of viewOptions to 72
            if "'${bg_path}'" is not equal to "" then
                set background picture of viewOptions to file ".background:'$(basename "${bg_path}")'"
            end if
            set position of item "'$(basename "${app_path}")'" of container window to {125, 175}
            if "'${app_path##*.}'" = "app" then
                set position of item "Applications" of container window to {275, 200}
            end if
            close
        end tell
    end tell
' | osascript

hdiutil detach "${device}"

hdiutil convert "${tmp_path}/${tmp_dmg}" -format UDBZ -o "${dmg}"
