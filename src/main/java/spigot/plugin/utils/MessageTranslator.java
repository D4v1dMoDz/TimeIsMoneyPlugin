package spigot.plugin.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import spigot.plugin.TimeIsMoney;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MessageTranslator {
    private static MessageTranslator instance;
    private static final String BASE_PATH_TRANSLATION = "messages";
    private static final String FILE_TRANSLATION_NAME = "messages.yml";

    private FileConfiguration translationFileConfiguration;

    private MessageTranslator() {
        loadTranslation();
    }

    public static MessageTranslator getInstance() {
        if(instance == null) {
            instance = new MessageTranslator();
        }

        return instance;
    }

    private void loadTranslation() {
        String language = TimeIsMoney.getInstance().getCustomConfig().getString("language");
        if(language != null) {
            String directoryToTranslationFile = String.format("%s/%s/%s", BASE_PATH_TRANSLATION, language, FILE_TRANSLATION_NAME);
            try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(directoryToTranslationFile)) {
                translationFileConfiguration = new YamlConfiguration();
                if(inputStream != null)
                    translationFileConfiguration.load(new InputStreamReader(inputStream));
                else
                    throw new IOException("Translation file not found!");
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException("Error during load translation file: " + e.getMessage(), e);
            }
        }
    }

    public String getTranslation(String key) {
        return translationFileConfiguration.getString(key, key);
    }
}
