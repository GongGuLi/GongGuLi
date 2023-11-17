package back.handler;

import back.ResponseCode;
import back.dao.BoardDAO;
import back.request.Post_Board_Request;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Board_Handler extends Thread {
    private Socket clientSocket = null;

    private OutputStream os = null;
    private ObjectOutputStream oos = null;

    private InputStream is = null;
    private ObjectInputStream ois = null;

    private final BoardDAO boardDAO = new BoardDAO();

    public Board_Handler(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;

            //서버 -> 클라이언트 Output Stream
            os = clientSocket.getOutputStream();
            oos = new ObjectOutputStream(os);

            //서버 <- 클라이언트 Input Stream
            is = clientSocket.getInputStream();
            ois = new ObjectInputStream(is);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Object readObj = ois.readObject();

            if (readObj instanceof Post_Board_Request) {
                Post_BoardMethod(readObj);
            }
        } catch (Exception exception) {
            CloseHandler();
            exception.printStackTrace();
        }
    }

    private void Post_BoardMethod(Object readObj) {
        try {
            //클라이언트에서 게시글 생성 요청을 받는다.
            Post_Board_Request postBoardRequest = (Post_Board_Request) readObj;

            //각 조건들을 비교하여 클라이언트에 응답을 보낸다.
            if (postBoardRequest.title().isBlank()) {
                oos.writeObject(ResponseCode.TITLE_MISSING);
            } else if (postBoardRequest.region().equals(" --")) {
                oos.writeObject(ResponseCode.REGION_NOT_SELECTED);
            } else if (postBoardRequest.category().equals(" --")) {
                oos.writeObject(ResponseCode.CATEGORY_NOT_SELECTED);
            } else if (postBoardRequest.peopleNum().isBlank()) {
                oos.writeObject(ResponseCode.PEOPLE_NUM_MISSING);
            } else if (postBoardRequest.content().isBlank()) {
                oos.writeObject(ResponseCode.CONTENT_MISSING);
            } else {
                if (Integer.parseInt(postBoardRequest.peopleNum()) > 30) {
                    oos.writeObject(ResponseCode.PEOPLE_NUM_OVER_LIMIT);
                } else if (Integer.parseInt(postBoardRequest.peopleNum()) <= 1) {
                    oos.writeObject(ResponseCode.PEOPLE_NUM_UNDER_LIMIT);
                } else {
                    boardDAO.posting(postBoardRequest);
                    oos.writeObject(ResponseCode.POST_BOARD_SUCCESS);
                }
            }

            CloseHandler();
        } catch (NumberFormatException numberFormatException) {
            try {
                oos.writeObject(ResponseCode.PEOPLE_NUM_NOT_NUMBER);

                CloseHandler();
            } catch (Exception exception) {
                CloseHandler();
                exception.printStackTrace();
            }
        } catch (Exception exception) {
            CloseHandler();
            exception.printStackTrace();
        }
    }

    private void CloseHandler() {
        try {
            oos.close();
            os.close();

            ois.close();
            is.close();

            clientSocket.close();
        } catch(Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                oos.close();
                os.close();

                ois.close();
                is.close();

                clientSocket.close();
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}