package com.yuand.study.xls;

import com.yuand.study.domain.Postal;
import com.yuand.study.domain.PostalAnalyse;
import com.yuand.study.domain.Report;
import com.yuand.study.domain.SortReport;
import com.yuand.study.util.ExcelUtils;
import com.yuand.study.util.POIExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/6/19 0019.
 */
public class PasePostal {

    private static final Map<Integer, String[]> fileHeader = new HashMap();

    private List<PostalAnalyse> fastPostalList = new ArrayList<PostalAnalyse>();

    private List<PostalAnalyse> standPostalList = new ArrayList<PostalAnalyse>();

    private Map<String, SortReport> sortReportMap = new HashMap<String, SortReport>();

    private Map<String, Report> custormerReportMap = new HashMap<String, Report>();

    private Map<String, Report> courierReportMap = new HashMap<String, Report>();

    static {
        /**
         * 数据源
         * header:"邮件号","收寄时间","机构名称","产品名称","大客户","寄达地","揽收员","重量","长","宽","高","体积重",计费重量,优惠率,实际优惠率,邮资,保价金额,保险金额,总邮资,标准邮资,件数,内件数
         * headerSize: 22

         * 标间收支（产品名称不包含《国内快递包裹物品经济时限》）  快包收支（产品名称为《国内快递包裹物品经济时限》）
         * header:"邮件号","收寄时间","机构名称","产品名称","大客户","寄达地","揽收员","重量","长","宽","高","体积重",
         *  计费重量,优惠率,实际优惠率,邮资,保价金额,保险金额,总邮资,标准邮资,件数,内件数,
         *  业务量（件）,总重量（千克）,费用合计,揽收,内部处理（经转+分拣+投递）,航空,一干陆运,一干陆运
         *
         * 业务量（件） = 件数
         * 总重量（千克） = 计费重量/1000
         * 揽收 = 总邮资 * 0.15
         * 内部处理（经转+分拣+投递）(IF(总重量/业务量<=3,2.1,IF(总重量/业务量<=5,(ROUNDUP(总重量/业务量,0)-3)*0.6+2.1,(ROUNDUP(总重量/业务量,0)-5)*0.7+3.3))+总重量*0.09)*业务量
         * 航空 = SUMIF(单价表!A:A,寄达地,单价表!E:E)*总重量
         * 一干陆运 = SUMIF(单价表!A:A,寄达地,单价表!M:M)*总重量
         * 二干陆运 = SUMIF(单价表!A:A,寄达地,单价表!G:G)*总重量
         *
         * 费用合计 = 揽收 + 内部处理 + 航空 + 一干陆运 + 二干陆运
         *
         * headerSize : 30

         * 基础表
         * header: "省份名称","地名名称","分区","省会","省内运程(km)","到省运程(km)","省外运程(km)","总运程(km)"
         * headerSize 8
         *
         * 单价表
         * header: '类型（省际部分-标准、省际部分-快包、省内部分）', "起","始","航空公司","距离","内部处理结算","航空结算","一干陆运结算","二干陆运结算",
         * headerSize 8
         */
        //数据源
        String[] header1 = new String[]{"邮件号", "收寄时间", "机构名称", "产品名称", "大客户", "寄达地", "揽收员", "重量", "长", "宽",
                "高", "体积重", "计费重量", "优惠率", "实际优惠率", "邮资", "保价金额", "保险金额", "总邮资", "标准邮资", "件数", "内件数"};
        //标间收支  快包收支
        String[] header2 = new String[]{"邮件号", "收寄时间", "机构名称", "产品名称", "大客户", "寄达地", "揽收员", "重量", "长", "宽",
                "高", "体积重", "计费重量", "优惠率", "实际优惠率", "邮资", "保价金额", "保险金额", "总邮资", "标准邮资", "件数", "内件数",
                "业务量（件）", "总重量（千克）", "费用合计", "揽收", "内部处理（经转+分拣+投递）", "航空", "一干陆运", "一干陆运"
        };
        //基础表
        String[] header3 = new String[]{"省份名称", "地名名称", "分区", "省会", "省内运程(km)", "到省运程(km)", "省外运程(km)", "总运程(km)"};
        //单价表
        String[] header4 = new String[]{"类型（省际部分-标准、省际部分-快包、省内部分）", "起", "始", "航空公司", "距离", "内部处理结算", "航空结算", "一干陆运结算", "二干陆运结算"};
        //客户统计
        String[] header5 = new String[]{"大客户", "件数", "收入", "成本"};
        //揽收员统计
        String[] header6 = new String[]{"揽收员", "件数", "收入", "成本"};

        String[] header7 = new String[]{"机构名称", "标件业务量", "标件业务收入", "标件成本", "快包业务量", "快包业务收入", "快包成本"};

        String[] header8 = new String[]{"机构名称", "合计业务量", "合计业务收入", "合计成本", "直接毛利"};

        fileHeader.put(1, header1);
        fileHeader.put(2, header2);
        fileHeader.put(3, header3);
        fileHeader.put(4, header4);
        fileHeader.put(5, header5);
        fileHeader.put(6, header6);
        fileHeader.put(7, header7);
        fileHeader.put(8, header8);
    }

    /**
     * 分析数据源
     *
     * @param file
     * @return
     * @throws Exception
     */
    public void analyseFile(File file) throws Exception {
        //分析数据
        List<Postal> result = checkFileName(file);
        //导出excel
        if (result != null && result.size() > 0) {
            exitOutExcel();
        }


    }

    private void exitOutExcel() {
        Workbook wb = new XSSFWorkbook();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            writeExcel(wb, fileHeader.get(2), standPostalList, "标件收支");
            writeExcel(wb, fileHeader.get(2), fastPostalList, "快件收支");
            writeReportExcel(wb, fileHeader.get(5), custormerReportMap, "大客户统计");
            writeReportExcel(wb, fileHeader.get(6), courierReportMap, "揽收员统计");
            writeSortReportExcel(wb, sortReportMap, "分类统计");


            String pathname = getJarDir() + "/分析统计"+format.format(new Date())+".xls";
            // 文件流
            File file = new File(pathname);
            OutputStream os = new FileOutputStream(file);
            os.flush();
            wb.write(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private static void writeExcel(Workbook wb, String[] header, List<PostalAnalyse> postalList, String sheetName) throws IOException {
        CellStyle style = ExcelUtils.getCellStyle(wb);
        Sheet sheet = wb.createSheet(sheetName);//在创建爱你表单的时候指定表单的名字

        /**
         * 设置Excel表的第一行即表头
         */
        Row row = sheet.createRow(0);
        for (int k = 0; k < header.length; k++) {
            Cell headCell = row.createCell(k);
            headCell.setCellStyle(style);// 设置表头样式
            headCell.setCellType(CellType.STRING);// 设置这个单元格的数据的类型,是文本类型还是数字类型
            headCell.setCellValue(String.valueOf(header[k]));// 给这个单元格设置值
        }

        for (int j = 0; j < postalList.size(); j++) {
            PostalAnalyse postalAnalyse = postalList.get(j);
            Row rowdata = sheet.createRow(j + 1);// 创建数据行
            for (int i = 0; i < header.length; i++) {
                Cell celldata = rowdata.createCell(i);
                //邮编号
                if (i == 0) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getPostalNo());
                    continue;
                }
                //收寄时间
                if (i == 1) {
                    celldata.setCellType(CellType.STRING);
                    celldata.setCellValue(postalAnalyse.getPostal().getSendTime());
                    continue;
                }
                //机构名称
                if (i == 2) {
                    celldata.setCellType(CellType.STRING);
                    celldata.setCellValue(postalAnalyse.getPostal().getSendTime());
                    continue;
                }
                //产品名称
                if (i == 3) {
                    celldata.setCellType(CellType.STRING);
                    celldata.setCellValue(postalAnalyse.getPostal().getProductName());
                    continue;
                }
                //大客户
                if (i == 4) {
                    celldata.setCellType(CellType.STRING);
                    celldata.setCellValue(postalAnalyse.getPostal().getCustomerName());
                    continue;
                }
                //寄达地
                if (i == 5) {
                    celldata.setCellType(CellType.STRING);
                    celldata.setCellValue(postalAnalyse.getPostal().getAddress());
                    continue;
                }
                //揽收员
                if (i == 6) {
                    celldata.setCellType(CellType.STRING);
                    celldata.setCellValue(postalAnalyse.getPostal().getCourier());
                    continue;
                }
                //重量
                if (i == 7) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getWeight());
                    continue;
                }
                //长
                if (i == 8) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getLen());
                    continue;
                }
                //宽
                if (i == 9) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getWidth());
                    continue;
                }
                //高
                if (i == 10) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getHeight());
                    continue;
                }
                //体积重
                if (i == 11) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getVolumeWeight());
                    continue;
                }
                //计费重量
                if (i == 12) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getChargedWeight());
                    continue;
                }
                //优惠率
                if (i == 13) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getRate());
                    continue;
                }
                //实际优惠率
                if (i == 14) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getEffectiveRate());
                    continue;
                }
                //邮资
                if (i == 15) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getPostage());
                    continue;
                }
                //保价金额
                if (i == 16) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getBjAmount());
                    continue;
                }
                //保险金额
                if (i == 17) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getInsuredAmount());
                    continue;
                }
                //总邮资
                if (i == 18) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getTotalPostage());
                    continue;
                }
                //标准邮资
                if (i == 19) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getStandPostage());
                    continue;
                }
                //件数
                if (i == 20) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getNum());
                    continue;
                }
                //内件数
                if (i == 21) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getPostal().getInnerNum());
                    continue;
                }
                //业务量（件）
                if (i == 22) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getBusNum());
                    continue;
                }
                //总重量（千克）
                if (i == 23) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getTotalWeight());
                    continue;
                }
                //揽收
                if (i == 24) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getFree());
                    continue;
                }
                //揽收
                if (i == 25) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getFree());
                    continue;
                }
                //内部处理（经转+分拣+投递）
                if (i == 26) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getInnerFree());
                    continue;
                }
                //航空
                if (i == 27) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getAirfreight());
                    continue;
                }
                //一干陆运
                if (i == 28) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getOneFree());
                    continue;
                }
                //二干陆运
                if (i == 29) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getTwoFree());
                    continue;
                }
                //费用合计
                if (i == 30) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(postalAnalyse.getTotalFree());
                    continue;
                }
            }
        }
    }

    private static void writeSortReportExcel(Workbook wb, Map<String, SortReport> reportMap, String sheetName) {
        Sheet sheet = wb.createSheet(sheetName);//在创建爱你表单的时候指定表单的名字
        CellStyle style = ExcelUtils.getCellStyle(wb);
        /**
         * 设置分类统计的第一行即表头
         */
        Row row = sheet.createRow(0);
        for (int k = 0; k < fileHeader.get(7).length; k++) {
            Cell headCell = row.createCell(k);
            headCell.setCellType(CellType.STRING);// 设置这个单元格的数据的类型,是文本类型还是数字类型
            headCell.setCellStyle(style);// 设置表头样式
            headCell.setCellValue(String.valueOf(fileHeader.get(7)[k]));// 给这个单元格设置值
        }
        Iterator<String> iterator = reportMap.keySet().iterator();
        int j = 0;
        int totalBjNum = 0;
        double totalBjIncome = 0.0;
        double totalBjCost = 0.0;
        int totalKbNum = 0;
        double totalKbIncome = 0.0;
        double totalKbCost = 0.0;
        while (iterator.hasNext()) {
            SortReport report = reportMap.get(iterator.next());
            totalBjNum += report.getBjNum();
            totalBjIncome += report.getBjIncome();
            totalBjCost += report.getBjCost();
            totalKbNum += report.getKbNum();
            totalKbIncome += report.getKbIncome();
            totalKbCost += report.getKbCost();

            Row rowdata = sheet.createRow(j + 1);// 创建数据行
            for (int i = 0; i < fileHeader.get(7).length; i++) {
                Cell celldata = rowdata.createCell(i);// 创建单元格
                if (i == 0) {
                    celldata.setCellType(CellType.STRING);
                    celldata.setCellValue(report.getKey());
                    continue;
                }
                if (i == 1) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getBjNum());
                    continue;
                }
                if (i == 2) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getBjIncome());
                    continue;
                }
                if (i == 3) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getBjCost());
                    continue;
                }
                if (i == 4) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getKbNum());
                    continue;
                }
                if (i == 5) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getKbIncome());
                    continue;
                }
                if (i == 6) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getKbCost());
                    continue;
                }
            }
            j++;
        }

        Row totalRow = sheet.createRow(j + 1);// 创建数据行
        for (int i = 0; i < fileHeader.get(7).length; i++) {
            Cell celldata = totalRow.createCell(i);// 创建单元格
            if (i == 0) {
                celldata.setCellType(CellType.STRING);
                celldata.setCellValue("合计");
                continue;
            }
            if (i == 1) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalBjNum);
                continue;
            }
            if (i == 2) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalBjIncome);
                continue;
            }
            if (i == 3) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalBjCost);
                continue;
            }
            if (i == 4) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalKbNum);
                continue;
            }
            if (i == 5) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalKbIncome);
                continue;
            }
            if (i == 6) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalKbCost);
                continue;
            }
        }
        j++;

        /**
         * 设置分类统计的第二张表的表头
         */
        Row row2 = sheet.createRow(++j);
        for (int k = 0; k < fileHeader.get(8).length; k++) {
            Cell headCell = row2.createCell(k);
            headCell.setCellType(CellType.STRING);// 设置这个单元格的数据的类型,是文本类型还是数字类型
            headCell.setCellStyle(style);// 设置表头样式
            headCell.setCellValue(String.valueOf(fileHeader.get(8)[k]));// 给这个单元格设置值
        }

        Iterator<String> it = reportMap.keySet().iterator();
        while (it.hasNext()) {
            SortReport report = reportMap.get(it.next());
            Row rowdata = sheet.createRow(j + 1);// 创建数据行
            for (int i = 0; i < fileHeader.get(8).length; i++) {
                Cell celldata = rowdata.createCell(i);// 创建单元格
                if (i == 0) {
                    celldata.setCellType(CellType.STRING);
                    celldata.setCellValue(report.getKey());
                    continue;
                }
                if (i == 1) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getBjNum() + report.getKbNum());
                    continue;
                }
                if (i == 2) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getBjIncome() + report.getKbIncome());
                    continue;
                }
                if (i == 3) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getBjCost() + report.getKbCost());
                    continue;
                }
                if (i == 4) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getBjIncome() + report.getKbIncome() - report.getBjCost() - report.getKbCost());
                    continue;
                }
            }
            j++;
        }
        Row rowdata = sheet.createRow(j + 1);// 创建数据行
        for (int i = 0; i < fileHeader.get(8).length; i++) {
            Cell celldata = rowdata.createCell(i);// 创建单元格
            if (i == 0) {
                celldata.setCellType(CellType.STRING);
                celldata.setCellValue("合计");
                continue;
            }
            if (i == 1) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalBjNum + totalKbNum);
                continue;
            }
            if (i == 2) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalBjIncome + totalKbIncome);
                continue;
            }
            if (i == 3) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalBjCost + totalKbCost);
                continue;
            }
            if (i == 4) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalBjIncome + totalKbIncome - totalBjCost - totalKbCost);
                continue;
            }
        }

    }

    private static void writeReportExcel(Workbook wb, String[] header, Map<String, Report> reportMap, String sheetName) {
        Sheet sheet = wb.createSheet(sheetName);//在创建爱你表单的时候指定表单的名字
        CellStyle style = ExcelUtils.getCellStyle(wb);
        /**
         * 设置Excel表的第一行即表头
         */
        Row row = sheet.createRow(0);
        for (int k = 0; k < header.length; k++) {
            Cell headCell = row.createCell(k);
            headCell.setCellType(CellType.STRING);// 设置这个单元格的数据的类型,是文本类型还是数字类型
            headCell.setCellStyle(style);// 设置表头样式
            headCell.setCellValue(String.valueOf(header[k]));// 给这个单元格设置值
        }
        Iterator<String> iterator = reportMap.keySet().iterator();
        int j = 0;
        int totalNum = 0;
        double totalIncome = 0.0;
        double totalCost = 0.0;
        while (iterator.hasNext()) {
            Report report = reportMap.get(iterator.next());
            totalNum += report.getNum();
            totalIncome += report.getIncome();
            totalCost += report.getCost();
            Row rowdata = sheet.createRow(j + 1);// 创建数据行
            for (int i = 0; i < header.length; i++) {
                Cell celldata = rowdata.createCell(i);// 创建单元格
                if (i == 0) {
                    celldata.setCellType(CellType.STRING);
                    celldata.setCellValue(report.getKey());
                    continue;
                }
                if (i == 1) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getNum());
                    continue;
                }
                if (i == 2) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getIncome());
                    continue;
                }
                if (i == 3) {
                    celldata.setCellType(CellType.NUMERIC);
                    celldata.setCellValue(report.getCost());
                    continue;
                }
            }
            j++;
        }
        Row rowdata = sheet.createRow(j + 1);// 创建数据行
        for (int i = 0; i < header.length; i++) {
            Cell celldata = rowdata.createCell(i);// 创建单元格
            if (i == 0) {
                celldata.setCellType(CellType.STRING);
                celldata.setCellValue("合计");
                continue;
            }
            if (i == 1) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalNum);
                continue;
            }
            if (i == 2) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalIncome);
                continue;
            }
            if (i == 3) {
                celldata.setCellType(CellType.NUMERIC);
                celldata.setCellValue(totalCost);
                continue;
            }
        }

    }


    private List<Postal> checkFileName(File file) throws Exception {
        String fileName = file.getName();
        if (fileName.contains("test")) {
            List<ArrayList<String>> datas = getFileDatas(file, fileName);
            return parsePostal(datas);
        }
        return null;
    }

    private List<ArrayList<String>> getFileDatas(File file, String fileName) throws Exception {
        InputStream is = new FileInputStream(file);
        Workbook wb = new POIExcelUtil().validateExcel(fileName, is);
        POIExcelUtil poiExcelUtil = new POIExcelUtil();
        List<ArrayList<String>> datas = poiExcelUtil.read(wb);
        is.close();
        return datas;
    }

    /**
     * 数据源数据解析
     * header:交易日期|主机交易流水号|借方发生额|贷方发生额|账户余额|凭证号|摘要|对方账号|对方账号名称|对方开户行|交易时间
     * headerSize: 11
     *
     * @param datas
     * @return
     */
    private List<Postal> parsePostal(List<ArrayList<String>> datas) throws Exception {
        List<Postal> postalList = new ArrayList<Postal>();
        for (int j = 3; j < datas.size(); j++) {
            List<String> line = datas.get(j);
            Postal postal = new Postal();
            for (int i = 0; i < line.size(); i++) {
                try {
                    String field = formatField(line.get(i));
                    if (StringUtils.isAllEmpty(field)) {
                        continue;
                    }
                    //邮编号
                    if (i == 0) {
                        postal.setPostalNo(field);
                        continue;
                    }
                    //收寄时间
                    if (i == 1) {
                        postal.setSendTime(field);
                        continue;
                    }
                    //机构名称
                    if (i == 2) {
                        postal.setOrgName(field);
                        continue;
                    }
                    //产品名称
                    if (i == 3) {
                        postal.setProductName(field);
                        continue;
                    }
                    //大客户
                    if (i == 4) {
                        postal.setCustomerName(field);
                        continue;
                    }
                    //寄达地
                    if (i == 5) {
                        postal.setAddress(field);
                        continue;
                    }
                    //揽收员
                    if (i == 6) {
                        postal.setCourier(field);
                        continue;
                    }
                    //重量
                    if (i == 7) {
                        Integer weight = field == "" ? 0 : new Integer(field);
                        postal.setWeight(weight);
                        continue;
                    }
                    //长
                    if (i == 8) {
                        Integer len = field == "" ? 0 : new Integer(field);
                        postal.setLen(len);
                        continue;
                    }
                    //宽
                    if (i == 9) {
                        Integer width = field == "" ? 0 : new Integer(field);
                        postal.setWidth(width);
                        continue;
                    }
                    //高
                    if (i == 10) {
                        Integer height = field == "" ? 0 : new Integer(field);
                        postal.setHeight(height);
                        continue;
                    }
                    //体积重
                    if (i == 11) {
                        Integer volumeWeight = field == "" ? 0 : new Integer(field);
                        postal.setVolumeWeight(volumeWeight);
                        continue;
                    }
                    //计费重量
                    if (i == 12) {
                        Integer chargedWeight = field == "" ? 0 : new Integer(field);
                        postal.setChargedWeight(chargedWeight);
                        continue;
                    }
                    //优惠率
                    if (i == 13) {
                        Double rate = field == "" ? 0 : new Double(field);
                        postal.setRate(rate);
                        continue;
                    }
                    //实际优惠率
                    if (i == 14) {
                        Double effectiveRate = field == "" ? 0 : new Double(field);
                        postal.setEffectiveRate(effectiveRate);
                        continue;
                    }
                    //邮资
                    if (i == 15) {
                        Double postage = field == "" ? 0 : new Double(field);
                        postal.setPostage(postage);
                        continue;
                    }
                    //保价金额
                    if (i == 16) {
                        Double bjAmount = field == "" ? 0 : new Double(field);
                        postal.setBjAmount(bjAmount);
                        continue;
                    }
                    //保险金额
                    if (i == 17) {
                        Double insuredAmount = field == "" ? 0 : new Double(field);
                        postal.setInsuredAmount(insuredAmount);
                        continue;
                    }
                    //总邮资
                    if (i == 18) {
                        Double totalPostage = field == "" ? 0 : new Double(field);
                        postal.setTotalPostage(totalPostage);
                        continue;
                    }
                    //标准邮资
                    if (i == 19) {
                        Double standPostage = field == "" ? 0 : new Double(field);
                        postal.setStandPostage(standPostage);
                        continue;
                    }
                    //件数
                    if (i == 20) {
                        Integer num = field == "" ? 0 : new Integer(field);
                        postal.setNum(num);
                        continue;
                    }
                    //内件数
                    if (i == 21) {
                        Integer innerNum = field == "" ? 0 : new Integer(field);
                        postal.setInnerNum(innerNum);
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("第：" + j + "行数据有问题,解析数据项：" + line.get(i) + "出错");
                }
            }
            //统计标件收支和快件收支
            if (postal.getProductName().contains("国内快递包裹物品经济时限")) {
                PostalAnalyse fastPostal = new PostalAnalyse(postal, 2);
                fastPostalList.add(fastPostal);
                statisticsPostal(fastPostal, 2);

            } else {
                PostalAnalyse standPostal = new PostalAnalyse(postal, 1);
                standPostalList.add(standPostal);
                statisticsPostal(standPostal, 1);
            }
            postalList.add(postal);

        }
        return postalList;
    }

    private void statisticsPostal(PostalAnalyse postalAnalyse, int type) {
        //分类统计 标件和快包
        SortReport report = getSortReportMap(sortReportMap, postalAnalyse.getPostal().getOrgName());
        if (type == 1) {
            report.setKey(postalAnalyse.getPostal().getOrgName());
            report.setBjNum(report.getBjNum() + postalAnalyse.getBusNum());
            report.setBjIncome(report.getBjIncome() + postalAnalyse.getPostal().getTotalPostage());
            report.setBjCost(report.getBjCost() + postalAnalyse.getTotalFree());
        } else if (type == 2) {
            report.setKey(postalAnalyse.getPostal().getOrgName());
            report.setKbNum(report.getKbNum() + postalAnalyse.getBusNum());
            report.setKbIncome(report.getKbIncome() + postalAnalyse.getPostal().getTotalPostage());
            report.setKbCost(report.getKbCost() + postalAnalyse.getTotalFree());
        }
        sortReportMap.put(postalAnalyse.getPostal().getOrgName(), report);

        //大客户统计
        Report customerReport = getReportMap(custormerReportMap, postalAnalyse.getPostal().getCustomerName());
        customerReport.setKey(postalAnalyse.getPostal().getCustomerName());
        customerReport.setNum(customerReport.getNum() + postalAnalyse.getBusNum());
        customerReport.setIncome(customerReport.getIncome() + postalAnalyse.getPostal().getTotalPostage());
        customerReport.setCost(customerReport.getCost() + postalAnalyse.getTotalFree());
        custormerReportMap.put(postalAnalyse.getPostal().getCustomerName(), customerReport);

        //揽收员统计
        Report courierReport = getReportMap(courierReportMap, postalAnalyse.getPostal().getCourier());
        courierReport.setKey(postalAnalyse.getPostal().getCourier());
        courierReport.setNum(courierReport.getNum() + postalAnalyse.getBusNum());
        courierReport.setIncome(courierReport.getIncome() + postalAnalyse.getPostal().getTotalPostage());
        courierReport.setCost(courierReport.getCost() + postalAnalyse.getTotalFree());
        courierReportMap.put(postalAnalyse.getPostal().getCourier(), courierReport);

    }

    private SortReport getSortReportMap(Map<String, SortReport> map, String key) {
        if (map.get(key) != null) {
            return map.get(key);
        } else {
            return new SortReport();
        }
    }

    private Report getReportMap(Map<String, Report> map, String key) {
        if (map.get(key) != null) {
            return map.get(key);
        } else {
            return new Report();
        }
    }


    private String formatField(String field) {
        if (!StringUtils.isAnyEmpty(field)) {
            return field.replace("\"", "").trim();
        }
        return "";
    }


    public static void main(String[] args) {
        System.out.println("当前路径："+ getJarPath());
        System.out.println("当前目录："+ getJarDir());
        if(args.length < 1){
            System.out.println("参数必须包含读取文件路径");
            System.out.println("usage:\n" +
                    "java -jar xls-test.jar inputFile.xls");
            return;
        }else{
            if(args[0].endsWith(".xls") || args[0].endsWith(".xlsx") ){
                System.out.println("分析的文件:"+ args[0]);
                try {
                    new PasePostal().analyseFile(new File(args[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("文件类型不正确");
            }

        }
    }
    /**
     * 获取jar绝对路径
     *
     * @return
     */
    public static String getJarPath()
    {
        File file = getFile();
        if (file == null)
            return null;
        return file.getAbsolutePath();
    }

    /**
     * 获取jar目录
     *
     * @return
     */
    public static String getJarDir()
    {
        File file = getFile();
        if (file == null)
            return null;
        return getFile().getParent();
    }

    /**
     * 获取jar包名
     *
     * @return
     */
    public static String getJarName()
    {
        File file = getFile();
        if (file == null)
            return null;
        return getFile().getName();
    }

    /**
     * 获取当前Jar文件
     *
     * @return
     */
    private static File getFile()
    {
        // 关键是这行...
        String path = PasePostal.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try
        {
            path = java.net.URLDecoder.decode(path, "UTF-8"); // 转换处理中文及空格
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            return null;
        }
        return new File(path);
    }


}
