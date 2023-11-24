package front;

import back.response.board.Board_Info_Response;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

public class FrontSetting extends JFrame{

    public Color mainColor = new Color(255, 240, 227);
    public Color c2 = new Color(254, 217 , 183);  // 밝은 색
    public Color c3 = new Color(240, 112, 103);  // 진한 색

    public Font fb14 = new Font("SUITE", Font.BOLD, 14);
    public Font fb16 = new Font("SUITE", Font.BOLD, 16);
    public Font fb20 = new Font("SUITE", Font.BOLD, 20);
    public Font f14 = new Font("SUITE", Font.PLAIN, 14);
    public Font f16 = new Font("SUITE", Font.PLAIN, 16);
    public Font f18 = new Font("SUITE", Font.PLAIN, 18);

    public String regionArr[] = {" --", "서울", "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주도"};
    public String categoryArr[] = {" --", "음식", "생활용품", "OTT"};

    public String mainPageHeader[] = {"지역", "카테고리", "제목", "작성자", "현황"};
    public String mainPageDB[][] = null;

            //{{"경기도", "생활용품", "같이 살사람ㅋ", "윤경쓰", "1/4"},
                    //{"충청남도", "OTT", "얏호", "윤솢이", "3/4"}};

    public String myPageHeader[] =  {"지역", "카테고리", "제목", "현황"};
    public String myPageDB[][] = {{"경기도", "생활용품", "같이 살사람ㅋ", "1/4"},
            {"충청남도", "OTT", "얏호", "3/4"}};

    public String userInfoHeader[] = {"이름", "아이디", "지역", "휴대폰 번호", "생년월일"};

    //  이름 닉네임 아이디 비밀번호 지역 폰번호 생일
    public int mainTableWidths[] = {30, 30, 300, 30, 10};  // 열 사이즈 크기 -> 비율로 적용되는 듯
    public int myPostingTableWidths[] = {20, 20, 120, 1};
    public int myHistoryTableWidths[] = {20, 20, 90, 2, 1};

    public void tableCellCenter(JTable t) {  // 모든 열의 텍스트 가운데 지정
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumnModel tcm = t.getColumnModel();

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            tcm.getColumn(i).setCellRenderer(dtcr);
        }
    }

    public void tableSetting(JTable t, int[] widths) {  // JTable 세팅
        for (int i = 0; i < widths.length ; i++) {
            TableColumn c = t.getColumnModel().getColumn(i);
            c.setPreferredWidth(widths[i]);
        }

        t.setRowHeight(27);
        t.setFont(f14);
        tableCellCenter(t);  // 가운데 정렬
        t.getTableHeader().setReorderingAllowed(false);  // 헤더 이동 불가
        t.getTableHeader().setResizingAllowed(false);  // 헤더 크기 조절 불가
    }

    public void FrameSetting(JFrame f) {  // JFrame 세팅
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
    }

    public void showCompleteDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "", JOptionPane.INFORMATION_MESSAGE);
    }

    public void setmainPageDB(List<Board_Info_Response> boardList) {
        mainPageDB = new String[boardList.size()][5];

        for (int i = 0; i < boardList.size(); i++) {
            Board_Info_Response boardInfo = boardList.get(i);
            mainPageDB[i][0] = boardInfo.region();
            mainPageDB[i][1] = boardInfo.category();
            mainPageDB[i][2] = boardInfo.title();
            mainPageDB[i][3] = boardInfo.writer();
            mainPageDB[i][4] = boardInfo.peopleNum();
        }
    }

    public int getmainPageDB() {
        return mainPageDB.length;
    }
}