package io.arkx.framework.data.jdbc;

/**
 * @class org.ark.framework.orm.query.Order
 * @author Darkness
 * @date 2012-9-15 上午11:12:13
 * @version V1.0
 */
public class Order {

    public static Order asc(String field) {
        return new Order("asc", field);
    }

    public static Order desc(String field) {
        return new Order("desc", field);
    }

    private String order;

    private String field;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    private Order(String order, String field) {
        this.order = order;
        this.field = field;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Order)) {
            return false;
        }

        Order _order = (Order) obj;

        return this.order.equals(_order.order) && this.field.equals(_order.field);
    }

}
