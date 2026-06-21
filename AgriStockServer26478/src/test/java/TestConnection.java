import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/agristock_rw_db",
                    "postgres",
                    "asd123"
            );
            System.out.println("✅ PostgreSQL connection successful!");
            conn.close();
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }
}