package salesmanagement.salesmanagement.SalesComponent;

public class Order {
//    int orderNumber;
//    Date orderDate;
//    Date requiredDate;
//    Date shippedDate;
//    String status;
//    String comments;
    String productCode;
    int quantityOrdered;
    double priceEach;

    public Order(String productCode, int quantityOrdered, double priceEach) {
        this.productCode = productCode;
        this.quantityOrdered = quantityOrdered;
        this.priceEach = priceEach;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public double getPriceEach() {
        return priceEach;
    }

    public void setPriceEach(double priceEach) {
        this.priceEach = priceEach;
    }
}
