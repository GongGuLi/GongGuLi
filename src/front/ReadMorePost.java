package front;

import back.response.ResponseCode;
import back.request.account.GetNickNameRequest;
import back.request.board.DeleteBoardRequest;
import back.request.chatroom.JoinChatRoomRequest;
import back.response.account.GetNickNameResponse;
import back.response.board.BoardInfoMoreResponse;
import back.response.mypage.MyBoardInfoMoreResponse;
import front.mainPage.MainPage;
import front.myPage.MyPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ReadMorePost {
    FrontSetting frontSetting = new FrontSetting();
    private String uuid = null;

    private BoardInfoMoreResponse boardInfoMoreResponse;
    private MyBoardInfoMoreResponse myBoardInfoMoreResponse;
    private int port = 0;

    private JFrame frame;
    private JFrame readMoreFrame;
    private boolean isMainFrame;

    public ReadMorePost(boolean isMainFrame, String uuid, JFrame frame, BoardInfoMoreResponse boardInfoMoreResponse) {
        this.uuid = uuid;
        this.frame = frame;
        this.isMainFrame = isMainFrame;
        this.boardInfoMoreResponse = boardInfoMoreResponse;
        this.port = boardInfoMoreResponse.port();

        if (boardInfoMoreResponse.authority()) {
            readMoreMyPost();
        } else {
            readMorePost();
        }
    }

    public ReadMorePost(boolean isMainFrame, String uuid, JFrame frame, MyBoardInfoMoreResponse myBoardInfoMoreResponse) {
//        마이페이지에서 자세히 보기 할 때 이 생성자 사용
        this.uuid = uuid;
        this.frame = frame;
        this.isMainFrame = isMainFrame;
        this.port = myBoardInfoMoreResponse.port();
        String nickName = "";
        boardInfoMoreResponse = new BoardInfoMoreResponse(myBoardInfoMoreResponse.port(), myBoardInfoMoreResponse.title(), myBoardInfoMoreResponse.region(), myBoardInfoMoreResponse.category(), nickName, myBoardInfoMoreResponse.peopleNum(), myBoardInfoMoreResponse.content(), myBoardInfoMoreResponse.view(), true);

        readMoreMyPost();
    }

    /*테이블 값 더블 클릭 시 자세히 보기(작성자 타인)*/
    public void readMorePost() {
        JFrame readMoreFrame = new JFrame(boardInfoMoreResponse.title());  // 자세히보기 팝업창 프레임
        readMoreFrame.setSize(500, 600);
        frontSetting.FrameSetting(readMoreFrame);

        Container c = readMoreFrame.getContentPane();  // 자세히보기 팝업창 패널
        c.setLayout(null);
        c.setBackground(frontSetting.mainColor);

        JLabel logoLabel = new JLabel("공구리");  // 라벨
        logoLabel.setFont(frontSetting.fb20);
        logoLabel.setBounds(220, 20, 100, 40);

        JLabel title = new JLabel("  제목:   " + boardInfoMoreResponse.title());
        title.setBounds(20, 80, 445, 35);
        title.setFont(frontSetting.f18);
        title.setOpaque(true);
        title.setBackground(Color.WHITE);

        JLabel info = new JLabel("  지역:   " + boardInfoMoreResponse.region() +
                "             카테고리:   " + boardInfoMoreResponse.category() +
                "             현황: " + boardInfoMoreResponse.peopleNum());
        info.setBounds(20, 125, 445, 35);
        info.setFont(frontSetting.f18);
        info.setOpaque(true);
        info.setBackground(Color.WHITE);

        JTextArea contentArea = new JTextArea(boardInfoMoreResponse.content());
        contentArea.setBounds(20, 185, 445, 250);
        contentArea.setFont(frontSetting.f18);
        contentArea.setEditable(false);
        contentArea.setDragEnabled(false);

        JLabel viewCountLabel = new JLabel("조회수: " + boardInfoMoreResponse.view());
        viewCountLabel.setFont(frontSetting.f14);
        viewCountLabel.setBounds(20, 440, 150, 20);

        RoundedButton joinChatRoomBtn = new RoundedButton("채팅 참여");
        joinChatRoomBtn.setBounds(200, 480, 90, 50);
        joinChatRoomBtn.setFont(frontSetting.fb16);

        /*채팅 참여 버튼 클릭시*/
        joinChatRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Socket clientSocket = new Socket("localhost", 1026);

                    OutputStream outputStream = clientSocket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    InputStream inputStream = clientSocket.getInputStream();
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                    JoinChatRoomRequest joinChatroomRequest = new JoinChatRoomRequest(port, uuid);

                    objectOutputStream.writeObject(joinChatroomRequest);

                    ResponseCode responseCode = (ResponseCode) objectInputStream.readObject();

                    if (responseCode.getKey() == ResponseCode.JOIN_CHATROOM_SUCCESS.getKey()) {
                        clientSocket.close();
                        new ChatClient(port, uuid);
                    } else if (responseCode.getKey() == ResponseCode.JOIN_CHATROOM_FAILURE.getKey()) {
                        showErrorDialog(responseCode.getValue());
                    } else if (responseCode.getKey() == ResponseCode.CHATROOM_FULL.getKey()) {
//                        채팅방 가득 참
                        System.out.println("채팅방이 가득 참");
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        c.add(logoLabel);
        c.add(title);
        c.add(info);
        c.add(contentArea);
        c.add(joinChatRoomBtn);
        c.add(viewCountLabel);

        readMoreFrame.setVisible(true);
    }

    /*테이블 값 더블 클릭 시 자세히보기(작성자 본인)*/
    public void readMoreMyPost() {
        readMoreFrame = new JFrame(boardInfoMoreResponse.title());  // 자세히보기 팝업창 프레임
        readMoreFrame.setSize(500, 600);
        frontSetting.FrameSetting(readMoreFrame);

        Container c = readMoreFrame.getContentPane();  // 자세히보기 팝업창 패널
        c.setLayout(null);
        c.setBackground(frontSetting.mainColor);

        JLabel logoLabel = new JLabel("공구리");  // 라벨
        logoLabel.setFont(frontSetting.fb20);
        logoLabel.setBounds(220, 20, 100, 40);

        JLabel title = new JLabel("  제목:   " + boardInfoMoreResponse.title());
        title.setBounds(20, 80, 445, 35);
        title.setFont(frontSetting.f18);
        title.setOpaque(true);
        title.setBackground(Color.WHITE);

        JLabel info = new JLabel("  지역:   " + boardInfoMoreResponse.region() +
                "             카테고리:   " + boardInfoMoreResponse.category() +
                "             현황: " + boardInfoMoreResponse.peopleNum());
        info.setBounds(20, 125, 445, 35);
        info.setFont(frontSetting.f18);
        info.setOpaque(true);
        info.setBackground(Color.WHITE);

        JTextArea contentArea = new JTextArea(boardInfoMoreResponse.content());
        contentArea.setBounds(20, 185, 445, 250);
        contentArea.setFont(frontSetting.f18);
        contentArea.setEditable(false);
        contentArea.setDragEnabled(false);

        JLabel viewCountLabel = new JLabel("조회수: " + boardInfoMoreResponse.view());
        viewCountLabel.setFont(frontSetting.f14);
        viewCountLabel.setBounds(20, 440, 150, 20);

//        게시글 수정
        RoundedButton modifyPostBtn = new RoundedButton("수정하기");
        modifyPostBtn.setBounds(200, 480, 90, 50);
        modifyPostBtn.setFont(frontSetting.fb16);

        modifyPostBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readMoreFrame.dispose();
                if (isMainFrame) new ModifyMyPost(frame, uuid, port, boardInfoMoreResponse);
                else new ModifyMyPost(frame, uuid, boardInfoMoreResponse, port);
            }
        });

        // 게시글 삭제
        JButton deletePostBtn = new JButton("삭제하기");
        deletePostBtn.setBounds(400, 440, 70, 20);
        deletePostBtn.setBackground(null);
        deletePostBtn.setBorder(null);
        deletePostBtn.setFont(frontSetting.f14);
        deletePostBtn.setForeground(frontSetting.c3);

        // 게시글 삭제 기능
        deletePostBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.showConfirmDialog(null, "삭제하시겠습니까?", "", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    try (Socket clientSocket = new Socket("localhost", 1025);
                         OutputStream outputStream = clientSocket.getOutputStream();
                         ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                         InputStream inputStream = clientSocket.getInputStream();
                         ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    ){
                        DeleteBoardRequest deleteBoardRequest = new DeleteBoardRequest(port);
                        objectOutputStream.writeObject(deleteBoardRequest);

                        ResponseCode responseCode = (ResponseCode) objectInputStream.readObject();

                        if (responseCode.getKey() == ResponseCode.DELETE_MY_BOARD_SUCCESS.getKey()) {
//                            삭제 완료 GUI
                            readMoreFrame.dispose();
                            frame.dispose();
                            if(isMainFrame)  new MainPage(uuid);
                            else new MyPage(uuid);
                        } else {
                            frontSetting.showErrorDialog(responseCode.getValue());
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        c.add(logoLabel);
        c.add(title);
        c.add(info);
        c.add(contentArea);
        c.add(contentArea);
        c.add(modifyPostBtn);
        c.add(deletePostBtn);
        c.add(viewCountLabel);

        readMoreFrame.setVisible(true);
    }

    private String getNickNameMethod(String uuid) {
        String nickName = "";
        try (Socket clientSocket = new Socket("localhost", 1024);
             OutputStream outputStream = clientSocket.getOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
             InputStream inputStream = clientSocket.getInputStream();
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        ){
            GetNickNameRequest getNickNameRequest = new GetNickNameRequest(uuid);
            objectOutputStream.writeObject(getNickNameRequest);
            ResponseCode responseCode = (ResponseCode) objectInputStream.readObject();

            if (responseCode.getKey() == ResponseCode.GET_UUID_NICKNAME_SUCCESS.getKey()) { // 닉네임 가져오기 성공
                GetNickNameResponse getNickNameResponse = (GetNickNameResponse) objectInputStream.readObject();
                nickName = getNickNameResponse.nickName();
            } else { // 닉네임 가져오기 실패
                showErrorDialog(responseCode.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nickName;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "안내", JOptionPane.ERROR_MESSAGE);
    }
}
