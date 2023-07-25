<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Free MyBatis Tool Changelog

## [2.2.1]

### Fixed
- Fix Generate Options Overwrite-Xml

## [2.2.0]

### Fixed
- Fix Generate Options Comment(实体注释)
- Fix Generate Options Repository-Annotation(Repository注解)

### Changed
- Generate page layout optimization
- Upgrade Gradle 8.2.1
- Upgrade Java 17

## [2.1.3]

### Fixed
- Upgrade Support to `2022.3` or higher

## [2.1.2]

### Fixed
- Upgrade Support to `2022.2`

## [2.1.1]

### Fixed
- Fix `java.lang.IllegalArgumentException: Argument for @NotNull parameter 'module' of ... must not be null`

## [2.1.0]

### Fixed
- Upgrade Support to `2022.1`

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