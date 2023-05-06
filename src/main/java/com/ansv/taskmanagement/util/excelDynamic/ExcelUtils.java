package com.ansv.taskmanagement.util.excelDynamic;

import com.ansv.taskmanagement.dto.excel.XlsxField;
import com.ansv.taskmanagement.annotation.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelUtils {

    public static void writeCompositeFieldRow(Object objectValue, XlsxField xlsCompositeColumnField, Row childRow,
                                        CellStyle currencyStyle, CellStyle centerAlignedStyle, CellStyle genericStyle,
                                        Workbook workbook)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method nestedCompositeXlsMethod = getMethod(objectValue.getClass(), xlsCompositeColumnField);
        Object nestedCompositeValue = nestedCompositeXlsMethod.invoke(objectValue, (Object[]) null);
        Cell compositeNewCell = childRow.createCell(xlsCompositeColumnField.getCellIndex());
        setCellValue(compositeNewCell, nestedCompositeValue, currencyStyle, centerAlignedStyle, genericStyle, workbook);

    }

    public static void writeArrayFieldRow(Row childRow, XlsxField xlsColumnField, Object objectValue,
                                    CellStyle currencyStyle, CellStyle centerAlignedStyle, CellStyle genericStyle, Workbook workbook) {
        Cell newCell = childRow.createCell(xlsColumnField.getCellIndex());
        setCellValue(newCell, objectValue, currencyStyle, centerAlignedStyle, genericStyle, workbook);
    }

    public static <T> void writeSingleFieldRow(Row mainRow, XlsxField xlsColumnField, Class<?> clazz, CellStyle currencyStyle,
                                         CellStyle centerAlignedStyle, CellStyle genericStyle, T record, Workbook workbook)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Cell newCell = mainRow.createCell(xlsColumnField.getCellIndex());
        Method xlsMethod = getMethod(clazz, xlsColumnField);
        Object xlsObjValue = xlsMethod.invoke(record, (Object[]) null);
        setCellValue(newCell, xlsObjValue, currencyStyle, centerAlignedStyle, genericStyle, workbook);

    }

    public static <T> boolean isNextColumnAnArray(List<XlsxField> xlsColumnFields, XlsxField xlsColumnField,
                                            Class<?> clazz, T record)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        XlsxField nextXlsColumnField;
        int fieldsSize = xlsColumnFields.size();
        Method nestedXlsMethod;
        Object nestedObjValue;
        ArrayList<Object> nestedObjValueList;

        if (xlsColumnFields.indexOf(xlsColumnField) < (fieldsSize - 1)) {
            nextXlsColumnField = xlsColumnFields.get(xlsColumnFields.indexOf(xlsColumnField) + 1);
            if (nextXlsColumnField.isAnArray()) {
                nestedXlsMethod = getMethod(clazz, nextXlsColumnField);
                nestedObjValue = nestedXlsMethod.invoke(record, (Object[]) null);
                nestedObjValueList = (ArrayList<Object>) nestedObjValue;
                return nestedObjValueList.size() > 1;
            }
        }

        return xlsColumnFields.indexOf(xlsColumnField) == (fieldsSize - 1);

    }


    public static void setCellValue(Cell cell, Object objValue, CellStyle currencyStyle, CellStyle centerAlignedStyle,
                              CellStyle genericStyle, Workbook workbook) {

        Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);

        if (objValue != null) {
            if (objValue instanceof String) {
                String cellValue = (String) objValue;
                cell.setCellStyle(genericStyle);
                if (cellValue.contains("https://") || cellValue.contains("http://")) {
                    link.setAddress(cellValue);
                    cell.setCellValue(cellValue);
                    cell.setHyperlink(link);
                } else {
                    cell.setCellValue(cellValue);
                }
            } else if (objValue instanceof Long) {
                cell.setCellValue((Long) objValue);
            } else if (objValue instanceof Integer) {
                cell.setCellValue((Integer) objValue);
            } else if (objValue instanceof Double) {
                Double cellValue = (Double) objValue;
                cell.setCellStyle(currencyStyle);
                cell.setCellValue(cellValue);
            } else if (objValue instanceof Boolean) {
                cell.setCellStyle(centerAlignedStyle);
                if (objValue.equals(true)) {
                    cell.setCellValue(1);
                } else {
                    cell.setCellValue(0);
                }
            }
        }
    }

    public static List<XlsxField> getFieldNamesForClass(Class<?> clazz) {
        List<XlsxField> xlsColumnFields = new ArrayList();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            Annotation[] annotations = field.getAnnotations();
            XlsxField xlsColumnField = new XlsxField();
            if (Collection.class.isAssignableFrom(field.getType())) {

                xlsColumnField.setAnArray(true);
//                Annotation[] annotations = field.getAnnotations();
//                XlsxCompositeField xlsCompositeField = null;
//                XlsxSingleField xlsField = null;
                XlsxCompositeField xlsCompositeField = field.getAnnotation(XlsxCompositeField.class);
//                for (Annotation fieldAnnotation : annotations) {
//                    if(fieldAnnotation.annotationType().getSimpleName().equals(XlsxCompositeField.class)) {
//                        xlsCompositeField = (XlsxCompositeField) fieldAnnotation;
//                    }
//                }

                if (xlsCompositeField != null) {
                    xlsColumnField.setCellIndexFrom(xlsCompositeField.from());
                    xlsColumnField.setCellIndexTo(xlsCompositeField.to());
                    xlsColumnField.setComposite(true);
                } else {
//                    for (Annotation fieldAnnotation : annotations) {
//                        if(fieldAnnotation.annotationType().getSimpleName().equals(XlsxSingleField.class)) {
//                            xlsField = (XlsxSingleField) fieldAnnotation;
//                        }
//                    }
                    XlsxSingleField xlsField = field.getAnnotation(XlsxSingleField.class);
                    xlsColumnField.setCellIndex(xlsField.columnIndex());
                }
            } else {
//                XlsxSingleField xlsField = null;
//                for (Annotation fieldAnnotation : annotations) {
//                    if(fieldAnnotation.annotationType().getSimpleName().equals(XlsxSingleField.class)) {
//                        xlsField = (XlsxSingleField) fieldAnnotation;
//                    }
//                }
                XlsxSingleField xlsField = field.getAnnotation(XlsxSingleField.class);
                xlsColumnField.setAnArray(false);
                if (xlsField != null) {
                    xlsColumnField.setCellIndex(xlsField.columnIndex());
                    xlsColumnField.setComposite(false);
                }
            }
            xlsColumnField.setFieldName(field.getName());
            xlsColumnFields.add(xlsColumnField);
        }
        return xlsColumnFields;
    }

    public static String capitalize(String s) {
        if (s.length() == 0)
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    public static <T> int getMaxListSize(T record, List<XlsxField> xlsColumnFields, Class<? extends Object> aClass)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        List<Integer> listSizes = new ArrayList<>();

        for (XlsxField xlsColumnField : xlsColumnFields) {
            if (xlsColumnField.isAnArray()) {
                Method method = getMethod(aClass, xlsColumnField);
                Object value = method.invoke(record, (Object[]) null);
                ArrayList<Object> objects = (ArrayList<Object>) value;
                if (objects.size() > 1) {
                    listSizes.add(objects.size());
                }
            }
        }

        if (listSizes.isEmpty()) {
            return 1;
        } else {
            return Collections.max(listSizes);
        }

    }

    public static Method getMethod(Class<?> clazz, XlsxField xlsColumnField) throws NoSuchMethodException {
        Method method;
        try {
            method = clazz.getMethod("get" + capitalize(xlsColumnField.getFieldName()));
        } catch (NoSuchMethodException nme) {
            method = clazz.getMethod(xlsColumnField.getFieldName());
        }

        return method;
    }

    public static long processTime(long start) {
        return (System.currentTimeMillis() - start) / 1000;
    }

    public static void autoSizeColumns(Sheet sheet, int noOfColumns) {
        for (int i = 0; i < noOfColumns; i++) {
            sheet.autoSizeColumn((short) i);
        }
    }

    public static Row getOrCreateNextRow(Sheet sheet, int rowNo) {
        Row row;
        if (sheet.getRow(rowNo) != null) {
            row = sheet.getRow(rowNo);
        } else {
            row = sheet.createRow(rowNo);
        }
        return row;
    }

    public static CellStyle setCurrencyCellStyle(Workbook workbook) {
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setWrapText(true);
        DataFormat df = workbook.createDataFormat();
        currencyStyle.setDataFormat(df.getFormat("#0.00"));
        return currencyStyle;
    }

    public static Font getBoldFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) (10 * 20));
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    public static Font getGenericFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeight((short) (10 * 20));
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    public static CellStyle getCenterAlignedCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        return cellStyle;
    }

    public static CellStyle getLeftAlignedCellStyle(Workbook workbook, Font font) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        return cellStyle;
    }

}
