package us.crazycrew.crazyauctions.bootstrap;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class CrazyLibs implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        // Configurations
        resolver.addDependency(new Dependency(new DefaultArtifact("ch.jalu:configme:1.4.1"), null));

        // Commands
        resolver.addDependency(new Dependency(new DefaultArtifact("dev.jorel:commandapi-bukkit-shade:9.2.0"), null));

        // Other
        //resolver.addDependency(new Dependency(new DefaultArtifact("com.ryderbelserion.cluster.api:cluster-api:4.1.2"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.ryderbelserion.cluster.paper:cluster-paper:4.1.2"), null));

        resolver.addRepository(new RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build());
        resolver.addRepository(new RemoteRepository.Builder("crazycrew-releases", "default", "https://repo.crazycrew.us/releases/").build());
        resolver.addRepository(new RemoteRepository.Builder("jitpack", "default", "https://jitpack.io/").build());

        classpathBuilder.addLibrary(resolver);
    }
}