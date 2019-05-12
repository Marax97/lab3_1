package lab3_1;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation.ReservationStatus;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

public class AddProductCommandHandlerTest {

    private static ReservationBuilder reservationBuilder;
    private static ProductBuilder productBuilder;

    private AddProductCommandHandler productHandler;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext;
    private AddProductCommand productCommand;
    private Client client;
    private Reservation reservation;
    private Product product;

    @BeforeClass
    public static void initialize() {
        reservationBuilder = new ReservationBuilder();
        productBuilder = new ProductBuilder();
    }

    @Before
    public void setUp() {
        productHandler = new AddProductCommandHandler();

        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
        suggestionService = mock(SuggestionService.class);
        clientRepository = mock(ClientRepository.class);
        systemContext = new SystemContext();

        productCommand = new AddProductCommand(new Id("1"), new Id("2"), 10);
        client = new Client();

        Whitebox.setInternalState(productHandler, "reservationRepository", reservationRepository);
        Whitebox.setInternalState(productHandler, "productRepository", productRepository);
        Whitebox.setInternalState(productHandler, "suggestionService", suggestionService);
        Whitebox.setInternalState(productHandler, "clientRepository", clientRepository);
        Whitebox.setInternalState(productHandler, "systemContext", systemContext);

        initProductCommandData();
    }

    private void initProductCommandData() {
        reservation = reservationBuilder.addId(Id.generate())
                                        .addStatus(ReservationStatus.OPENED)
                                        .createReservation();

        product = productBuilder.addPrice(new Money(5))
                                .addName("paluszki")
                                .addProductType(ProductType.FOOD)
                                .createProductData();

        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);
        when(productRepository.load(any(Id.class))).thenReturn(product);
        when(clientRepository.load(any(Id.class))).thenReturn(client);
    }

    private void suggestEquivalentProduct() {
        product.markAsRemoved();
        Product equivalent = productBuilder.addPrice(new Money(50))
                                           .addName("pepsi")
                                           .addProductType(ProductType.STANDARD)
                                           .createProductData();

        when(suggestionService.suggestEquivalent(any(Product.class), any(Client.class))).thenReturn(equivalent);
    }

    @Test
    public void testIfProductAndReservationRepositoriesWereCalledOnce() {
        productHandler.handle(productCommand);
        verify(reservationRepository, times(1)).load(any());
        verify(productRepository, times(1)).load(any());
        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    public void testIfProductIsActiveClientWillNotBeCalled() {
        productHandler.handle(productCommand);

        verify(clientRepository, never()).load(any());
    }

    @Test
    public void testIfProductIsArchiveClientWillBeCalled() {
        suggestEquivalentProduct();

        productHandler.handle(productCommand);

        verify(clientRepository, times(1)).load(any());
    }

    @Test
    public void testIfNewProductIsSuggestedIfOrderProductIsArchive() {
        suggestEquivalentProduct();

        productHandler.handle(productCommand);

        verify(suggestionService, times(1)).suggestEquivalent(any(), any());
    }

}
