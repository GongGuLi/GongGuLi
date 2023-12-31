package front;

import back.response.ResponseCode;
import back.request.account.FindUserPasswordRequest;
import back.response.account.FindUserPasswordResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

class FindPassword extends JDialog {
    private JTextField nameText;
    private JTextField idText;
    private JTextField birthText;
    private JTextField phoneNumberText;
    private Font f1 = new Font("SUITE", Font.BOLD, 16);
    private Font f2 = new Font("SUITE", Font.BOLD, 9);
    private Color c1 = new Color(255, 240, 227);

    private Socket clientSocket = null;

    public FindPassword(JFrame parentFrame) {
        setTitle("비밀번호 찾기");
        setSize(400, 300);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame); // 부모 프레임 중앙에 표시
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(c1);

        // 이름 입력 필드
        JLabel nameLabel = new JLabel("이름");
        nameLabel.setBounds(30, 30, 100, 30);
        panel.add(nameLabel);
        nameLabel.setFont(f1);
        nameLabel.setHorizontalAlignment(JLabel.RIGHT);

        nameText = new JTextField(20);
        nameText.setBounds(160, 30, 180, 30);
        panel.add(nameText);
        nameText.setFont(f2);

        // 아이디 입력 필드
        JLabel idLabel = new JLabel("아이디");
        idLabel.setBounds(30, 70, 100, 30);
        panel.add(idLabel);
        idLabel.setFont(f1);
        idLabel.setHorizontalAlignment(JLabel.RIGHT);

        idText = new JTextField(20);
        idText.setBounds(160, 70, 180, 30);
        panel.add(idText);
        idText.setFont(f2);

        // 생년월일 입력 필드
        JLabel birthLabel = new JLabel("생년월일");
        birthLabel.setBounds(30, 110, 100, 30);
        panel.add(birthLabel);
        birthLabel.setFont(f1);
        birthLabel.setHorizontalAlignment(JLabel.RIGHT);

        birthText = new JTextField(20);
        birthText.setBounds(160, 110, 180, 30);
        panel.add(birthText);
        birthText.setFont(f2);

        // 핸드폰 번호 입력 필드
        JLabel phoneNumberLabel = new JLabel("핸드폰 번호");
        phoneNumberLabel.setBounds(30, 150, 100, 30);
        panel.add(phoneNumberLabel);
        phoneNumberLabel.setFont(f1);
        phoneNumberLabel.setHorizontalAlignment(JLabel.RIGHT);

        phoneNumberText = new JTextField(20);
        phoneNumberText.setBounds(160, 150, 180, 30);
        panel.add(phoneNumberText);
        phoneNumberText.setFont(f2);

        //비밀번호 찾기 버튼
        JButton FindpwButton = new RoundedButton("비밀번호 찾기");
        FindpwButton.setBounds(208, 200, 130, 30);
        panel.add(FindpwButton);
        FindpwButton.setFont(f1);

        FindpwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameText.getText().trim();
                    String userId = idText.getText();
                    String birth = birthText.getText().trim();
                    String phoneNumber = phoneNumberText.getText().trim();

                    //아이피, 포트 번호로 소켓을 연결
                    clientSocket = new Socket("localhost", 1024);

                    //서버로 정보를 전달 해주기 위해서 객체 형식으로 변환
                    FindUserPasswordRequest findUserPasswordRequest = new FindUserPasswordRequest(name, userId, birth, phoneNumber);

                    //서버와 정보를 주고 받기 위한 스트림 생성
                    OutputStream os = clientSocket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(os);

                    InputStream is = clientSocket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);

                    oos.writeObject(findUserPasswordRequest);

                    ResponseCode responseCode = (ResponseCode) ois.readObject();
                    System.out.println("1");
                    if (responseCode.getKey() == 240) { //비밀번호 찾기 성공
                        dispose();
                        System.out.println("2");
                        FindUserPasswordResponse findUserPasswordResponse = (FindUserPasswordResponse) ois.readObject();
                        System.out.println("3");
                        //showSuccessDialog(findUserPasswordResponse.password());
                        setChangePassword();  // 비밀번호 변경 창 호출
                    } else { //비밀번호 찾기 실패
                        showErrorDialog(responseCode.getValue());
                    }
                    System.out.println("4");
                    oos.close();
                    os.close();

                    ois.close();
                    is.close();

                    clientSocket.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        add(panel);
        setVisible(true);
    }

    private void setChangePassword() {
        JFrame changePWFrame = new JFrame("비밀번호 변경");
        changePWFrame.setSize(400, 250);
        changePWFrame.setResizable(false);
        changePWFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        changePWFrame.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(c1);

        JLabel pwLabel = new JLabel("비밀번호");
        pwLabel.setBounds(30, 50, 100, 30);
        pwLabel.setFont(f1);
        pwLabel.setHorizontalAlignment(JLabel.RIGHT);

        JPasswordField pwField = new JPasswordField(20);
        pwField.setBounds(160, 50, 180, 30);

        JLabel pwCheckLabel = new JLabel("비밀번호 확인");
        pwCheckLabel.setBounds(30, 90, 100, 30);

        pwCheckLabel.setFont(f1);
        pwCheckLabel.setHorizontalAlignment(JLabel.RIGHT);

        JPasswordField pwCheckField = new JPasswordField(20);
        pwCheckField.setBounds(160, 90, 180, 30);

        RoundedButton btn = new RoundedButton("비밀번호 변경");
        btn.setBounds(208, 150, 130, 30);
        btn.setFont(f1);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 비밀번호 변경 버튼 클릭 시
                changePWFrame.dispose();
                showSuccessDialog("비밀번호 변경이 완료되었습니다.");
            }
        });

        changePWFrame.add(panel);
        panel.add(pwLabel);
        panel.add(pwField);
        panel.add(pwCheckLabel);
        panel.add(pwCheckField);
        panel.add(btn);

        changePWFrame.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "입력 오류", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "알림", JOptionPane.INFORMATION_MESSAGE);
    }
}