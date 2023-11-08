package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.domain.HSProduct;
import fr.slickteam.hubspot.api.service.HSProductService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HSProductServiceIT {

    private final String testProductName = "Test product";
    private final String testProductDescription = "Product for testing";
    private final BigDecimal testPrice = BigDecimal.valueOf(50);
    private final String testRecurringPeriod = "P12M";
    private Long createdProductId;

    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }

    @After
    public void tearDown() throws Exception {
        if (createdProductId != null) {
            hubSpot.product().delete(createdProductId);
        }
        // add sleep to avoid "Too many requests" error
        sleep(100);
    }

    @Test
    public void createProduct_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);
        product = hubSpot.product().create(product);

        createdProductId = product.getId();

        assertNotEquals(0L, product.getId());
        assertEquals(product.getName(), hubSpot.product().getByID(product.getId()).getName());
    }

    @Test
    public void createProduct_NetworkError_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);

        HSProductService mockHSProductService = mock(HSProductService.class);
        Mockito.doThrow(new HubSpotException("Network error")).when(mockHSProductService).create(product);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.product()).thenReturn(mockHSProductService);
        exception.expect(HubSpotException.class);
        mockHubSpot.product().create(product);
    }

    @Test
    public void createProductIncorrectProperty_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);
        product.setProperty("badpropertyz", "Test value 1");

        exception.expect(HubSpotException.class);
        hubSpot.product().create(product);
    }

    @Test
    public void getProduct_Id_Test() throws Exception {
        long productId = hubSpot
                .product()
                .create(new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod))
                .getId();
        createdProductId = productId;

        HSProduct product = hubSpot.product().getByID(productId);

        assertEquals(productId, product.getId());
        assertEquals(testProductName, product.getName());
    }

    @Test
    public void getProduct_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertNull(hubSpot.product().getByID(id));
        assertNull(hubSpot.product().getByID(id));
    }


    @Test
    public void patch_phone_product_Test() throws Exception {
        String test_value = "new phone number";
        HSProduct product = hubSpot
                .product()
                .create(new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod));
        createdProductId = product.getId();

        HSProduct editProduct = new HSProduct();
        editProduct.setId(product.getId());
        editProduct.setName(test_value);
        HSProduct result = hubSpot.product().patch(editProduct);

        assertEquals(editProduct.getName(), result.getName());
    }

    @Test
    public void patchProductMissedRequiredProperty_Test() throws Exception {
        String test_property = "linkedinbio";
        String test_value = "Test value 1";
        HSProduct product = hubSpot
                .product()
                .create(new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod));
        createdProductId = product.getId();
        product.setProperty(test_property, test_value);
        HSProduct missedProduct = new HSProduct(product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getRecurringBillingPeriod());

        exception.expect(HubSpotException.class);
        hubSpot.product().patch(missedProduct);
    }

    @Test
    public void patchProductIncorrectProperty_Test() throws Exception {
        String test_property = "badpropertyz";
        String test_value = "Test value 1";
        HSProduct product = hubSpot
                .product()
                .create(new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod));
        createdProductId = product.getId();

        HSProduct editProduct = new HSProduct();
        editProduct.setId(product.getId());
        editProduct.setProperty(test_property, test_value);

        exception.expect(HubSpotException.class);
        hubSpot.product().patch(editProduct);
    }

    @Test
    public void patchProduct_Not_Found_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod).setId(-777);

        HSProduct editProduct = new HSProduct();
        editProduct.setId(product.getId());
        editProduct.setName(product.getName());

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");

        hubSpot.product().patch(editProduct);
    }

    @Test
    public void deleteProduct_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);
        product = hubSpot.product().create(product);
        createdProductId = product.getId();

        hubSpot.product().delete(product);

        assertNull(hubSpot.product().getByID(product.getId()));

    }

    @Test
    public void deleteProduct_by_id_Test() throws Exception {
        HSProduct product = new HSProduct(testProductName, testProductDescription, testPrice, testRecurringPeriod);
        product = hubSpot.product().create(product);
        createdProductId = product.getId();

        hubSpot.product().delete(product.getId());

        assertNull(hubSpot.product().getByID(product.getId()));

    }

    @Test
    public void deleteProduct_No_ID_Test() throws Exception {
        HSProduct product = new HSProduct().setName(testProductName);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("Product ID must be provided"));
        hubSpot.product().delete(product);
    }
}
