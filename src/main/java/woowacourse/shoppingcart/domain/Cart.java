package woowacourse.shoppingcart.domain;

import java.util.Objects;

public class Cart {

    private Long id;
    private int quantity;
    private Long productId;
    private String name;
    private int price;
    private String imageUrl;

    private Cart() {
    }

    public Cart(final Long id, final int quantity, final Product product) {
        this(id, quantity, product.getId(), product.getName(), product.getPrice(), product.getImageUrl());
    }

    public Cart(final Long id, final int quantity, final Long productId, final String name, final int price,
                final String imageUrl) {
        this.id = id;
        this.quantity = quantity;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Cart cart = (Cart) o;
        return quantity == cart.quantity && price == cart.price && Objects.equals(id, cart.id)
                && Objects.equals(productId, cart.productId) && Objects.equals(name, cart.name)
                && Objects.equals(imageUrl, cart.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, productId, name, price, imageUrl);
    }
}
