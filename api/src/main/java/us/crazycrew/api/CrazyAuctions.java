package us.crazycrew.api;

import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import us.crazycrew.api.adapters.items.IAuctionItem;
import us.crazycrew.api.adapters.sender.ISenderAdapter;
import us.crazycrew.api.registry.IUserRegistry;
import java.nio.file.Path;
import java.util.UUID;

@NullMarked
public abstract class CrazyAuctions<K, I> {

    public static final UUID CONSOLE_UUID = new UUID(0, 0);
    public static final String CONSOLE_NAME = "Console";
    public static final String namespace = "crazyauctions";
    public static final FusionKey default_locale = FusionKey.key(namespace, "default");

    protected final Path path;

    public CrazyAuctions(final Path path) {
        this.path = path;
    }

    public abstract IAuctionItem<I> getItem(final I itemStack);

    @ApiStatus.Internal
    public abstract MessageRegistry getMessageRegistry();

    public abstract IUserRegistry getUserRegistry();

    public abstract ISenderAdapter getSenderAdapter();

    public abstract boolean isSellModuleEnabled();

    public abstract boolean isBidModuleEnabled();

    public abstract Path getDataPath();

    @ApiStatus.Internal
    public abstract K getFusion();

    @ApiStatus.Internal
    public abstract void loadMessages();

    @ApiStatus.Internal
    public abstract void loadExamples();

    @ApiStatus.Internal
    public abstract void reload();

    @ApiStatus.Internal
    public abstract void init();

    @ApiStatus.Internal
    public abstract void post();

    @ApiStatus.Internal
    public abstract void stop();
}