package lab3_1;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRequest;

public class InvoiceRequestBuilder {

    private ClientData client = new ClientData();

    public InvoiceRequestBuilder addClinet(ClientData client) {
        this.client = client;
        return this;
    }

    public InvoiceRequest createInvoiceRequest() {
        return new InvoiceRequest(client);
    }
}
