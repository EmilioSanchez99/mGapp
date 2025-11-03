# ADR-002: Persistence Strategy

**Date:** 2025-11-03  
**Status:** Approved

## Context
The project needs local storage, offline persistence, and export/backup capabilities.

## Alternatives Considered
1. SharedPreferences
2. Proto DataStore
3. Room Database with JSON export

## Decision
Use **Room** as the local ORM to store `HotspotEntity` objects with ID, name, description, and state.  
For export and import, serialize the data into JSON using a `HotspotJsonHelper`.

## Consequences
- Reliable and relational local persistence.
- Compatible with unit testing and database migrations.
- JSON export enables interoperability and external backup.
