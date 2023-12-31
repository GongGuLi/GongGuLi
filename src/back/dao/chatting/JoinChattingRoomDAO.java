package back.dao.chatting;

import back.dao.GetInfoDAO;
import database.DBConnector;
import serverStructure.ChatServer;

import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class JoinChattingRoomDAO {
    Connection conn = null;
    PreparedStatement pt = null;
    ResultSet rs = null;

    public int joinChattingRoom(int port, String uuid) throws Exception {
        GetInfoDAO getInfoDAO = new GetInfoDAO();
        int nowPeopleNum = getInfoDAO.getNowPeopleNum(port);
        int maxPeopleNum = getInfoDAO.getMaxPeopleNum(port);

        if (nowPeopleNum == -1 || maxPeopleNum == -1) {
            System.out.println("DB 오류?");
        } else if (isJoinedChat(port, uuid)) {
            if (nowPeopleNum >= maxPeopleNum) {
                return 0;   //  채팅방 인원이 가득 참
            }
            try {
                conn = DBConnector.getConnection();

                String insertSQL = "INSERT INTO chattingMember (port, memberUuid) VALUES (?, ?);";
                pt = conn.prepareStatement(insertSQL);
                pt.setInt(1, port);
                pt.setString(2, uuid);
                pt.execute();

                String updateSQL = "UPDATE board SET nowPeopleNum = nowPeopleNum + 1 WHERE port = ?;";
                pt = conn.prepareStatement(updateSQL);
                pt.setInt(1, port);
                pt.execute();

                updateSQL = "UPDATE chattingRoom SET nowPeopleNum = nowPeopleNum + 1 WHERE port = ?;";
                pt = conn.prepareStatement(updateSQL);
                pt.setInt(1, port);
                pt.execute();

                pt.close();
                conn.close();
                return 1;   //  채팅방 입장 성공
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            return 1;
        }

        return -1;  //  에러
    }

//    채팅방에 이미 존재 하는지 여부 확인
    public boolean isJoinedChat(int port, String uuid) {
        try {
            conn = DBConnector.getConnection();
            String selectSQL = "SELECT EXISTS(select * from chattingMember where port = ? and memberUuid = ?) as cnt;";
            pt = conn.prepareStatement(selectSQL);
            pt.setInt(1, port);
            pt.setString(2, uuid);
            rs = pt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 1) {
                return false;   //  채팅방에 이미 존재 (디비에 중복으로 저장돼서 구분해야함.)
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return true;    //  채팅방에 존재하지 않음 (신규 입장 유저)
    }
}