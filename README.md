# Kore: A Minecraft Modding Framework

Kore is a comprehensive Minecraft modding framework designed to streamline the development of new content for the game. It provides a set of utilities, abstractions, and helper functions to simplify common modding tasks, allowing developers to focus on creating unique gameplay experiences.

## Features

- **Simplified Registration**: Kore offers a streamlined process for registering various game elements, including blocks, items, entities, and more, reducing boilerplate code.
- **Event Handling**: A robust event system allows for easy subscription and handling of in-game events, enabling dynamic and responsive mod behavior.
- **Data Generation**: Tools for automated data generation (e.g., recipes, block states, item models) help maintain consistency and reduce manual effort.
- **Capability Integration**: Seamless integration with NeoForge capabilities for handling inventories, energy, fluids, and other interactions.
- **Utility Functions**: A collection of utility functions for common tasks, such as NBT serialization, resource location management, and attribute manipulation.

## Installation

This repository contains the source code for the Kore framework. To use Kore in your Minecraft modding project, you typically add it as a dependency in your `build.gradle` file. Please refer to the specific instructions for your build system (e.g., Gradle, Maven) on how to include a library from a GitHub repository or a published artifact.

### For Developers (Cloning and Building)

If you intend to contribute to Kore or build it from source, follow these steps:

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/AugustoMegener/Kore.git
    cd Kore
    ```
2.  **Checkout the desired branch** (e.g., `NF-1.21.1-0.1.0`):
    ```bash
    git checkout NF-1.21.1-0.1.0
    ```
3.  **Set up the development environment**:
    Kore is a NeoForge project. You can set up your development environment using Gradle:
    ```bash
    ./gradlew genEclipseRuns
    ./gradlew genIntellijRuns
    ```
    Or, if you prefer to use the standard Gradle wrapper:
    ```bash
    gradlew genEclipseRuns
    gradlew genIntellijRuns
    ```
    (Replace `genEclipseRuns` and `genIntellijRuns` with the appropriate tasks for your IDE, e.g., `setupDecompWorkspace` for older Forge versions).

4.  **Build the project**:
    ```bash
    ./gradlew build
    ```
    The compiled `.jar` files will be located in the `build/libs` directory.

## Usage

Once Kore is set up in your project, you can start leveraging its features. Here are some common use cases:

### Registering Items

```kotlin
// In your main mod class or a dedicated registration class
object MyItems {
    val ITEMS = ItemRegister(Kore.ID) // Assuming Kore.ID is your mod ID

    val EXAMPLE_ITEM = ITEMS.of { Item(it) } where { // 'of' or 'where' can be used
        props { creativeTab(CreativeModeTabs.BUILDING_BLOCKS) }
    }

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}

// In your main mod class's constructor
@Mod(Kore.ID)
class MyMod {
    init {
        val modEventBus = FMLJavaModLoadingContext.get().modEventBus
        MyItems.register(modEventBus)
    }
}
```

### Handling Events

```kotlin
// In an event subscriber class or object
object MyEvents {
    @SubscribeEvent
    fun onPlayerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        // Handle player login event
        println("Player ${event.entity.name.string} logged in!")
    }

    fun register(eventBus: IEventBus) {
        eventBus.register(this)
    }
}

// In your main mod class's constructor
@Mod(Kore.ID)
class MyMod {
    init {
        val modEventBus = FMLJavaModLoadingContext.get().modEventBus
        MyEvents.register(modEventBus)
    }
}
```

### Data Generation

Kore provides utilities to simplify data generation. You typically create dedicated data provider classes.

```kotlin
// Example: A simple recipe data provider
class ModRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
    RecipeProvider(output, registries) {

    override fun buildRecipes(exporter: RecipeOutput) {
        // Define your recipes here using Kore's data generation helpers
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE)
            .pattern("SS", "SS")
            .define('S', Items.STICK)
            .unlockedBy("has_stick", has(Items.STICK))
            .save(exporter)
    }
}

// In your main mod class, during the DataGenEvent
@Mod(Kore.ID)
class MyMod {
    init {
        val modEventBus = FMLJavaModLoadingContext.get().modEventBus
        modEventBus.addListener(this::gatherData)
    }

    private fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val lookupProvider = event.lookupProvider

        generator.addProvider(event.includeServer(), ModRecipeProvider(packOutput, lookupProvider))
    }
}
```

## Contributing

We welcome contributions to Kore! Please see the [CONTRIBUTING.md](CONTRIBUTING.md) file for guidelines on how to contribute.

## License

Kore is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.


