<div align="center">
    <img height="250" src="https://github.com/AugustoMegener/Kore/blob/NF-1.21.10-0.1.x/kore-logo.png?raw=true">
</div>

# Kore: Kotlin Minecraft Modding Framework for NeoForge

<a href="https://github.com/AugustoMegener/Kore">
  <img alt="github" height="45" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg">
</a>

<a href="https://modrinth.com/projects/kore">
  <img alt="modrinth" height="45" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg">
</a>

<a href="https://neoforged.net/">
  <img alt="neoforge" height="45" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg">
</a>

---

Kore is a comprehensive Minecraft modding framework designed to optimize and simplify the development of new content for the game. It provides a set of auxiliary utilities, abstractions, and functions to simplify common modification tasks, allowing developers to focus on the effective creation of their mod.

## Features

- **Tweaked Registration**: Kore offers a streamlined process for registering various game elements, including blocks, items, entities, and more, reducing boilerplate code.
- **Event Handling**: A plug-and-play event subscription system for easy subscription of events anyway and anywhere.
- **Data Generation**: Datagen together with registers with minimal setup, helping to reduce manual effort.
- **Tweaked serialization:** Automatic generation of codecs and ValueIOSerializables.
- **Helpers:** Several helpers, making modding more kotlinsh!
- **And More!**

<!-- modrinth_exclude.start -->
## Depending on Kore

Add the following to your `build.gradle.kts` file

1. Add repository for kotlin for forge and Kore.
    ```kotlin
    repositories {
        mavenLocal()
        maven {
            name = "Kotlin for Forge"
            url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    
        }
        maven {
            name = "Kore"
            url = uri("https://augustomegener.github.io/Kore/")
        }
    }
    ```
2. Add the dependencies.
   ```kotlin
   dependencies {
       implementation("thedarkcolour:kotlinforforge-neoforge:6.0.0")
   
       implementation("augustomegener:Kore:<Lastest Version>")
       ksp("augustomegener.kore:ksp:<Lastest Version>")
   }
   ```

## Features

### Entrypoint

`src/main/kotlin/namespace/kore_tests/KoreTests.kt`
```kotlin
@KMod
fun init() {
    
}

/** generated code
* const val ID: String = "kore_tests"  // taken from the package name (can be something else using @Kmod("another_id"))
* 
* val logger: Logger = LogManager.getLogger(ID)
*
* @Mod(ID)
* object KoreTests : ModUtil(ID) {
*    init {
*       `init`()
*    }
* }
*/
```

### Registering + Datagen

```kotlin
@Scan
object Items : ItemRegister(ID) {

    val exampleItem: Item by "example_item" of ::Item where {
        // Datagenerate item locale
        named(EN_US to "Example Item")
       
        props { 
            stackTo(1) 
        }
       
        // Datagenerate item model
        defaultModel()

       // Datagenerate recipe
       recipe(local("example_recipe")) {
          ShapedRecipe(ID, CraftingBookCategory.MISC,
             shaped("##", 
                    "##")
                .by('#' to ingredientOf(STICK)),
             exampleItem.defaultInstance
          )
       }
    }
}
```

## Templates

Bulk registering based on passed parameters.

```kotlin
@Scan
object Registries {

   @RegisterEarlyRegistry
   val stringRegistry = EarlyRegistry<String>()
}

@Scan
object Strings : EarlyRegister<String>(ID, stringRegistry) {

   val myGroup = EarlyRegistryGoup(local("my_group"))

   val nice by "nice" of { "nice" } onGroup myGroup
   val fool by "fool" of { "fool" } onGroup myGroup
   val cute by "cute" of { "cute" } onGroup myGroup
   val weird by "weird" of { "weird" } onGroup myGroup
}

object Items : ItemRegister(ID) {
    val itemTemplate = RegistryTemplate<String, Item> { i: String ->
        "${i}_item" of ::Item where {
            named(
                EN_US to "${i.toTitle()} Item",
                PT_BR to "Item ${i.toTitle()}"
            )
        }
    }.include(myGroup)
}

val fooItem: Item? = itemTemplate["foo"] // kore_tests:foo
val barItem: Item? = itemTemplate["bar"] // kore_tests:bar
val bazItem: Item? = itemTemplate["baz"] // kore_tests:baz
```

### Handling Events

```kotlin
@Scan 
object Foo {
    
    @KSubscribe
    fun onPlayerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        logger.info("Player ${event.entity.name.string} logged in!")
    }
}
```

### Tweaked Serialization

```kotlin
data class MyData(@Save val str: String, @Save val int: Int) {
   
   @Scan
   companion object : KCodecSerializer<MyData>(MyData::class)
}

val json = MyData("foo", 123).encode(jsonOps) // { "str": "foo", "int": 123 }
val data = MyData.decode(jsonOps, json) // MyData("foo", 123)
```

And much more!
<!-- modrinth_exclude.end --> 
