package com.badbones69.crazyauctions.api.frame;

import com.badbones69.crazyauctions.ApiManager;
import com.badbones69.crazyauctions.config.types.Locale;
import com.badbones69.crazyauctions.config.types.PluginConfig;
import com.badbones69.crazyauctions.frame.CrazyCore;
import com.badbones69.crazyauctions.frame.storage.FileHandler;
import net.kyori.adventure.audience.Audience;
import java.nio.file.Path;

public class PaperCore extends CrazyCore {

    private final Path path;
    private final Audience audience;
    private final FileHandler fileHandler;
    private final String prefix;
    private final String consolePrefix;

    public PaperCore(Path path, Audience audience, String prefix, String consolePrefix) {
        // Create directory.
        this.path = path;
        this.path.toFile().mkdir();

        this.audience = audience;
        this.prefix = prefix;
        this.consolePrefix = consolePrefix;

        this.fileHandler = new FileHandler();
    }

    @Override
    public void enable() {
        super.enable();
    }

    @Override
    public void disable() {
        super.disable();
    }

    @Override
    public Path getDirectory() {
        return this.path;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public String getConsolePrefix() {
        return this.consolePrefix;
    }

    @Override
    public FileHandler getFileHandler() {
        return this.fileHandler;
    }

    @Override
    public Audience adventure() {
        return this.audience;
    }

    @Override
    public String commandTooFewArgs() {
        return ApiManager.getLocale().getProperty(Locale.NOT_ENOUGH_ARGS);
    }

    @Override
    public String commandTooManyArgs() {
        return ApiManager.getLocale().getProperty(Locale.TOO_MANY_ARGS);
    }

    @Override
    public String commandOptionalMsg() {
        return ApiManager.getLocale().getProperty(Locale.OPTIONAL_ARGUMENT);
    }

    @Override
    public String commandRequiredMsg() {
        return ApiManager.getLocale().getProperty(Locale.REQUIRED_ARGUMENT);
    }

    @Override
    public String commandRequirementNotPlayer() {
        return ApiManager.getLocale().getProperty(Locale.MUST_BE_PLAYER);
    }

    @Override
    public String commandRequirementNoPermission() {
        return ApiManager.getLocale().getProperty(Locale.NO_PERMISSION);
    }

    @Override
    public String commandHelpHeader() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.HELP_PAGE_HEADER);
    }

    @Override
    public String commandHelpFooter() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.HELP_PAGE_FOOTER);
    }

    @Override
    public String commandInvalidPage() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.INVALID_HELP_PAGE);
    }

    @Override
    public String commandPageFormat() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.HELP_PAGE_FORMAT);
    }

    @Override
    public String commandHoverFormat() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.HELP_PAGE_HOVER_FORMAT);
    }

    @Override
    public String commandHoverAction() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.HELP_PAGE_HOVER_ACTION);
    }

    @Override
    public String commandNavigationText() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.HELP_PAGE_GO_TO_PAGE);
    }

    @Override
    public String commandNavigationNextButton() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.HELP_PAGE_NEXT);
    }

    @Override
    public String commandNavigationBackButton() {
        return ApiManager.getPluginConfig().getProperty(PluginConfig.HELP_PAGE_BACK);
    }
}