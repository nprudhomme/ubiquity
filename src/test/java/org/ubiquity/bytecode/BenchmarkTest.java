package org.ubiquity.bytecode;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Stopwatch;
import junit.framework.Assert;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Test;
import org.ubiquity.Ubiquity;

/**
 * Benchmark test testing the speed of different copy frameworks.
 * This test should be run using the following arguments :
 * -XX:+PrintCompilation -verbose:gc
 * <p/>
 * Date: 03/06/12
 *
 * @author François LAROCHE
 */
public class BenchmarkTest {

    private static final int WARM_LOOP_NUMBER = 200000;
    private static final int LOOP_NUMBER = 10000;
    private static final int NANO_TO_MS = 1000000;

    private static final MapperFacade ORIKA;
    private static final Ubiquity UBIQUITY = new Ubiquity();
    private static final Mapper DOZER = new DozerBeanMapper();


    static {
        MapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.build();
        ORIKA = factory.getMapperFacade();

    }


    @Test
    public void displayStats() {
        Collection<Order> orders = new ArrayList<Order>(LOOP_NUMBER);
        Order order = createOrder();
        //long start, end;
        Stopwatch stopwatch = new Stopwatch();

        System.out.println("Warming up ORIKA...");
        stopwatch.start();
        for (int i = 0; i < WARM_LOOP_NUMBER; i++) {
            ORIKA.map(order, Order.class);
        }
        stopwatch.stop();
        long orikaWarmTime = stopwatch.elapsedMillis();
        stopwatch.reset();
        System.gc();
        System.out.println("Executing ORIKA");
        stopwatch.start();
        for (int i = 0; i < LOOP_NUMBER; i++) {
            orders.add(ORIKA.map(order, Order.class));
        }
        stopwatch.stop();
        long orikaTime = stopwatch.elapsedMillis();
        stopwatch.reset();
        System.out.println("Finished executing ORIKA");
        Assert.assertEquals(LOOP_NUMBER, orders.size());
        orders.clear();

        System.out.println("Warming up UBIQUITY...");
        stopwatch.start();
        for (int i = 0; i < WARM_LOOP_NUMBER; i++) {
            UBIQUITY.map(order, Order.class);
        }
        stopwatch.stop();
        long ubiquityWarmTime = stopwatch.elapsedMillis();
        stopwatch.reset();
        System.gc();
        System.out.println("Executing UBIQUITY");
        stopwatch.start();
        for (int i = 0; i < LOOP_NUMBER; i++) {
            orders.add(UBIQUITY.map(order, Order.class));
        }
        stopwatch.stop();
        long ubiquityTime = stopwatch.elapsedMillis();
        stopwatch.reset();
        System.out.println("Finished executing UBIQUITY");
        Assert.assertEquals(LOOP_NUMBER, orders.size());
        orders.clear();

        System.out.println("Warming up DOZER...");
        stopwatch.start();
        for (int i = 0; i < WARM_LOOP_NUMBER; i++) {
            DOZER.map(order, Order.class);
        }
        stopwatch.stop();
        long dozerWarmTime = stopwatch.elapsedMillis();
        stopwatch.reset();
        System.gc();

        System.out.println("Executing DOZER");
        stopwatch.start();
        for (int i = 0; i < LOOP_NUMBER; i++) {
            orders.add(DOZER.map(order, Order.class));
        }
        stopwatch.stop();
        long dozerTime = stopwatch.elapsedMillis();
        stopwatch.reset();
        System.out.println("Finished executing DOZER");
        Assert.assertEquals(LOOP_NUMBER, orders.size());
        orders.clear();

        System.out.println("Ubiquity copying took " + ubiquityWarmTime + "ms");
        System.out.println("Orika copying took " + orikaWarmTime + "ms");
        System.out.println("Dozer copying took " + dozerWarmTime + "ms");

        System.out.println("Ubiquity copying took (initialized) " + ubiquityTime + "ms");
        System.out.println("Orika copying took (initialized) " + orikaTime + "ms");
        System.out.println("Dozer copying took (initialized) " + dozerTime + "ms");
    }


    private Order createOrder() {
        Order order = new Order();
        order.setId(200l);
        order.setOrderType(new ReferenceObject(10l, "Direct sell order", "DIRECT"));
        Collection<Product> products = new ArrayList<Product>();
        Product p = new Product();
        p.setId(123456l);
        p.setName("product 1");
        p.setDescription("First test product, yeay !");
        p.setProductType(new ReferenceObject(25l, "Nice and lovely products", "NICE"));
        products.add(p);
        p = new Product();
        p.setId(123452l);
        p.setName("product 2");
        p.setDescription("Second test product, yeay !");
        p.setProductType(new ReferenceObject(22l, "Kawaii japanese stuff", "KAWAII"));
        products.add(p);
        order.setProducts(products);
        return order;
    }


    public static class ReferenceObject {
        private Long id;
        private String label;
        private String code;


        public ReferenceObject() {
        }


        public ReferenceObject(Long id, String label, String code) {
            this.id = id;
            this.label = label;
            this.code = code;
        }


        public Long getId() {
            return id;
        }


        public void setId(Long id) {
            this.id = id;
        }


        public String getLabel() {
            return label;
        }


        public void setLabel(String label) {
            this.label = label;
        }


        public String getCode() {
            return code;
        }


        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class Product {
        private Long id;
        private String name;
        private ReferenceObject productType;
        private String description;


        public Long getId() {
            return id;
        }


        public void setId(Long id) {
            this.id = id;
        }


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        public ReferenceObject getProductType() {
            return productType;
        }


        public void setProductType(ReferenceObject productType) {
            this.productType = productType;
        }


        public String getDescription() {
            return description;
        }


        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static final class Order {
        private Long id;
        private ReferenceObject orderType;
        private Collection<Product> products;


        public Long getId() {
            return id;
        }


        public void setId(Long id) {
            this.id = id;
        }


        public ReferenceObject getOrderType() {
            return orderType;
        }


        public void setOrderType(ReferenceObject orderType) {
            this.orderType = orderType;
        }


        public Collection<Product> getProducts() {
            return products;
        }


        public void setProducts(Collection<Product> products) {
            this.products = products;
        }
    }

}
