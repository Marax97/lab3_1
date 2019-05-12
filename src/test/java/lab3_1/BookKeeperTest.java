package lab3_1;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.invoicing.BookKeeper;
import pl.com.bottega.ecommerce.sales.domain.invoicing.Invoice;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceFactory;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRequest;
import pl.com.bottega.ecommerce.sales.domain.invoicing.RequestItem;
import pl.com.bottega.ecommerce.sales.domain.invoicing.Tax;
import pl.com.bottega.ecommerce.sales.domain.invoicing.TaxPolicy;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class BookKeeperTest {

    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private TaxPolicy taxPolicy;
    private ProductData[] productData;
    private RequestItem[] requestItem;

    private static ProductDataBuilder productBuilder;
    private static RequestItemBuilder requestBuilder;
    private static BookKeeperBuilder bookKeeperBuilder;
    private static InvoiceRequestBuilder invoiceRequestBuilder;

    @BeforeClass
    public static void initialize() {
        productBuilder = new ProductDataBuilder();
        requestBuilder = new RequestItemBuilder();
        bookKeeperBuilder = new BookKeeperBuilder();
        invoiceRequestBuilder = new InvoiceRequestBuilder();
    }

    @Before
    public void setUp() {
        bookKeeper = bookKeeperBuilder.addInvoiceFactory(new InvoiceFactory())
                                      .createBookKeeper();
        invoiceRequest = invoiceRequestBuilder.addClinet(new ClientData())
                                              .createInvoiceRequest();
        taxPolicy = mock(TaxPolicy.class);
        productData = new ProductData[2];
        requestItem = new RequestItem[2];
    }

    private void initInvoiceRequestData(int numberOfItems) {
        for (int i = 0; i < numberOfItems; i++) {
            productData[i] = productBuilder.addId(Id.generate())
                                           .addPrice(new Money(1))
                                           .addName("kabanos")
                                           .addProductType(ProductType.FOOD)
                                           .addSnapshotDate(new Date())
                                           .createProductData();
            int quantity = 10;

            requestItem[i] = requestBuilder.addProductData(productData[i])
                                           .addQuantity(quantity)
                                           .addTotalCost(productData[i].getPrice()
                                                                       .multiplyBy(quantity))
                                           .createRequestItem();

            invoiceRequest.add(requestItem[i]);
        }

    }

    @Test
    public void testInvoiceRequestWithOneProductReturnOneProductInvoice() {
        initInvoiceRequestData(1);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(1), "tax"));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoice.getItems()
                          .size(),
                is(equalTo(1)));
        assertThat(invoice.getItems()
                          .get(0)
                          .getProduct(),
                is(equalTo(productData[0])));
    }

    @Test
    public void testNumberOfCallsTaxCalculateForInvoiceRequestWithTwoProducts() {
        initInvoiceRequestData(2);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(new BigDecimal(1)), "tax"));
        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(any(ProductType.class), any(Money.class));
    }

    @Test
    public void testInvoiceRequestWithTwoProductsReturnTwoProductsInvoice() {
        initInvoiceRequestData(2);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(new BigDecimal(1)), "tax"));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoice.getItems()
                          .size(),
                is(equalTo(2)));
        assertThat(invoice.getItems()
                          .get(0)
                          .getProduct(),
                is(equalTo(productData[0])));
        assertThat(invoice.getItems()
                          .get(1)
                          .getProduct(),
                is(equalTo(productData[1])));
    }

    @Test
    public void testClientDataPassedToInvoice() {
        ClientData client = new ClientData(Id.generate(), "Pepsi");
        invoiceRequest = new InvoiceRequest(client);
        initInvoiceRequestData(1);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(new BigDecimal(1)), "tax"));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoice.getClient()
                          .getName(),
                is(equalTo(client.getName())));
        assertThat(invoice.getClient()
                          .getAggregateId(),
                is(equalTo(client.getAggregateId())));
    }

    @Test
    public void testIfMethodCreateFromInvoiceFactoryWasCalledOnce() {
        InvoiceFactory factory = mock(InvoiceFactory.class);
        bookKeeper = bookKeeperBuilder.addInvoiceFactory(factory)
                                      .createBookKeeper();
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(new BigDecimal(1)), "tax"));
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(factory, times(1)).create(any());
    }

    @Test
    public void testIfTaxCalculateNeverWasCalledForEmptyInvoiceRequest() {
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(new BigDecimal(1)), "tax"));
        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, never()).calculateTax(any(ProductType.class), any(Money.class));
    }

}
