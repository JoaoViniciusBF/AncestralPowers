# Versioning System for Ancestral Powers Mod

## Overview
This document establishes a logical versioning system for the Ancestral Powers mod that ensures clear communication about changes, maintains compatibility expectations, and provides a reliable framework for tracking the mod's evolution.

## Semantic Versioning Format

We follow **Semantic Versioning 2.0.0** with the format: **MAJOR.MINOR.PATCH**

### Version Components

#### MAJOR Version (X.y.z)
- **Increments when**: Breaking changes, major feature overhauls, or API incompatibilities
- **Examples**:
  - Complete rewrite of power system
  - Changing how stamina works fundamentally
  - Removing major features
  - Changing network packet structure in incompatible way
  - Updating to new Minecraft version that requires breaking changes
- **Impact**: May require updates to dependent mods, configurations, or user workflows

#### MINOR Version (x.Y.z)
- **Increments when**: Adding new features, enhancements, or backward-compatible changes
- **Examples**:
  - Adding new powers or abilities
  - Introducing new game mechanics
  - Expanding existing systems in compatible ways
  - Adding configuration options
  - Significant performance improvements
  - Major documentation improvements
- **Impact**: Safe to update, may add new functionality

#### PATCH Version (x.y.Z)
- **Increments when**: Bug fixes, small improvements, or documentation changes
- **Examples**:
  - Fixing crashes or exceptions
  - Correcting incorrect behavior
  - Addressing balance issues
  - Improving error handling
  - Updating translations
  - Minor code cleanups
  - Fixing minor UI issues
- **Impact**: Bug fixes only, no feature changes

## Version Progression Examples

```
1.0.0 → Initial release
1.0.1 → Fixed crash when using fireball power
1.0.2 → Improved stamina regen balance
1.1.0 → Added flight power and personal dimensions
1.1.1 → Fixed visual glitch in stamina HUD
1.1.2 → Added configuration for stamina regen rate
1.2.0 → Major power system overhaul, new activation types
1.2.1 → Fixed compatibility issue with other mods
1.3.0 → Added dimensional arena combat system
2.0.0 → Complete rewrite for new Minecraft version (breaking changes)
2.0.1 → Fixed memory leak in dimension loading
2.1.0 → Added new strength-based powers
```

## Pre-release and Development Versions

### SNAPSHOT Versions
- **Format**: `MAJOR.MINOR.PATCH-SNAPSHOT` or `MAJOR.MINOR.PATCH-prerelease-SNAPSHOT`
- **Usage**: Unstable development versions, continuous integration builds
- **Stability**: Not guaranteed, may change frequently
- **Example**: `1.2.0-SNAPSHOT`, `2.0.0-rc.1-SNAPSHOT`

### Release Candidates
- **Format**: `MAJOR.MINOR.PATCH-rc.X` (where X is a number)
- **Usage**: Pre-release testing before official launch
- **Stability**: Feature-complete, undergoing final testing
- **Example**: `1.2.0-rc.1`, `1.2.0-rc.2`

### Version Rules

1. **Monotonic Increase**: Version numbers must always increase over time
2. **Component Reset**: When incrementing a version component, reset all lower components to zero
   - Example: `1.2.3` → increment MINOR → `1.3.0` (not `1.2.4`)
   - Example: `1.2.3` → increment MAJOR → `2.0.0` (not `1.3.0`)
3. **Backward Compatibility**: MINOR and PATCH versions must remain backward compatible
4. **Breaking Changes**: Only MAJOR version increments introduce breaking changes
5. **Changelog**: Every version change should be documented in CHANGELOG.md

## Implementation in This Project

### Version Storage
The version is maintained in two places for consistency:

1. **`gradle.properties`**:
   ```properties
   mod_version=1.1.2
   ```

2. **`fabric.mod.json`**:
   ```json
   {
     "version": "${version}",
     ...
   }
   ```

### Version Update Process
When making changes:
1. Determine the appropriate version bump (MAJOR/MINOR/PATCH)
2. Update `gradle.properties` with new version
3. Update `CHANGELOG.md` with changes for this version
4. Commit changes
5. Tag release with `vMAJOR.MINOR.PATCH` format
6. Build and distribute

### Example Version Updates

#### Patch Update (Bug Fix)
- **Change**: Fixed stamina not resetting on death
- **Version**: 1.1.2 → 1.1.3
- **Files Modified**: 
  - `gradle.properties`: `mod_version=1.1.3`
  - `CHANGELOG.md`: Added entry for 1.1.3
  - `src/main/java/dev/joaq/ancestralpowers/events/PlayerDeathEvent.java`: Fix implemented

#### Minor Update (New Feature)
- **Change**: Added new teleport power with visual effects
- **Version**: 1.1.3 → 1.2.0
- **Files Modified**:
  - `gradle.properties`: `mod_version=1.2.0`
  - `CHANGELOG.md`: Added entry for 1.2.0 with feature description
  - New power class created
  - Updated `PowersManager` to include new power
  - Updated `PlayerTraits` if needed for new data fields

#### Major Update (Breaking Change)
- **Change**: Completely revised stamina system to use percentage-based values
- **Version**: 1.2.0 → 2.0.0
- **Files Modified**:
  - `gradle.properties`: `mod_version=2.0.0`
  - `CHANGELOG.md`: Major version entry explaining breaking changes
  - `PlayerTraits`: Changed stamina handling
  - `PowerBase`: Updated stamina cost logic
  - All power classes: Adjusted stamina cost values
  - `PlayerPowersTickHandler`: Updated stamina regen logic
  - Networking packets: Updated if needed
  - HUD rendering: Updated for new stamina values

## Changelog Format

Keep a `CHANGELOG.md` file with the following format:

```markdown
# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]
### Added
- New feature descriptions
### Changed
- Behavioral changes
### Fixed
- Bug fixes
### Removed
- Removed features

## [1.1.2] - 2026-07-23
### Added
- Stamina HUD overlay showing percentage-based stamina
### Changed
- Refactored stamina system to use percentage values (0-100)
- Updated all power stamina costs to use percentage-based system
- Improved stamina regeneration to be frame-rate independent
### Fixed
- Stamina not properly synchronizing between client and server
- Powers not deactivating correctly when stamina depleted
### Removed
- Debug chat messages showing raw stamina values
```

## Version Compatibility Guidelines

### For Players
- **PATCH updates**: Safe to install, fixes bugs
- **MINOR updates**: Safe to install, adds features
- **MAJOR updates**: May require new world, check changelog for breaking changes

### For Mod Developers
- **Depending on this mod**:
  - PATCH: Safe to depend on `=[1.1.2]`
  - MINOR: Safe to depend on `=[1.1.0,1.2.0)` 
  - MAJOR: Must check breaking changes, may need updates
- **API Stability**:
  - Interfaces in `powers` package: Stable within MINOR versions
  - Networking packets: Stable within MINOR versions unless noted
  - Component interfaces: Stable within MINOR versions
  - Event registration methods: Stable within MINOR versions

## Release Process

1. **Development**: Work on features/fixes in branches
2. **Testing**: Verify changes work correctly
3. **Version Decision**: Determine if change is PATCH/MINOR/MAJOR
4. **Update Version**: Modify `gradle.properties`
5. **Update Changelog**: Add entry for upcoming version
6. **Commit**: `chore(release): prepare version 1.1.3`
7. **Tag**: `git tag v1.1.3`
8. **Build**: Run `./gradlew build` to create artifacts
9. **Publish**: Distribute through appropriate channels
10. **Announce**: Share release notes with community

## Special Considerations

### API vs Implementation Versioning
- **API Version**: Tracks changes to public interfaces
- **Implementation Version**: Tracks all changes including internal
- **Our Approach**: We use a single version number that considers both, erring on the side of incrementing when in doubt about API impact

### Configuration Changes
- Adding new config options: MINOR version
- Changing config format/meaning: MAJOR version
- Fixing config parsing: PATCH version

### Data Migration
- If saved data format changes require conversion:
  - Consider if automatic migration is possible
  - Document manual steps if needed
  - Usually warrants at least MINOR version, possibly MAJOR

## Bad Version Practices to Avoid

❌ **Random version jumping**: Going from 1.0.0 to 2.5.0 without reason  
❌ **Decreasing versions**: Never go from 1.2.0 back to 1.1.0  
❌ **Skipping version components**: Going from 1.0.0 to 1.2.0 without 1.1.0 (unless intentional)  
❌ **Not updating version**: Making changes without bumping version  
❌ **Mislabeling compatibility**: Calling breaking changes MINOR updates  
❌ **Ignoring API changes**: Not incrementing MAJOR for breaking API changes  

## Summary

This versioning system provides:
- Clear communication about change impact
- Reliable expectations for users and downstream developers
- Simple automation for release processes
- Historical tracking of mod evolution
- Foundation for future dependency management

By following these guidelines, we ensure that version numbers meaningfully communicate the nature of changes and help all stakeholders make informed decisions about updating and depending on the mod.