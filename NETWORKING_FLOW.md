# Networking Packet Flow Documentation

## Overview
This document explains the networking architecture of the Ancestral Powers mod, detailing how packets are structured, transmitted, and processed between client and server to synchronize game state while maintaining security and responsiveness.

## Networking Principles

### Server-Authoritative Model
- **Server decides**: All game logic runs on server, client predicts and renders
- **Client trusts but verifies**: Client shows immediate feedback but accepts server corrections
- **Prevents cheating**: Clients cannot lie about their state to gain unfair advantage
- **Standard practice**: Used by most multiplayer games for fair competitive play

### Packet Types
- **C2S (Client to Server)**: 
  - Player inputs, requests, client-state changes
  - Examples: Keypresses, UI interactions, action confirmations
- **S2C (Server to Client)**:
  - Game state updates, entity information, world changes
  - Examples: Stamina updates, power activations, world events

### Design Goals
1. **Low Latency Feel**: Immediate client feedback for responsive gameplay
2. **Security**: Prevent exploitational packet manipulation
3. **Efficiency**: Minimize bandwidth usage
4. **Reliability**: Handle packet loss/reordering gracefully
5. **Extensibility**: Easy to add new packet types for new features

## Packet Architecture

### Packet Location and Structure
All packets implement `CustomPayload` interface and are located in:
- `networking.packet.c2s`: Client→Server packets
- `networking.packet.s2c`: Server→Client packets

Each packet follows this pattern:
```java
public record PacketName(/* data fields */) implements CustomPayload {
    public static final Identifier ID = Identifier.of("ancestralpowers", "packet_name");
    public static final CustomPayload.Id<PacketName> PAYLOAD_ID = new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, PacketName> CODEC = 
        PacketCodec.tuple(/* field codecs */, PacketName::new);
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
```

### Current Packet Types

#### C2S Packets (Client → Server)

1. **`ToggleGPayload`**
   - **Purpose**: Communicate G-key state changes
   - **Data**: `boolean value` (true = pressed, false = released)
   - **Usage**: 
     - Client sends when G key is pressed/released
     - Server uses to toggle secondary power state
   - **Frequency**: On key state change only

2. **`ToggleRPayload`**
   - **Purpose**: Communicate R-key state changes
   - **Data**: `boolean value` (true = pressed, false = released)
   - **Usage**: 
     - Client sends when R key is pressed/released
     - Server uses to trigger main power actions
   - **Frequency**: On key state change only

3. **`PersonalDimensionCounterPayload`**
   - **Purpose**: Track personal dimension usage for achievements/stats
   - **Data**: `Integer totalDirtBlocksBroken` (example counter)
   - **Usage**: 
     - Client sends periodic updates of personal dimension interactions
     - Server aggregates for统计/achievement purposes
   - **Frequency**: Periodic or on significant events

#### S2C Packets (Server → Client)

1. **`StaminaSyncPayload`**
   - **Purpose**: Synchronize stamina levels for HUD display
   - **Data**: 
     - `float currentStamina`: Player's current stamina (0-100)
     - `float maxStamina`: Player's maximum stamina (typically 100)
   - **Usage**: 
     - Server sends each tick with updated stamina values
     - Client uses to render stamina HUD overlay
   - **Frequency**: Every game tick (20 times/second)

## Networking Classes

### `ModPacketsC2S` (Client→Server Handling)
- **Location**: `src/main/java/dev/joaq/ancestralpowers/networking/ModPacketsC2S.java`
- **Purpose**: Register handlers for packets coming FROM clients TO server
- **Method**: `public static void register()`
- **Handlers**:
  - `ToggleGPayload`: Toggles secondary power flag in PlayerTraits
  - `ToggleRPayload`: Toggles main power flag in PlayerTraits
- **Threading**: Uses `context.player().getServer().execute()` to switch to main server thread

### `ModPacketsS2C` (Server→Client Handling)
- **Location**: `src/main/java/dev/joaq/ancestralpowers/networking/ModPacketsS2C.java`
- **Purpose**: Handle packets coming FROM server TO clients (currently just sending)
- **Method**: `public static void sendStaminaSync(ServerPlayerEntity player, float currentStamina, float maxStamina)`
- **Usage**: Called by `PlayerPowersTickHandler` each tick to sync stamina
- **Note**: Registration happens in `AncestralPowers.onInitialize()` via `PayloadTypeRegistry`

### Packet Registration (`AncestralPowers.java`)
- **Location**: `src/main/java/dev/joaq/ancestralpowers/AncestralPowers.java`
- **Method**: `public void onInitialize()`
- **Registration**:
  ```java
  // C2S Packets (Client → Server)
  PayloadTypeRegistry.playC2S().register(ToggleGPayload.PAYLOAD_ID, ToggleGPayload.CODEC);
  PayloadTypeRegistry.playC2S().register(ToggleRPayload.PAYLOAD_ID, ToggleRPayload.CODEC);
  PayloadTypeRegistry.playC2S().register(PersonalDimensionCounterPayload.ID, PersonalDimensionCounterPayload.CODEC);
  
  // S2C Packets (Server → Client)
  PayloadTypeRegistry.playS2C().register(StaminaSyncPayload.PAYLOAD_ID, StaminaSyncPayload.CODEC);
  ```
- **Additional**: Registers custom component handlers and other systems

## Detailed Packet Flow Examples

### Example 1: Toggle Power Activation (Flight Power)

#### Client → Server Flow
1. **Event**: Player presses G key
2. **Client**: `ModKeyBinds` detects keypress
3. **Client**: `ClientPlayNetworking.send(new ToggleGPayload(true))`
4. **Network**: Packet transmitted to server
5. **Server**: `ModPacketsC2S` receives `ToggleGPayload`
6. **Server**: Switches to main server thread via `getServer().execute()`
7. **Server**: Retrieves player's `PlayerTraits`
8. **Server**: Sets `traits.setActPower_secondary(true)`
9. **Server**: Sends confirmation message to client ("G = true")
10. **Server Tick**: `PlayerPowersTickHandler` sees active secondary power
11. **Server**: Calls `PowersManager.applyMovementPower()` for flight power
12. **Server**: `FlyPower.apply()` enables player flight abilities
13. **Server**: Each tick, stamina decreases by flight power cost
14. **Server**: Sends `StaminaSyncPayload` with updated stamina
15. **Client**: Receives packet, updates `StaminaHudOverlay`
16. **Client**: Renders stamina bar reflecting current level

#### Server → Client Flow (Deactivation)
1. **Event**: Player releases G key (or stamina depletes)
2. **Client**: `ModKeyBinds` detects key release
3. **Client**: `ClientPlayNetworking.send(new ToggleGPayload(false))`
4. **Network**: Packet transmitted to server
5. **Server**: `ModPacketsC2S` receives `ToggleGPayload`
6. **Server**: Sets `traits.setActPower_secondary(false)`
7. **Server Tick**: `PlayerPowersTickHandler` sees inactive secondary power
8. **Server**: `FlyPower.disablePowerSpecific()` disables flight
9. **Server**: Player loses flight abilities, falls if airborne
10. **Server**: Stamina stops decreasing for flight power
11. **Server**: Continues sending stamina sync packets
12. **Client**: HUD shows stamina regenerating normally

### Example 2: Instant Power Use (Fireball)

#### Client → Server Flow
1. **Event**: Player presses R key
2. **Client**: `ModKeyBinds` detects keypress
3. **Client**: `ClientPlayNetworking.send(new ToggleRPayload(true))`
4. **Network**: Packet transmitted to server
5. **Server**: `ModPacketsC2S` receives `ToggleRPayload`
6. **Server**: Switches to main server thread
7. **Server**: Retrieves player's `PlayerTraits`
8. **Server**: Sets `traits.setActPower_main(true)`
9. **Server Tick**: `PlayerPowersTickHandler` sees active main power
10. **Server**: Calls `PowersManager.applyMainPower()` for fireball power
11. **Server**: `FireballPower.apply()` creates and launches fireball entity
12. **Server**: Stamina decreases by fireball cost (20 points)
13. **Server**: `FireballPower` resets main power flag (instant use)
14. **Server**: Sends `StaminaSyncPayload` with updated stamina
15. **Client**: Receives packet, updates `StaminaHudOverlay`
16. **Client**: Renders stamina bar showing cost deducted
17. **Client**: May play sound/visual effects locally (client prediction)

### Example 3: Personal Dimension Tracking
1. **Event**: Player breaks dirt block in personal dimension
2. **Client**: Detects block break in personal dimension context
3. **Client**: Increments local dirt counter
4. **Client**: Periodically (or on threshold) sends `PersonalDimensionCounterPayload`
5. **Server**: `ModPacketsC2S` receives packet
6. **Server**: Updates personal dimension statistics
7. **Server**: May grant achievements or update stats based on count
8. **Note**: This is asynchronous tracking, not real-time gameplay critical

## Technical Implementation Details

### Packet Creation and Transmission
- **Immutable**: All packets are `record` classes (effectively final)
- **Thread Creation**: 
  - C2S: Created on client game thread, sent via networking thread
  - S2C: Created on server game thread, sent via networking thread
- **Serialization**: Handled by Mojang's PacketCodec system
- **Delivery**: Best-effort unreliable delivery (typical for game updates)

### Threading Model
- **Networking Threads**: Handle incoming/outgoing packet I/O
- **Game Threads**: Where game logic executes (main server/client threads)
- **Boundary Crossing**: 
  - C2S→Server: `getServer().execute()` runs Runnable on main server thread
  - S2C→Client: `context.client().execute()` runs Runnable on main client thread
- **Why**: Game state must only be modified from main game thread for safety

### Packet Size Optimization
- **Boolean**: 1 byte
- **Float**: 4 bytes
- **Integer**: 4 bytes
- **Typical Packet**: 
  - `ToggleGPayload`: ~1 byte data + overhead
  - `StaminaSyncPayload`: ~8 bytes data + overhead
  - `PersonalDimensionCounterPayload`: ~4 bytes data + overhead
- **Bandwidth**: With 20 ticks/sec and stamina sync:
  - ~160 bytes/sec/player for stamina alone
  - Well under 1 KB/sec/player with all packets
  - Easily handles hundreds of players on modest bandwidth

### Reliability and Loss Handling
- **Unreliable Delivery**: Packets may be dropped, duplicated, reordered
- **Implied Acknowledgement**: State updates are frequent, so loss is self-correcting
- **Stamina Example**: 
  - If one stamina packet lost, next tick's packet corrects it
  - Maximum error: 1 tick (0.05 seconds) of outdated stamina display
  - Acceptable trade-off for simplicity and performance
- **Critical State**: More important state (like position) may use reliable channels
- **Our Use Case**: Stamina is tolerant of occasional loss due to high frequency

### Security Considerations
- **Validation**: Server validates all incoming data
  - Example: Even if client sends invalid stamina, server ignores it
  - Server state is authoritative - client cannot dictate server state
- **Bounds Checking**: 
  - Server clamps/stores values safely
  - Invalid packet data is ignored or causes disconnection (handled by networking layer)
- **Rate Limiting**: Not implemented per-packet, but networking stack has protections
- **Encryption**: Handled by underlying transport (TLS-like in modern Minecraft)

## Extending the Networking System

### Adding a New Packet Type
1. **Determine Direction**: 
   - Does client need to tell server something? → C2S
   - Does server need to tell client something? → S2C
2. **Create Packet Record**:
   - In appropriate package (`networking.packet.c2s` or `s2c`)
   - Follow the record pattern with Identifier, Id, and PacketCodec
   - Keep data minimal - only send what's necessary
3. **Register Packet**:
   - In `AncestralPowers.onInitialize()`
   - Use correct registry (`playC2S()` or `playS2C()`)
4. **Create Handler**:
   - For C2S: Add to `ModPacketsC2S.register()`
   - For S2C: Usually just sending, handled by game logic
5. **Use in Game Logic**:
   - Send packet when appropriate event occurs
   - Process packet to update game state
   - Consider if response packet needed

### Best Practices for New Packets
1. **Minimal Data**: Send only what changed, not entire state
2. **Delta Updates**: When possible, send changes rather than absolute values
3. **Batching**: Combine related information in single packet when makes sense
4. **Versioning**: Consider backward compatibility if modifying existing packets
5. **Documentation**: Comment what packet is for and when it's sent
6. **Testing**: Verify packet flows correctly in both directions
7. **Performance**: Consider frequency - don't spam packets unnecessarily

## Common Patterns

### Request-Response Pattern
Less common in our system due to frequent state updates, but used for:
1. **Client**: Sends request packet (e.g., "open GUI")
2. **Server**: Processes request, sends response packet (e.g., "here's GUI data")
3. **Client**: Receives response, opens GUI with correct data

### State Synchronization Pattern (Our Primary Method)
Used for stamina and similar frequently-changing values:
1. **Server**: Periodically sends current state
2. **Client**: Updates local copy of state
3. **Client**: Uses state for rendering/prediction
4. **Server**: Authoritative - if client prediction wrong, server correction wins

### Event Notification Pattern
Used for infrequent, significant events:
1. **Event occurs** on server (e.g., player earns achievement)
2. **Server**: Sends notification packet to relevant clients
3. **Client**: Receives notification, plays sound/shows effect
4. **No State Change**: Purely informational, doesn't alter game state

## Performance and Scalability

### Per-Player Overhead
- **Stamina Sync**: ~12 bytes every 50ms = ~240 bytes/sec/player
- **Key Events**: Minimal - only on actual input changes
- **Other Packets**: Vary by feature usage
- **Typical Player**: < 1 KB/sec upstream + downstream
- **100 Players**: < 100 KB/sec total - very lightweight

### Server Processing
- **Packet Handling**: Minimal - simple boolean flips or float assignments
- **Main Cost**: Game logic that packets trigger (power effects, etc.)
- **Networking Overhead**: Negligible compared to actual gameplay processing

### Client Processing
- **Packet Handling**: Very low - just updating a couple floats
- **Rendering**: Stamina bar draws two rectangles and text each frame
- **Input Processing**: Keybind checking happens regardless

### Network Conditions
- **High Latency**: 
  - Client prediction feels responsive
  - Server correction may cause slight "rubberbanding" but rare with stamina
  - Power activation delay equals network RTT (standard limitation)
- **Packet Loss**: 
  - Self-correcting due to high frequency
  - Maximum visual error: one tick of stamina display
  - Practically unnoticeable
- **Bandwidth Limits**: 
  - System designed to work on very modest connections
  - Would require extreme packet loss or very low bandwidth to impact gameplay

## Troubleshooting Networking Issues

### Common Problems and Solutions

#### "Packet not received" Symptoms
- **Check**: 
  - Packet registered on correct side (C2S vs S2C)
  - Identifier matches exactly (case-sensitive)
  - PacketCodec properly implemented
  - Registration happens in `onInitialize()` (not client-only for S2C handlers)
- **Solution**: Verify registration and packet definitions match

#### "Server not responding to client" Symptoms
- **Check**: 
  - Packet sent from client (keybind working?)
  - Packet received by server (add logging to handler)
  - Server thread switching correct (`getServer().execute()`)
  - Game logic actually processes the state change
- **Solution**: Add debug logging to verify packet flow

#### "Client not updating display" Symptoms
- **Check**: 
  - S2C packet registered in `playS2C()` registry
  - Packet being sent regularly (check server logs)
  - Client receiver set up correctly (`ClientPlayNetworking.registerGlobalReceiver`)
  - Client using main thread executor (`context.client().execute()`)
  - Render callback properly registered and calling render method
- **Solution**: Verify stamina sync packet flow from server to client

#### "Inconsistent state between client/server" Symptoms
- **Check**: 
  - Who is authoritative? (Should be server for game logic)
  - Are packets being sent frequently enough?
  - Is client prediction being overridden by server corrections?
  - Are there missing validations allowing client to influence server state?
- **Solution**: Ensure server makes final decisions, client only predicts

### Debugging Techniques
1. **Add Temporary Logging**: 
   ```java
   System.out.println("[Network] Sending ToggleGPayload: " + value);
   ```
2. **Verify Registration**: 
   - Check that packet IDs match in registration and packet definition
3. **Check Threading**: 
   - Ensure game logic runs on main thread (crashes if not)
   - Use `getServer().execute()` / `context.client().execute()` correctly
4. **Monitor Frequency**: 
   - Add counters to see how often packets are sent/received
5. **Check Bounds**: 
   - Verify data stays in expected ranges (stamina 0-100, etc.)

## Future Networking Enhancements

### Potential Improvements
1. **Packet Batching**: Combine multiple small packets into one
2. **Compress Frequent Data**: For very high-frequency updates
3. **Adaptive Frequency**: Reduce update rate when values stable
4. **Priority Queuing**: Ensure critical packets send even during congestion
5. **Connection Quality Metrics**: Adapt behavior based on packet loss/latency
6. **Encryption/Signing**: For highly sensitive data (not currently needed)

### Features Requiring New Packets
1. **Advanced GUI Interactions**: Inventory changes, crafting, etc.
2. **World Modifications**: Block placement/breaking (if adding build powers)
3. **Entity Interactions**: Custom entity AI, riding, etc.
4. **Visual Effects**: Party particles, sounds, screen effects (may be client-only)
5. **Achievements/Stats**: Detailed progression tracking
6. **Communication Systems**: Chat, markers, pings between players

## Summary

The Ancestral Powers networking system provides a solid foundation for client-server communication that:
- Maintains server security while enabling responsive client feedback
- Uses efficient, minimal packets for frequent updates like stamina
- Follows established Minecraft/Fabric networking patterns
- Is straightforward to extend for new features
- Handles real-world network conditions gracefully
- Scales well from single-player to multiplayer servers

By separating concerns (input handling → networking → game logic → state sync → rendering) and using clear packet types for specific purposes, the system remains maintainable and extensible while providing the foundation for engaging multiplayer gameplay.