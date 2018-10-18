package io.microvibe.dbv;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.microvibe.dbv.model.Column;
import io.microvibe.dbv.model.Index;
import io.microvibe.dbv.model.PrimaryKey;
import io.microvibe.dbv.model.Table;
import io.microvibe.util.StringUtil;

public class ExcelTplToolkit {

	private static final String EXCEL_TPL_NAME = "tables.xlsm";

	public static InputStream getTemplateResourceStream() {
		InputStream in = ExcelTplToolkit.class.getResourceAsStream(EXCEL_TPL_NAME);
		return in;
	}

	public static XSSFWorkbook newXSSFWorkbook(InputStream in) throws IOException {
		XSSFWorkbook book = new XSSFWorkbook(in);
		return book;
	}

	public static void addTables(XSSFWorkbook book, List<Table> tables) {

		XSSFSheet idxSheet = book.getSheet("目录");
		XSSFSheet tplSheet = book.getSheet("模板");

		int idxRow = 5;

		for (Table table : tables) {
			String catalog = table.getTableCatalog();

			XSSFRow row = idxSheet.getRow(idxRow);
			if (row == null) {
				row = idxSheet.createRow(idxRow);
			}
			if (row.getCell(2) == null
					|| row.getCell(3) == null
					|| row.getCell(4) == null
					|| row.getCell(5) == null) {
				copy(idxSheet, idxRow - 1, 0, idxSheet, idxRow, 0, 'J' - 'A');
			}

			// String sheetName = StringUtil.coalesce(table.getRemarks(), table.getTableName());
			String sheetName = StringUtil.coalesce(table.getTableName());
			idxSheet.getRow(idxRow).getCell(2).setCellValue(catalog);
			idxSheet.getRow(idxRow).getCell(3).setCellValue(table.getRemarks());
			idxSheet.getRow(idxRow).getCell(4).setCellValue(table.getTableName());
			idxSheet.getRow(idxRow).getCell(6).setCellValue(table.getRemarks());

			if (book.getSheet(sheetName) != null) {
				book.removeSheetAt(book.getSheetIndex(sheetName));
			}
			XSSFSheet sheet = book.createSheet(sheetName);

			for (int i = 0; i < 7; i++) {
				copy(tplSheet, i, 0, sheet, i, 0, 'K' - 'A');
			}
			sheet.addMergedRegion(new CellRangeAddress(2, 4, 0, 'J' - 'A'));
			sheet.getRow(0).getCell(1).setCellValue(sheetName);
			sheet.getRow(2).getCell(0).setCellValue(table.getRemarks());

			// columns
			int iRow = 7;
			int colNum = 0;
			for (Column col : table.getColumnList()) {
				copy(tplSheet, 7, 0, sheet, iRow, 0, 'K' - 'A');
				sheet.getRow(iRow).getCell(0).setCellValue(++colNum);
				sheet.getRow(iRow).getCell(1).setCellValue(col.getColumnName());
				sheet.getRow(iRow).getCell(2).setCellValue(StringUtil.coalesce(col.getRemarks(), col.getColumnName()));
				sheet.getRow(iRow).getCell(3).setCellValue(col.getColumnType());
				sheet.getRow(iRow).getCell(4).setCellValue(col.getColumnSize());
				if (col.getDecimalDigits() > 0) {
					sheet.getRow(iRow).getCell(5).setCellValue(col.getDecimalDigits());
				}
				if (col.isPrimaryKey()) {
					sheet.getRow(iRow).getCell(6).setCellValue("Y");
				}
				if (col.isNotNull()) {
					sheet.getRow(iRow).getCell(7).setCellValue("Y");
				}
				if (StringUtil.isNotEmpty(col.getColumnDef())) {
					sheet.getRow(iRow).getCell(8).setCellValue(col.getColumnDef());
				}
				if ("YES".equalsIgnoreCase(col.getIsAutoincrement())) {
					sheet.getRow(iRow).getCell(9).setCellValue("自增长列");// 备注
				}
				iRow++;
			}

			copy(tplSheet, 7, 0, sheet, iRow, 0, 'K' - 'A');// empty col line
			sheet.getRow(iRow).getCell(0).setCellValue(++colNum);
			iRow++;
			copy(tplSheet, 12, 0, sheet, iRow, 0, 'K' - 'A');// end col

			iRow++;
			iRow++;
			// index
			copy(tplSheet, 15, 0, sheet, ++iRow, 0, 'K' - 'A');

			int idxNum = 0;
			idxLoop: for (Index idx : table.getIndexList()) {
				for (PrimaryKey pk : table.getPrimaryKeyList()) {
					if (pk.getPkName().equals(idx.getIndexName())) {
						continue idxLoop;
					}
				}

				copy(tplSheet, 16, 0, sheet, ++iRow, 0, 'K' - 'A');
				sheet.getRow(iRow).getCell(0).setCellValue(++idxNum);
				sheet.getRow(iRow).getCell(1).setCellValue(idx.getIndexName());
				String[] columnNames = idx.getColumnNames().split(",", 6);
				int iCol = 2;
				for (String columnName : columnNames) {
					sheet.getRow(iRow).getCell(iCol++).setCellValue(columnName);
				}
				sheet.getRow(iRow).getCell(8).setCellValue(idx.isUnique() ? "Y" : "N");
				sheet.getRow(iRow).getCell(9).setCellValue("D".equals(idx.getAscOrDesc()) ? "DESC" : "ASC");
			}
			copy(tplSheet, 16, 0, sheet, ++iRow, 0, 'K' - 'A');
			copy(tplSheet, 21, 0, sheet, ++iRow, 0, 'K' - 'A');
			for (int i = 0; i < 'K' - 'A'; i++) {
				sheet.setColumnWidth(i, tplSheet.getColumnWidth(i));
			}
			for (int i = 0; i < 'K' - 'A'; i++) {
				sheet.autoSizeColumn(i, true);
			}

			idxRow++;

		}

	}

	@SuppressWarnings("deprecation")
	private static void copy(XSSFSheet tplSheet, int tplRow, int tplCol, XSSFSheet sheet, int row, int col, int cols) {
		XSSFRow tplSheetRow = tplSheet.getRow(tplRow);
		XSSFRow sheetRow = sheet.createRow(row);
		if (tplSheetRow == null)
			return;
		sheetRow.setRowStyle(tplSheetRow.getRowStyle());
		for (int i = 0; i < cols; i++) {
			XSSFCell tplSheetCell = tplSheetRow.getCell(tplCol + i);
			XSSFCell sheetCell = sheetRow.createCell(col + i);
			if (tplSheetCell == null) {
				continue;
			}
			XSSFComment cellComment = tplSheetCell.getCellComment();
			if (cellComment != null)
				sheetCell.setCellComment(cellComment);
			XSSFCellStyle cellStyle = tplSheetCell.getCellStyle();
			if (cellStyle != null)
				sheetCell.setCellStyle(cellStyle);
			int cellType = tplSheetCell.getCellType();
			sheetCell.setCellType(cellType);
			switch (cellType) {
			case Cell.CELL_TYPE_STRING:
				sheetCell.setCellValue(tplSheetCell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				sheetCell.setCellValue(tplSheetCell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				sheetCell.setCellValue(tplSheetCell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_ERROR:
				sheetCell.setCellValue(tplSheetCell.getErrorCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				String cellFormula = tplSheetCell.getCellFormula();
				if (cellFormula != null)
					sheetCell.setCellFormula(cellFormula);
				break;
			case Cell.CELL_TYPE_BLANK:
				break;
			default:
				break;
			}
			XSSFHyperlink hyperlink = tplSheetCell.getHyperlink();
			if (hyperlink != null)
				sheetCell.setHyperlink(hyperlink);
		}
	}
}
