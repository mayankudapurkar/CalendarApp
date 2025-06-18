import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Calendar;
import javax.swing.*;

public class CalendarApp implements ActionListener {
    private JFrame frame;
    private JComboBox<String> monthCombo;
    private JTextField yearField;
    private JPanel calendarPanel;

    private final String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private Connection conn;

    public CalendarApp() {
        connectDatabase();
        createTableIfNeeded();

        frame = new JFrame("Calendar App");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel();
        monthCombo = new JComboBox<>(months);
        yearField = new JTextField(5);
        yearField.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        monthCombo.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));

        JButton showBtn = new JButton("Show Calendar");
        showBtn.addActionListener(this);

        top.add(new JLabel("Month:"));
        top.add(monthCombo);
        top.add(new JLabel("Year:"));
        top.add(yearField);
        top.add(showBtn);

        calendarPanel = new JPanel(new GridLayout(0, 7));
        frame.add(top, BorderLayout.NORTH);
        frame.add(calendarPanel, BorderLayout.CENTER);

        frame.setVisible(true);
        drawCalendar();
    }

    public void actionPerformed(ActionEvent e) {
        drawCalendar();
    }

    private void drawCalendar() {
        calendarPanel.removeAll();

        int month = monthCombo.getSelectedIndex();
        int year = Integer.parseInt(yearField.getText());

        for (String d : new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(l.getFont().deriveFont(Font.BOLD));
            calendarPanel.add(l);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        int firstDay = cal.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i < firstDay; i++) calendarPanel.add(new JLabel(""));

        for (int day = 1; day <= daysInMonth; day++) {
            String key = year + "-" + (month + 1) + "-" + day;
            JButton btn = new JButton(String.valueOf(day));

            if (hasEvents(key)) {
                btn.setBackground(Color.CYAN);
                btn.setOpaque(true);
                btn.setToolTipText(getEventsTooltip(key));
            }

            btn.addActionListener(e -> editEvents(key, btn));
            calendarPanel.add(btn);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void editEvents(String key, JButton btn) {
        String current = getEventsTooltip(key);

        JTextArea area = new JTextArea(current, 8, 25);
        int option = JOptionPane.showConfirmDialog(frame, new JScrollPane(area),
                "Events on " + key + " (one per line)", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            deleteEvents(key);
            for (String line : area.getText().split("\\R")) {
                if (!line.trim().isEmpty()) insertEvent(key, line.trim());
            }

            if (hasEvents(key)) {
                btn.setBackground(Color.CYAN);
                btn.setToolTipText(getEventsTooltip(key));
            } else {
                btn.setBackground(null);
                btn.setToolTipText(null);
            }
        }
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:calendar.db");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to database.");
            System.exit(1);
        }
    }

    private void createTableIfNeeded() {
        String sql = "CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT NOT NULL," +
                "description TEXT NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertEvent(String date, String description) {
        String sql = "INSERT INTO events (date, description) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteEvents(String date) {
        String sql = "DELETE FROM events WHERE date = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean hasEvents(String date) {
        String sql = "SELECT COUNT(*) FROM events WHERE date = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getEventsTooltip(String date) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT description FROM events WHERE date = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(rs.getString("description")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        new CalendarApp();
    }
}
