package back.dao;

import database.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckDAO {

    Connection conn = null;
    PreparedStatement pt = null;
    ResultSet rs = null;

//    닉네임 중복 확인
    public boolean nickNameCheck(String inpNickName) {
        boolean nickNameCheck = false;
        conn = DBConnector.getConnection();
        String existssql = "SELECT exists(select nickName from user where nickName = ?) as cnt;";
        try {
            pt = conn.prepareStatement(existssql);
            pt.setString(1, inpNickName);
            rs = pt.executeQuery();
            if (rs.next()) {
                int cnt = rs.getInt("cnt");
                System.out.println(cnt);
                if (cnt == 0) {
                    nickNameCheck = true;
                }
            }
        } catch (Exception e) {
            System.out.println("nickNameCheck Error");
        }

        return nickNameCheck;
    }

//    로그인 확인
    public int logInCheck(String inpId, String inpPassword) {
        int result = 0;
        String checkSQL = "SELECT password FROM user WHERE userId = ?";
        try {
            conn = DBConnector.getConnection();
            pt = conn.prepareStatement(checkSQL);
            pt.setString(1, inpId);
            rs = pt.executeQuery();
            if (rs.next()) {
                if (rs.getString(1).equals(inpPassword)) {
                    result = 1;     // 로그인 성공
                } else {
                    result = 2;     // 비밀번호 불일치
                }
            } else {
                result = 3;     // 아이디 존재 X
            }
            rs.close();
            pt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;        // ?
    }
}
