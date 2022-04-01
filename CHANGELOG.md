<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Free MyBatis Tool Changelog

## [2.0.4]
### Fixed
- Fix the bug of jumping error when there is a **Dao/Xml** file with the same name.
- Fix the bug that the **Xml** file is automatically generated when there is a **Dao** file with the same name.
- Fix the `IncorrectOperationException` that was automatically generated when there was an **Xml** file with the same name

## [2.0.3]
### Fixed
- Fix the problem of being overwritten by the first class when generating multiple tables.

## [2.0.2]
### Fixed
- Fix `ClassCastException` Exception

## [2.0.1]
### Fixed
- Fix `Page(分页)`,`Add-ForUpdate(select增加ForUpdate)` function

## [2.0.0]
### Changed
- Upgrade Support to `2021.3.x`

### Fixed
- Fix `LineMarkerInfo` Exception