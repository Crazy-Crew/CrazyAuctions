package us.crazycrew.api.items;

import org.jspecify.annotations.NonNull;

public interface IAuctionItem<I> {

    @NonNull I getItem();

}