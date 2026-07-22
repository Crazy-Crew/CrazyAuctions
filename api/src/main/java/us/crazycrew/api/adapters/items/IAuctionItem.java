package us.crazycrew.api.adapters.items;

import org.jspecify.annotations.NonNull;

public interface IAuctionItem<I> {

    @NonNull I getItem();

}