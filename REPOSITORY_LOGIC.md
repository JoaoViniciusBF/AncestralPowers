# Ancestral Powers Mod - Repository Structure and Logic

## Overview
This document explains the logical structure and design decisions behind the Ancestral Powers mod repository. It explains how different systems interact and the reasoning behind architectural choices.

## Repository Skeleton Explained

### Core Mod Class (`AncestralPowers.java`)
- **Purpose**: Entry point for the mod, implements Fabric's initializer interfaces
- **Responsibilities**: 
  - Register all systems on initialization
  - Set up networking packet registry
  - Initialize core game systems (dimensions, commands, effects, entities)
  - Separates client-only initialization (`onInitializeClient`)
- **Location**: Root package (`dev.joaq.ancestralpowers`)

### Power System (`powers/` package)
- **Purpose**: Implement all player abilities and special powers
- **Structure**:
  - `Power.java`: Interface defining power contract
  - `PowerBase.java`: Abstract base class with common logic
  - `powers.main/`: Primary powers (typically R-key activated)
  - `powers.secondary/`: Secondary powers (typically G-key activated)
  - `PowersManager.java`: Central registry and dispatcher for powers
- **Logic Flow**:
  1. Player activates power via keybind
  2. Network packet sent to server (`ToggleGPayload`/`ToggleRPayload`)
  3. Server sets power activation flags in `PlayerTraits`
  4. `PlayerPowersTickHandler` processes active powers each tick
  5. Appropriate power's `apply()` method called via `PowersManager`
  6. Power executes logic, consumes stamina, applies effects

### Components System (`components/` package)
- **Purpose**: Store and synchronize player-specific data using Cardinal Components
- **Key Component**: `PlayerTraits`
  - Stores stamina levels
  - Tracks active powers
  - Manages dimension-specific data
  - Handles teleport/usage positions
  - Controls scale modifiers and arena states
- **Registration**: `MyComponents` implements `EntityComponentInitializer`

### Events System (`events/` package)
- **Purpose**: Respond to game events and maintain periodic logic
- **Key Events**:
  - `PlayerJoinEvent`: Initialize new players (stamina=100)
  - `PlayerDeathEvent`: Handle immortality power and death logic
  - `PlayerPowersTickHandler`: Main game loop for power processing and stamina regen
- **Registration**: Through Fabric's event lifecycle system

### Networking System (`networking/` package)
- **Purpose**: Synchronize state between client and server
- **Packet Types**:
  - **C2S (Client→Server)**: 
    - `ToggleGPayload`: G-key state changes
    - `ToggleRPayload`: R-key state changes
    - `PersonalDimensionCounterPayload`: Track personal dimension usage
  - **S2C (Server→Client)**:
    - `StaminaSyncPayload`: Synchronize stamina levels for HUD display
- **Flow**: 
  - Input → Keybind → Packet → Server processing → State update → Response packet → Client update

### Client System (`client/` package)
- **Purpose**: Handle client-specific rendering and input
- **Components**:
  - `ModKeyBinds`: Register G and R keybinds
  - `StaminaHudOverlay`: Render stamina bar as XP-style overlay
  - `AncestralPowersClient`: Initialize client systems and register render callback
- **Rendering**: Uses `HudRenderCallback` to draw stamina bar each frame

### Dimensions System (`dimensions/` package)
- **Purpose**: Manage custom dimensions and their structures
- **Features**:
  - Personal dimensions (player-specific pockets of space)
  - Dimensional arena (combat/challenge space)
  - Structure placement and generation
  - Dimensional travel mechanics

### Registry System (`registry/` package)
- **Purpose**: Register custom game objects with Minecraft
- **Registered Objects**:
  - Custom entities (e.g., `CustomFireballEntity`)
  - Custom effects (e.g., `POWER_SUPPRESSION`)
  - (Potentially) custom blocks/items in future

### Utilities (`util/` package)
- **Purpose**: Helper functions and utilities
- **Current Contents**:
  - `RandomUtils`: Random number generation helpers
  - `PlayerUtils`: Player-related helper functions

## Logical Design Decisions

### Why This Architecture?

#### 1. Component-Based Data Storage (Cardinal Components)
- **Reason**: Clean separation of player data from entity logic
- **Benefits**: 
  - Automatic synchronization
  - Persistence across sessions
  - Clean dependency injection
  - Easy to extend with new data fields

#### 2. Tick-Based Power Processing
- **Reason**: Powers need continuous evaluation, not just activation/deactivation
- **Benefits**:
  - Smooth toggling of persistent powers
  - Continuous stamina drain for active powers
  - Regular stamina regeneration
  - Consistent game state updates

#### 3. Separation of Power Types (Main/Secondary/Specific)
- **Reason**: Different activation patterns and UI expectations
- **Benefits**:
  - Clear mental model for players
  - Different input handling (toggle vs hold vs press)
  - Flexible power design space
  - Prevents conflicting power interactions

#### 4. Client-Side Prediction with Server Authority
- **Reason**: Responsive feel while preventing cheating
- **Benefits**:
  - Immediate client feedback for inputs
  - Server-authoritative game state prevents exploits
  - Smooth interpolation via networking packets
  - Fair multiplayer experience

#### 5. Modular Power Implementation
- **Reason**: Easy expansion and maintenance
- **Benefits**:
  - Isolated power logic reduces bugs
  - Easy to test individual powers
  - Simple to add new powers
  - Clear extension points for new mechanics

## Data Flow Examples

### Activating a Toggle Power (e.g., Flight)
1. Client: Player presses G key
2. Client: `ModKeyBinds` detects keypress
3. Client: Sends `ToggleGPayload(true)` to server
4. Server: `ModPacketsC2S` receives packet
5. Server: Toggles `actPower_secondary` in `PlayerTraits`
6. Server Tick: `PlayerPowersTickHandler` sees active secondary power
7. Server: Calls `PowersManager.applyMovementPower()` 
8. Server: `FlyPower.apply()` executes
9. Server: Player gains ability to fly
10. Server: Stamina begins draining each tick
11. Server: Sends `StaminaSyncPayload` to client
12. Client: `StaminaHudOverlay` updates stamina bar display

### Using an Instant Power (e.g., Fireball)
1. Client: Player presses R key
2. Client: `ModKeyBinds` detects keypress
3. Client: Sends `ToggleRPayload(true)` to server
4. Server: `ModPacketsC2S` receives packet
5. Server: Sets `actPower_main` to true in `PlayerTraits`
6. Server Tick: `PlayerPowersTickHandler` sees active main power
7. Server: Calls `PowersManager.applyMainPower()`
8. Server: `FireballPower.apply()` executes
9. Server: Creates and launches fireball entity
10. Server: Deducts 20 stamina from player
11. Server: Resets `actPower_main` to false (instant power)
12. Server: Sends `StaminaSyncPayload` to client
13. Client: `StaminaHudOverlay` updates stamina bar display

## Extension Guidelines

### Adding a New Power
1. Decide if it's Main, Secondary, or Specific type
2. Choose activation pattern (PRESS, TOGGLE, HOLD, PRESS-PERSISTENT, TELEPORT)
3. Extend `PowerBase` in appropriate subpackage
4. Implement required abstract methods
5. Register in `PowersManager.getPower()` or `getPowerSecondary()`
6. Add to reset arrays in `PowersManager.resetAll()` if needed
7. Consider if it needs special networking or rendering

### Adding New Player Data
1. Add field to `PlayerTraits` interface
2. Implement in `PlayerTraitsComponent`
3. Add to `writeData()` and `readData()` methods
4. Register access in `fabric.mod.json` custom section if needed
5. Use appropriate data type (prefer primitives/enums over String)
6. Consider default values and synchronization needs

### Adding Networking Functionality
1. Create packet record in appropriate package (`c2s` or `s2c`)
2. Implement `CustomPayload` with proper `Id` and `PacketCodec`
3. Register packet in `AncestralPowers.onInitialize()`
4. Create handler in appropriate networking class
5. Consider thread switching (`execute()`) for server-bound logic
6. Keep packets minimal - only send essential data

## Inter-System Dependencies

### Power System Dependencies
- Depends on: Components (for stamina/traits), Networking (for activation), Effects (for suppression)
- Used by: Events (tick handler), Networking (for activation packets)

### Components Dependencies
- Depends on: Cardinal Components API (external)
- Used by: Powers (for stamina/traits), Events (for player data), Networking (for sync)

### Networking Dependencies
- Depends on: Components (for data to send), Powers (for knowing what to sync)
- Used by: Powers (for remote activation), Components (for data sync), Client (for HUD)

### Client System Dependencies
- Depends on: Networking (for synced data), Components (indirectly via networking)
- Used by: None (client-only, doesn't affect server logic)

This architecture provides a solid foundation that separates concerns while maintaining clear communication paths between systems. The design prioritizes extensibility, maintainability, and clear data flow patterns.