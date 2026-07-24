# AI Development Guidelines for Ancestral Powers Mod

## Overview
This document provides guidelines for AI-assisted development of the Ancestral Powers mod. It establishes conventions, best practices, and logical frameworks to ensure consistent, maintainable code that aligns with the mod's vision.

## Core Principles

### 1. Modularity and Separation of Concerns
- Each system (powers, networking, components, events) should have clear boundaries
- Avoid tight coupling between systems
- Use interfaces and abstract classes for extensibility
- Prefer composition over inheritance where appropriate

### 2. Data Flow Architecture
- Server-side logic determines game state
- Client-side predicts and renders based on server data
- Networking packets synchronize critical state changes
- Components (via Cardinal) store persistent player data

### 3. Performance Considerations
- Minimize object creation in tick handlers
- Use efficient data structures for frequent operations
- Cache expensive calculations when possible
- Avoid blocking operations in game threads

### 4. Extensibility Design
- New powers should be easy to add without modifying core systems
- Configuration should be data-driven where possible
- Event systems should allow for easy hooking
- Networking should support new packet types cleanly

## Coding Conventions

### Naming
- Use clear, descriptive names in Portuguese (matching existing code)
- Classes: PascalCase
- Methods/variables: camelCase
- Constants: UPPER_SNAKE_CASE
- Interfaces: descriptive adjectives or nouns (e.g., `Power`)

### Code Organization
- Group related functionality in packages:
  - `powers`: Power implementations
  - `networking`: Packet handling
  - `components`: Data storage via Cardinal
  - `events`: Game event handlers
  - `client`: Client-specific rendering and input
  - `dimensions`: Custom dimension logic
  - `registry`: Game object registration
  - `util`: Helper functions

### Comments and Documentation
- Explain WHY, not WHAT (unless complex)
- Document complex algorithms or non-obvious logic
- Keep comments updated with code changes
- Use Javadoc for public APIs

## System-Specific Guidelines

### Power System
- Extend `PowerBase` for new powers
- Implement abstract methods: `staminaCost()`, `ActivationType()`, `disablePowerSpecific()`, `executeLogic()`
- Use `spendStamina()` for stamina consumption
- Use `canActivate()` for activation checks
- Distinguish between Main, Secondary, and Specific power types

### Networking
- Create packets in `networking.packet.c2s` (client→server) or `networking.packet.s2c` (server→client)
- Register packets in `AncestralPowers.onInitialize()`
- Use `ModPacketsC2S` and `ModPacketsS2C` for sending packets
- Keep packets small and focused

### Components (Player Data)
- Extend `PlayerTraits` interface for new data
- Implement in `PlayerTraitsComponent`
- Register in `MyComponents`
- Use appropriate data types (avoid String for enum-like data)
- Implement proper serialization in `writeData()`/`readData()`

### HUD and Rendering
- Client-only rendering in `client` package
- Use `HudRenderCallback` for HUD elements
- Synchronize data via networking packets
- Consider performance (render only when needed)
- Follow Minecraft's HUD conventions

### Events
- Use Fabric's event lifecycle system
- Register in `AncestralPowers.onInitialize()`
- Keep event handlers lightweight
- Delegate complex logic to specialized classes
- Use `@Environment(EnvType.SERVER)` for server-only events

## Versioning Scheme

### Format: MAJOR.MINOR.PATCH
- **MAJOR**: Breaking changes, major feature additions, or API incompatibilities
- **MINOR**: New features, enhancements, backward-compatible changes
- **PATCH**: Bug fixes, small improvements, documentation

### Examples
- 1.0.0 → Initial stable release
- 1.1.0 → Added new power system
- 1.1.1 → Fixed stamina regen bug
- 2.0.0 → Major refactor of networking layer

### Pre-release Tags (for development)
- Use `-SNAPSHOT` for unstable development versions
- Use `-rc.X` for release candidates
- Example: 1.2.0-rc.1

### Version Rules
1. Never decrease version numbers
2. Increment the appropriate level based on change impact
3. Reset lower numbers when incrementing higher ones (e.g., 1.2.3 → 1.3.0)
4. Keep versions in sync between:
   - `build.gradle` (`version` variable)
   - `fabric.mod.json` (`version` field)
   - Git tags (format: `vMAJOR.MINOR.PATCH`)
   - Changelog entries

## AI Collaboration Best Practices

### When Requesting Changes
1. Be specific about what you want to achieve
2. Reference existing similar implementations
3. Ask for clarification if requirements are unclear
4. Consider edge cases and error handling

### When Reviewing AI-Generated Code
1. Verify it follows existing patterns
2. Check for proper error handling
3. Ensure it doesn't break existing functionality
4. Confirm it addresses the actual requirement
5. Look for performance issues
6. Verify proper imports and dependencies

### When Adding Features
1. Start with minimal implementation
2. Add error handling and edge cases
3. Consider how it integrates with existing systems
4. Think about configurability and extensibility
5. Document any non-obvious behavior

## Troubleshooting Common Issues

### Compilation Errors
- Check for missing imports
- Verify method signatures match interfaces/abstract classes
- Ensure proper package declarations
- Look for typos in variable/method names

### Runtime Issues
- Check logs for exceptions
- Verify networking packets are registered on both sides
- Ensure components are properly registered
- Confirm event registrations happen at correct time

### Performance Problems
- Profile tick handlers for expensive operations
- Check for object creation in loops
- Verify networking isn't sending unnecessary packets
- Look for blocking operations in game threads

## Future Extension Points

### Easy to Add
1. New powers (extend PowerBase)
2. New player data (extend PlayerTraits)
3. New commands (add to ModCommands)
4. New dimensions (add to ModDimensions)
5. New effects (add to registry/ModEffects)

### Requires More Work
1. New networking systems (major packet redesign)
2. Changes to core power mechanics
3. Major rendering system overhauls
4. Saved data format changes

## Contributing to This Document
- Update guidelines when patterns change
- Add examples for common scenarios
- Clarify confusing sections
- Remove outdated advice
- Keep it concise but comprehensive