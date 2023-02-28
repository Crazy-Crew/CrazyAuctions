package us.crazycrew.crazyauctions;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class CrazyLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addDependency(new Dependency(new DefaultArtifact("us.crazycrew.crazycore:crazycore-paper:1.1.0.0"), null));

        // Configs
        resolver.addDependency(new Dependency(new DefaultArtifact("ch.jalu:configme:1.3.0"), null));

        resolver.addRepository(new RemoteRepository.Builder("maven2", "default", "https://repo1.maven.org/maven2").build());
        resolver.addRepository(new RemoteRepository.Builder("crazycrew-libraries", "default", "https://repo.crazycrew.us/libraries").build());

        //resolver.addDependency(new Dependency(new DefaultArtifact("us.crazycrew.crazycore:crazycore-core:1.1.0.0"), null));

        //resolver.addRepository(new RemoteRepository.Builder("triumphteam-snapshots", "default", "https://repo.triumphteam.dev/snapshots").build());

        //resolver.addDependency(new Dependency(new DefaultArtifact("dev.triumphteam:triumph-gui:3.1.2"), null));

        classpathBuilder.addLibrary(resolver);
    }
}