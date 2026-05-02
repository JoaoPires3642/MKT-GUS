package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.cart.CartSnapshot;
import com.mktgus.autoatendimento.application.cart.SaveCartInput;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.gateway.CartCacheGateway;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SaveCartUseCaseTest {

    @Test
    void shouldThrowWhenCpfIsMissing() {
        SaveCartUseCase useCase = new SaveCartUseCase(new InMemoryCartCacheGateway());

        assertThrows(ValidationException.class, () -> useCase.execute(new SaveCartInput("", java.util.List.of())));
    }

    @Test
    void shouldThrowWhenItemsAreMissing() {
        SaveCartUseCase useCase = new SaveCartUseCase(new InMemoryCartCacheGateway());

        assertThrows(ValidationException.class, () -> useCase.execute(new SaveCartInput("52998224725", java.util.List.of())));
    }

    @Test
    void shouldSaveCartSnapshotWhenInputIsValid() {
        InMemoryCartCacheGateway gateway = new InMemoryCartCacheGateway();
        SaveCartUseCase useCase = new SaveCartUseCase(gateway);

        useCase.execute(new SaveCartInput(
                "52998224725",
                java.util.List.of(new SaveCartInput.CartItemInput("789", "Produto", 10.0, 2, false))
        ));

        CartSnapshot snapshot = gateway.recover("52998224725").orElseThrow();
        assertEquals("52998224725", snapshot.cpf());
        assertEquals(1, snapshot.items().size());
        assertEquals("789", snapshot.items().getFirst().ean());
    }

    private static final class InMemoryCartCacheGateway implements CartCacheGateway {
        private CartSnapshot snapshot;

        @Override
        public void save(CartSnapshot snapshot) {
            this.snapshot = snapshot;
        }

        @Override
        public Optional<CartSnapshot> recover(String cpf) {
            if (snapshot == null || !snapshot.cpf().equals(cpf)) {
                return Optional.empty();
            }
            return Optional.of(snapshot);
        }

        @Override
        public void evict(String cpf) {
            if (snapshot != null && snapshot.cpf().equals(cpf)) {
                snapshot = null;
            }
        }
    }
}
