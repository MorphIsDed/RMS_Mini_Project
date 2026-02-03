import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

// ---------------------------------------------------------------
// Represents one item on the menu (e.g. Pasta, $12.50)
// ---------------------------------------------------------------
class MenuItem {
    private String name;
    private String category;
    private double price;
    private int timesOrdered = 0;  // track popularity

    MenuItem(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    String getName()       { return name; }
    String getCategory()   { return category; }
    double getPrice()      { return price; }
    int getTimesOrdered()  { return timesOrdered; }

    void incrementOrders(int qty) { timesOrdered += qty; }

    public String toString() {
        return String.format("%-22s %-12s $%.2f", name, category, price);
    }

    String toDetailedString() {
        return String.format("%-22s %-12s $%.2f (ordered %d times)", name, category, price, timesOrdered);
    }
}

// ---------------------------------------------------------------
// Holds the full list of menu items, saves/loads to menu_data.txt
// ---------------------------------------------------------------
class Menu {
    private ArrayList<MenuItem> items = new ArrayList<>();
    private static final String FILE = "menu_data.txt";

    Menu() {
        loadFromFile();
    }

    void addItem(MenuItem item) {
        items.add(item);
        saveToFile();
    }

    void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            saveToFile();
            System.out.println("Item removed.");
        } else {
            System.out.println("Invalid number.");
        }
    }

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

    MenuItem getItem(int index) {
        if (index >= 0 && index < items.size()) return items.get(index);
        return null;
    }

    ArrayList<MenuItem> searchByCategory(String category) {
        ArrayList<MenuItem> result = new ArrayList<>();
        for (MenuItem item : items) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    MenuItem searchByName(String name) {
        for (MenuItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    int size() { return items.size(); }

    // saves each item as:  Name|Category|Price|TimesOrdered
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

    // reads menu_data.txt line by line
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
            // file doesn't exist yet, that is fine
        }
    }
}

// ---------------------------------------------------------------
// One line inside an order  (e.g. 2x Pasta)
// ---------------------------------------------------------------
class OrderItem {
    private MenuItem menuItem;
    private int quantity;
    private double discount = 0.0;  // discount as percentage (0-100)

    OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    MenuItem getMenuItem()  { return menuItem; }
    int getQuantity()       { return quantity; }
    double getDiscount()    { return discount; }

    void setDiscount(double discountPercent) {
        if (discountPercent >= 0 && discountPercent <= 100) {
            this.discount = discountPercent;
        }
    }

    double getSubtotal() {
        double subtotal = menuItem.getPrice() * quantity;
        double discountAmount = subtotal * (discount / 100.0);
        return subtotal - discountAmount;
    }

    double getOriginalSubtotal() {
        return menuItem.getPrice() * quantity;
    }

    public String toString() {
        if (discount > 0) {
            return String.format("  %dx %-22s @ $%.2f = $%.2f (%.0f%% off)",
                quantity, menuItem.getName(), menuItem.getPrice(), getSubtotal(), discount);
        }
        return String.format("  %dx %-22s @ $%.2f = $%.2f",
                quantity, menuItem.getName(), menuItem.getPrice(), getSubtotal());
    }
}

// ---------------------------------------------------------------
// One complete customer order with a unique ID
// ---------------------------------------------------------------
class Order {
    private static int nextId = 1;   // auto-increment counter

    private int id;
    private ArrayList<OrderItem> items = new ArrayList<>();
    private boolean paid = false;
    private boolean cancelled = false;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt = null;

    // used when creating a new order
    Order() {
        this.id = nextId++;
        this.createdAt = LocalDateTime.now();
    }

    // used when loading an old order from file
    Order(int id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        if (id >= nextId) nextId = id + 1;   // keep counter ahead
    }

    int getID()                          { return id; }
    ArrayList<OrderItem> getItems()      { return items; }
    boolean isPaid()                     { return paid; }
    boolean isCancelled()                { return cancelled; }
    LocalDateTime getCreatedAt()         { return createdAt; }
    LocalDateTime getPaidAt()            { return paidAt; }

    void addItem(OrderItem oi)  { items.add(oi); }
    void markPaid()             { 
        paid = true; 
        paidAt = LocalDateTime.now();
    }

    void cancel() {
        if (!paid) {
            cancelled = true;
            items.clear();
            System.out.println("Order #" + id + " has been cancelled.");
        } else {
            System.out.println("Cannot cancel a paid order.");
        }
    }

    double getTotal() {
        double t = 0;
        for (OrderItem oi : items) t += oi.getSubtotal();
        return t;
    }

    double getOriginalTotal() {
        double t = 0;
        for (OrderItem oi : items) t += oi.getOriginalSubtotal();
        return t;
    }

    void display() {
        System.out.println("  --- Order #" + id + " ---");
        if (cancelled) {
            System.out.println("    [CANCELLED]");
            return;
        }
        if (items.isEmpty()) {
            System.out.println("    (empty)");
        } else {
            for (OrderItem oi : items) System.out.println("  " + oi);
        }
        if (getOriginalTotal() > getTotal()) {
            System.out.printf("    Subtotal: $%.2f%n", getOriginalTotal());
            System.out.printf("    Total   : $%.2f   [%s]%n", getTotal(), paid ? "PAID" : "UNPAID");
        } else {
            System.out.printf("    Total   : $%.2f   [%s]%n", getTotal(), paid ? "PAID" : "UNPAID");
        }
    }
}

// ---------------------------------------------------------------
// Manages all orders; saves/loads to sales_data.txt
// ---------------------------------------------------------------
class Sales {
    private ArrayList<Order> orders = new ArrayList<>();
    public Order current = null;          // the order being built right now (public for menu access)
    private Menu menu;
    private static final String FILE = "sales_data.txt";

    Sales(Menu menu) {
        this.menu = menu;
        loadFromFile();
    }

    // start a new order
    void newOrder() {
        if (current != null && !current.isPaid() && !current.isCancelled()) {
            System.out.println("Finish or pay the current order first (Order #" + current.getID() + ").");
            return;
        }
        current = new Order();
        orders.add(current);
        System.out.println("New Order #" + current.getID() + " started.");
    }

    // add a menu item to the current order
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
        mi.incrementOrders(qty);
        System.out.println("Added " + qty + "x " + mi.getName());
        saveToFile();
    }

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

    void viewCurrent() {
        if (current == null) System.out.println("No active order.");
        else current.display();
    }

    // mark current order as paid
    void pay() {
        if (current == null || current.isPaid() || current.isCancelled()) {
            System.out.println("Nothing to pay.");
            return;
        }
        if (current.getItems().isEmpty()) {
            System.out.println("Order is empty, add items first.");
            return;
        }
        current.markPaid();
        System.out.printf("Payment of $%.2f received for Order #%d.%n", current.getTotal(), current.getID());
        current = null;
        saveToFile();
    }

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

    void viewAll() {
        if (orders.isEmpty()) {
            System.out.println("  No orders yet.");
            return;
        }
        for (Order o : orders) o.display();
    }

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

    void summary() {
        double revenue = 0;
        double discountGiven = 0;
        int paid = 0, unpaid = 0, cancelled = 0;
        int totalItems = 0;

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

    // file format:
    //   ORDER|id|paid|cancelled
    //   ITEM|name|category|price|qty|discount
    //   END_ORDER
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

    void loadFromFile() {
        orders.clear();
        try {
            Scanner sc = new Scanner(new File(FILE));
            Order temp = null;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("ORDER|")) {
                    String[] p = line.split("\\|");
                    temp = new Order(Integer.parseInt(p[1]));
                    if (Boolean.parseBoolean(p[2])) temp.markPaid();
                    if (p.length >= 4 && Boolean.parseBoolean(p[3])) {
                        temp.cancel();
                    }

                } else if (line.startsWith("ITEM|") && temp != null) {
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
            // if the last order is unpaid and not cancelled, set it as current
            if (!orders.isEmpty()) {
                Order last = orders.get(orders.size() - 1);
                if (!last.isPaid() && !last.isCancelled()) current = last;
            }
        } catch (FileNotFoundException e) {
            // no file yet
        }
    }
}

// ---------------------------------------------------------------
// MAIN CLASS  –  all user input is handled here
// ---------------------------------------------------------------
public class RestaurantMain {

    static Scanner sc = new Scanner(System.in);
    static Menu  menu;
    static Sales sales;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Restaurant Management System");
        System.out.println("========================================\n");

        menu  = new Menu();
        sales = new Sales(menu);

        boolean run = true;
        while (run) {
            System.out.println("\n--- Main Menu ---");
            System.out.println(" 1. Menu Management");
            System.out.println(" 2. Sales / Orders");
            System.out.println(" 3. Exit");
            int ch = readInt("Choice: ");

            if      (ch == 1) menuSection();
            else if (ch == 2) salesSection();
            else if (ch == 3) { System.out.println("Goodbye."); run = false; }
            else System.out.println("Invalid choice.");
        }
        sc.close();
    }

    // ---------- Menu Management sub-menu ----------
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

    // ---------- Sales / Orders sub-menu ----------
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

    // ---- safe input helpers ----
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

    static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Cannot be empty.");
        }
    }
}