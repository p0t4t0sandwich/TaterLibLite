# TaterLibLite

Just like TaterLib, but with half the fat!

In other words, it's a collection of abstract modloader utilities to make cross-platform development easier.

## Dependency Graph

Base
Metadata -> Base
Core -> Base, Metadata
Muxins -> Metadata
CrossPerms -> Core, Base, Metadata, Muxins
BrigadierGeneral -> CrossPerms, Metadata, Muxins

## Modloader Metadata API
An abstract API for querying modloader metadata at runtime

## Muxins
A mixin plugin and annotation set that allow you to apply mixins based on the platform, loaded plugins/mods, and minecraft version

## CrossPerms
An abstract permission query API that supports all major modding platforms

## BrigadierGeneral
A cross-platform command registration API, primarily focused on exposing brigadier and wrapping platform-specific command implementations.
