package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.domain.HSProduct;
import fr.slickteam.hubspot.api.service.HSProductService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class HSProductServiceIT {

    private final String testProductName = "Test product";
    private final String testProductDescription = "Product for testing";
    private final BigDecimal testPrice = BigDecimal.valueOf(50);
    private final String testRecurringPeriod = "P12M";
    private Long createdProductId;

    private HubSpot hubSpot;

    @BeforeEach
    void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (createdProductId != null) {
            hubSpot.product().delete(createdProductId);
        }
        // add sleep to avoid "Too many requests" error
        sleep(100);
    }

    @Test
    void createProduct_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);
        product = hubSpot.product().create(product);

        createdProductId = product.getId();

        assertThat(product.getId()).isNotZero();
        assertThat(product.getName()).isEqualTo(hubSpot.product().getByID(product.getId()).getName());
    }

    @Test
    void createProduct_NetworkError_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);

        HSProductService mockHSProductService = mock(HSProductService.class);
        Mockito.doThrow(new HubSpotException("Network error")).when(mockHSProductService).create(product);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.product()).thenReturn(mockHSProductService);
        assertThatThrownBy(() -> mockHubSpot.product().create(product))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void createProductIncorrectProperty_Test() {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);
        product.setProperty("badpropertyz", "Test value 1");

        assertThatThrownBy(() -> hubSpot.product().create(product))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void getProduct_Id_Test() throws Exception {
        long productId = hubSpot
                .product()
                .create(new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod))
                .getId();
        createdProductId = productId;

        HSProduct product = hubSpot.product().getByID(productId);

        assertThat(product.getId()).isEqualTo(productId);
        assertThat(product.getName()).isEqualTo(testProductName);
    }

    @Test
    void getProduct_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertThat(hubSpot.product().getByID(id)).isNull();
        assertThat(hubSpot.product().getByID(id)).isNull();
    }


    @Test
    void patch_phone_product_Test() throws Exception {
        String testValue = "new phone number";
        HSProduct product = hubSpot
                .product()
                .create(new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod));
        createdProductId = product.getId();

        HSProduct editProduct = new HSProduct();
        editProduct.setId(product.getId());
        editProduct.setName(testValue);
        HSProduct result = hubSpot.product().patch(editProduct);

        assertThat(result.getName()).isEqualTo(editProduct.getName());
    }

    @Test
    void patchProductMissedRequiredProperty_Test() throws Exception {
        String testProperty = "linkedinbio";
        String testValue = "Test value 1";
        HSProduct product = hubSpot
                .product()
                .create(new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod));
        createdProductId = product.getId();
        product.setProperty(testProperty, testValue);
        HSProduct missedProduct = new HSProduct(product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getRecurringBillingPeriod());

        assertThatThrownBy(() -> hubSpot.product().patch(missedProduct))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void patchProductIncorrectProperty_Test() throws Exception {
        String testProperty = "badpropertyz";
        String testValue = "Test value 1";
        HSProduct product = hubSpot
                .product()
                .create(new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod));
        createdProductId = product.getId();

        HSProduct editProduct = new HSProduct();
        editProduct.setId(product.getId());
        editProduct.setProperty(testProperty, testValue);

        assertThatThrownBy(() -> hubSpot.product().patch(editProduct))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void patchProduct_Not_Found_Test() {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod).setId(-777);

        HSProduct editProduct = new HSProduct();
        editProduct.setId(product.getId());
        editProduct.setName(product.getName());

        assertThatThrownBy(() -> hubSpot.product().patch(editProduct))
            .isInstanceOf(HubSpotException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void deleteProduct_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);
        product = hubSpot.product().create(product);
        createdProductId = product.getId();

        hubSpot.product().delete(product);

        assertThat(hubSpot.product().getByID(product.getId())).isNull();

    }

    @Test
    void deleteProduct_by_id_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);
        product = hubSpot.product().create(product);
        createdProductId = product.getId();

        hubSpot.product().delete(product.getId());

        assertThat(hubSpot.product().getByID(product.getId())).isNull();

    }

    @Test
    void deleteProduct_No_ID_Test() {
        HSProduct product = new HSProduct().setName(testProductName);

        assertThatThrownBy(() -> hubSpot.product().delete(product))
            .isInstanceOf(HubSpotException.class)
            .hasMessageContaining("Product ID must be provided");
    }
}
