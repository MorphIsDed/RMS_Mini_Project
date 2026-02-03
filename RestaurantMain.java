import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * ==================================================================================
 * RESTAURANT MANAGEMENT SYSTEM v2.0
 * ==================================================================================
 * 
 * A comprehensive command-line application for managing a restaurant's menu and sales.
 * Features include menu management, order processing, discount application, and 
 * detailed sales analytics. All data is automatically persisted to files between sessions.
 * 
 * Key Features:
 * - Menu Management: Add, remove, search, and view menu items
 * - Order Management: Create orders, add items, apply discounts, process payments
 * - Sales Analytics: Track revenue, popular items, and category-based insights
 * - Data Persistence: Automatic save/load from menu_data.txt and sales_data.txt
 * - Order Cancellation: Cancel unpaid orders
 * - Discount Support: Apply percentage-based discounts to individual items
 * 
 * ==================================================================================
 */

/**
 * MenuItem Class
 * 
 * Represents a single item on the restaurant menu.
 * Stores information about the item including name, category, price, and popularity metrics.
 * Each MenuItem tracks how many times it has been ordered to provide insights into customer preferences.
 * 
 * @author Abhinay Kumar Sahu
 * @version 2.0
 */
class MenuItem {
    // Instance variables
    private String name;              // Name of the menu item (e.g., "Pasta", "Burger")
    private String category;          // Category classification (e.g., "Main Course", "Appetizer")
    private double price;             // Price of the item in dollars
    private int timesOrdered = 0;     // Counter tracking popularity - increases each time item is ordered

    /**
     * Constructor for MenuItem.
     * Creates a new menu item with the specified name, category, and price.
     * 
     * @param name     The name of the menu item
     * @param category The category/type of the menu item
     * @param price    The price of the menu item in dollars
     */
    MenuItem(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    // Getter methods for all menu item properties
    
    /** @return The name of this menu item */
    String getName()       { return name; }
    
    /** @return The category of this menu item */
    String getCategory()   { return category; }
    
    /** @return The price of this menu item */
    double getPrice()      { return price; }
    
    /** @return The number of times this item has been ordered */
    int getTimesOrdered()  { return timesOrdered; }

    /**
     * Increments the order counter by the specified quantity.
     * This is called whenever this item is added to an order.
     * 
     * @param qty The quantity ordered (number of times to increment)
     */
    void incrementOrders(int qty) { timesOrdered += qty; }

    /**
     * Returns a standard string representation of the menu item.
     * Format: "Name                  Category     $Price"
     * Used in normal menu display.
     * 
     * @return Formatted string with item details
     */
    public String toString() {
        return String.format("%-22s %-12s $%.2f", name, category, price);
    }

    /**
     * Returns a detailed string representation including popularity metrics.
     * Format: "Name                  Category     $Price (ordered X times)"
     * Used when displaying menu with popularity information.
     * 
     * @return Formatted string with item details and order count
     */
    String toDetailedString() {
        return String.format("%-22s %-12s $%.2f (ordered %d times)", name, category, price, timesOrdered);
    }
}

/**
 * Menu Class
 * 
 * Manages the restaurant's complete menu of items.
 * Handles menu operations including:
 * - Adding/removing menu items
 * - Displaying the menu in different formats
 * - Searching for items by category or name
 * - Persisting menu data to file (menu_data.txt)
 * 
 * The menu is automatically loaded from file on initialization and saved
 * after any modifications to ensure data persistence.
 * 
 * Data Format: Name|Category|Price|TimesOrdered (pipe-separated values)
 */
class Menu {
    // Instance variables
    private ArrayList<MenuItem> items = new ArrayList<>();  // Collection of all menu items
    private static final String FILE = "menu_data.txt";      // File name for persistent storage

    /**
     * Constructor for Menu.
     * Initializes an empty menu and loads items from file (if it exists).
     */
    Menu() {
        loadFromFile();  // Automatically load persisted menu items from file
    }

    /**
     * Adds a new menu item to the menu.
     * Automatically saves the updated menu to file.
     * 
     * @param item The MenuItem to add to the menu
     */
    void addItem(MenuItem item) {
        items.add(item);
        saveToFile();  // Persist changes immediately
    }

    /**
     * Removes a menu item at the specified index.
     * Uses 0-based indexing. Automatically saves changes to file.
     * 
     * @param index The zero-based index of the item to remove
     */
    void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            saveToFile();
            System.out.println("Item removed.");
        } else {
            System.out.println("Invalid number.");
        }
    }

    /**
     * Displays the menu in standard format.
     * Shows item number, name, category, and price.
     * Displays "Menu is empty" message if no items exist.
     */
    void display() {
        if (items.isEmpty()) {
            System.out.println("  (Menu is empty)");
            return;
        }
        System.out.println("  #   Name                   Category     Price");
        System.out.println("  ---------------------------------------------------");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ".  " + items.get(i));
        }
    }

    /**
     * Displays the menu with detailed information including popularity metrics.
     * Shows how many times each item has been ordered.
     * Useful for analyzing customer preferences.
     */
    void displayDetailed() {
        if (items.isEmpty()) {
            System.out.println("  (Menu is empty)");
            return;
        }
        System.out.println("  #   Name                   Category     Price         Popularity");
        System.out.println("  -----------------------------------------------------------------------");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ".  " + items.get(i).toDetailedString());
        }
    }

    /**
     * Retrieves a menu item at the specified index.
     * 
     * @param index The zero-based index of the item to retrieve
     * @return The MenuItem at the specified index, or null if index is invalid
     */
    MenuItem getItem(int index) {
        if (index >= 0 && index < items.size()) return items.get(index);
        return null;
    }

    /**
     * Searches for all menu items in a specific category.
     * Search is case-insensitive.
     * 
     * @param category The category to search for
     * @return ArrayList of MenuItems matching the category (empty if none found)
     */
    ArrayList<MenuItem> searchByCategory(String category) {
        ArrayList<MenuItem> result = new ArrayList<>();
        for (MenuItem item : items) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Searches for a menu item by exact name match.
     * Search is case-insensitive.
     * 
     * @param name The name of the item to search for
     * @return The MenuItem if found, null otherwise
     */
    MenuItem searchByName(String name) {
        for (MenuItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /** @return The total number of menu items */
    int size() { return items.size(); }

    /**
     * Saves all menu items to file in pipe-separated format.
     * Format: Name|Category|Price|TimesOrdered
     * This method is called automatically after any menu modification.
     * 
     * File location: menu_data.txt (in the project root directory)
     */
    void saveToFile() {
        try {
            PrintWriter pw = new PrintWriter(FILE);
            for (MenuItem item : items) {
                pw.println(item.getName() + "|" + item.getCategory() + "|" 
                    + item.getPrice() + "|" + item.getTimesOrdered());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save menu.");
        }
    }

    /**
     * Loads menu items from file into memory.
     * Reads from menu_data.txt using pipe-separated format.
     * This is called automatically during Menu initialization.
     * If file doesn't exist, the menu starts empty (no error).
     * 
     * File format: Name|Category|Price|TimesOrdered
     */
    void loadFromFile() {
        items.clear();
        try {
            Scanner sc = new Scanner(new File(FILE));
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length >= 3) {
                    MenuItem item = new MenuItem(p[0], p[1], Double.parseDouble(p[2]));
                    if (p.length == 4) {
                        item.incrementOrders(Integer.parseInt(p[3]));
                    }
                    items.add(item);
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            // File doesn't exist yet - that's fine
        }
    }
}

/**
 * OrderItem Class
 * 
 * Represents a single line item within a customer order.
 * Contains a menu item, quantity ordered, and any discount applied.
 * Examples: "2x Pasta at $12.50 = $25.00 (10% off)"
 * 
 * Calculates totals including discounts and maintains the relationship
 * between ordered quantity and original menu item information.
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
class OrderItem {
    // Instance variables
    private MenuItem menuItem;                 // Reference to the menu item being ordered
    private int quantity;                      // Quantity of this item in the order
    private double discount = 0.0;             // Discount as percentage (0-100, e.g., 10.0 means 10% off)

    /**
     * Constructor for OrderItem.
     * Creates a new order line item with the specified menu item and quantity.
     * 
     * @param menuItem The MenuItem being ordered
     * @param quantity The number of this item to order
     */
    OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    // Getter methods for order item properties
    
    /** @return The MenuItem associated with this order item */
    MenuItem getMenuItem()  { return menuItem; }
    
    /** @return The quantity of items in this order line */
    int getQuantity()       { return quantity; }
    
    /** @return The discount percentage (0-100) applied to this item */
    double getDiscount()    { return discount; }

    /**
     * Applies a discount percentage to this order item.
     * Discount is capped between 0% and 100%.
     * 
     * @param discountPercent The discount percentage to apply (0-100)
     */
    void setDiscount(double discountPercent) {
        if (discountPercent >= 0 && discountPercent <= 100) {
            this.discount = discountPercent;
        }
    }

    /**
     * Calculates the subtotal for this order item AFTER discount is applied.
     * Formula: (Price × Quantity) × (1 - Discount%/100)
     * 
     * @return The final price to charge for this order item
     */
    double getSubtotal() {
        double subtotal = menuItem.getPrice() * quantity;  // Base amount: price per item × quantity
        double discountAmount = subtotal * (discount / 100.0);  // Calculate discount value
        return subtotal - discountAmount;  // Return price after discount
    }

    /**
     * Calculates the original subtotal BEFORE any discount is applied.
     * Useful for showing what the price was before discount was given.
     * 
     * @return The price before discount
     */
    double getOriginalSubtotal() {
        return menuItem.getPrice() * quantity;
    }

    /**
     * Returns a formatted string representation of the order item.
     * Includes quantity, name, unit price, total price, and discount (if applied).
     * Example: "2x Pasta                  @ $12.50 = $25.00" or
     *          "2x Pasta                  @ $12.50 = $22.50 (10% off)"
     * 
     * @return Formatted string for display in order
     */
    public String toString() {
        if (discount > 0) {
            // Display with discount information
            return String.format("  %dx %-22s @ $%.2f = $%.2f (%.0f%% off)",
                quantity, menuItem.getName(), menuItem.getPrice(), getSubtotal(), discount);
        }
        // Display without discount
        return String.format("  %dx %-22s @ $%.2f = $%.2f",
                quantity, menuItem.getName(), menuItem.getPrice(), getSubtotal());
    }
}

/**
 * Order Class
 * 
 * Represents a complete customer order with a unique ID.
 * Manages order items, payment status, and cancellation.
 * 
 * Features:
 * - Unique auto-incrementing order ID
 * - Track creation time and payment time
 * - Support for multiple items with various discounts
 * - Order can be in states: Active, Paid, or Cancelled
 * - Calculate totals with and without discounts applied
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
class Order {
    // Static variable for auto-incrementing order IDs
    private static int nextId = 1;   // Counter ensuring each order gets a unique sequential ID

    // Instance variables
    private int id;                              // Unique identifier for this order
    private ArrayList<OrderItem> items = new ArrayList<>();  // List of items in this order
    private boolean paid = false;                // Whether payment has been received
    private boolean cancelled = false;           // Whether this order was cancelled
    private LocalDateTime createdAt;             // Timestamp when order was created
    private LocalDateTime paidAt = null;         // Timestamp when payment was received (null if unpaid)

    /**
     * Constructor for creating a new order.
     * Automatically assigns a new unique ID and records creation time.
     */
    Order() {
        this.id = nextId++;  // Assign next available ID and increment counter
        this.createdAt = LocalDateTime.now();  // Record when order was created
    }

    /**
     * Constructor for loading an existing order from file.
     * Used during file load operations to preserve original order IDs.
     * 
     * @param id The order ID to use (from file)
     */
    Order(int id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        // Ensure static counter stays ahead of loaded order IDs to avoid duplicates
        if (id >= nextId) nextId = id + 1;
    }

    // Getter methods for order properties
    
    /** @return The unique ID of this order */
    int getID()                          { return id; }
    
    /** @return The list of OrderItems in this order */
    ArrayList<OrderItem> getItems()      { return items; }
    
    /** @return True if order has been paid, false otherwise */
    boolean isPaid()                     { return paid; }
    
    /** @return True if order was cancelled, false otherwise */
    boolean isCancelled()                { return cancelled; }
    
    /** @return The timestamp when this order was created */
    LocalDateTime getCreatedAt()         { return createdAt; }
    
    /** @return The timestamp when payment was received (null if unpaid) */
    LocalDateTime getPaidAt()            { return paidAt; }

    /**
     * Adds an OrderItem to this order.
     * 
     * @param oi The OrderItem to add
     */
    void addItem(OrderItem oi)  { items.add(oi); }
    
    /**
     * Marks this order as paid.
     * Records the current time as the payment timestamp.
     */
    void markPaid()             { 
        paid = true;  // Set payment status to true
        paidAt = LocalDateTime.now();  // Record payment time
    }

    /**
     * Cancels this order (only if not already paid).
     * Clears all items and prevents future modifications.
     * Paid orders cannot be cancelled.
     */
    void cancel() {
        if (!paid) {
            cancelled = true;
            items.clear();
            System.out.println("Order #" + id + " has been cancelled.");
        } else {
            System.out.println("Cannot cancel a paid order.");
        }
    }

    /**
     * Calculates the total order amount AFTER all discounts are applied.
     * 
     * @return The final amount to charge the customer
     */
    double getTotal() {
        double t = 0;
        // Sum up all items with their discounts already applied
        for (OrderItem oi : items) t += oi.getSubtotal();
        return t;
    }

    /**
     * Calculates the total order amount BEFORE any discounts.
     * Used to show discount savings to customer.
     * 
     * @return The original undiscounted total
     */
    double getOriginalTotal() {
        double t = 0;
        for (OrderItem oi : items) t += oi.getOriginalSubtotal();
        return t;
    }

    /**
     * Displays the order details in a formatted manner.
     * Shows order ID, all items, totals, and payment status.
     * Highlights discounts if any were applied.
     */
    void display() {
        System.out.println("  --- Order #" + id + " ---");
        
        // Show cancellation status if applicable
        if (cancelled) {
            System.out.println("    [CANCELLED]");
            return;
        }
        
        // Show items or "empty" message
        if (items.isEmpty()) {
            System.out.println("    (empty)");
        } else {
            for (OrderItem oi : items) System.out.println("  " + oi);
        }
        
        // Show subtotal and final total if discounts were applied
        if (getOriginalTotal() > getTotal()) {
            System.out.printf("    Subtotal: $%.2f%n", getOriginalTotal());
            System.out.printf("    Total   : $%.2f   [%s]%n", getTotal(), paid ? "PAID" : "UNPAID");
        } else {
            System.out.printf("    Total   : $%.2f   [%s]%n", getTotal(), paid ? "PAID" : "UNPAID");
        }
    }
}

/**
 * Sales Class
 * 
 * Manages all customer orders and sales data for the restaurant.
 * Handles order operations including:
 * - Creating new orders
 * - Adding/removing items from orders
 * - Applying discounts to order items
 * - Processing payments
 * - Cancelling orders
 * - Generating sales reports and analytics
 * 
 * Persists all orders to file (sales_data.txt) to maintain history across sessions.
 * 
 * Data Format: 
 *   ORDER|id|paid|cancelled
 *   ITEM|name|category|price|qty|discount
 *   END_ORDER
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
class Sales {
    // Instance variables
    private ArrayList<Order> orders = new ArrayList<>();  // Complete history of all orders
    public Order current = null;                            // The order currently being built by customer (public for menu access)
    private Menu menu;                                      // Reference to the menu (to access menu items)
    private static final String FILE = "sales_data.txt";    // File for persistent storage

    /**
     * Constructor for Sales.
     * Initializes the sales manager and loads order history from file.
     * 
     * @param menu The Menu object to reference when adding items to orders
     */
    Sales(Menu menu) {
        this.menu = menu;
        loadFromFile();  // Load all previous orders from file
    }

    /**
     * Starts a new customer order.
     * Prevents starting a new order if current order is unpaid.
     * Sets the new order as the current order.
     */
    void newOrder() {
        if (current != null && !current.isPaid() && !current.isCancelled()) {
            System.out.println("Finish or pay the current order first (Order #" + current.getID() + ").");
            return;
        }
        current = new Order();
        orders.add(current);
        System.out.println("New Order #" + current.getID() + " started.");
    }

    /**
     * Adds a menu item to the current order.
     * Automatically increments the item's popularity counter.
     * Validates order status and menu item before adding.
     * 
     * @param menuIndex The zero-based index of the menu item to add
     * @param qty The quantity of the item to add
     */
    void addToOrder(int menuIndex, int qty) {
        if (current == null || current.isCancelled()) {
            System.out.println("No active order. Start one first.");
            return;
        }
        if (current.isPaid()) {
            System.out.println("Current order is already paid. Start a new one.");
            return;
        }
        MenuItem mi = menu.getItem(menuIndex);
        if (mi == null) {
            System.out.println("Invalid item number.");
            return;
        }
        current.addItem(new OrderItem(mi, qty));
        mi.incrementOrders(qty);  // Update popularity counter
        System.out.println("Added " + qty + "x " + mi.getName());
        saveToFile();  // Persist changes
    }

    /**
     * Applies a discount percentage to an item in the current order.
     * Discount is applied to the line item total, not just the unit price.
     * 
     * @param itemIndex The zero-based index of the item in current order to discount
     * @param discountPercent The discount percentage (0-100)
     */
    void applyDiscount(int itemIndex, double discountPercent) {
        if (current == null || current.isPaid() || current.isCancelled()) {
            System.out.println("No active order to apply discount to.");
            return;
        }
        ArrayList<OrderItem> items = current.getItems();
        if (itemIndex >= 0 && itemIndex < items.size()) {
            items.get(itemIndex).setDiscount(discountPercent);
            System.out.printf("Applied %.0f%% discount to item.%n", discountPercent);
            saveToFile();
        } else {
            System.out.println("Invalid item index.");
        }
    }

    /**
     * Removes an item from the current order.
     * Only works on active (unpaid, uncancelled) orders.
     * 
     * @param itemIndex The zero-based index of the item to remove from current order
     */
    void removeFromOrder(int itemIndex) {
        if (current == null || current.isPaid() || current.isCancelled()) {
            System.out.println("No active order.");
            return;
        }
        ArrayList<OrderItem> items = current.getItems();
        if (itemIndex >= 0 && itemIndex < items.size()) {
            items.remove(itemIndex);
            System.out.println("Item removed from order.");
            saveToFile();
        } else {
            System.out.println("Invalid item number.");
        }
    }

    /**
     * Displays the current order being built.
     * Shows "No active order" message if none is active.
     */
    void viewCurrent() {
        if (current == null) System.out.println("No active order.");
        else current.display();
    }

    /**
     * Processes payment for the current order.
     * Marks order as paid with current timestamp.
     * Prevents payment for empty or already paid orders.
     * Sets current order to null after payment.
     */
    void pay() {
        if (current == null || current.isPaid() || current.isCancelled()) {
            System.out.println("Nothing to pay.");
            return;
        }
        if (current.getItems().isEmpty()) {
            System.out.println("Order is empty, add items first.");
            return;
        }
        current.markPaid();  // Mark as paid with timestamp
        System.out.printf("Payment of $%.2f received for Order #%d.%n", current.getTotal(), current.getID());
        current = null;  // Clear current order so new one can be started
        saveToFile();  // Persist payment
    }

    /**
     * Cancels the current active order.
     * Only unpaid orders can be cancelled.
     * Clears the current order after cancellation.
     */
    void cancelCurrent() {
        if (current == null) {
            System.out.println("No active order to cancel.");
            return;
        }
        current.cancel();
        if (current.isCancelled()) {
            current = null;
            saveToFile();
        }
    }

    /**
     * Displays all orders (paid, unpaid, and cancelled).
     * Shows complete order history since program started.
     */
    void viewAll() {
        if (orders.isEmpty()) {
            System.out.println("  No orders yet.");
            return;
        }
        for (Order o : orders) o.display();
    }

    /**
     * Displays only unpaid orders.
     * Useful for tracking outstanding customer payments.
     */
    void viewUnpaid() {
        ArrayList<Order> unpaidOrders = new ArrayList<>();
        for (Order o : orders) {
            if (!o.isPaid() && !o.isCancelled()) {
                unpaidOrders.add(o);
            }
        }
        if (unpaidOrders.isEmpty()) {
            System.out.println("  No unpaid orders.");
            return;
        }
        for (Order o : unpaidOrders) o.display();
    }

    /**
     * Displays a comprehensive sales summary.
     * Shows statistics including:
     * - Total number of orders and their statuses
     * - Total items sold
     * - Revenue from completed orders
     * - Total discounts given
     */
    void summary() {
        // Initialize counters and accumulators
        double revenue = 0;      // Total money received from paid orders
        double discountGiven = 0;  // Total discount amount given to customers
        int paid = 0, unpaid = 0, cancelled = 0;  // Order status counts
        int totalItems = 0;      // Total number of individual items sold

        // Iterate through all orders and collect statistics
        for (Order o : orders) {
            if (o.isCancelled()) {
                cancelled++;
            } else if (o.isPaid()) {
                revenue += o.getTotal();
                discountGiven += (o.getOriginalTotal() - o.getTotal());
                paid++;
                totalItems += o.getItems().size();
            } else {
                unpaid++;
                totalItems += o.getItems().size();
            }
        }

        System.out.println("\n  ===== Sales Summary =====");
        System.out.println("  Total orders    : " + orders.size());
        System.out.println("  Completed       : " + paid);
        System.out.println("  Unpaid          : " + unpaid);
        System.out.println("  Cancelled       : " + cancelled);
        System.out.println("  Total items sold: " + totalItems);
        System.out.printf( "  Revenue         : $%.2f%n", revenue);
        if (discountGiven > 0) {
            System.out.printf("  Discount given  : $%.2f%n", discountGiven);
        }
        System.out.println();
    }

    /**
     * Displays revenue breakdown by menu category.
     * Shows how much revenue was generated from each food category.
     * Only includes paid orders.
     * Useful for analyzing which menu categories are most profitable.
     */
    void revenueByCategory() {
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Double> categoryRevenue = new ArrayList<>();

        for (Order o : orders) {
            if (o.isPaid()) {
                for (OrderItem oi : o.getItems()) {
                    String cat = oi.getMenuItem().getCategory();
                    int idx = categories.indexOf(cat);
                    if (idx == -1) {
                        categories.add(cat);
                        categoryRevenue.add(oi.getSubtotal());
                    } else {
                        categoryRevenue.set(idx, categoryRevenue.get(idx) + oi.getSubtotal());
                    }
                }
            }
        }

        if (categories.isEmpty()) {
            System.out.println("  No revenue data yet.");
            return;
        }

        System.out.println("\n  ===== Revenue by Category =====");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("  %-20s : $%.2f%n", categories.get(i), categoryRevenue.get(i));
        }
        System.out.println();
    }

    /**
     * Saves all orders to file in a structured format.
     * Each order is stored with its items and status information.
     * This is called after every order modification to maintain data persistence.
     * 
     * File Format:
     *   ORDER|id|paid|cancelled
     *   ITEM|name|category|price|qty|discount
     *   END_ORDER
     *   (repeat for each order)
     */
    void saveToFile() {
        try {
            PrintWriter pw = new PrintWriter(FILE);
            for (Order o : orders) {
                pw.println("ORDER|" + o.getID() + "|" + o.isPaid() + "|" + o.isCancelled());
                for (OrderItem oi : o.getItems()) {
                    pw.println("ITEM|" + oi.getMenuItem().getName() + "|"
                            + oi.getMenuItem().getCategory() + "|"
                            + oi.getMenuItem().getPrice() + "|"
                            + oi.getQuantity() + "|"
                            + oi.getDiscount());
                }
                pw.println("END_ORDER");
            }
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not save sales data.");
        }
    }

    /**
     * Loads all orders from file into memory.
     * Reconstructs order objects and their items from file.
     * Called automatically during Sales initialization.
     * If file doesn't exist, starts with empty order history (no error).
     * 
     * Restores the current order if it's unpaid to allow resuming work.
     */
    void loadFromFile() {
        orders.clear();  // Clear existing orders
        try {
            Scanner sc = new Scanner(new File(FILE));
            Order temp = null;  // Temporary order being constructed from file
            while (sc.hasNextLine()) {  // Read file line by line
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("ORDER|")) {
                    // Parse order header line
                    String[] p = line.split("\\|");
                    temp = new Order(Integer.parseInt(p[1]));  // Create order with file ID
                    if (Boolean.parseBoolean(p[2])) temp.markPaid();  // Restore paid status
                    if (p.length >= 4 && Boolean.parseBoolean(p[3])) {  // Restore cancelled status
                        temp.cancel();
                    }

                } else if (line.startsWith("ITEM|") && temp != null) {
                    // Parse order item line
                    String[] p = line.split("\\|");
                    MenuItem mi = new MenuItem(p[1], p[2], Double.parseDouble(p[3]));
                    OrderItem oi = new OrderItem(mi, Integer.parseInt(p[4]));
                    if (p.length >= 6) {
                        oi.setDiscount(Double.parseDouble(p[5]));
                    }
                    temp.addItem(oi);

                } else if (line.equals("END_ORDER") && temp != null) {
                    orders.add(temp);
                    temp = null;
                }
            }
            sc.close();
            // Restore the last unpaid order as current (to resume work)
            if (!orders.isEmpty()) {
                Order last = orders.get(orders.size() - 1);
                if (!last.isPaid() && !last.isCancelled()) current = last;
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet - that's fine, start fresh
        }
    }
}

/**
 * RestaurantMain Class (Main Application)
 * 
 * Main entry point for the Restaurant Management System.
 * Provides command-line user interface for:
 * - Menu Management: Add/remove/search menu items
 * - Sales Management: Create orders, add items, apply discounts, process payments
 * - Reporting: View sales summaries and revenue analytics
 * 
 * All user input is validated and sanitized through helper methods.
 * Automatically loads/saves data to persist state across sessions.
 * 
 * @author Restaurant Management System
 * @version 2.0
 */
public class RestaurantMain {

    // Static variables shared across the application
    static Scanner sc = new Scanner(System.in);  // Input scanner for reading user input
    static Menu  menu;                           // Reference to the menu system
    static Sales sales;                          // Reference to the sales system

    /**
     * Main method - Entry point of the application.
     * Initializes the menu and sales systems and runs the main menu loop.
     * Continues until user selects "Exit" option.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Display application header
        System.out.println("========================================");
        System.out.println("   Restaurant Management System");
        System.out.println("========================================\n");

        // Initialize the menu and sales systems
        menu  = new Menu();      // Load menu from file
        sales = new Sales(menu); // Load sales history from file

        // Main application loop - continues until user exits
        boolean run = true;
        while (run) {
            System.out.println("\n--- Main Menu ---");
            System.out.println(" 1. Menu Management");
            System.out.println(" 2. Sales / Orders");
            System.out.println(" 3. Exit");
            int ch = readInt("Choice: ");

            // Route user to appropriate subsystem
            if      (ch == 1) menuSection();
            else if (ch == 2) salesSection();
            else if (ch == 3) { System.out.println("Goodbye."); run = false; }  // Exit the application
            else System.out.println("Invalid choice.");
        }
        sc.close();  // Close the scanner resource
    }

    /**
     * Menu Management submenu.
     * Allows users to:
     * - View menu (standard and detailed views)
     * - Add new menu items
     * - Remove menu items
     * - Search items by category or name
     * 
     * Displays a loop until user selects "Back".
     */
    static void menuSection() {
        boolean open = true;
        while (open) {
            System.out.println("\n--- Menu Management ---");
            System.out.println(" 1. View Menu");
            System.out.println(" 2. View Menu (with popularity)");
            System.out.println(" 3. Add Item");
            System.out.println(" 4. Remove Item");
            System.out.println(" 5. Search by Category");
            System.out.println(" 6. Search by Name");
            System.out.println(" 7. Back");
            int ch = readInt("Choice: ");

            if (ch == 1) {
                menu.display();

            } else if (ch == 2) {
                menu.displayDetailed();

            } else if (ch == 3) {
                String name     = readString("  Item name : ");
                String category = readString("  Category  : ");
                double price    = readDouble("  Price ($) : ");
                menu.addItem(new MenuItem(name, category, price));
                System.out.println("Item added.");

            } else if (ch == 4) {
                menu.display();
                if (menu.size() == 0) continue;
                int n = readInt("  Remove item # : ");
                menu.removeItem(n - 1);

            } else if (ch == 5) {
                String category = readString("  Search category: ");
                ArrayList<MenuItem> results = menu.searchByCategory(category);
                if (results.isEmpty()) {
                    System.out.println("  No items found in category: " + category);
                } else {
                    System.out.println("  Found " + results.size() + " item(s):");
                    for (MenuItem item : results) {
                        System.out.println("    " + item);
                    }
                }

            } else if (ch == 6) {
                String name = readString("  Search item name: ");
                MenuItem item = menu.searchByName(name);
                if (item == null) {
                    System.out.println("  Item not found: " + name);
                } else {
                    System.out.println("  Found: " + item);
                }

            } else if (ch == 7) {
                open = false;
            }
        }
    }

    /**
     * Sales / Orders submenu.
     * Allows users to:
     * - Create and manage customer orders
     * - Add/remove items from orders
     * - Apply discounts to items
     * - Process payments
     * - Cancel unpaid orders
     * - View order history
     * - Generate sales reports and analytics
     * 
     * Displays a loop until user selects "Back".
     */
    static void salesSection() {
        boolean open = true;
        while (open) {
            System.out.println("\n--- Sales / Orders ---");
            System.out.println(" 1. New Order");
            System.out.println(" 2. Add Item to Order");
            System.out.println(" 3. Remove Item from Order");
            System.out.println(" 4. Apply Discount to Item");
            System.out.println(" 5. View Current Order");
            System.out.println(" 6. Pay Order");
            System.out.println(" 7. Cancel Current Order");
            System.out.println(" 8. View All Orders");
            System.out.println(" 9. View Unpaid Orders");
            System.out.println(" 10. Sales Summary");
            System.out.println(" 11. Revenue by Category");
            System.out.println(" 12. Back");
            int ch = readInt("Choice: ");

            if (ch == 1) {
                sales.newOrder();

            } else if (ch == 2) {
                menu.display();
                if (menu.size() == 0) { System.out.println("Menu is empty."); continue; }
                int item = readInt("  Pick item # : ");
                int qty  = readInt("  Quantity    : ");
                sales.addToOrder(item - 1, qty);

            } else if (ch == 3) {
                sales.viewCurrent();
                if (sales.current == null || sales.current.getItems().isEmpty()) continue;
                int idx = readInt("  Remove item # : ");
                sales.removeFromOrder(idx - 1);

            } else if (ch == 4) {
                sales.viewCurrent();
                if (sales.current == null || sales.current.getItems().isEmpty()) continue;
                int idx = readInt("  Item # to discount : ");
                double discount = readDouble("  Discount % (0-100) : ");
                sales.applyDiscount(idx - 1, discount);

            } else if (ch == 5) {
                sales.viewCurrent();

            } else if (ch == 6) {
                sales.pay();

            } else if (ch == 7) {
                sales.cancelCurrent();

            } else if (ch == 8) {
                sales.viewAll();

            } else if (ch == 9) {
                sales.viewUnpaid();

            } else if (ch == 10) {
                sales.summary();

            } else if (ch == 11) {
                sales.revenueByCategory();

            } else if (ch == 12) {
                open = false;
            }
        }
    }

    /**
     * HELPER METHODS FOR SAFE USER INPUT
     * These methods validate and sanitize user input before processing.
     */

    /**
     * Reads an integer from user input with error handling.
     * Continuously prompts until valid integer is entered.
     * 
     * @param prompt The message to display to user
     * @return The integer value entered by user
     */
    static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    /**
     * Reads a decimal number from user input with error handling.
     * Continuously prompts until valid number is entered.
     * 
     * @param prompt The message to display to user
     * @return The double value entered by user
     */
    static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    /**
     * Reads a non-empty string from user input with validation.
     * Continuously prompts until non-empty string is entered.
     * Trims whitespace from input.
     * 
     * @param prompt The message to display to user
     * @return The string value entered by user (non-empty)
     */
    static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Cannot be empty.");
        }
    }
}