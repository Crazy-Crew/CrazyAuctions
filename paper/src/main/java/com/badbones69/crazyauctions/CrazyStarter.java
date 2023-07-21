package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.frame.PaperCore;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazyauctions.config.types.PluginConfig;

public class CrazyStarter implements PluginBootstrap, PluginLoader {

    private ApiManager apiManager;

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        this.apiManager = new ApiManager(context.getDataDirectory());
        this.apiManager.load();
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        PaperCore paperCore = new PaperCore(context.getDataDirectory(), Bukkit.getConsoleSender(), ApiManager.getPluginConfig().getProperty(PluginConfig.COMMAND_PREFIX), ApiManager.getPluginConfig().getProperty(PluginConfig.CONSOLE_PREFIX));
        paperCore.enable();

        return new CrazyAuctions(this.apiManager, paperCore);
    }

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        // Configs
        resolver.addDependency(new Dependency(new DefaultArtifact("ch.jalu:configme:1.3.1"), null));

        resolver.addDependency(new Dependency(new DefaultArtifact("org.bstats:bstats-bukkit:3.0.2"), null));

        resolver.addDependency(new Dependency(new DefaultArtifact("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4"), null));

        // Repositories
        resolver.addRepository(new RemoteRepository.Builder("jitpack", "default", "https://jitpack.io").build());
        resolver.addRepository(new RemoteRepository.Builder("maven2", "default", "https://repo1.maven.org/maven2").build());
        resolver.addRepository(new RemoteRepository.Builder("codemc-repo", "default", "https://repo.codemc.org/repository/maven-public/").build());

        classpathBuilder.addLibrary(resolver);
    }
}