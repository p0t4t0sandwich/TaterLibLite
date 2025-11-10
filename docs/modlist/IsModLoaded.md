# IsModLoaded Research

## Bukkit

```java
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.UnknownNullability;

@UnknownNullability Plugin plugin = Bukkit.getPluginManager().getPlugin("pluginName");
plugin != null;
```

## BungeeCord

```java
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jspecify.annotations.UnknownNullability;

@UnknownNullability Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin("pluginName");
plugin != null;
```

## Fabric

```java
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.FabricLoader;

FabricLoader.getInstance().isModLoaded("modId");
```

## Forge

### 1.7.10 and below

```java
import cpw.mods.fml.common.Loader;

Loader.isModLoaded("modId");
```

### 1.8 - 1.12.2

```java
import net.minecraftforge.fml.common.Loader;

Loader.isModLoaded("modId");
```

### 1.13+

Using `net.minecraftforge.fml.ModList`

```java
import net.minecraftforge.fml.ModList;

ModList.get().isLoaded("modid");
```

Using `net.minecraftforge.fml.loading.LoadingModList`

```java
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.jspecify.annotations.UnknownNullability;

@UnknownNullability ModFileInfo modFileInfo = LoadingModList.get().getModFileById("modId");
modFileInfo != null;
```

## Ignite

```java
import space.vectrix.ignite.Ignite;

Ignite.mods().loaded("modId");
```

## NeoForge

### 1.20.2+

Using `net.neoforged.fml.ModList`

```java
import net.neoforged.fml.ModList;

ModList.get().isLoaded("modid");
```

Using `net.neoforged.neoforgespi.loading.LoadingModList`

```java
import net.neoforged.neoforgespi.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.jspecify.annotations.UnknownNullability;

@UnknownNullability ModFileInfo modFileInfo = LoadingModList.get().getModFileById("modId");
modFileInfo != null;
```

## Sponge 1.8 - 1.12.2

```java
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

Sponge.getPluginManager().isLoaded("pluginId");
```

## Sponge 1.16.5+

```java
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

Sponge.pluginManager().getPlugin("pluginId").isPresent();
```

## Velocity

```java
import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;
import com.google.inject.Inject;

@Inject private ProxyServer proxyServer;

proxyServer.getPluginManager().isLoaded("pluginId");
```
