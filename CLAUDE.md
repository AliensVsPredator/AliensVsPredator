# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BLib is a Minecraft mod library (v0.2.0-alpha.32) for Minecraft 1.21.1 that simplifies multi-loader mod development, supporting both Fabric and NeoForge. Written in Java 21.

## Build Commands

```bash
# Build both Fabric and NeoForge jars
./gradlew build

# Run data generation (NeoForge then Fabric, outputs to common/src/main/generated)
./gradlew runAllDatagen

# Format code with Spotless (required before PRs)
./gradlew spotlessApply

# Run clients for testing
./gradlew :fabric:runClient
./gradlew :neoforge:runClient

# Build individual loaders
./gradlew :fabric:build
./gradlew :neoforge:build

# Individual datagen
./gradlew :neoforge:runData
./gradlew :fabric:runDatagen
```

## Architecture

### Multiloader Structure

```
common/     # Shared code - has NO knowledge of loader-specific APIs
fabric/     # Fabric-specific implementation and entry point (BLibFabric)
neoforge/   # NeoForge-specific implementation and entry point (BLibNeoForge)
buildSrc/   # Custom Gradle plugins (multiloader-common.gradle, multiloader-loader.gradle)
```

**Key principle**: The `common` module cannot access loader-specific code. Loader-specific modules consume common via `commonJava` and `commonResources` configurations.

### Package Structure

- `com.blib.api.*` - Public APIs (versioned with `v1` suffix for stability)
- `com.blib.internal.*` - Internal implementation (not for external use)
- `com.blib.mod.*` - Main BLib mod entry point

### Service Abstraction

Loader-specific implementations are hidden behind service interfaces in `BLibInternalServices`. Each loader project provides its own service implementations in its `service/` package.

### Key API Entry Points

- `BLibAPI.createMod(modId)` → `BLibMod` - Per-mod facade providing access to events, registries, networking, factories, resources
- `BLibClientMod` - Extends BLibMod with client-specific registries and events
- Access classes (suffixed `*Access`) provide typed access to internal services

### Major API Packages

| Package | Purpose |
|---------|---------|
| `api.client.animation.v1` | Bedrock-format animation system (animators, controllers, keyframe events) |
| `api.client.render.v1` | Pipeline-based rendering (entity, item, block, armor renderers) |
| `api.common.entity.v1` | Entity utilities, AI goals, spawning configuration |
| `api.common.explosion.v1` | Advanced explosion system with builder pattern and callbacks |
| `api.common.data_sync.v1` | Automatic client/server data synchronization |
| `api.common.goap.v1` | Goal-Oriented Action Planning AI system |
| `api.common.registry.v1` | Custom registry system (commands, attributes, trades, fuel, etc.) |
| `api.common.event.v1` | Cross-loader event system |
| `api.common.network.v1` | Packet registration and handling |

### Internal Implementation

- `internal.client.animation` - Animation state machines, easing functions, Molang parser
- `internal.client.model` - Baked model caching and bone structure
- `internal.common.molang` - Bedrock animation language parser

## Development Guidelines

- Develop most code in `common/` module
- Use versioned API packages (`v1`) for public interfaces
- Run `./gradlew spotlessApply` before submitting PRs
- DataGen outputs to `common/src/main/generated` (shared by both loaders)
- Requires Java 21 and IntelliJ IDEA (Eclipse/VSCode not supported)

## Dependencies

Core external libraries:
- `just-codec`, `just-core`, `just-goap` - Custom utility libraries
- Mixin 0.8.5, MixinExtras 0.3.5 - Bytecode manipulation
- Parchment mappings for deobfuscation
