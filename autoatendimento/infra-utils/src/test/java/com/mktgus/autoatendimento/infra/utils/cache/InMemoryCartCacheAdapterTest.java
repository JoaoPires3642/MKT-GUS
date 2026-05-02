package com.mktgus.autoatendimento.infra.utils.cache;

import com.mktgus.autoatendimento.application.cart.CartSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCartCacheAdapterTest {

    private InMemoryCartCacheAdapter cache;

    @BeforeEach
    void setUp() {
        cache = new InMemoryCartCacheAdapter();
    }

    private CartSnapshot snapshotNow(String cpf) {
        return new CartSnapshot(cpf, List.of(
                new CartSnapshot.CartItemSnapshot("001", "Pão", 2.0, 1, false)
        ), LocalDateTime.now());
    }

    private CartSnapshot expiredSnapshot(String cpf) {
        return new CartSnapshot(cpf, List.of(
                new CartSnapshot.CartItemSnapshot("001", "Pão", 2.0, 1, false)
        ), LocalDateTime.now().minusMinutes(InMemoryCartCacheAdapter.TTL_MINUTES + 1));
    }

    // --- save / recover within TTL ---

    @Test
    void shouldRecoverSavedCartWithinTtl() {
        CartSnapshot snapshot = snapshotNow("11122233344");
        cache.save(snapshot);
        Optional<CartSnapshot> recovered = cache.recover("11122233344");
        assertTrue(recovered.isPresent());
        assertEquals("11122233344", recovered.get().cpf());
        assertEquals(1, recovered.get().items().size());
    }

    // --- expired entry ---

    @Test
    void shouldReturnEmptyForExpiredCart() {
        CartSnapshot expired = expiredSnapshot("11122233344");
        cache.save(expired);
        Optional<CartSnapshot> result = cache.recover("11122233344");
        assertTrue(result.isEmpty());
    }

    @Test
    void expiredEntryShouldBeEvictedFromCacheOnAccess() {
        CartSnapshot expired = expiredSnapshot("11122233344");
        cache.save(expired);
        cache.recover("11122233344");
        // Second access should also be empty (entry removed)
        assertTrue(cache.recover("11122233344").isEmpty());
    }

    // --- evict ---

    @Test
    void shouldReturnEmptyAfterExplicitEviction() {
        CartSnapshot snapshot = snapshotNow("11122233344");
        cache.save(snapshot);
        cache.evict("11122233344");
        assertTrue(cache.recover("11122233344").isEmpty());
    }

    @Test
    void evictForNonExistentCpfShouldNotThrow() {
        assertDoesNotThrow(() -> cache.evict("99999999999"));
    }

    // --- CPF isolation ---

    @Test
    void shouldIsolateCacheEntriesByCpf() {
        CartSnapshot alice = snapshotNow("11111111111");
        CartSnapshot bob = snapshotNow("22222222222");
        cache.save(alice);
        cache.save(bob);

        assertTrue(cache.recover("11111111111").isPresent());
        assertTrue(cache.recover("22222222222").isPresent());
        assertTrue(cache.recover("33333333333").isEmpty());
    }

    @Test
    void evictingOneCpfShouldNotAffectAnother() {
        cache.save(snapshotNow("11111111111"));
        cache.save(snapshotNow("22222222222"));

        cache.evict("11111111111");

        assertTrue(cache.recover("11111111111").isEmpty());
        assertTrue(cache.recover("22222222222").isPresent());
    }

    // --- missing entry ---

    @Test
    void shouldReturnEmptyWhenCpfNotInCache() {
        assertTrue(cache.recover("00000000000").isEmpty());
    }

    // --- overwrite ---

    @Test
    void savingTwiceForSameCpfShouldOverwritePreviousEntry() {
        CartSnapshot first = new CartSnapshot("11122233344",
                List.of(new CartSnapshot.CartItemSnapshot("001", "Pão", 2.0, 1, false)),
                LocalDateTime.now());
        CartSnapshot second = new CartSnapshot("11122233344",
                List.of(new CartSnapshot.CartItemSnapshot("002", "Leite", 5.0, 3, false)),
                LocalDateTime.now());

        cache.save(first);
        cache.save(second);

        CartSnapshot recovered = cache.recover("11122233344").orElseThrow();
        assertEquals("Leite", recovered.items().get(0).productName());
    }
}
