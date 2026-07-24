# Energy/Stamina System Documentation

## Overview
This document explains the energy/stamina system implementation in the Ancestral Powers mod, detailing how stamina is managed, consumed, regenerated, and synchronized between client and server.

## System Design Goals

1. **Intuitive Resource Management**: Stamina should feel meaningful but not frustrating
2. **Clear Feedback**: Players should always know their stamina status
3. **Balanced Usage**: Powers should have meaningful costs relative to their effects
4. **Multiplayer Safety**: System should prevent exploitation in multiplayer
5. **Client Responsiveness**: UI should feel immediate while maintaining server authority

## Core Concepts

### Stamina as Percentage (0-100)
- **Range**: 0.0% (empty) to 100.0% (full)
- **Default**: Players start with 100% stamina
- **Representation**: Stored as float in `PlayerTraits`
- **Benefits**: 
  - Intuitive for players (0-100 scale familiar from XP bars)
  - Easy to display as percentage
  - Simple to calculate regen and costs
  - Consistent across all powers

### Stamina Mechanics

#### Consumption
- Each power has a `staminaCost()` method returning percentage points
- Costs are deducted when powers are activated/maintained
- Example costs:
  - Low-cost powers (Speed, Strength): 0.5% per activation/tick
  - Medium-cost powers (Flight): 1.0% per tick
  - High-cost powers (Fireball): 20.0% per use
  - Ultimate powers (Dimensional Arena): 50.0% per activation

#### Regeneration
- **Rate**: 0.25% per game tick (20 ticks/second = 5% per second)
- **Cap**: Cannot exceed 100%
- **Location**: `PlayerPowersTickHandler.java`
- **Formula**: `newStamina = min(currentStamina + REGEN_RATE, MAX_STAMINA)`
- **Note**: Regeneration happens regardless of power usage (can fight and regen simultaneously)

#### Limitations
- Powers check `canActivate()` before execution
- Prevents activation if `currentStamina < staminaCost()`
- Provides feedback: "§eVocê está cansado demais para usar esse poder!"
- Automatically deactivates toggle powers when stamina depleted

## Implementation Details

### Data Storage
- **Location**: `PlayerTraits` component (via Cardinal Components)
- **Field**: `private Float stamina;`
- **Default**: 100f (set in `PlayerTraitsComponent.readData()` and `PlayerJoinEvent`)
- **Serialization**: Automatically handled by Cardinal Components
- **Sync**: Sent to client via `StaminaSyncPayload` networking packet

### Key Methods

#### `PlayerTraits`
- `Float getStamina()`: Returns current stamina percentage
- `void setStamina(Float stamina)`: Sets stamina (clamps internally if needed)

#### `PowerBase` (Abstract Base Class)
- `protected abstract float staminaCost();`: Returns cost percentage
- `protected void spendStamina(PlayerTraits traits, float cost);`:
  ```java
  traits.setStamina(Math.max(0, traits.getStamina() - cost));
  ```
- `protected boolean canActivate(...)`: 
  - Checks Power Suppression effect
  - Verifies sufficient stamina
  - Handles activation type logic

### Power Implementation Requirements
Every power must:
1. Implement `staminaCost()` returning appropriate percentage cost
2. Call `spendStamina()` when consuming stamina
3. Respect `canActivate()` checks (handled by base `execute()` methods)
4. Not manually set stamina (use provided methods)

### Tick Processing (`PlayerPowersTickHandler`)
Each server tick:
1. Process active main powers via `PowersManager.applyMainPower()`
2. Process active secondary powers via `PowersManager.applyMovementPower()`
3. Regenerate stamina: `stamina = min(stamina + 0.25, 100.0)`
4. Send stamina sync to client via `ModPacketsS2C.sendStaminaSync()`

### Client-Side Display
- **Component**: `StaminaHudOverlay.java`
- **Source**: Receives `StaminaSyncPayload` packets from server
- **Storage**: `clientStamina` and `clientMaxStamina` static fields
- **Rendering**: 
  - Bar width: 182 pixels (similar to XP bar)
  - Bar height: 5 pixels
  - Position: Centered horizontally, 40 pixels from bottom
  - Background: Semi-transparent black
  - Empty bar: Dark gray (`0xFF000000`)
  - Filled bar: Green (`0xFF00FF00`)
  - Text: Percentage value above bar (`xx%`)
  - Update: Only renders after first packet received

## Networking Flow

### Stamina Synchronization
1. **Server**: Each tick in `PlayerPowersTickHandler`:
   ```java
   ModPacketsS2C.sendStaminaSync(player, newStamina, MAX_STAMINA);
   ```
2. **Packet**: `StaminaSyncPayload(currentStamina, maxStamina)`
3. **Client**: `StaminaHudOverlay.register()` sets up receiver:
   ```java
   ClientPlayNetworking.registerGlobalReceiver(
       StaminaSyncPayload.PAYLOAD_ID, 
       (payload, context) -> {
           context.client().execute(() -> {
               clientStamina = payload.currentStamina();
               clientMaxStamina = payload.maxStamina();
               receivedFirstPacket = true;
           });
       });
   ```
4. **Rendering**: HudRenderCallback draws stamina bar using stored values
5. **Thread Safety**: Uses `context.client().execute()` to switch to main thread

## Power Cost Rationale

### Low Cost (0.1-1.0%)
- **Powers**: Speed, Strength, basic teleport
- **Reason**: Enable frequent use, encourage experimentation
- **Gameplay**: Form core of power combinations, should be spammable in moderation

### Medium Cost (1.0-5.0%)
- **Powers**: Flight, scale modification, advanced movement
- **Reason**: Significant but sustainable resource investment
- **Gameplay": Tactical choices, require stamina management during use

### High Cost (5.0-20.0%)
- **Powers**: Fireball, area effects, major transformations
- **Reason": Powerful abilities with clear trade-offs
- **Gameplay": Special occasion abilities, require planning and recovery time

### Very High Cost (20.0%+)
- **Powers": Dimensional arena, summoning, reality warping
- **Reason": Game-changing abilities with substantial costs
- **Gameplay": Epic moments, significant downtime afterwards

## Balance Considerations

### Stamina Economy
- **Income**: 5% per second from regeneration
- **Typical Outcome**: 
  - Low-cost power: ~10 activations/second sustainable
  - Medium-cost power: ~5 activations/second sustainable
  - High-cost power: 1 activation every 4+ seconds
  - Ultimate power: Requires saving up, significant downtime after use

### Combat Implications
- Players can maintain toggle powers while regenerating (net loss depends on cost vs regen)
- Example: Flight (1.0%/tick) with regen (0.25%/tick) = net loss of 0.75%/tick
- At 20 ticks/sec: Flight costs ~15%/second net, lasts ~6-7 seconds on full stamina
- Encourages tactical use rather than permanent activation

### Risk/Reward
- High-cost powers leave player vulnerable during cooldown
- Encourages strategic timing and positioning
- Creates opportunities for counterplay in PvP
- Rewards skilled resource management

## Edge Cases and Safety

### Stamina Clamping
- All stamina setting goes through `PlayerTraits.setStamina()`
- Values are naturally clamped by implementation (0-100 range)
- Prevents overflow/underflow issues

### Network Reliability
- Stamina sent every tick ensures quick recovery from packet loss
- Last known good state used if packet missed
- No interpolation needed - stamina changes are discrete and frequent enough

### Death and Respawn
- `PlayerJoinEvent` sets stamina to 100% for new/ respawning players
- Ensures fresh start after death
- Prevents carrying over exhausted states

### Mod Interactions
- Power Suppression effect properly blocks power use
- Could be extended for other effects that modify stamina regen/cost
- Designed to work with other mods that don't interfere with core mechanics

## Performance Characteristics

### Server-Side
- **Memory**: One float per player (~4 bytes)
- **CPU**: Minimal - simple arithmetic per player per tick
- **Networking**: One small packet per player per tick (~8-16 bytes)
- **Scaling**: Linear with player count, very lightweight

### Client-Side
- **Memory**: Two floats for stamina values
- **CPU**: Minimal - simple bar drawing each frame
- **Networking**: Same as server - receives sync packets
- **Rendering**: Efficient rectangle and text drawing

## Future Extensions

### Possible Enhancements
1. **Stamina Modifiers**: Effects that increase/decrease regen rate
2. **Stamina Shields**: Temporary barriers that absorb stamina cost
3. **Stamina Leech**: Powers that restore stamina on hit/kill
4. **Overcharge System**: Allow exceeding 100% with penalty
5. **Different Regen Rates**: Based on player stats, equipment, or buffs
6. **Stamina-based Visual/Sound Effects**: Low stamina warnings, etc.

### Configuration Options
- Base stamina regen rate
- Stamina cost multipliers for balancing
- Enable/disable stamina system (for creative mode)
- Stamina display options (position, size, colors)

## Implementation Notes

### Why Percentage-Based?
- Familiar to players from XP bars, hunger bars, etc.
- Eliminates confusion about arbitrary units
- Easy to reason about ("I have half my stamina left")
- Simple to balance (costs as percentages of total)
- Scales well if max stamina ever becomes variable

### Why Per-Tick Regeneration?
- Matches Minecraft's game loop (20 ticks/second)
- Easy to calculate (0.25% = 5%/second)
- Smooth visual regeneration in HUD
- Consistent with other Minecraft systems (healing, absorption)

### Why Server-Authoritative with Client Prediction?
- Prevents cheating/hacking of stamina values
- Allows immediate UI response to player actions
- Server ultimately decides if actions are valid
- Standard practice in client-server games

This stamina system provides a solid foundation for meaningful power usage while remaining intuitive and responsive. The percentage-based approach, regular regeneration, and clear cost structure create engaging gameplay decisions around resource management.