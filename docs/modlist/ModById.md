# ModById Research

## Bukkit

```java
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.UnknownNullability;

@UnknownNullability Plugin plugin = Bukkit.getPluginManager().getPlugin("pluginName");
```

## BungeeCord

```java
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jspecify.annotations.UnknownNullability;

@UnknownNullability Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin("pluginName");
```

## Fabric

```java
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.FabricLoader;

Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer("modId");
```

## Forge

### 1.7.10 and below

```java
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import java.util.Optional;

Optional<ModContainer> mod = Loader.instance().getModList().stream().filter(m -> m.getModId().equals("modId")).findFirst();
```

### 1.8 - 1.12.2

```java
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import java.util.Optional;

Optional<ModContainer> mod = Loader.instance().getModList().stream().filter(m -> m.getModId().equals("modId")).findFirst();
```

### 1.13+

Using `net.minecraftforge.fml.ModList`

```java
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import java.util.Optional;

Optional<ModContainer> mod = ModList.get().getModContainerById("modid");
```

Using `net.minecraftforge.fml.loading.LoadingModList`

```java
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import java.util.Optional;

Optional<ModInfo> mod = LoadingModList.get().getMods().stream().filter(m -> m.getModId().equals("modId")).findFirst();
// Should be able to down-cast
Optional<IModInfo> imod = mod.map(m -> (IModInfo) m);
```

## Ignite

```java
import space.vectrix.ignite.Ignite;
import space.vectrix.ignite.mod.ModContainer;
import java.util.Optional;

Optional<ModContainer> mod = Ignite.mods().container("modId");
```

## NeoForge

### 1.20.2+

Using `net.neoforged.fml.ModList`

```java
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import java.util.Optional;

Optional<ModContainer> mod = ModList.get().getModContainerById("modid");
```

Using `net.neoforged.fml.loading.LoadingModList`

```java
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import java.util.Optional;

Optional<ModInfo> mod = LoadingModList.get().getMods().stream().filter(m -> m.getModId().equals("modId")).findFirst();
// Should be able to down-cast
Optional<IModInfo> imod = mod.map(m -> (IModInfo) m);
```

## Sponge 1.8 - 1.12.2

```java
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import java.util.Optional;

Optional<PluginContainer> plugin = Sponge.getPluginManager().getPlugin("pluginId");
```

## Sponge 1.16.5+

```java
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import java.util.Optional;

Optional<PluginContainer> plugin = Sponge.pluginManager().getPlugin("pluginId");
```

## Velocity

```java
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.Optional;

@Inject private ProxyServer proxyServer;

Optional<PluginContainer> plugin = proxyServer.getPluginManager().getPlugin("pluginId");
```
