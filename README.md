[forks]: https://img.shields.io/github/forks/demkom58/DivineDrop
[stars]: https://img.shields.io/github/stars/demkom58/DivineDrop
[issues]: https://img.shields.io/github/issues/demkom58/DivineDrop
[license]: https://img.shields.io/github/license/demkom58/DivineDrop
[spigot]: https://img.shields.io/badge/SpigotMC-Resource-ffb600
[ ![forks][] ](https://github.com/demkom58/DivineDrop/network/members)
[ ![stars][] ](https://github.com/demkom58/DivineDrop/stargazers)
[ ![issues][] ](https://github.com/demkom58/DivineDrop/issues)
[ ![license][] ](https://github.com/demkom58/DivineDrop/blob/master/LICENSE)
[ ![spigot][] ](https://www.spigotmc.org/resources/51715/)
<img align="right" src="https://i.ibb.co/JmLN3qn/51715.png" height="100" width="100">

# DivineDrop
It's bukkit plugin that allows display name of item, stack size and time 
before removing item if drop-cleaner enabled, also it's have options for 
drop like pickup items by shift key.

![GIF](https://i.imgur.com/1QuiJsz.gif)

## Build
### Preparing
We cannot store the server core dependencies in the repository, 
downloading them from the public mavens of the repositories is 
also quite problematic. Therefore, if you want to build the plugin 
yourself, you must download all versions of the spigot cores that the 
plugin supports (with different NMS releases) and place in
the `libraries` directory

List of needed cores (Any popular fork should work too):
```
Spigot 1.8.8
Spigot 1.9
Spigot 1.9.4
Spigot 1.10.2
Spigot 1.11.2
Spigot 1.12.2
Spigot 1.13
Spigot 1.13.2
Spigot 1.14.4
Spigot 1.15.2
Spigot 1.16.1
Spigot 1.16.2
Spigot 1.16.4
```

### Creating JAR
To create simple plugin jar, open root directory of DivineDrop 
and enter: `gradlew jar`.

If all will successfully done, you will able to plugin jar
it in `build/libs/` directory.