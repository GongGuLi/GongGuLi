package front;

import back.user.UserDAO;
import back.user.UserDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class FindUserID extends JDialog {
    UserDAO userDAO = new UserDAO();
    private JTextField nameText;
    private JTextField birthText;
    private JTextField phoneNumberText;
    private Font f1 = new Font("SUITE", Font.BOLD, 16);
    private Font f2 = new Font("SUITE", Font.BOLD, 9);

    public FindUserID(JFrame parentFrame) {
        setTitle("아이디 찾기");
        setSize(400, 300);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame); // 부모 프레임 중앙에 표시

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // 이름 입력 필드
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setBounds(30, 50, 100, 30);
        panel.add(nameLabel);
        nameLabel.setFont(f1);

        nameText = new JTextField(20);
        nameText.setBounds(150, 50, 200, 30);
        panel.add(nameText);
        nameText.setFont(f2);

        // 생년월일 입력 필드
        JLabel birthLabel = new JLabel("생년월일:");
        birthLabel.setBounds(30, 100, 100, 30);
        panel.add(birthLabel);
        birthLabel.setFont(f1);

        birthText = new JTextField(20);
        birthText.setBounds(150, 100, 200, 30);
        panel.add(birthText);
        birthText.setFont(f2);

        // 핸드폰 번호 입력 필드
        JLabel phoneNumberLabel = new JLabel("핸드폰 번호:");
        phoneNumberLabel.setBounds(30, 150, 100, 30);
        panel.add(phoneNumberLabel);
        phoneNumberLabel.setFont(f1);

        phoneNumberText = new JTextField(20);
        phoneNumberText.setBounds(150, 150, 200, 30);
        panel.add(phoneNumberText);
        phoneNumberText.setFont(f2);

        // 아이디 찾기 버튼
        JButton findIdButton = new RoundedButton("아이디찾기");
        findIdButton.setBounds(30, 220, 150, 30);
        panel.add(findIdButton);
        findIdButton.setFont(f1);

        findIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    String name = nameText.getText();
                    String birth = birthText.getText();
                    String phoneNumber = phoneNumberText.getText();
                    System.out.println(name + " " + birth + " " + phoneNumber);
                    UserDTO userDTO = new UserDTO();
                    userDTO.setName(name);
                    userDTO.setBirth(birth);
                    userDTO.setPhoneNum(phoneNumber);
                    System.out.println(userDAO.findID(userDTO));
                }
            }
        });
        add(panel);
        setVisible(true);
    }

    private boolean validateInput() {
        String name = nameText.getText().trim();
        String birth = birthText.getText().trim();
        String phoneNumber = phoneNumberText.getText().trim();

        if (name.isEmpty() || birth.isEmpty() || phoneNumber.isEmpty()) {
            showErrorDialog("모든 항목을 입력하세요.");
            return false;
        }

        return true;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "입력 오류", JOptionPane.ERROR_MESSAGE);
    }
}
