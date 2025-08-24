# Kore: Kotlin Minecraft Modding Framework for neoforge

Kore is a comprehensive Minecraft modding framework designed to optimize and simplify the development of new content for the game. It provides a set of auxiliary utilities, abstractions, and functions to simplify common modification tasks, allowing developers to focus on the effective creation of their mod.

## Features

- **Tweaked Registration**: Kore offers a streamlined process for registering various game elements, including blocks, items, entities, and more, reducing boilerplate code.
- **Event Handling**: A robust event system allows for easy subscription of events anyway and anywhere.
- **Data Generation**: Tools for automated data generation (e.g., recipes, block states, item models and custom data) help  reduce manual effort.
- **Capability Integration**: Tweaked integration with NeoForge capabilities for handling inventories, energy, fluids, and other interactions.
- **Tweaked serialization:** Automatic generation of codecs and INBTSerializables.
- **And More!**

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
       implementation("thedarkcolour:kotlinforforge-neoforge:5.3.0")
   
       implementation("augustomegener:Kore:<Lastest Version>")
       ksp("augustomegener.kore:ksp:<Lastest Version>")
   }
   ```

## Features

### Entrypoint

```kotlin
@KMod
fun init() {
    
}

// generated code
const val ID: String = "kore_tests" 

val logger: Logger = LogManager.getLogger(ID)

@Mod(ID)
object KoreTests : ModUtil(ID) {
   init {
      `init`()
   }
}
```

### Registering 

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
                .by('#' to Ingredient.of(STICK)),
             exampleItem.defaultInstance
          )
       }
    }
}
```

## Templates

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

## License

Kore is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.