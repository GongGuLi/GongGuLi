package front;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class FrontSetting extends JFrame{

    Color mainColor = new Color(255, 240, 227);
    Color c2 = new Color(254, 217 , 183);  // 밝은 색
    Color c3 = new Color(240, 112, 103);  // 진한 색

    Font fb14 = new Font("SUITE", Font.BOLD, 14);
    Font fb16 = new Font("SUITE", Font.BOLD, 16);
    Font fb20 = new Font("SUITE", Font.BOLD, 20);
    Font f14 = new Font("SUITE", Font.PLAIN, 14);
    Font f16 = new Font("SUITE", Font.PLAIN, 16);
    Font f18 = new Font("SUITE", Font.PLAIN, 18);

    String regionArr[] = {" --", "서울", "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주도"};
    String categoryArr[] = {" --", "음식", "생활용품", "OTT"};

    String mainPageHeader[] = {"지역", "카테고리", "제목", "작성자", "현황"};
    String mainPageDB[][] = {{"경기도", "생활용품", "같이 살사람ㅋ", "윤경쓰", "1/4"},
            {"충청남도", "OTT", "얏호", "윤솢이", "3/4"}};  // 차후 DB 연동

    String myPageHeader[] =  {"지역", "카테고리", "제목", "현황"};
    String myPageDB[][] = {{"경기도", "생활용품", "같이 살사람ㅋ", "1/4"},
            {"충청남도", "OTT", "얏호", "3/4"}};

    String userInfoHeader[] = {"이름", "아이디", "지역", "휴대폰 번호", "생년월일"};
    String soDDuck[] = {"윤소정", "윤소떡소떡", "sodduck0", "sodduckpass01!", "충청남도", "01036207566", "031122"};
    //  이름 닉네임 아이디 비밀번호 지역 폰번호 생일
    int mainTableWidths[] = {30, 30, 300, 30, 10};  // 열 사이즈 크기 -> 비율로 적용되는 듯
    int myPostingTableWidths[] = {20, 20, 120, 1};
    int myHistoryTableWidths[] = {20, 20, 90, 2, 1};

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
        JOptionPane.showMessageDialog(null, message, "입력 오류", JOptionPane.ERROR_MESSAGE);
    }

    public void showCompleteDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "완료", JOptionPane.INFORMATION_MESSAGE);
    }
}