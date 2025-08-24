package com.typ.mythicanvil.datagen;

import com.typ.mythicanvil.MythicAnvil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MythicAnvil.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Add ritual recipe provider
        generator.addProvider(event.includeServer(), new ModRitualRecipeProvider(packOutput, lookupProvider));
        
        // Add post-processing to move files to correct "recipes" folder
        generator.addProvider(event.includeServer(), new RecipePostProcessor(packOutput));
    }
    
    // Post-processor to move recipe files from "recipe" to "recipes" folder
    private static class RecipePostProcessor implements net.minecraft.data.DataProvider {
        private final PackOutput output;
        
        public RecipePostProcessor(PackOutput output) {
            this.output = output;
        }
        
        @Override
        public CompletableFuture<?> run(net.minecraft.data.CachedOutput cachedOutput) {
            return CompletableFuture.runAsync(() -> {
                try {
                    // Wait a bit for other providers to finish
                    Thread.sleep(1000);
                    
                    Path wrongFolder = output.getOutputFolder()
                        .resolve("data")
                        .resolve(MythicAnvil.MOD_ID)
                        .resolve("recipe");
                        
                    Path correctFolder = output.getOutputFolder()
                        .resolve("data")
                        .resolve(MythicAnvil.MOD_ID)
                        .resolve("recipes");
                    
                    if (Files.exists(wrongFolder)) {
                        // Create correct folder if it doesn't exist
                        Files.createDirectories(correctFolder);
                        
                        // Move all JSON files from "recipe" to "recipes"
                        Files.list(wrongFolder)
                            .filter(path -> path.toString().endsWith(".json"))
                            .forEach(file -> {
                                try {
                                    Path target = correctFolder.resolve(file.getFileName());
                                    Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
                                    System.out.println("Moved recipe file to correct location: " + target);
                                } catch (IOException e) {
                                    System.err.println("Failed to move recipe file: " + e.getMessage());
                                }
                            });
                            
                        // Remove empty "recipe" folder if it's empty
                        try {
                            if (Files.list(wrongFolder).findAny().isEmpty()) {
                                Files.delete(wrongFolder);
                                System.out.println("Removed empty 'recipe' folder");
                            }
                        } catch (IOException e) {
                            System.err.println("Could not remove empty recipe folder: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Recipe post-processing failed: " + e.getMessage());
                }
            });
        }
        
        @Override
        public String getName() {
            return "Recipe Post-Processor";
        }
    }
}
