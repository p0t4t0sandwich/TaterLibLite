# ModList Research

## Bukkit

```java
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import java.util.List;

List<Plugin> plugins = List.of(Bukkit.getPluginManager().getPlugins());
```

## BungeeCord

```java
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import java.util.Collection;

Collection<Plugin> plugins = ProxyServer.getInstance().getPluginManager().getPlugins();
```

## Fabric

```java
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.FabricLoader;
import java.util.Collection;

Collection <ModContainer> mods = FabricLoader.getInstance().getAllMods();
```

## Forge

### 1.7.10 and below

```java
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import java.util.List;

List<ModContainer> mods = Loader.instance().getModList();
```

### 1.8 - 1.12.2

```java
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import java.util.List;

List<ModContainer> mods = Loader.instance().getModList();
```

### 1.13 - 1.21.3

Using `net.minecraftforge.fml.ModList`

```java
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import java.util.List;
import java.lang.reflect.Field;

Field modsField = ModList.class.getDeclaredField("mods");
modsField.setAccessible(true);

List<ModContainer> mods = (List<ModContainer>) modsField.get(ModList.get());
```

### 1.21.4+

Using `net.minecraftforge.fml.ModList`

```java
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import java.util.List;

List<ModContainer> mods = ModList.get().getLoadedMods();
```

## Ignite

```java
import space.vectrix.ignite.Ignite;
import space.vectrix.ignite.mod.ModContainer;
import java.util.Collection;

Collection<ModContainer> mods = Ignite.mods().containers();
```

## NeoForge

### 1.20.2+

Using `net.neoforged.fml.ModList`

```java
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import java.util.List;

List<ModContainer> mods = ModList.get().getSortedMods();
```

Using `net.neoforged.fml.loading.LoadingModList`

```java
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforgespi.language.IModInfo;

List<ModInfo> mods = LoadingModList.get().getMods();
// Should be able to down-cast
List<IModInfo> modsSpi = mods;
```

## Sponge 1.8 - 1.12.2

```java
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import java.util.Collection;

Collection<PluginContainer> plugins = Sponge.getPluginManager().getPlugins();
```

## Sponge 1.16.5+

```java
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import java.util.Collection;

Collection<PluginContainer> plugins = Sponge.pluginManager().getPlugins();
```

## Velocity

```java
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.Collection;

@Inject private ProxyServer proxyServer;

Collection<PluginContainer> plugins = proxyServer.getPluginManager().getPlugins();
```
