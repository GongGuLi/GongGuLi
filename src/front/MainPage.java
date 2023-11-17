package front;

import back.ResponseCode;
import back.dao.BoardDAO;
import back.BoardDTO;
import back.request.Board_Info_Request;
import back.request.Post_Board_Request;
import back.response.Board_Info_Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class MainPage extends JFrame{
    BoardDAO boardDAO = new BoardDAO();
    FrontSetting fs = new FrontSetting();

    String region = " --";
    String category = " --";

    JTable postTable;
    JScrollPane listScrollPane;

    private Socket clientSocket = null;

    private String uuid;

    public MainPage(String uuid) {  // 생성자
        this.uuid = uuid;
        System.out.println(this.uuid);

        setListFrame();
        setLeftPanel();
        setCenterPanel();
        setRightPanel();

        setVisible(true);
    }

    public void setListFrame() {  // 기본 프레임 세팅
        setSize(1120, 700);
        fs.FrameSetting(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("공구리 게시글 페이지");
    }

    private void setLeftPanel() {  // 왼쪽 색깔 바 (프로필, 카테고리)
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBounds(0, 0, 300, 700);
        leftPanel.setBackground(fs.mainColor);

        ImagePanel logoImg = new ImagePanel(new ImageIcon("img/logo2.png").getImage());
        logoImg.setBounds(50, 430, 200, 200);

        ImageIcon profileImgIcon = new ImageIcon("img/profile2.png");
        JButton profileBtn = new JButton(profileImgIcon);  // 유저 프로필 버튼
        profileBtn.setBounds(30, 30, 50, 50);
        profileBtn.setBorder(null);

        profileBtn.addActionListener(new ActionListener() {  // 유저 프로필 버튼 클릭 시
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                // MyPage mp = new MyPage(userDTO); // 마이페이지 넘어가는 코드
                // mp.setMyPage();
            }
        });

        RoundedButton chattingListBtn = new RoundedButton("채팅 목록");  // 채팅 목록 버튼
        chattingListBtn.setBounds(120, 35, 130, 40);
        chattingListBtn.setFont(fs.f16);

        chattingListBtn.addActionListener(new ActionListener() {  // 채팅 목록 버튼 클릭 시
            @Override
            public void actionPerformed(ActionEvent e) {
                setChattingList();
            }
        });

        JLabel regionLabel = new JLabel("지역 필터");  // 지역 필터 레이블
        regionLabel.setBounds(72, 170, 120, 30);
        regionLabel.setFont(fs.fb16);

        JComboBox regionBtn = new JComboBox(fs.regionArr);  // 지역 필터 버튼
        regionBtn.setBounds(70, 200, 170, 35);
        regionBtn.setFont(fs.f18);

        JLabel categoryLabel = new JLabel("카테고리 필터");  // 카테고리 필터 레이블
        categoryLabel.setBounds(72, 270, 120, 30);
        categoryLabel.setFont(fs.fb16);

        JComboBox categoryBtn = new JComboBox(fs.categoryArr);  // 카테고리 필터 버튼
        categoryBtn.setBounds(70, 300, 170, 35);
        categoryBtn.setFont(fs.f18);

        regionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    region = (String) regionBtn.getSelectedItem();
                    category = (String) categoryBtn.getSelectedItem();

                    //서버로 정보를 전달 해주기 위해서 객체 형식으로 변환
                    Board_Info_Request boardInfoRequest = new Board_Info_Request(region, category, uuid);

                    //아이피, 포트 번호로 소켓을 연결
                    clientSocket = new Socket("localhost", 1027);

                    //서버와 정보를 주고 받기 위한 스트림 생성
                    OutputStream os = clientSocket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(os);

                    InputStream is = clientSocket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);

                    oos.writeObject(boardInfoRequest);

                    ResponseCode responseCode = (ResponseCode) ois.readObject();

                    if (responseCode.getKey() == ResponseCode.BOARD_INFO_SUCCESS.getKey()) { //게시글 갱신 성공
                        List <Board_Info_Response> boardList = (List <Board_Info_Response>) ois.readObject();
                        //boardList안에 레코드 형태에 게시글 정보가 다 들어있음.
                    } else { //게시글 갱신 실패
                        showErrorDialog(responseCode.getValue());
                    }

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

        categoryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    region = (String) regionBtn.getSelectedItem();
                    category = (String) categoryBtn.getSelectedItem();

                    //서버로 정보를 전달 해주기 위해서 객체 형식으로 변환
                    Board_Info_Request boardInfoRequest = new Board_Info_Request(region, category, uuid);

                    //아이피, 포트 번호로 소켓을 연결
                    clientSocket = new Socket("localhost", 1027);

                    //서버와 정보를 주고 받기 위한 스트림 생성
                    OutputStream os = clientSocket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(os);

                    InputStream is = clientSocket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);

                    oos.writeObject(boardInfoRequest);

                    ResponseCode responseCode = (ResponseCode) ois.readObject();

                    if (responseCode.getKey() == ResponseCode.BOARD_INFO_SUCCESS.getKey()) { //게시글 갱신 성공
                        List <Board_Info_Response> boardList = (List <Board_Info_Response>) ois.readObject();
                        //boardList안에 레코드 형태에 게시글 정보가 다 들어있음.
                    } else { //게시글 갱신 실패
                        showErrorDialog(responseCode.getValue());
                    }

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

        add(leftPanel);
        leftPanel.add(logoImg);
        leftPanel.add(profileBtn);
        leftPanel.add(chattingListBtn);
        leftPanel.add(regionLabel);
        leftPanel.add(categoryLabel);
        leftPanel.add(regionBtn);
        leftPanel.add(categoryBtn);
    }

    private void setCenterPanel() {  // 게시글 패널
        JPanel centerPanel = new JPanel(null);
        centerPanel.setBounds(300, 0, 770, 700);
        centerPanel.setBackground(Color.WHITE);

        JTextField searchField = new JTextField();  // 검색창
        searchField.setBounds(550, 40, 150, 30);
        searchField.setFont(fs.f16);

        ImageIcon searchImg = new ImageIcon("img/searchBtn.png");
        JButton searchBtn = new JButton(searchImg);  // 검색 버튼
        searchBtn.setBorder(null);
        searchBtn.setBounds(702, 40, 30, 30 );

        String searchFilter[] =  {"제목", "작성자"};
        JComboBox searchFilterBox = new JComboBox(searchFilter);
        searchFilterBox.setBounds(460, 40, 80, 30);

        // 검색창에 입력 후 검색 버튼 누르면 텍스트 출력
        // (테스트용 - 추후 백엔드로 넘기는 과정 필요)
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(searchField.getText());
            }
        });
        // 게시글 출력 <- 이거 좀 해결 해줘봐
        postTable = new JTable(boardDAO.printBoard(region, category), fs.mainPageHeader) {
            @Override
            public boolean isCellEditable(int row, int column) {  // 셀 내용 수정 불가 설정
                return false;
            }
        };

        fs.tableSetting(postTable, fs.mainTableWidths);  // postTable 세팅

        postTable.addMouseListener(new MouseAdapter() {  // 테이블 값 더블 클릭 시
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int selectRow = postTable.getSelectedRow();
                    System.out.println(selectRow);
                    BoardDTO boardDTO = boardDAO.readMorePost(selectRow);
                    readMorePost(postTable, boardDTO);
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(postTable);  // 게시글 스크롤 생성
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        listScrollPane.setBounds(50, 110, 680, 470);

        RoundedButtonR newPostBtn = new RoundedButtonR("새 글");  // 새 글 작성 버튼
        newPostBtn.setBounds(638, 600, 90, 40);
        newPostBtn.setFont(fs.f16);

        newPostBtn.addActionListener(new ActionListener() {  // 새 글 버튼 클릭 시
            @Override
            public void actionPerformed(ActionEvent e) {
                setPosting();
            }
        });

        add(centerPanel);
        centerPanel.add(searchField);
        centerPanel.add(searchBtn);
        centerPanel.add(searchFilterBox);
        centerPanel.add(listScrollPane);
        centerPanel.add(newPostBtn);
    }

    private void setRightPanel() {  // 오른쪽 색깔 바 ( 미적으로 ㄱ냥 잇으면  갠찮을듯)
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(1070, 0, 50, 700);
        rightPanel.setBackground(fs.mainColor);

        add(rightPanel);
    }

    private void setChattingList() {  // 채팅 목록
        JFrame chattingListFrame = new JFrame();  // 채팅 목록 팝업창 프레임
        chattingListFrame.setTitle("채팅 목록");
        chattingListFrame.setSize(400, 600);
        fs.FrameSetting(chattingListFrame);

        JPanel chattingListPanel = new JPanel(null);  // 채팅 목록 팝업창 패널
        chattingListPanel.setBounds(0, 0, 400, 600);
        chattingListPanel.setBackground(fs.mainColor);

        JLabel chattingListLabel = new JLabel("채팅 목록");  // 채팅 목록 레이블
        chattingListLabel.setFont(new Font("SUITE", Font.BOLD, 20));
        chattingListPanel.setBounds(10, 10, 100, 60);

        // 채팅 목록 출력
        String listHeader[] = {"카테고리", "제목", "작성자", "참여하기"};
        String listDB[][] = {{"", "", "", ""}};  // 차후 DB 연동
        JTable listTable = new JTable(listDB, listHeader);

        listScrollPane = new JScrollPane(listTable);
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        listScrollPane.setBounds(30, 80, 340, 430);

        chattingListFrame.add(chattingListPanel);
        chattingListPanel.add(chattingListLabel);
        chattingListPanel.add(listScrollPane);

        chattingListFrame.setVisible(true);
    }

    public void readMorePost(JTable t, BoardDTO boardDTO) {  // 테이블 값 더블 클릭 시 자세히보기
        System.out.println(boardDTO.getTitle());

        JFrame readMoreFrame = new JFrame(boardDTO.getTitle());  // 자세히보기 팝업창 프레임
        readMoreFrame.setSize(500, 600);
        fs.FrameSetting(readMoreFrame);

        Container c = readMoreFrame.getContentPane();  // 자세히보기 팝업창 패널
        c.setLayout(null);
        c.setBackground(fs.mainColor);

        JLabel logoLabel = new JLabel("공구리");  // 라벨
        logoLabel.setFont(fs.fb20);
        logoLabel.setBounds(220, 20, 100, 40);

        JTextArea titleArea = new JTextArea(" 제목: " + boardDTO.getTitle());
        titleArea.setBounds(20, 80, 445, 35);
        titleArea.setFont(fs.f18);
        titleArea.setEditable(false);

        JTextArea infoArea1 = new JTextArea(" 지역: " + boardDTO.getRegion() +
                "\n 글쓴이: " + boardDTO.getNickName());
        infoArea1.setBounds(20, 125, 230, 55);
        infoArea1.setFont(fs.f18);
        infoArea1.setEditable(false);

        JTextArea infoArea2 = new JTextArea("카테고리: " + boardDTO.getCategory() +
                "\n현황: " + boardDTO.getPeopleNum());
        infoArea2.setBounds(250, 125, 215, 55);
        infoArea2.setFont(fs.f18);
        infoArea2.setEditable(false);

        JTextArea contentArea = new JTextArea(" " + boardDTO.getContent());
        contentArea.setBounds(20, 210, 445, 250);
        contentArea.setFont(fs.f18);
        contentArea.setEditable(false);
        contentArea.setDragEnabled(false);

        JLabel viewCountLabel = new JLabel("조회수: " + boardDTO.getView());
        viewCountLabel.setFont(fs.f14);
        viewCountLabel.setBounds(20, 465, 150, 20);

        RoundedButton joinChatBtn = new RoundedButton("채팅 참여");
        joinChatBtn.setBounds(190, 480, 110, 50);
        joinChatBtn.setFont(fs.fb16);

        c.add(logoLabel);
        c.add(titleArea);
        c.add(infoArea1);
        c.add(infoArea2);
        c.add(contentArea);
        c.add(joinChatBtn);
        c.add(viewCountLabel);

        readMoreFrame.setVisible(true);
    }

    private void setPosting() {  // 새 글 작성
        JFrame newPostFrame = new JFrame("새 글");   // 새 글 프레임
        newPostFrame.setSize(500, 600);
        fs.FrameSetting(newPostFrame);

        Container c = newPostFrame.getContentPane();  // 새 글 팝업창 패널
        c.setBackground(fs.mainColor);
        c.setLayout(null);

        JLabel newPostLabel = new JLabel("새 글");  // 라벨
        newPostLabel.setFont(fs.fb20);
        newPostLabel.setBounds(220, 20, 100, 40);

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

        RoundedButton postBtn = new RoundedButton("올리기");  // 올리기 버튼
        postBtn.setBounds(200, 480, 90, 50);
        postBtn.setFont(fs.fb16);

        postBtn.addActionListener(new ActionListener() {  // 올리기 버튼 클릭 시
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String title = titleField.getText();
                    String region = (String) regionField.getSelectedItem();
                    String category = (String) categoryField.getSelectedItem();
                    String peopleNum = peopleNumField.getText();
                    String content = contentArea.getText();

                    Post_Board_Request Post_BoardInfo = new Post_Board_Request(title, region, category, peopleNum, content, uuid);

                    //아이피, 포트 번호로 소켓을 연결
                    clientSocket = new Socket("localhost", 1025);

                    //서버와 정보를 주고 받기 위한 스트림 생성
                    OutputStream os = clientSocket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(os);

                    InputStream is = clientSocket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);

                    oos.writeObject(Post_BoardInfo);

                    ResponseCode responseCode = (ResponseCode) ois.readObject();

                    if (responseCode.getKey() == ResponseCode.POST_BOARD_SUCCESS.getKey()) { //게시글 생성 성공
                        setSuccessPopUpFrame();
                    } else { //게시글 생성 실패
                        showErrorDialog(responseCode.getValue());
                    }

                    oos.close();
                    os.close();

                    ois.close();
                    is.close();

                    clientSocket.close();

                    newPostFrame.dispose();
                    setSuccessPopUpFrame();
                } catch (Exception exception) {
                    exception.printStackTrace();
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
        c.add(postBtn);

        newPostFrame.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "안내", JOptionPane.ERROR_MESSAGE);
    }

    private void setSuccessPopUpFrame() {
        JFrame notifyFrame = new JFrame();  // 알림 팝업 프레임 "글이 올라갔어용"
        notifyFrame.setSize(300, 200);
        fs.FrameSetting(notifyFrame);

        Container c = notifyFrame.getContentPane();
        c.setLayout(null);
        c.setBackground(fs.mainColor);


        JLabel successLabel = new JLabel("글 작성 완료");
        successLabel.setFont(fs.fb16);
        successLabel.setBounds(100, 35, 200, 40);

        RoundedButton okBtn = new RoundedButton("확인");
        okBtn.setFont(fs.fb16);
        okBtn.setBounds(105, 95, 80, 30);

        okBtn.addActionListener(new ActionListener() {  // ok 버튼 누르면 알림창 닫힘
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyFrame.dispose();
                dispose();
                // new MainPage(userDTO); // 새 글 쓰고 나서 새로고침
            }
        });

        c.add(successLabel);
        c.add(okBtn);

        notifyFrame.setVisible(true);
    }
}