# ADR-003: Dialog vs BottomSheet Decision

**Date:** 2025-11-03  
**Status:** Approved

## Context
Each hotspot must open an editable form when tapped.  
Different UI options were evaluated.

## Alternatives Considered
1. Classic `AlertDialog`.
2. `DialogFragment`.
3. `ModalBottomSheet` from Compose.

## Decision
Use **`ModalBottomSheet` (Material 3)** for its accessibility, Compose-native integration, and consistent Material Design experience.  
It offers better adaptability for mobile screens and native TalkBack support.

## Consequences
- Modern and consistent UX.
- Easy integration with `HotspotViewModel`.
- Less boilerplate compared to a traditional `DialogFragment`.
