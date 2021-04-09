
package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    @Mock
    private InvoiceFactory factory;
    @Mock
    private TaxPolicy taxPolicy;

    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private ClientData clientData;

    @BeforeEach
    void setUp() throws Exception {
        bookKeeper = new BookKeeper(factory);
        clientData = new ClientData(Id.generate(), "name");
        invoiceRequest = new InvoiceRequest(clientData);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(Money.ZERO, "tax"));
        when(factory.create(clientData)).thenReturn(new Invoice(Id.generate(), clientData));
    }


    @Test
    public void checkIfSinglePositionRequestReturnSinglePositionInvoice() {

        ProductBuilder productBuilder = new ProductBuilder();
        RequestItemBuilder requestItemBuilder = new RequestItemBuilder();
        Money money = new Money(10);
        Product product = productBuilder.withPrice(money).withName("produkt").withProductType(ProductType.STANDARD).build();
        RequestItem requestItem = requestItemBuilder.withProductData(product.generateSnapshot()).withTotalCost(money).build();
        invoiceRequest.add(requestItem);

        int result = bookKeeper.issuance(invoiceRequest, taxPolicy).getItems().size();
        assertEquals(1,result);
    }
}
