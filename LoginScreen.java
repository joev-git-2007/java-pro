import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JFrame {

    private static final Color PURPLE = new Color(124, 77, 255);
    private FitTrackService service = new FitTrackService();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMe;

    public LoginScreen() {
        setTitle("FitTrack - Login");
        setSize(360, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setBackground(PURPLE);
        top.setPreferredSize(new Dimension(360, 140));
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("FitTrack");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Track Daily Activities | Set Goals | Monitor Progress");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tagline.setForeground(Color.WHITE);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        top.add(Box.createVerticalGlue());
        top.add(title);
        top.add(Box.createRigidArea(new Dimension(0, 6)));
        top.add(tagline);
        top.add(Box.createVerticalGlue());
        add(top, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        usernameField = new JTextField("user");
        passwordField = new JPasswordField("1234");
        rememberMe = new JCheckBox("Remember Me");

        form.add(labeled("Username", usernameField));
        form.add(Box.createRigidArea(new Dimension(0, 12)));
        form.add(labeled("Password", passwordField));
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(300, 30));
        JButton forgot = new JButton("Forgot Password?");
        forgot.setBorderPainted(false);
        forgot.setContentAreaFilled(false);
        forgot.setForeground(PURPLE);
        row.add(rememberMe, BorderLayout.WEST);
        row.add(forgot, BorderLayout.EAST);
        form.add(row);

        form.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(PURPLE);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(300, 40));
        loginBtn.addActionListener(e -> doLogin());
        form.add(loginBtn);

        form.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton registerBtn = new JButton("Register");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(300, 40));
        registerBtn.addActionListener(e -> doRegister());
        form.add(registerBtn);

        add(form, BorderLayout.CENTER);
    }

    private JPanel labeled(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(labelText);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(300, 30));
        p.add(l);
        p.add(field);
        return p;
    }

    private void doLogin() {
        String u = usernameField.getText().trim();
        String pw = new String(passwordField.getPassword());
        if (service.login(u, pw)) {
            dispose();
            new Dashboard(service).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.\n(Try user / 1234)",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doRegister() {
        JTextField nf = new JTextField();
        JTextField uf = new JTextField();
        JPasswordField pf = new JPasswordField();
        JPanel p = new JPanel(new GridLayout(3, 2, 5, 5));
        p.add(new JLabel("Name:")); p.add(nf);
        p.add(new JLabel("Username:")); p.add(uf);
        p.add(new JLabel("Password:")); p.add(pf);
        int r = JOptionPane.showConfirmDialog(this, p, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            boolean ok = service.register(uf.getText().trim(), new String(pf.getPassword()), nf.getText().trim());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Registered! You can now log in.");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}