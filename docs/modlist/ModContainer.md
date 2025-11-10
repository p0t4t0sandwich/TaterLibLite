# ModContainer and Mod Resource Path Research

## Bukkit

```java
import org.bukkit.plugin.JavaPlugin;
import java.io.File;
import java.lang.reflect.Method;

Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
getFileMethod.setAccessible(true);

((File) getFileMethod.invoke(o)).toPath();
```

## BungeeCord

```java
import net.md_5.bungee.api.plugin.Plugin;
import java.io.File;

((Plugin) o).getFile().toPath();
```

## Fabric

```java
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.FabricLoader;

((ModContainer) o).getRootPaths().getFirst();
```

## Forge

### 1.7.10 and below
ModContainer -> File -> Path
```java
import cpw.mods.fml.common.ModContainer;

((ModContainer) o).getSource().toPath();
```

### 1.8 - 1.12.2
ModContainer -> File -> Path
```java
import net.minecraftforge.fml.common.ModContainer;

((ModContainer) o).getSource().toPath();
```

### 1.13 - 1.16.5
IModInfo -> ModFileInfo -> ModFile -> Path
```java
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

((ModFileInfo) ((IModInfo) o).getOwningFile()).getFile().getFilePath();
```

### 1.17.1+
IModInfo -> IModFileInfo -> IModFile -> Path

```java
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;

((IModInfo) o).getOwningFile().getFile().getFilePath();
```

## Ignite

```java
import space.vectrix.ignite.mod.ModContainer;
import space.vectrix.ignite.mod.ModResource;

((ModContainer) o).resource().path();
```

## NeoForge

### 1.20.2+

ModContainer -> IModInfo -> IModFileInfo -> IModFile -> Path

```java
import nnet.neoforged.neoforgespi.language.IModInfo;
import nnet.neoforged.neoforgespi.langauge.IModFileInfo;
import nnet.neoforged.neoforgespi.locating.IModFile;
import net.neoforged.fml.ModContainer;

((ModContainer) o).getModInfo().getOwningFile().getFile().getFilePath();
```

IModInfo -> IModFileInfo -> IModFile -> Path

```java
import nnet.neoforged.neoforgespi.language.IModInfo;
import nnet.neoforged.neoforgespi.langauge.IModFileInfo;
import nnet.neoforged.neoforgespi.locating.IModFile;

((IModInfo) o).getOwningFile().getFile().getFilePath();
```

## Sponge 1.8 - 1.12.2

```java
import org.spongepowered.api.plugin.PluginContainer;

((PluginContainer) o).getSource().get();
```

## Sponge 1.16.5+

```java
import dev.neuralnexus.taterapi.util.PathUtils;
import org.spongepowered.api.plugin.PluginContainer;

PathUtils.getPathFromClass(((PluginContainer) o).instance().getClass());
```

## Velocity

```java
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;

((PluginContainer) o).getDescription().getSource().get();
```
