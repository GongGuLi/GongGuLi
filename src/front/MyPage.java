package front;

import back.dao.BoardDAO;
import back.dao.UserDAO;
import back.UserDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyPage extends JFrame {
    UserDTO userDTO = null;
    UserDAO userDAO = new UserDAO();
    BoardDAO boardDAO = new BoardDAO();
    FrontSetting fs = new FrontSetting();

    boolean checkNickDup = false;

    public MyPage() {  // 생성자
        setLeftPanel();
        setCenterPanel();
        setRightPanel();
    }

    public MyPage(UserDTO userDTO) {  // 생성자
        this.userDTO = userDTO;
        setLeftPanel();
        setCenterPanel();
        setRightPanel();
    }

    public void setMyPage() {
        setSize(1120, 700);
        fs.FrameSetting(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("공구리 마이페이지");
        setLayout(null);

        setVisible(true);
    }

    private void setLeftPanel() {  // 위에 파란 부분 패널
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(fs.mainColor);
        leftPanel.setBounds(0, 0, 300, 700);

        ImageIcon profileImgIcon = new ImageIcon("img/profile2.png");
        JButton profileBtn = new JButton(profileImgIcon);  // 유저 프로필 버튼
        profileBtn.setBounds(30, 30, 50, 50);
        profileBtn.setBorder(null);

        profileBtn.addActionListener(new ActionListener() {  // 프로필 버튼 누르면 작동될 기능
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainPage lf = new MainPage(userDTO);
                lf.setListFrame();
            }
        });

        JLabel nickNameLabel = new JLabel(userDTO.getNickName() + " 님");  // 닉네임 라벨
        nickNameLabel.setFont(fs.fb20);
        nickNameLabel.setForeground(fs.c3);
        nickNameLabel.setBounds(100, 30, 150, 50);

        RoundedButton modifyInfoBtn = new RoundedButton("정보 수정");
        modifyInfoBtn.setBounds(35, 350, 100, 30);
        modifyInfoBtn.setFont(fs.f16);

        modifyInfoBtn.addActionListener(new ActionListener() {  // 정보 수정 버튼 클릭 시
            @Override
            public void actionPerformed(ActionEvent e) {
                setModifyUserInfoFrame();
            }
        });

        JButton logoutBtn = new JButton("로그아웃");  // 로그아웃 버튼
        logoutBtn.setBounds(20, 590, 100, 20);
        logoutBtn.setBackground(null);
        logoutBtn.setBorder(null);
        logoutBtn.setFont(fs.f14);
        logoutBtn.setForeground(fs.c3);

        logoutBtn.addActionListener(new ActionListener() {  // 로그아웃 버튼 클릭
            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.showConfirmDialog(null, "로그아웃 하시겠습니까?", "", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    System.out.println("로그아웃");
                    new LogIn();
                    dispose();
                } else System.out.println("노 로그아웃");

                // 로그인 화면으로
            }
        });

        JButton deleteIdBtn = new JButton("회원탈퇴");  // 회원탈퇴 버튼
        deleteIdBtn.setBounds(20, 615, 100, 20);
        deleteIdBtn.setBackground(null);
        deleteIdBtn.setBorder(null);
        deleteIdBtn.setFont(fs.f14);
        deleteIdBtn.setForeground(fs.c3);

        deleteIdBtn.addActionListener(new ActionListener() {  // 회원탈퇴 버튼 클릭
            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.showConfirmDialog(null, "회원탈퇴 하시겠습니까?", "", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {

                }

                String password = JOptionPane.showInputDialog("비밀번호를 입력하세요.");
                System.out.println("비밀번호: " + password);

                if (password.equals(userDTO.getPassword())) {
                    System.out.println("회원탈퇴");
                    new LogIn();
                    dispose();
                } else System.out.println("노 회원탈퇴");

                // 로그인 화면으로 + 회원 삭제
            }
        });

        add(leftPanel);
        leftPanel.add(setUserInfoPanel());
        leftPanel.add(profileBtn);
        leftPanel.add(nickNameLabel);
        leftPanel.add(modifyInfoBtn);
        leftPanel.add(logoutBtn);
        leftPanel.add(deleteIdBtn);
    }

    private void setCenterPanel() {
        JPanel centerPanel = new JPanel(null);
        centerPanel.setBounds(300, 0, 770, 700);
        centerPanel.setBackground(Color.white);

        add(centerPanel);

        setMyPostingPanel(centerPanel);
        setMyHistoryPanel(centerPanel);
    }
    private void setRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(1070, 0, 50, 700);
        rightPanel.setBackground(fs.mainColor);

        add(rightPanel);
    }

    private void setMyPostingPanel(JPanel centerPanel) {
        JLabel myPostingLabel = new JLabel("내가 쓴 글");
        myPostingLabel.setFont(fs.fb20);
        myPostingLabel.setBounds(155, 50, 150, 40);

        JPanel myPostingPanel = new JPanel(null);
        myPostingPanel.setBounds(30, 120, 340, 500);
        myPostingPanel.setBackground(Color.WHITE);

        JTable myPostingTable = new JTable(boardDAO.printMyBoard(userDTO), fs.myPageHeader) { // 셀 내용 수정 불가
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        fs.tableSetting(myPostingTable, fs.myPostingTableWidths);

        JScrollPane myPostingScroll = new JScrollPane(myPostingTable);
        myPostingScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        myPostingScroll.setBounds(0, 0, 340, 480);

        myPostingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int selectRow = myPostingTable.getSelectedRow();
                    int selectColumn = myPostingTable.getSelectedColumn();
                    readMoreMyPost(myPostingTable, selectRow, selectColumn);
                }
            }
        });

        centerPanel.add(myPostingPanel);
        centerPanel.add(myPostingLabel);
        myPostingPanel.add(myPostingScroll);
    }

    private void setMyHistoryPanel(JPanel centerPanel) {
        JLabel myHistoryLabel = new JLabel("나의 공구 내역");
        myHistoryLabel.setFont(fs.fb20);
        myHistoryLabel.setBounds(510, 50, 150, 40);

        JPanel myHistoryPanel = new JPanel(null);
        myHistoryPanel.setBounds(400, 120, 340, 480);
        myHistoryPanel.setBackground(Color.WHITE);

        JTable myHistoryTable = new JTable(boardDAO.printMyHistoryBoard(userDTO), fs.mainPageHeader) { // 셀 내용 수정 불가
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        fs.tableSetting(myHistoryTable, fs.myHistoryTableWidths);

        JScrollPane myHistoryScroll = new JScrollPane(myHistoryTable);
        myHistoryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        myHistoryScroll.setBounds(0, 0, 340, 480);

        myHistoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int selectRow = myHistoryTable.getSelectedRow();
                    int selectColumn = myHistoryTable.getSelectedColumn();
                    readMoreMyHistory(myHistoryTable, selectRow, selectColumn);
                }
            }
        });

        centerPanel.add(myHistoryLabel);
        centerPanel.add(myHistoryPanel);
        myHistoryPanel.add(myHistoryScroll);
    }

    private JPanel setUserInfoPanel(){  // 유저 정보 출력 패널
        ImagePanel userInfoPanel = new ImagePanel(new ImageIcon("img/roundedPanel.png").getImage());
        userInfoPanel.setBackground(Color.white);
        userInfoPanel.setBounds(25, 150, 210, 190);

        String[] userInfo = new String[5];

        userInfo[0] = userDTO.getName();
        userInfo[1] = userDTO.getUserId();
        userInfo[2] = userDTO.getRegion();
        userInfo[3] = userDTO.getPhoneNum();
        userInfo[4] = userDTO.getBirth();

        for (int i = 0; i < userInfo.length; i++) {
            int size = i;
            JLabel info = new JLabel(fs.userInfoHeader[i] + ": " + userInfo[i]);
            info.setFont(fs.fb14);
            info.setForeground(fs.c3);
            info.setBounds(20, 25+(30*size), 240, 20);
            userInfoPanel.add(info);
        }

        return userInfoPanel;
    }

    private void setModifyUserInfoFrame() {
        JFrame modifyUserInfoFrame = new JFrame("회원정보 수정");
        modifyUserInfoFrame.setSize(400, 500);
        fs.FrameSetting(modifyUserInfoFrame);
        modifyUserInfoFrame.setVisible(true);
        Container c = modifyUserInfoFrame.getContentPane();
        c.setBackground(fs.mainColor);
        c.setLayout(null);

        JLabel modifyUserInfoLabel = new JLabel("정보 수정");
        modifyUserInfoLabel.setForeground(fs.c3);
        modifyUserInfoLabel.setFont(fs.fb20);
        modifyUserInfoLabel.setBounds(158, 30, 100, 30);

        ImagePanel whitePanel = new ImagePanel(new ImageIcon("img/modifyUserInfoPanel.png").getImage());
        whitePanel.setBounds(25, 80, 330, 298);

        JLabel userNameLabel = new JLabel("이름");
        userNameLabel.setBounds(35, 20, 300, 30);
        userNameLabel.setFont(fs.f16);
        JLabel userName = new JLabel(userDTO.getName());
        userName.setBounds(160, 20, 300, 30);
        userName.setFont(fs.f16);

        JLabel userIDLabel = new JLabel("아이디");
        userIDLabel.setBounds(35, 52, 300, 30);
        userIDLabel.setFont(fs.f16);
        JLabel userID = new JLabel(userDTO.getUserId());
        userID.setBounds(160, 52, 300, 30);
        userID.setFont(fs.f16);

        JLabel userNickNameLabel = new JLabel("닉네임");
        userNickNameLabel.setBounds(35, 84, 70, 30);
        userNickNameLabel.setFont(fs.f16);

        JTextField userNickNameField = new JTextField(userDTO.getNickName());
        userNickNameField.setBounds(160, 84, 85, 30);

        RoundedButtonR NickDupBtn = new RoundedButtonR("확인");
        NickDupBtn.setBounds(250, 84, 48, 30);
        NickDupBtn.setFont(fs.f16);

        NickDupBtn.addActionListener(new ActionListener() {  // 닉네임 중복 확인 버튼 클릭
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userDAO.nickNameCheck(userNickNameField.getText())) {
                    // 닉네임 변경 가능.
                    fs.showCompleteDialog("해당 닉네임은 사용 가능합니다.");
                    checkNickDup = true;
                }
                else fs.showErrorDialog("중복되는 닉네임이 존재합니다.");


                checkNickDup = true;
            }
        });

        JLabel userPWLabel = new JLabel("비밀번호" );
        userPWLabel.setBounds(35, 116, 120, 30);
        userPWLabel.setFont(fs.f16);

        JPasswordField userPWField = new JPasswordField(20);
        userPWField.setBounds(160,116, 140, 30);
        userPWField.setFont(fs.f16);

        JLabel userPWCheckLabel = new JLabel("비밀번호 확인" );
        userPWCheckLabel.setBounds(35, 148, 150, 30);
        userPWCheckLabel.setFont(fs.f16);

        JPasswordField userPWCheckField = new JPasswordField(20);
        userPWCheckField.setBounds(160, 148, 140, 30);
        userPWCheckField.setFont(fs.f16);

        JLabel userRegionLabel = new JLabel("지역");
        userRegionLabel.setBounds(35, 180, 150, 30);
        userRegionLabel.setFont(fs.f16);

        JComboBox userRegionBtn = new JComboBox(fs.regionArr);
        userRegionBtn.setSelectedItem(userDTO.getRegion());
        userRegionBtn.setBounds(160, 180, 95, 30);
        userRegionBtn.setFont(fs.f16);

        JLabel userPhoneNumberLabel = new JLabel("휴대폰 번호");
        userPhoneNumberLabel.setBounds(35, 212, 300,  30);
        userPhoneNumberLabel.setFont(fs.f16);
        JLabel userPhoneNumber = new JLabel(userDTO.getPhoneNum());
        userPhoneNumber.setBounds(160, 212, 300,  30);
        userPhoneNumber.setFont(fs.f16);

        JLabel userBirthLabel = new JLabel("생년월일");
        userBirthLabel.setBounds(35, 244, 300, 30);
        userBirthLabel.setFont(fs.f16);
        JLabel userBirth = new JLabel(userDTO.getBirth());
        userBirth.setBounds(160, 244, 300, 30);
        userBirth.setFont(fs.f16);

        RoundedButton modifyUserInfoBtn = new RoundedButton("수정하기");
        modifyUserInfoBtn.setBounds(143, 395, 100, 40);
        modifyUserInfoBtn.setFont(fs.fb16);

        modifyUserInfoBtn.addActionListener(new ActionListener() {  // 원래 본인 지역 먼저 뜨게 가능한지
            @Override
            public void actionPerformed(ActionEvent e) {
                // 닉네임 중복 확인 기능 필요
                char[] userPW = userPWField.getPassword();
                char[] userPWC = userPWCheckField.getPassword();
                String userPWStr = String.valueOf(userPW);

                boolean changeNick = false;
                boolean checkModify = false;

                if (!userNickNameField.getText().equals(userDTO.getNickName())) changeNick = true;

                if (userPWField.getText().isBlank() || userPWCheckField.getText().isBlank()) fs.showErrorDialog("비밀번호를 입력해주세요.");
                else if(!userPWField.getText().equals(userPWCheckField.getText())) fs.showErrorDialog("비밀번호가 일치하지 않습니다.");
                else if (userPWStr.length() < 8 ||
                        !userPWStr.matches(".*[a-zA-Z].*") || // 영어 포함
                        !userPWStr.matches(".*\\d.*") ||      // 숫자 포함
                        !userPWStr.matches(".*[@#$%^&*+_=!].*")) { // 특수문자 포함
                    fs.showErrorDialog("비밀번호는 영어, 숫자, 특수문자를 포함하고 8글자 이상이어야 합니다.");
                }
                else if(userRegionBtn.getSelectedItem().equals(" --")) fs.showErrorDialog("지역을 선택해주세요.");
                else if (userNickNameField.getText().isBlank()) fs.showErrorDialog("닉네임을 입력해주세요.");
                else if (changeNick && !checkNickDup) fs.showErrorDialog("닉네임 중복확인을 해주세요.");
                else checkModify = true;

                if (checkModify) {
                    System.out.println("수정하기");
                    checkNickDup = false;
                    fs.showCompleteDialog("수정이 완료되었습니다.");
                    modifyUserInfoFrame.dispose();
                    // DB 비밀번호, 지역 수정
                }
            }
        });

        c.add(modifyUserInfoLabel);
        c.add(whitePanel);
        whitePanel.add(userNameLabel);
        whitePanel.add(userName);
        whitePanel.add(userIDLabel);
        whitePanel.add(userID);
        whitePanel.add(userNickNameLabel);
        whitePanel.add(userNickNameField);
        whitePanel.add(NickDupBtn);
        whitePanel.add(userPWLabel);
        whitePanel.add(userPWField);
        whitePanel.add(userPWCheckLabel);
        whitePanel.add(userPWCheckField);
        whitePanel.add(userRegionLabel);
        whitePanel.add(userRegionBtn);
        whitePanel.add(userPhoneNumberLabel);
        whitePanel.add(userPhoneNumber);
        whitePanel.add(userBirthLabel);
        whitePanel.add(userBirth);
        c.add(modifyUserInfoBtn);
    }

    public void readMoreMyPost(JTable t, int selectRow, int selectColumn) {  // 테이블 값 더블 클릭 시 자세히보기
        System.out.println(t.getValueAt(selectRow, 2));

        JFrame readMoreFrame = new JFrame((String)t.getValueAt(selectRow, 2));  // 자세히보기 팝업창 프레임
        readMoreFrame.setSize(500, 600);
        fs.FrameSetting(readMoreFrame);

        Container c = readMoreFrame.getContentPane();  // 자세히보기 팝업창 패널
        c.setLayout(null);
        c.setBackground(fs.mainColor);

        JLabel logoLabel = new JLabel("공구리");  // 라벨
        logoLabel.setFont(fs.fb20);
        logoLabel.setBounds(220, 20, 100, 40);

        JTextArea titleArea = new JTextArea(" 제목: " + (String) t.getValueAt(selectRow, 2));
        titleArea.setBounds(20, 80, 445, 35);
        titleArea.setFont(fs.f18);
        titleArea.setEditable(false);

        JTextArea infoArea1 = new JTextArea(" 지역: " + (String) t.getValueAt(selectRow, 0) +
                "\n 현황: " + (String) t.getValueAt(selectRow, 3));
        infoArea1.setBounds(20, 125, 230, 55);
        infoArea1.setFont(fs.f18);
        infoArea1.setEditable(false);

        JTextArea infoArea2 = new JTextArea("카테고리: " + (String) t.getValueAt(selectRow, 1));
        infoArea2.setBounds(250, 125, 215, 55);
        infoArea2.setFont(fs.f18);
        infoArea2.setEditable(false);

        JTextArea contentArea = new JTextArea(" 내용");
        contentArea.setBounds(20, 210, 445, 250);
        contentArea.setFont(fs.f18);
        contentArea.setEditable(false);
        contentArea.setDragEnabled(false);

        JLabel viewCountLabel = new JLabel("조회수: +변수");
        viewCountLabel.setFont(fs.f14);
        viewCountLabel.setBounds(20, 465, 150, 20);

        RoundedButton modifyPostBtn = new RoundedButton("수정하기");
        modifyPostBtn.setBounds(190, 480, 110, 50);
        modifyPostBtn.setFont(fs.fb16);

        modifyPostBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readMoreFrame.dispose();
                //getTitle, 등 값을 얻어 modifyMyPost()에 매개변수로 넘기기
                modifyMyPost();
            }
        });

        JButton deletePostBtn = new JButton("삭제하기");
        deletePostBtn.setBounds(400, 465, 70, 20);
        deletePostBtn.setBackground(null);
        deletePostBtn.setBorder(null);
        deletePostBtn.setFont(fs.f14);
        deletePostBtn.setForeground(fs.c3);

        deletePostBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.showConfirmDialog(null, "삭제하시겠습니까?", "", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    System.out.println("삭제");
                } else System.out.println("삭제 안함");
            }
        });


        c.add(logoLabel);
        c.add(titleArea);
        c.add(infoArea1);
        c.add(infoArea2);
        c.add(contentArea);
        c.add(modifyPostBtn);
        c.add(deletePostBtn);
        c.add(viewCountLabel);

        readMoreFrame.setVisible(true);
    }

    public void readMoreMyHistory(JTable t, int selectRow, int selectColumn) {  // 테이블 값 더블 클릭 시 자세히보기
        System.out.println(t.getValueAt(selectRow, 2));

        JFrame readMoreFrame = new JFrame((String)t.getValueAt(selectRow, 2));  // 자세히보기 팝업창 프레임
        readMoreFrame.setSize(500, 600);
        fs.FrameSetting(readMoreFrame);

        Container c = readMoreFrame.getContentPane();  // 자세히보기 팝업창 패널
        c.setLayout(null);
        c.setBackground(fs.mainColor);

        JLabel logoLabel = new JLabel("공구리");  // 라벨
        logoLabel.setFont(fs.fb20);
        logoLabel.setBounds(220, 20, 100, 40);

        JTextArea titleArea = new JTextArea(" 제목: " + (String) t.getValueAt(selectRow, 2));
        titleArea.setBounds(20, 80, 445, 35);
        titleArea.setFont(fs.f18);
        titleArea.setEditable(false);

        JTextArea infoArea1 = new JTextArea(" 지역: " + (String) t.getValueAt(selectRow, 0) +
                "\n 글쓴이: " + (String) t.getValueAt(selectRow, 3));
        infoArea1.setBounds(20, 125, 230, 55);
        infoArea1.setFont(fs.f18);
        infoArea1.setEditable(false);

        JTextArea infoArea2 = new JTextArea("카테고리: " + (String) t.getValueAt(selectRow, 1) +
                "\n현황: " + (String) t.getValueAt(selectRow, 4));
        infoArea2.setBounds(250, 125, 215, 55);
        infoArea2.setFont(fs.f18);
        infoArea2.setEditable(false);

        JTextArea contentArea = new JTextArea(" 내용");
        contentArea.setBounds(20, 210, 445, 250);
        contentArea.setFont(fs.f18);
        contentArea.setEditable(false);
        contentArea.setDragEnabled(false);

        JLabel viewCountLabel = new JLabel("조회수: +변수");
        viewCountLabel.setFont(fs.f14);
        viewCountLabel.setBounds(20, 465, 150, 20);

        c.add(logoLabel);
        c.add(titleArea);
        c.add(infoArea1);
        c.add(infoArea2);
        c.add(contentArea);
        c.add(viewCountLabel);

        readMoreFrame.setVisible(true);
    }

    // get메소드로 title 등 매개변수를 받아 JTextField의 기본 텍스트로 선정하기
    private void modifyMyPost() {  // 글 수정하기
        JFrame modifyFrame = new JFrame("수정하기");   // 수정 프레임
        modifyFrame.setSize(500, 600);
        fs.FrameSetting(modifyFrame);

        Container c = modifyFrame.getContentPane();  // 수정 컨테이너
        c.setBackground(fs.mainColor);
        c.setLayout(null);

        JLabel newPostLabel = new JLabel("수정하기");  // 라벨
        newPostLabel.setFont(fs.fb20);
        newPostLabel.setBounds(210, 20, 100, 40);

        JLabel titleLabel = new JLabel("  제목:");  // 제목 라벨
        titleLabel.setBounds(20, 80, 80, 35);
        titleLabel.setFont(fs.f18);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.WHITE);

        JTextField titleField = new JTextField();  // 제목 입력 필드
        titleField.setBounds(80, 80, 385, 35);
        titleField.setFont(fs.f18);
        titleField.setBorder(null);

        JPanel whitePanel = new JPanel(null);  // 지역, 카테고리의 흰색 배경
        whitePanel.setBounds(20, 125, 445, 35);
        whitePanel.setBackground(Color.WHITE);

        JLabel regionLabel = new JLabel(" 지역: ");  // 지역 라벨
        regionLabel.setBounds(5, 3, 70, 30);
        regionLabel.setFont(fs.f18);

        JComboBox regionField = new JComboBox(fs.regionArr);  // 지역 콤보 박스
        regionField.setBounds(60, 3, 100, 30);
        regionField.setFont(fs.f16);

        JLabel categoryLabel = new JLabel("카테고리: ");  // 카테고리 라벨
        categoryLabel.setBounds(170, 3, 100, 30);
        categoryLabel.setFont(fs.f18);

        JComboBox categoryField = new JComboBox(fs.categoryArr);  // 카테고리 콤보 박스
        categoryField.setBounds(255, 3, 100, 30);
        categoryField.setFont(fs.f16);

        JLabel peopleNumLabel = new JLabel("인원: ");  // 인원 수 라벨
        peopleNumLabel.setBounds(365, 3, 50, 30);
        peopleNumLabel.setFont(fs.f18);

        JTextField peopleNumField = new JTextField();
        peopleNumField.setBounds(410, 3, 25, 30);
        peopleNumField.setFont(fs.f18);

        JTextArea contentArea = new JTextArea(445, 250);  // 내용 작성 필드
        contentArea.setFont(fs.f16);

        JScrollPane contentScroll = new JScrollPane(contentArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScroll.setBounds(20, 185, 445, 250);
        contentScroll.setBorder(null);

        RoundedButton modifyBtn = new RoundedButton("올리기");  // 수정 버튼
        modifyBtn.setBounds(200, 480, 90, 50);
        modifyBtn.setFont(fs.fb16);

        modifyBtn.addActionListener(new ActionListener() {  // 수정 버튼 클릭 시
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String region = (String) regionField.getSelectedItem();
                String category = (String) categoryField.getSelectedItem();
                String peopleNum = peopleNumField.getText();
                String content = contentArea.getText();

                if(modifyErrorCheck(title, region, category, peopleNum, content)) { // 오류 검출 후 DB 넘기기
                    System.out.println("수정: [" + title + ", " + region + ", " + category + ", " + peopleNum + ", " + content + "]");
                    modifyFrame.dispose();
                    setSuccessPopUpFrame();
                }
            }
        });

        c.add(newPostLabel);
        c.add(titleLabel);
        c.add(titleField);
        c.add(whitePanel);
        whitePanel.add(regionLabel);
        whitePanel.add(regionField);
        whitePanel.add(categoryLabel);
        whitePanel.add(categoryField);
        whitePanel.add(peopleNumLabel);
        whitePanel.add(peopleNumField);
        c.add(contentScroll);
        c.add(modifyBtn);

        modifyFrame.setVisible(true);
    }

    private boolean modifyErrorCheck(String title, String region, String category, String peopleNum, String content) {  // 빈칸 있는 지 확인 후 올리기
        System.out.println("오류 검사: [" + title + ", " + region + ", " + category + ", " + peopleNum + ", " + content + "]");  // 테스트

        boolean checkBlank = false;  // 빈 칸 & 공백 확인
        boolean checkPeopleNum = false;  // 인원 수 입력 조건 확인

        if (title.isBlank()) fs.showErrorDialog("제목을 입력해주세요.");
        else if (region.equals(" --")) fs.showErrorDialog("지역을 선택해주세요.");
        else if (category.equals(" --")) fs.showErrorDialog("카테고리를 선택해주세요.");
        else if (peopleNum.isBlank()) fs.showErrorDialog("인원 수를 입력해주세요.");
        else if (content.isBlank()) fs.showErrorDialog("내용을 입력해주세요.");
        else {
            try {
                if (Integer.parseInt(peopleNum) > 30) fs.showErrorDialog("인원은 30명까지 입력 가능합니다.");
                else if (Integer.parseInt(peopleNum) <= 1) fs.showErrorDialog("인원은 2명부터 입력 가능합니다.");
                else checkPeopleNum = true;
            } catch (NumberFormatException e) { fs.showErrorDialog("인원은 숫자만 입력 가능합니다."); }
            checkBlank = true;
        }

        if(checkBlank && checkPeopleNum) return true;  // 마지막 오류 검출
        else return false;
    }

    private void setSuccessPopUpFrame() {  // 수정 완료 팝업
        JFrame notifyFrame = new JFrame();
        notifyFrame.setSize(300, 200);
        fs.FrameSetting(notifyFrame);

        Container c = notifyFrame.getContentPane();
        c.setLayout(null);
        c.setBackground(fs.mainColor);


        JLabel successLabel = new JLabel("수정 완료");
        successLabel.setFont(fs.fb16);
        successLabel.setBounds(110, 35, 200, 40);

        RoundedButton okBtn = new RoundedButton("확인");
        okBtn.setFont(fs.fb16);
        okBtn.setBounds(105, 95, 80, 30);

        okBtn.addActionListener(new ActionListener() {  // ok 버튼 누르면 알림창 닫힘
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyFrame.dispose();
                // dispose(); 메인프레임이랑 똑같은디 왜 걍 꺼지냐 ㅋㅋ;
                new MyPage(userDTO);
            }
        });

        c.add(successLabel);
        c.add(okBtn);

        notifyFrame.setVisible(true);
    }
}