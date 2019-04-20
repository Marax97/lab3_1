package lab3_1;

import pl.com.bottega.ecommerce.sales.domain.invoicing.BookKeeper;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceFactory;

public class BookKeeperBuilder {

    private InvoiceFactory invoiceFactory = new InvoiceFactory();

    public BookKeeperBuilder addInvoiceFactory(InvoiceFactory invoiceFactory) {
        this.invoiceFactory = invoiceFactory;
        return this;
    }

    public BookKeeper createBookKeeper() {
        return new BookKeeper(invoiceFactory);
    }
}
