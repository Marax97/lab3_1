package lab3_1;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class ProductBuilder {

    private Id productId = Id.generate();

    private Money price = new Money(0);

    private String name = "none";

    private ProductType productType = ProductType.STANDARD;

    public ProductBuilder addId(Id productId) {
        this.productId = productId;
        return this;
    }

    public ProductBuilder addPrice(Money price) {
        this.price = price;
        return this;
    }

    public ProductBuilder addName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder addProductType(ProductType type) {
        this.productType = type;
        return this;
    }

    public Product createProductData() {
        return new Product(productId, price, name, productType);
    }

}
