# embARC GUI Change Log

All notable changes to this project will be documented in this file


## 1.3.1 2024-08-28
	Accessibility Tasks:
	 - emb-w-01: make splash screen text accessible to screen readers
	 - emb-w-01: make splash screen text accessible to screen readers
	 - emb-m-01: make splash screen text accessible to screen readers (same as emb-w-01)
	 - emb-w-04: make tables navigable & sortable with screen reader
	 - emb-m-03: make tables navigable & sortable with screen reader (same as emb-w-04)


## 1.3.0 2024-02-27
	Feature Additions:
	- Add DPX CSV metadata export and sequence analysis
	Improvements:
	- Accessibility review, round 1
		- emb-w-03: make dialog contents accessible to screen readers
		- emb-w-05/emb-m-04: make table info and controls accessible to screen readers
		- emb-w-06/emb-m-05: add focus indicators
		- emb-w-07/emb-m-06: improve button color/text contrast
		- emb-w-08: make text elements accessible to screen readers
		- emb-w-09/emb-m-07: make sure all controls are labeled
		- emb-w-10/emb-m-08: move start editing button before fields (fix: remove start editing button)
		- emb-w-11/emb-m-09: make pop-out buttons accessible to screen readers
		- emb-w-14/emb-m-12: make image dialog accessible to screen reader
		- emb-w-15/emb-m-13: make download buttons accessible to screen reader

## 1.2.0 2023-03-23
	Improvements:
	- Add Text DMS manifest validation
	- Allow custom file naming for Text DMS download
	- Highlight mandatory properties with asterisk
	- Validate mandatory properties, display warning icon when absent
	- Add cancel button on scalar property modal
	- Read second AS07 Core DMS from footer when present
	- Write AS07 Core DMS to both header and footer
	- Fix RFC5646 Language Tag alphabetization
	- Sort Text DMS display by GSPID ascending

## 1.1.1 2022-11-28
	Improvements:
	- code cleanup

## 1.1.0 2022-08-03
	Feature Additions:
	- MXF Manifest display and validation
	- MXF FFV1 support
	Bug fixes:
	- Nested directory processing issue

## 1.0.4 2022-07-29
	Bug fixes:
	- Fix DPX large file count processing bug
	- DPX & MXF write files improvement

## 1.0.3 2022-03-24
	Bug fixes:
	- Fix write files button erroneously disabled bug

## 1.0.2 2022-03-09
	Feature additions:
	- Adds file write error report on write files dialog
	- Adds mandatory/optional indicators on AS07 Core DMS fields
	Bug fixes:
	- File miscounting
	- MXF shim name edit
	- MXF file read issues
	- disable write files if missing AS07 Core DMS or required fields

## 1.0.1 2021-10-26
	Bug fixes:
	- Disable write files button when required core fields are empty

## 1.0.0 2021-09-01
	Feature additions:
	- MXF File Support

## 0.1.7-beta 2020-01-23
	Feature additions:
	- File sorting
	- Accessibility updates to validation icons
	Bug fixes:
	- Quit application bug

## 0.1.6-beta 2019-10-29
	Feature additions:
	- Added data templates for users to quickly apply many changes to file set in workspace
	
	Bug fixes:
	- Attach all modals to main window

## 0.1.5-beta 2019-07-30
	Feature additions:
	- Added persistence for user options such as write files dialog options, report paths, etc.
	- Added reporting feature indicating whether checksums matched when writing files
	Bug fixes:
	- Addressed bug in number of changed fields when editing
	- Prevent users from pasting scientific notation in float fields
	- Cleanup of licenses section

## 0.1.3-beta 2019-06-18
	Bug fixes:
	- Adjusted DPX parsing to skip over user defined data and industry specific sections when header indicates a length of 0.

## 0.1.1-beta 2019-05-15
	Added Amazon Corretto OpenJDK in release bundle

## 0.1.0-beta 2019-04-23
	First beta release
