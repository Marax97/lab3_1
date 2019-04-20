package lab3_1;

import java.util.Date;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class ProductDataBuilder {

    private Id productId = Id.generate();
    private Money price = new Money(0);

    private String name = "none";

    private Date snapshotDate = new Date();

    private ProductType type = ProductType.STANDARD;

    public ProductDataBuilder addId(Id productId) {
        this.productId = productId;
        return this;
    }

    public ProductDataBuilder addPrice(Money price) {
        this.price = price;
        return this;
    }

    public ProductDataBuilder addName(String name) {
        this.name = name;
        return this;
    }

    public ProductDataBuilder addSnapshotDate(Date snapshotDate) {
        this.snapshotDate = snapshotDate;
        return this;
    }

    public ProductDataBuilder addProductType(ProductType type) {
        this.type = type;
        return this;
    }

    public ProductData createProductData() {
        return new ProductData(productId, price, name, type, snapshotDate);
    }
}
