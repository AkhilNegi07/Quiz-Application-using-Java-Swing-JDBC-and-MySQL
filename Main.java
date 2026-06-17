import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

 class QuizGUI {

    // 🔥 GLOBAL VARIABLES
    static String category = "";
    static String correctAnswer = "";
    static int score = 0;
    static int count = 0;
    static int totalQuestions = 10;

    static String emailGlobal = "";

    static JLabel questionLabel;
    static JButton btnA, btnB, btnC, btnD;

    static JFrame frame;

    public static void main(String[] args) {

        frame = new JFrame("Quiz App");
        frame.setSize(500, 400);
        frame.setLayout(null);

        showLogin();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // 🎨 THEME METHOD
    static void setTheme(Color bgColor) {
        frame.getContentPane().setBackground(bgColor);
        frame.setLayout(null);
    }

    // 🔐 LOGIN SCREEN
    static void showLogin() {

        frame.getContentPane().removeAll();
        setTheme(new Color(30, 30, 30)); // dark theme

        JLabel title = new JLabel("User Login", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setBounds(150, 20, 200, 30);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(100, 80, 100, 25);

        JTextField name = new JTextField();
        name.setBounds(180, 80, 180, 25);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(100, 120, 100, 25);

        JTextField email = new JTextField();
        email.setBounds(180, 120, 180, 25);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(100, 160, 100, 25);

        JPasswordField pass = new JPasswordField();
        pass.setBounds(180, 160, 180, 25);

        JButton btn = new JButton("Start Quiz");
        btn.setBounds(170, 220, 140, 30);

        frame.add(title);
        frame.add(nameLabel);
        frame.add(name);
        frame.add(emailLabel);
        frame.add(email);
        frame.add(passLabel);
        frame.add(pass);
        frame.add(btn);

        btn.addActionListener(e -> {

            String n = name.getText();
            String em = email.getText();
            String p = new String(pass.getPassword());

            if (n.isEmpty() || em.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields required 😤");
            } else {
                emailGlobal = em;
                saveUser(n, em, p);
                showCategories();
            }
        });

        frame.repaint();
    }

    // 🏠 CATEGORY SCREEN
    static void showCategories() {

        frame.getContentPane().removeAll();
        setTheme(new Color(40, 60, 100)); // blue theme

        JLabel title = new JLabel("Select Category", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setBounds(100, 20, 300, 30);

        JButton marvel = new JButton("Marvel");
        JButton java = new JButton("Java");
        JButton cpp = new JButton("C++");
        JButton hp = new JButton("Harry Potter");

        marvel.setBounds(150, 80, 200, 30);
        java.setBounds(150, 120, 200, 30);
        cpp.setBounds(150, 160, 200, 30);
        hp.setBounds(150, 200, 200, 30);

        frame.add(title);
        frame.add(marvel);
        frame.add(java);
        frame.add(cpp);
        frame.add(hp);

        ActionListener start = e -> {

            category = ((JButton) e.getSource()).getText().toLowerCase();

            if (category.equals("harry potter")) category = "hp";
            if (category.equals("c++")) category = "cpp";

            score = 0;
            count = 0;

            showQuiz();
            loadQuestion();
        };

        marvel.addActionListener(start);
        java.addActionListener(start);
        cpp.addActionListener(start);
        hp.addActionListener(start);

        frame.repaint();
    }

    // 🎯 QUIZ SCREEN
    static void showQuiz() {

        frame.getContentPane().removeAll();

        // 🎨 CATEGORY THEMES
        if (category.equals("marvel")) {
            setTheme(new Color(150, 0, 0)); // red
        }
        else if (category.equals("java")) {
            setTheme(new Color(200, 100, 0)); // orange
        }
        else if (category.equals("cpp")) {
            setTheme(new Color(0, 70, 140)); // blue
        }
        else if (category.equals("hp")) {
            setTheme(new Color(80, 0, 100)); // purple
        }

        questionLabel = new JLabel("", JLabel.CENTER);
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setBounds(50, 20, 400, 30);

        btnA = new JButton();
        btnB = new JButton();
        btnC = new JButton();
        btnD = new JButton();

        btnA.setBounds(50, 80, 150, 30);
        btnB.setBounds(300, 80, 150, 30);
        btnC.setBounds(50, 130, 150, 30);
        btnD.setBounds(300, 130, 150, 30);

        frame.add(questionLabel);
        frame.add(btnA);
        frame.add(btnB);
        frame.add(btnC);
        frame.add(btnD);

        ActionListener check = e -> {

            JButton b = (JButton) e.getSource();
            String ans = "";

            if (b == btnA) ans = "A";
            if (b == btnB) ans = "B";
            if (b == btnC) ans = "C";
            if (b == btnD) ans = "D";

            count++;

            if (ans.equals(correctAnswer)) score++;

            if (count >= totalQuestions) {
                saveScore();
                JOptionPane.showMessageDialog(frame, "Final Score: " + score + "/" + totalQuestions);
                System.exit(0);
            } else {
                loadQuestion();
            }
        };

        btnA.addActionListener(check);
        btnB.addActionListener(check);
        btnC.addActionListener(check);
        btnD.addActionListener(check);

        frame.repaint();
    }

    // 🔥 LOAD QUESTION FROM DB
    static void loadQuestion() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/quizdb",
                    "root",
                    "12345678@87654321"
            );

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM questions WHERE category=? ORDER BY RAND() LIMIT 1"
            );

            ps.setString(1, category);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                questionLabel.setText(rs.getString("question"));
                btnA.setText(rs.getString("optionA"));
                btnB.setText(rs.getString("optionB"));
                btnC.setText(rs.getString("optionC"));
                btnD.setText(rs.getString("optionD"));
                correctAnswer = rs.getString("correct");
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 💾 SAVE USER
    static void saveUser(String name, String email, String pass) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/quizdb",
                    "root",
                    "12345678@87654321"
            );

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users(name, email, password, category, score) VALUES (?, ?, ?, '', 0)"
            );

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);

            ps.executeUpdate();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🏆 SAVE SCORE
    static void saveScore() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/quizdb",
                    "root",
                    "12345678@87654321"
            );

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE users SET category=?, score=? WHERE email=?"
            );

            ps.setString(1, category);
            ps.setInt(2, score);
            ps.setString(3, emailGlobal);

            ps.executeUpdate();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}