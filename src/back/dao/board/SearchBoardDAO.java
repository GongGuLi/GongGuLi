package back.dao.board;

import back.dao.GetInfoDAO;
import back.request.board.SearchBoardInfoRequest;
import back.response.board.BoardInfoResponse;
import database.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchBoardDAO {
    Connection conn = null;
    PreparedStatement pt = null;
    ResultSet rs = null;

    public List<BoardInfoResponse> searchBoard(SearchBoardInfoRequest searchBoardInfoRequest) {
        PrintBoardDAO printBoardDAO = new PrintBoardDAO();
        String searchFilter = searchBoardInfoRequest.searchFilter();
        String searchText = searchBoardInfoRequest.searchText();
        String region = searchBoardInfoRequest.region();
        String category = searchBoardInfoRequest.category();
        List<BoardInfoResponse> boardList= printBoardDAO.printBoard(region, category);

        if (searchFilter.equals("제목")) {
            try {
                conn = DBConnector.getConnection();
                String selectSQL = "SELECT title FROM board WHERE title LIKE CONCAT('%', ?, '%') ORDER BY postingTime DESC;";
                pt = conn.prepareStatement(selectSQL);
                pt.setString(1, searchText);
                rs = pt.executeQuery();
                List<BoardInfoResponse> filteredList = new ArrayList<>();
                while (rs.next()) {
                    String title = rs.getString("title");
                    for (BoardInfoResponse board : boardList) {
                        if (board.title().equals(title)) {
                            filteredList.add(board);
                        }
                    }
                }
                Collections.sort(filteredList, Comparator.comparing(BoardInfoResponse::postingTime).reversed());
                System.out.println(filteredList);
                return filteredList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (searchFilter.equals("작성자")) {
            try {
                conn = DBConnector.getConnection();
                GetInfoDAO getInfoDAO = new GetInfoDAO();
                String nickName = getInfoDAO.getUuidMethod(searchText);
                String uuidSQL = "SELECT uuid FROM user WHERE nickName LIKE CONCAT('%', ?, '%');";
                pt = conn.prepareStatement(uuidSQL);
                pt.setString(1, nickName);
                rs = pt.executeQuery();

                List<BoardInfoResponse> filteredList = new ArrayList<>();
                while (rs.next()) {
                    String title = rs.getString("title");
                    for (BoardInfoResponse board : boardList) {
                        if (board.title().equals(title)) {
                            filteredList.add(board);
                        }
                    }
                }
                Collections.sort(filteredList, Comparator.comparing(BoardInfoResponse::postingTime).reversed());
                return filteredList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("검색 필터 적용 안됨.");
        return null;
    }
}