# ADR-001: Hit Testing Approach

**Date:** 2025-11-03  
**Status:** Approved

## Context
The project requires precise touch detection over shapes within a scalable SVG.  
Touch coordinates must remain accurate even when zooming or panning.

## Alternatives Considered
1. Calculate approximate bounding boxes.
2. Use external vector map libraries.
3. Parse the SVG `pathData` and use `Path.contains()` with transformation matrices.

## Decision
Implement manual hit-testing using `PathParser` to convert `pathData` elements from the SVG into `Path` objects, and use `Path.contains(offset)` to determine whether a tap falls inside a shape.  
Zoom and pan transformation matrices are applied to maintain positional accuracy.

## Consequences
- Guarantees precise hit detection even at high zoom levels.
- Requires additional calculations but avoids external dependencies.
