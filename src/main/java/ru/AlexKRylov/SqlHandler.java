package ru.AlexKRylov;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class SqlHandler  extends Task<Void> {
    private String url, sql, inParameter, outParameter, filePath, ext;
    public SqlHandler(String url, String sql, String inParameter, String outParameter, String filePath, String ext) {
        this.url = url;
        this.sql = sql;
        this.inParameter = inParameter;
        this.outParameter = outParameter;
        this.filePath = filePath;
        this.ext = ext;
    }

    private void action() throws SQLException, IOException {
        Connection connection = Connector.connectToDb(url);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        assert ext != null;
        if (ext.equals(".xlsx")) {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(filePath));
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            XSSFRow xssfRow;
            for (int rowIndex = 0; rowIndex <= xssfSheet.getLastRowNum(); rowIndex++) {
                updateProgress(rowIndex, xssfSheet.getLastRowNum());
                xssfRow = xssfSheet.getRow(rowIndex);
                if (xssfRow != null) {
                    XSSFCell xssfCell = xssfRow.getCell(Integer.parseInt(inParameter));
                    if (xssfCell != null) {
                        XSSFCell xssfCellOut = xssfRow.createCell(Integer.parseInt(outParameter));
                        preparedStatement.setString(1, xssfCell.toString().replace(".0", ""));
                        ResultSet rs = preparedStatement.executeQuery();
                        while (rs.next()) {
                            ResultSetMetaData rsMetaData = rs.getMetaData();
                            String result = rs.getString(rsMetaData.getColumnName(1));
                            System.out.println(result);
                            xssfCellOut.setCellValue(result);
                            FileOutputStream fileOut = new FileOutputStream(filePath);
                            xssfWorkbook.write(fileOut);
                            fileOut.flush();
                            fileOut.close();
                            rs.close();
                        }
                    }
                }
            }
        }

        if (ext.equals(".xls")) {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(filePath));
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            HSSFRow hssfRow;
            for (int rowIndex = 0; rowIndex <= hssfSheet.getLastRowNum(); rowIndex++) {
                updateProgress(rowIndex+1, hssfSheet.getLastRowNum()+1);
                hssfRow = hssfSheet.getRow(rowIndex);
                if (hssfRow != null) {
                    HSSFCell hssfCell = hssfRow.getCell(Integer.parseInt(inParameter));
                    if (hssfCell != null) {
                        HSSFCell hssfCellOut = hssfRow.createCell(Integer.parseInt(outParameter));
                        preparedStatement.setString(1, hssfCell.toString().replace(".0", ""));
                        ResultSet rs = preparedStatement.executeQuery();
                        while (rs.next()) {
                            ResultSetMetaData rsMetaData = rs.getMetaData();
                            String result = rs.getString(rsMetaData.getColumnName(1));
                            System.out.println(result);
                            hssfCellOut.setCellValue(result);
                            FileOutputStream fileOut = new FileOutputStream(filePath);
                            hssfWorkbook.write(fileOut);
                            fileOut.flush();
                            fileOut.close();
                            rs.close();
                        }
                    }
                }
            }
        }
        preparedStatement.close();
        connection.close();
        finishTask();
    }

    @Override
    protected Void call() throws Exception {
        action();
        return null;
    }

    private void finishTask() {
        Platform.runLater(() -> Messager.setMessage("Готово!"));
    }
}
