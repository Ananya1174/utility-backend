package com.utility.billing.service;

import com.utility.billing.dto.TariffSlabDto;
import com.utility.billing.exception.ApiException;
import com.utility.billing.model.TariffSlab;
import com.utility.billing.repository.TariffSlabRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    void createSlab_success() {

        TariffSlabDto dto =
                new TariffSlabDto(0, 100, 5.0);

        when(repository.findByUtilityTypeAndPlanCodeOrderByMinUnitsAsc(any(), any()))
                .thenReturn(List.of());

        TariffSlab saved = new TariffSlab();
        saved.setMinUnits(0);
        saved.setMaxUnits(100);
        saved.setRate(5.0);

        when(repository.save(any(TariffSlab.class)))
                .thenReturn(saved);

        TariffSlabDto result = service.createSlab(dto);

        assertEquals(5.0, result.getRate());
    }

    @Test
    void deleteSlab_success() {

        TariffSlab slab = new TariffSlab();
        slab.setId("S1");

        when(repository.findById("S1"))
                .thenReturn(Optional.of(slab));

        TariffSlabDto deleted = service.deleteSlab("S1");

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