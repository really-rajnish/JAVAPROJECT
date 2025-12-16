package Ecommerce;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// ======================= 1. EXCEPTIONS [cite: 72] =======================
class InvalidCouponException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidCouponException(String message) { super(message); }
}

class OutOfStockException extends Exception {
	private static final long serialVersionUID = 1L;

	public OutOfStockException(String message) { super(message); }
}

// ======================= 2. INTERFACES & ABSTRACT CLASSES [cite: 67-69] =======================
interface Taxable {
    double getTaxRate();
    default double calculateTax(double price) {
        return price * (getTaxRate() / 100.0);
    }
}

// Strategy/Policy Interface
interface DiscountPolicy {
    double applyDiscount(double totalAmount);
}

// Abstract Item
abstract class Item {
    protected String id;
    protected String name;
    protected double price;
    protected String category;

    public Item(String id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getId() { return id; }
    public String getCategory() { return category; }
    
    @Override
    public String toString() {
        return String.format("%-5s | %-20s | $%-8.2f | %s", id, name, price, category);
    }
}

// ======================= 3. CONCRETE MODELS =======================
class Product extends Item implements Taxable {
    public Product(String id, String name, double price, String category) {
        super(id, name, price, category);
    }

    @Override
    public double getTaxRate() {
        // Example: Electronics 18% GST, Books 5% GST
        return category.equalsIgnoreCase("Electronics") ? 18.0 : 5.0;
    }
}

class OrderItem {
    private Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getSubtotal() { return product.getPrice() * quantity; }
    public double getTaxAmount() { return product.calculateTax(product.getPrice()) * quantity; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
}

// ======================= 4. PATTERNS: DECORATOR (Promotions)  =======================
// Component interface for cost calculation
interface CostComponent {
    double getTotalCost();
    String getDescription();
}

// Base Component (The basic order total)
class BaseOrderCost implements CostComponent {
    private double total;

    public BaseOrderCost(double total) {
        this.total = total;
    }

    @Override
    public double getTotalCost() { return total; }

    @Override
    public String getDescription() { return "Subtotal"; }
}

// Abstract Decorator
abstract class PromotionDecorator implements CostComponent {
    protected CostComponent tempOrder;

    public PromotionDecorator(CostComponent tempOrder) {
        this.tempOrder = tempOrder;
    }
}

// Concrete Decorator 1: Percentage Off
class PercentageDiscount extends PromotionDecorator {
    private double percent;

    public PercentageDiscount(CostComponent tempOrder, double percent) {
        super(tempOrder);
        this.percent = percent;
    }

    @Override
    public double getTotalCost() {
        return tempOrder.getTotalCost() * (1.0 - percent / 100.0);
    }

    @Override
    public String getDescription() {
        return tempOrder.getDescription() + " + Applied " + percent + "% Discount";
    }
}

// Concrete Decorator 2: Flat Off
class FlatDiscount extends PromotionDecorator {
    private double amount;

    public FlatDiscount(CostComponent tempOrder, double amount) {
        super(tempOrder);
        this.amount = amount;
    }

    @Override
    public double getTotalCost() {
        return Math.max(0, tempOrder.getTotalCost() - amount);
    }

    @Override
    public String getDescription() {
        return tempOrder.getDescription() + " + Applied Flat $" + amount + " Off";
    }
}

// ======================= 5. PATTERNS: FACTORY (Payments)  =======================
interface PaymentProcessor {
    void processPayment(double amount);
}

class CreditCardProcessor implements PaymentProcessor {
    public void processPayment(double amount) { System.out.println("Processing Credit Card charge: $" + String.format("%.2f", amount)); }
}

class UPIProcessor implements PaymentProcessor {
    public void processPayment(double amount) { System.out.println("Processing UPI transaction: $" + String.format("%.2f", amount)); }
}

class PaymentFactory {
    public static PaymentProcessor getProcessor(String type) {
        if (type.equalsIgnoreCase("CARD")) return new CreditCardProcessor();
        if (type.equalsIgnoreCase("UPI")) return new UPIProcessor();
        return new CreditCardProcessor(); // default
    }
}

// ======================= 6. SERVICE LAYER & I/O =======================
class EcommerceService {
    private Map<String, Product> inventory = new HashMap<>(); // [cite: 71]
    private List<OrderItem> cart = new ArrayList<>();
    
    // Load products from CSV (Simulation) 
    public void loadProducts(String filename) {
        // In a real run, use BufferedReader. Here we mock data for instant testing.
        inventory.put("P101", new Product("P101", "Laptop", 1200.00, "Electronics"));
        inventory.put("P102", new Product("P102", "Java Book", 45.00, "Books"));
        inventory.put("P103", new Product("P103", "Headphones", 150.00, "Electronics"));
        inventory.put("P104", new Product("P104", "Desk Lamp", 30.00, "Home"));
    }

    public List<Product> getAllProducts() {
        // Sort by price (Stream API) [cite: 64]
        return inventory.values().stream()
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .collect(Collectors.toList());
    }

    public void addToCart(String productId, int qty) throws OutOfStockException {
        if (!inventory.containsKey(productId)) {
            System.out.println("Error: Product ID not found.");
            return;
        }
        // Simple stock check simulation
        if (qty > 10) throw new OutOfStockException("Requested quantity exceeds stock limits.");
        
        cart.add(new OrderItem(inventory.get(productId), qty));
        System.out.println("Added to cart!");
    }

    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("\n--- Your Cart ---");
        // Streams for subtotal 
        cart.forEach(item -> System.out.println(
            item.getProduct().getName() + " x" + item.getQuantity() + 
            " = $" + item.getSubtotal()
        ));
    }

    public void checkout() {
        if (cart.isEmpty()) return;

        // 1. Calculate Base Totals using Streams 
        double grossTotal = cart.stream().mapToDouble(OrderItem::getSubtotal).sum();
        double totalTax = cart.stream().mapToDouble(OrderItem::getTaxAmount).sum();
        double totalWithTax = grossTotal + totalTax;

        System.out.println("\n--- Checkout ---");
        System.out.println("Gross Total: $" + grossTotal);
        System.out.println("Tax (GST):   $" + totalTax);
        System.out.println("Subtotal:    $" + totalWithTax);

        try (// 2. Apply Decorator Pattern for Coupons
		Scanner sc = new Scanner(System.in)) {
			System.out.print("Enter Coupon Code (or press enter to skip): ");
			String coupon = sc.nextLine().trim();

			CostComponent finalOrder = new BaseOrderCost(totalWithTax);

			try {
			    if (coupon.equalsIgnoreCase("SAVE10")) {
			        finalOrder = new PercentageDiscount(finalOrder, 10.0);
			    } else if (coupon.equalsIgnoreCase("FLAT50")) {
			        finalOrder = new FlatDiscount(finalOrder, 50.0);
			    } else if (!coupon.isEmpty()) {
			        throw new InvalidCouponException("Coupon code invalid."); // [cite: 72]
			    }
			} catch (InvalidCouponException e) {
			    System.out.println("Warning: " + e.getMessage());
			}

			double finalAmount = finalOrder.getTotalCost();
			System.out.println("Promo Applied: " + finalOrder.getDescription());
			System.out.println("FINAL PAYABLE: $" + String.format("%.2f", finalAmount));

			// 3. Payment via Factory 
			System.out.print("Payment Method (CARD/UPI): ");
			String method = sc.nextLine();
			PaymentProcessor processor = PaymentFactory.getProcessor(method);
			processor.processPayment(finalAmount);

			// 4. Generate Invoice File 
			generateInvoice(finalAmount, totalTax);
		}
        cart.clear(); // Empty cart after success
    }

    private void generateInvoice(double finalAmount, double tax) {
        String orderId = "ORD-" + System.currentTimeMillis();
        String filename = "invoice_" + orderId + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("E-COMMERCE INVOICE");
            writer.println("Order ID: " + orderId);
            writer.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.println("------------------------------------------------");
            writer.printf("%-20s %-10s %-10s%n", "Item", "Qty", "Total");
            for (OrderItem item : cart) {
                writer.printf("%-20s %-10d $%-10.2f%n", 
                    item.getProduct().getName(), 
                    item.getQuantity(), 
                    item.getSubtotal());
            }
            writer.println("------------------------------------------------");
            writer.println("Total Tax: $" + String.format("%.2f", tax));
            writer.println("Grand Total: $" + String.format("%.2f", finalAmount));
            System.out.println("Invoice generated: " + filename);
        } catch (IOException e) {
            System.out.println("Error generating invoice: " + e.getMessage());
        }
    }
}

// ======================= 7. MAIN CLI LOOP [cite: 58, 62] =======================
public class EcommerceSystem {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
			EcommerceService service = new EcommerceService();
			
			// Initialize data
			service.loadProducts("products.csv");

			System.out.println("Welcome to Java E-Commerce System");

			while (true) { // Iterative Loop
			    System.out.println("\n1. View Products");
			    System.out.println("2. Add to Cart");
			    System.out.println("3. View Cart");
			    System.out.println("4. Checkout");
			    System.out.println("5. Exit");
			    System.out.print("Select Option: ");

			    int choice;
			    try {
			        choice = Integer.parseInt(scanner.nextLine());
			    } catch (NumberFormatException e) {
			        choice = -1;
			    }

			    switch (choice) {
			        case 1:
			            System.out.println("\nID    | Name                 | Price     | Category");
			            System.out.println("---------------------------------------------------");
			            service.getAllProducts().forEach(System.out::println);
			            break;
			        case 2:
			            System.out.print("Enter Product ID: ");
			            String pid = scanner.nextLine();
			            System.out.print("Enter Quantity: ");
			            int qty = Integer.parseInt(scanner.nextLine());
			            try {
			                service.addToCart(pid, qty);
			            } catch (OutOfStockException e) {
			                System.err.println("Error: " + e.getMessage());
			            }
			            break;
			        case 3:
			            service.viewCart();
			            break;
			        case 4:
			            service.checkout();
			            break;
			        case 5:
			            System.out.println("Exiting...");
			            return;
			        default:
			            System.out.println("Invalid option.");
			    }
			}
		} catch (NumberFormatException e) {
		
			e.printStackTrace();
		}
    }
}