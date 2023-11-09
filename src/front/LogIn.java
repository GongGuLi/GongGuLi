package front;

import back.user.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogIn extends JFrame {
    FrontSetting fs = new FrontSetting();

    private Color c1 = new Color(255, 240, 227);
    private Color c3 = new Color(255, 255, 255);

    private Font f1 = new Font("SUITE", Font.BOLD, 16);
    private Font f2 = new Font("SUITE", Font.BOLD, 9);

    private JTextField idText;
    private JPasswordField passwordText;

    UserDAO userDAO = new UserDAO();
    public LogIn() {
        setLoginFrame();
        setLeftPanel();
        setRightPanel();
        setCenterPanel();
        setVisible(true);
    }

    private void setLoginFrame() {
        setTitle("로그인");
        setSize(1120, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fs.FrameSetting(this);
    }

    private void setLeftPanel() {
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBounds(0, 0, 560, 700);
        leftPanel.setBackground(c1);

        ImageIcon imageIcon = new ImageIcon("C:\\Users\\user\\Desktop\\찐공구리.png");
        Image image = imageIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon = new ImageIcon(image);

        JLabel imageLabel = new JLabel(scaledImageIcon);
        imageLabel.setBounds(25, 10, 300, 300);
        leftPanel.add(imageLabel);
        add(leftPanel);
    }

    private void setRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(1070, 0, 50, 700);
        rightPanel.setBackground(c1);

        add(rightPanel);
    }

    public void setCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(c3);
        add(panel);

        // 아이디 입력 필드
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(600, 365, 100, 30);
        panel.add(idLabel);
        idLabel.setFont(f1);

        idText = new JTextField(20);
        idText.setBounds(700, 365, 200, 30);
        panel.add(idText);
        idText.setFont(f2);

        // 비밀번호 입력 필드
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(600, 405, 100, 30);
        panel.add(passwordLabel);
        passwordLabel.setFont(f1);

        passwordText = new JPasswordField(20);
        passwordText.setBounds(700, 405, 200, 30);
        panel.add(passwordText);
        passwordText.setFont(f2);

        // 로그인 버튼
        JButton loginButton = new RoundedButton("로그인");
        loginButton.setBounds(910, 365, 100, 70);
        panel.add(loginButton);
        loginButton.setFont(f1);

        // 회원가입 버튼
        JButton registerButton = new RoundedButton("회원가입");
        registerButton.setBounds(700, 445, 200, 30);
        panel.add(registerButton);
        registerButton.setFont(f1);

        // 아이디 찾기 버튼
        JButton idSearchButton = new JButton("아이디 찾기");
        idSearchButton.setBounds(700, 485, 100, 30);
        idSearchButton.setFont(f2);
        idSearchButton.setOpaque(false);
        idSearchButton.setContentAreaFilled(false);
        idSearchButton.setBorderPainted(false);
        panel.add(idSearchButton);

        JLabel verticalLineLabel = new JLabel("|");
        panel.add(verticalLineLabel);
        verticalLineLabel.setBounds(799, 484, 100, 30);

        // 비밀번호 찾기 버튼
        JButton pwSearchButton = new JButton("비밀번호 찾기");
        pwSearchButton.setBounds(800, 485, 100, 30);
        pwSearchButton.setFont(f2);
        pwSearchButton.setOpaque(false);
        pwSearchButton.setContentAreaFilled(false);
        pwSearchButton.setBorderPainted(false);
        panel.add(pwSearchButton);

        // 로그인 버튼 클릭 시
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    String id = idText.getText();
                    char[] password = passwordText.getPassword();
                    System.out.println("아이디: " + id);
                    System.out.println("비밀번호: " + new String(password));

                    switch (userDAO.logInCheck(id, new String(password))) {
                        case 1 -> {
                            userDAO.logIn(id);
                            dispose();
                        }
                        case 0 -> System.out.println("비밀번호 불일치.");
                        case -1 -> System.out.println("아이디가 존재하지 않음");
                        case -2 -> System.out.println("DB 오류 발생");
                    }
                }
            }
        });

        // 회원가입 버튼 클릭 시
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SignUp();
            }
        });

        // 아이디 찾기 버튼 클릭 시
        idSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 아이디 찾기 창을 띄우기
                new FindUserID(LogIn.this);
            }
        });

        // 비밀번호 찾기 버튼 클릭 시
        pwSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 비밀번호 찾기 창을 띄우기
                new FindPassword(LogIn.this);
            }
        });
    }

    private boolean validateInput() {
        String username = idText.getText().trim();
        char[] password = passwordText.getPassword();

        if (username.isBlank()) {
            showErrorDialog("아이디를 입력해 주세요.");
            return false;
        }

        if (String.valueOf(password).isBlank()) {
            showErrorDialog("비밀번호를 입력해 주세요.");
            return false;
        }

        return true;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "입력 오류", JOptionPane.ERROR_MESSAGE);
    }
}