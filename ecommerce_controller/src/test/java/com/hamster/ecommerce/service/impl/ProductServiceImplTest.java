package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.model.entity.Product;
import com.hamster.ecommerce.repository.ProductRepository;
import com.hamster.ecommerce.util.AuditUtil;
import com.hamster.ecommerce.util.PriceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuditUtil auditUtil;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFind_returnsPaged() {
        // given
        Page<Product> page = new PageImpl<>(List.of(new Product()));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // when
        Page<Product> result = productService.find(PageRequest.of(0, 10));

        // then
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testFindById_found() {
        // given
        Product p = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        // when
        Optional<Product> res = productService.findById(1L);

        // then
        assertTrue(res.isPresent());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_missing() {
        // given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        Optional<Product> res = productService.findById(99L);

        // then
        assertTrue(res.isEmpty());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void testFindByProductName_found() {
        // given
        Product p = new Product();
        when(productRepository.findByProductName("Mouse")).thenReturn(Optional.of(p));

        // when
        Optional<Product> res = productService.findByProductName("Mouse");

        // then
        assertTrue(res.isPresent());
        verify(productRepository, times(1)).findByProductName("Mouse");
    }

    @Test
    void testFindByProductName_missing() {
        // given
        when(productRepository.findByProductName("Ghost")).thenReturn(Optional.empty());

        // when
        Optional<Product> res = productService.findByProductName("Ghost");

        // then
        assertTrue(res.isEmpty());
        verify(productRepository, times(1)).findByProductName("Ghost");
    }

    @Test
    void testSaveProduct_setsAudit_scalesPrice_andSaves() {
        // given
        Product incoming = new Product();
        incoming.setPrice(new BigDecimal("10.125"));
        // adjust according to your type (String/Enum). Using String here:
        incoming.setCurrency("USD");

        BigDecimal scaled = new BigDecimal("10.13"); // expected from PriceUtil.scale

        try (MockedStatic<PriceUtil> mocked = mockStatic(PriceUtil.class)) {
            // set expectations
            doNothing().when(auditUtil).setAuditColumns(incoming);
            mocked.when(() -> PriceUtil.scale(new BigDecimal("10.125"), "USD"))
                    .thenReturn(scaled);

            // capture entity sent to repo.save
            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            Product saved = productService.saveProduct(incoming);

            // then
            assertNotNull(saved);
            assertEquals(scaled, saved.getPrice());

            mocked.verify(() -> PriceUtil.scale(new BigDecimal("10.125"), "USD"), times(1));
            verify(auditUtil, times(1)).setAuditColumns(incoming);
            verify(productRepository, times(1)).save(captor.capture());

            Product passedToSave = captor.getValue();
            assertEquals(scaled, passedToSave.getPrice());
            assertEquals("USD", passedToSave.getCurrency());
        }
    }

    @Test
    void testDelete_callsRepository() {
        // when
        productService.delete(7L);

        // then
        verify(productRepository, times(1)).deleteById(7L);
    }
}