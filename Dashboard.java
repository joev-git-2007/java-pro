import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class Dashboard extends JFrame {

    private static final Color PURPLE = new Color(124, 77, 255);
    private static final Color LIGHT_BG = new Color(245, 245, 250);

    private FitTrackService service;
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);

    private JProgressBar stepsBar, calBar, waterBar, exBar;
    private JLabel bmiLabel, bmiStatusLabel, goalProgressLabel, motivationLabel;

    public Dashboard(FitTrackService service) {
        this.service = service;

        setTitle("FitTrack - Dashboard");
        setSize(380, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        cards.add(buildHomePanel(), "Home");
        cards.add(buildAddPanel(), "Add");
        cards.add(buildHistoryPanel(), "History");
        cards.add(buildGoalsPanel(), "Goals");
        cards.add(buildProfilePanel(), "Profile");

        add(cards, BorderLayout.CENTER);
        add(buildBottomNav(), BorderLayout.SOUTH);

        refreshHome();
    }

    private JPanel buildBottomNav() {
    JPanel nav = new JPanel(new GridLayout(1, 5));
    String[] names = {"Home", "Add", "History", "Goals", "Trainer", "Profile"};
    String[] icons = {"🏠", "➕", "📜", "🎯", "🏋", "👤"};
    for (int i = 0; i < names.length; i++) {
        String n = names[i];
        JButton b = new JButton(icons[i]);
        b.setFont(new Font("SansSerif", Font.PLAIN, 20));
        b.setToolTipText(n);
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.addActionListener(e -> {
            cardLayout.show(cards, n);
            cards.add(buildTrainerPanel(), "Trainer");
            if (n.equals("Home")) refreshHome();
            if (n.equals("Goals")) refreshGoals();
        });
        nav.add(b);
    }
    return nav;
}
    private JPanel buildHomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel header = sectionHeader("Daily Overview");
        panel.add(header);

        FitTrackService.DailyActivity t = service.getToday();

        stepsBar = metricBar();
        calBar = metricBar();
        waterBar = metricBar();
        exBar = metricBar();

        panel.add(metricCard("Steps", stepsBar));
        panel.add(metricCard("Calories", calBar));
        panel.add(metricCard("Water (L)", waterBar));
        panel.add(metricCard("Exercise (min)", exBar));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(sectionHeader("Health & Goal"));

        JPanel bmiCard = card();
        bmiLabel = new JLabel();
        bmiStatusLabel = new JLabel();
        bmiLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bmiCard.add(bmiLabel);
        bmiCard.add(bmiStatusLabel);
        panel.add(bmiCard);

        JPanel goalCard = card();
        goalProgressLabel = new JLabel();
        goalCard.add(goalProgressLabel);
        panel.add(goalCard);

        motivationLabel = new JLabel();
        motivationLabel.setForeground(PURPLE);
        motivationLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        motivationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(motivationLabel);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private void refreshHome() {
        FitTrackService.DailyActivity t = service.getToday();
        setBar(stepsBar, t.getSteps(), t.getStepGoal());
        setBar(calBar, t.getCalories(), t.getCalorieGoal());
        setBar(waterBar, (int) Math.round(t.getWaterLiters() * 10), (int) Math.round(t.getWaterGoalLiters() * 10));
        setBar(exBar, t.getExerciseMinutes(), t.getExerciseGoalMinutes());

        FitTrackService.User u = service.getCurrentUser();
        bmiLabel.setText(String.format("BMI: %.1f", u.getBmi()));
        bmiStatusLabel.setText("Status: " + u.getBmiStatus());

        FitTrackService.Goal g = service.getCurrentGoal();
        double pct = service.getGoalProgressPercent();
        goalProgressLabel.setText(String.format("<html>Goal: %s<br>Current Weight: %.0f kg &nbsp; Target: %.0f kg<br>Progress: %.0f%%</html>",
                g.getGoalType(), u.getWeightKg(), g.getTargetWeightKg(), pct));

        motivationLabel.setText(service.getMotivationalMessage());
    }

    private void setBar(JProgressBar bar, int value, int max) {
        bar.setMaximum(Math.max(max, 1));
        bar.setValue(Math.min(value, max));
        bar.setString(value + " / " + max);
    }

  
    private JPanel buildAddPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(sectionHeader("Add Activity"));

        JTextField stepsField = new JTextField();
        JTextField calField = new JTextField();
        JTextField waterField = new JTextField();
        JTextField exField = new JTextField();

        panel.add(fieldRow("Steps to add:", stepsField));
        panel.add(fieldRow("Calories to add:", calField));
        panel.add(fieldRow("Water to add (L):", waterField));
        panel.add(fieldRow("Exercise to add (min):", exField));

        JButton save = new JButton("Save Entry");
        save.setBackground(PURPLE);
        save.setForeground(Color.WHITE);
        save.setAlignmentX(Component.LEFT_ALIGNMENT);
        save.addActionListener(e -> {
            try {
                FitTrackService.DailyActivity t = service.getToday();
                if (!stepsField.getText().isBlank()) t.addSteps(Integer.parseInt(stepsField.getText().trim()));
                if (!calField.getText().isBlank()) t.addCalories(Integer.parseInt(calField.getText().trim()));
                if (!waterField.getText().isBlank()) t.addWater(Double.parseDouble(waterField.getText().trim()));
                if (!exField.getText().isBlank()) t.addExercise(Integer.parseInt(exField.getText().trim()));
                stepsField.setText(""); calField.setText(""); waterField.setText(""); exField.setText("");
                JOptionPane.showMessageDialog(this, "Activity saved!");
                cardLayout.show(cards, "Home");
                refreshHome();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(save);
        return panel;
    }

    private JPanel fieldRow(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(LIGHT_BG);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(300, 28));
        p.add(l);
        p.add(field);
        p.add(Box.createRigidArea(new Dimension(0, 8)));
        return p;
    }

   
    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(sectionHeader("Activity History"));

        List<FitTrackService.DailyActivity> history = service.getHistory();
        for (FitTrackService.DailyActivity d : history) {
            JPanel c = card();
            JLabel date = new JLabel(d.getDate());
            date.setFont(new Font("SansSerif", Font.BOLD, 14));
            JLabel details = new JLabel(String.format(
                    "<html>Steps: %d/%d &nbsp; Calories: %d/%d<br>Water: %.1fL/%.1fL &nbsp; Exercise: %d/%d min</html>",
                    d.getSteps(), d.getStepGoal(), d.getCalories(), d.getCalorieGoal(),
                    d.getWaterLiters(), d.getWaterGoalLiters(), d.getExerciseMinutes(), d.getExerciseGoalMinutes()));
            c.add(date);
            c.add(details);
            panel.add(c);
        }
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }


    private JPanel goalsPanel;
    private JLabel goalTypeLabel, goalTargetLabel, goalCurrentLabel, goalStepsLabel, goalPctLabel;

    private JPanel buildGoalsPanel() {
        goalsPanel = new JPanel();
        goalsPanel.setLayout(new BoxLayout(goalsPanel, BoxLayout.Y_AXIS));
        goalsPanel.setBackground(LIGHT_BG);
        goalsPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        goalsPanel.add(sectionHeader("My Goal"));

        JPanel c = card();
        goalTypeLabel = new JLabel();
        goalTargetLabel = new JLabel();
        goalCurrentLabel = new JLabel();
        goalStepsLabel = new JLabel();
        goalPctLabel = new JLabel();
        c.add(goalTypeLabel);
        c.add(goalTargetLabel);
        c.add(goalCurrentLabel);
        c.add(goalStepsLabel);
        c.add(goalPctLabel);
        goalsPanel.add(c);

        JButton edit = new JButton("Edit Goal");
        edit.setBackground(PURPLE);
        edit.setForeground(Color.WHITE);
        edit.setAlignmentX(Component.LEFT_ALIGNMENT);
        edit.addActionListener(e -> editGoal());
        goalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        goalsPanel.add(edit);
        return goalsPanel;
    }

    private void refreshGoals() {
        FitTrackService.Goal g = service.getCurrentGoal();
        FitTrackService.User u = service.getCurrentUser();
        goalTypeLabel.setText("Goal: " + g.getGoalType());
        goalTargetLabel.setText(String.format("Target Weight: %.0f kg", g.getTargetWeightKg()));
        goalCurrentLabel.setText(String.format("Current Weight: %.0f kg", u.getWeightKg()));
        goalStepsLabel.setText("Daily Steps Target: " + g.getDailyStepTarget());
        goalPctLabel.setText(String.format("Progress: %.0f%%", service.getGoalProgressPercent()));
    }

    private void editGoal() {
        FitTrackService.Goal g = service.getCurrentGoal();
        JTextField typeF = new JTextField(g.getGoalType());
        JTextField targetF = new JTextField(String.valueOf(g.getTargetWeightKg()));
        JTextField stepsF = new JTextField(String.valueOf(g.getDailyStepTarget()));
        JPanel p = new JPanel(new GridLayout(3, 2, 5, 5));
        p.add(new JLabel("Goal Type:")); p.add(typeF);
        p.add(new JLabel("Target Weight (kg):")); p.add(targetF);
        p.add(new JLabel("Daily Step Target:")); p.add(stepsF);
        int r = JOptionPane.showConfirmDialog(this, p, "Edit Goal", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            try {
                g.setGoalType(typeF.getText().trim());
                g.setTargetWeightKg(Double.parseDouble(targetF.getText().trim()));
                g.setDailyStepTarget(Integer.parseInt(stepsF.getText().trim()));
                refreshGoals();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private JLabel trainerAdviceLabel;

private JPanel buildTrainerPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(LIGHT_BG);
    panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    panel.add(sectionHeader("Trainer's Advice"));

    JPanel c = card();
    trainerAdviceLabel = new JLabel();
    trainerAdviceLabel.setVerticalAlignment(SwingConstants.TOP);
    c.add(trainerAdviceLabel);
    panel.add(c);

    JButton refresh = new JButton("Refresh Advice");
    refresh.setBackground(PURPLE);
    refresh.setForeground(Color.WHITE);
    refresh.setAlignmentX(Component.LEFT_ALIGNMENT);
    refresh.addActionListener(e -> refreshTrainer());
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(refresh);

    refreshTrainer();

    JScrollPane scroll = new JScrollPane(panel);
    scroll.setBorder(null);
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.add(scroll, BorderLayout.CENTER);
    return wrapper;
}

private void refreshTrainer() {
    String advice = service.getTrainerAdvice().replace("\n", "<br>");
    trainerAdviceLabel.setText("<html><div style='width:280px'>" + advice + "</div></html>");
}
  
    private JLabel pNameLabel, pAgeLabel, pHeightLabel, pWeightLabel, pMemberLabel;

    private JPanel buildProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(sectionHeader("My Profile"));

        JPanel c = card();
        pNameLabel = new JLabel();
        pAgeLabel = new JLabel();
        pHeightLabel = new JLabel();
        pWeightLabel = new JLabel();
        pMemberLabel = new JLabel();
        c.add(pNameLabel);
        c.add(pAgeLabel);
        c.add(pHeightLabel);
        c.add(pWeightLabel);
        c.add(pMemberLabel);
        panel.add(c);
        refreshProfileLabels();

        JButton edit = new JButton("Edit Profile");
        edit.setBackground(PURPLE);
        edit.setForeground(Color.WHITE);
        edit.setAlignmentX(Component.LEFT_ALIGNMENT);
        edit.addActionListener(e -> editProfile());
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(edit);

        return panel;
    }

    private void refreshProfileLabels() {
        FitTrackService.User u = service.getCurrentUser();
        pNameLabel.setText("Name: " + u.getName());
        pAgeLabel.setText("Age: " + u.getAge());
        pHeightLabel.setText("Height: " + u.getHeightCm() + " cm");
        pWeightLabel.setText(String.format("Weight: %.0f kg", u.getWeightKg()));
        pMemberLabel.setText("Member Since: " + u.getMemberSince());
    }

    private void editProfile() {
        FitTrackService.User u = service.getCurrentUser();
        JTextField nameF = new JTextField(u.getName());
        JTextField ageF = new JTextField(String.valueOf(u.getAge()));
        JTextField heightF = new JTextField(String.valueOf(u.getHeightCm()));
        JTextField weightF = new JTextField(String.valueOf(u.getWeightKg()));
        JPanel p = new JPanel(new GridLayout(4, 2, 5, 5));
        p.add(new JLabel("Name:")); p.add(nameF);
        p.add(new JLabel("Age:")); p.add(ageF);
        p.add(new JLabel("Height (cm):")); p.add(heightF);
        p.add(new JLabel("Weight (kg):")); p.add(weightF);
        int r = JOptionPane.showConfirmDialog(this, p, "Edit Profile", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            try {
                u.setName(nameF.getText().trim());
                u.setAge(Integer.parseInt(ageF.getText().trim()));
                u.setHeightCm(Integer.parseInt(heightF.getText().trim()));
                u.setWeightKg(Double.parseDouble(weightF.getText().trim()));
                refreshProfileLabels();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

   
    private JLabel sectionHeader(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setForeground(PURPLE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        return l;
    }

    private JPanel card() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(1000, 200));
        p.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(LIGHT_BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        wrap.add(p, BorderLayout.CENTER);
        return p;
    }

    private JPanel metricCard(String label, JProgressBar bar) {
        JPanel c = card();
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        c.add(l);
        c.add(bar);
        return c;
    }

    private JProgressBar metricBar() {
        JProgressBar bar = new JProgressBar();
        bar.setStringPainted(true);
        bar.setForeground(PURPLE);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        bar.setMaximumSize(new Dimension(1000, 20));
        return bar;
    }
}