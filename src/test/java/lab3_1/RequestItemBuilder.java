package lab3_1;

import java.util.Date;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.invoicing.RequestItem;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class RequestItemBuilder {

    private ProductData productData = new ProductData(Id.generate(), new Money(0), "none", ProductType.STANDARD, new Date());

    private int quantity = 0;

    private Money totalCost = new Money(0);

    public RequestItemBuilder addProductData(ProductData productData) {
        this.productData = productData;
        return this;
    }

    public RequestItemBuilder addQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public RequestItemBuilder addTotalCost(Money totalCost) {
        this.totalCost = totalCost;
        return this;
    }

    public RequestItem createRequestItem() {
        return new RequestItem(productData, quantity, totalCost);
    }
}
