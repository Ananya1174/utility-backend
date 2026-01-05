package com.utility.billing.service;

import com.utility.billing.exception.ApiException;
import com.utility.billing.model.TariffSlab;
import com.utility.billing.repository.TariffSlabRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffSlabServiceTest {

    @Mock
    private TariffSlabRepository repository;

    @InjectMocks
    private TariffSlabService service;

    @Test
    void deleteSlab_success() {

        TariffSlab slab = new TariffSlab();
        slab.setId("S1");

        when(repository.findById("S1"))
                .thenReturn(Optional.of(slab));

        TariffSlab deleted = service.deleteSlab("S1");

        assertNotNull(deleted);
        verify(repository).delete(slab);
    }

    @Test
    void deleteSlab_notFound() {

        when(repository.findById("S1"))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.deleteSlab("S1"));
    }
}