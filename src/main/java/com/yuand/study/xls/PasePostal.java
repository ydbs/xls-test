package com.yuand.study.xls;

import com.yuand.study.util.POIExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/19 0019.
 */
public class PasePostal {

    private static final Map<Integer, String[]> fileHeader = new HashMap();

    static {
        /**
         * 民生银行数据解析
         * header:交易日期","主机交易流水号","借方发生额","贷方发生额","账户余额","凭证号","摘要","对方账号","对方账号名称","对方开户行","交易时间
         * headerSize: 11

         * 建行数据解析
         * header:"记账日期","交易时间","凭证种类","凭证号","借方发生额/元","贷方发生额/元","余额/元","钞汇标志","对方户名","对方账号","摘要","备注",
         *        "账户明细编号-交易流水号","企业流水号","本方账号","本方账户名称","本方账户开户机构"
         * headerSize : 17

         * 工行数据解析
         * header:
         * old      "凭证号","本方账号","对方账号","交易时间","借/贷","借方发生额","贷方发生额","对方行号","摘要","用途","对方单位名称","余额","个性化信息" 13
         * new      "本方账号","对方账号","交易时间","借/贷","金额","凭证号","对方单位","对方行号","用途","摘要","附言","个性化信息" 12
         * headerSize 13

         * 平安银行数据解析
         * header:"交易日期","账号","借","贷","账户余额","对方账户","对方账户名称","交易流水号","摘要","用途"
         * headerSize:10

         * 农行数据解析
         * headers:
         * old      "交易日期","交易时间戳","收入金额","支出金额","本次余额","手续费总额","交易方式","交易行名","交易类别","对方省市","对方账号","对方户名","交易说明","交易摘要","交易附言" 15
         * new      "交易时间","收入金额","支出金额","账户余额","交易行名","对方省市","对方账号","对方户名","交易用途" 9
         * headerSize:15
         */
        //民生
        String[] header1 = new String[]{"交易日期", "主机交易流水号", "借方发生额", "贷方发生额", "账户余额", "凭证号", "摘要", "对方账号", "对方账号名称", "对方开户行", "交易时间"};
        //建行
        String[] header2 = new String[]{"记账日期", "交易时间", "凭证种类", "凭证号", "借方", "贷方", "余额/元", "钞汇标志", "对方户名", "对方账号", "摘要", "备注",
                "账户明细编号-交易流水号", "企业流水号", "本方账号", "本方账户名称", "本方账户开户机构"};
        //工行
        String[] header3 = new String[]{"本方账号", "对方账号", "交易时间", "借/贷", "金额", "凭证号", "对方单位", "对方行号", "用途", "摘要", "附言", "个性化信息"};
        //平安
        String[] header4 = new String[]{"交易日期", "账号", "借", "贷", "账户余额", "对方账户", "对方账户名称", "交易流水号", "摘要", "用途"};
        //农行
        String[] header5 = new String[]{"交易时间", "收入金额", "支出金额", "账户余额", "交易行名", "对方省市", "对方账号", "对方户名", "交易用途"};
        fileHeader.put(1, header1);
        fileHeader.put(2, header2);
        fileHeader.put(3, header3);
        fileHeader.put(4, header4);
        fileHeader.put(5, header5);
        fileHeader.put(6, header1);
    }

    public Map<String, String> receiptVoucherImport(File file, Integer bankId) throws Exception {
        String result = checkFileName(file, bankId);
        HashMap<String, String> map = new HashMap();
        map.put("result", result);
        return map;
    }

    private String checkFileName(File file, Integer bankId) throws Exception {
        String fileName = file.getName();
        if (fileName.contains("民生银行") && (bankId == 1 || bankId == 6)) {//民生银行=>贷方发生额(收)
            List<ArrayList<String>> datas = getFileDatas(file, fileName, bankId);
            return parseCMBC(datas, bankId);
        }
        return "";
    }

    private List<ArrayList<String>> getFileDatas(File file, String fileName, Integer bankId) throws Exception {
        InputStream is = new FileInputStream(file);
        Workbook wb = new POIExcelUtil().validateExcel(fileName, is);
        POIExcelUtil poiExcelUtil = new POIExcelUtil();
        List<ArrayList<String>> datas = poiExcelUtil.read(wb);
        checkFileHead(datas, bankId);
        is.close();
        return datas;
    }

    private void checkFileHead(List<ArrayList<String>> datas, Integer bankId ) throws Exception {
        List<String> headerList = null;
        String[] correctHead = fileHeader.get(bankId);
        if (bankId == 1 || bankId == 6) {//民生银行
            headerList = datas.get(11);
        }
        if (bankId == 2) {//建设银行
            headerList = datas.get(5);
            headerList.set(4, "借方");
            headerList.set(5, "贷方");
        }
        if (bankId == 3) {//工商银行
            headerList = datas.get(2);
        }
        if (bankId == 4) {//平安银行
            headerList = datas.get(0);
            return;
        }
        if (bankId == 5) {//农业银行
            headerList = datas.get(0);
        }
    }

    /**
     * 民生银行数据解析
     * header:交易日期|主机交易流水号|借方发生额|贷方发生额|账户余额|凭证号|摘要|对方账号|对方账号名称|对方开户行|交易时间
     * headerSize: 11
     *
     * @param datas
     * @return
     */
    private String parseCMBC(List<ArrayList<String>> datas, Integer bankId) throws Exception {
        for (int j = 12; j < datas.size(); j++) {
            boolean isAdd = true;
            List<String> line = datas.get(j);
            for (int i = 0; i < line.size(); i++) {
                try {
                    String field = formatField(line.get(i));
                    if (StringUtils.isAllEmpty(field)) {
                        continue;
                    }
                    //交易日期 ==> 记账日期（交易日期）
                    if (i == 0) {
                        //receiptLinesDto.setPaymentDate(field);
                        continue;
                    }
                    //交易流水号 ==> 银行流水号
                    if (i == 1) {
                        //receiptLinesDto.setBankJournalNo(field);
                        continue;
                    }
                    //借方发生额 ==> 借方发生额
                    if (i == 2) {
                        Double debit = new Double(field);
                        if (debit > 0) {
                            isAdd = false;
                            break;
                        }
                        continue;
                    }
                    //贷方发生额(收款) ==> 贷方发生额
                    //民生银行=>贷方发生额
                    if (i == 3) {
                        Double credit = new Double(field);
                        //receiptLinesDto.setCreditAmount(credit);
                        //receiptLinesDto.setReceiptAmount(credit);
                        continue;
                    }
                    //账户余额 ==> 当前余额
                    if (i == 4) {
                        //receiptLinesDto.setBalance(new Double(field));
                        continue;
                    }
                    //凭证号 ==> 凭证号
                    if (i == 5) {
                        //receiptLinesDto.setVoucher(field);
                        continue;
                    }
                    //摘要 ==> 摘要备注
                    if (i == 6) {
                        if ("手续费".equals(field)) {
                            isAdd = false;
                            break;
                        }
                       // receiptLinesDto.setRemark(field);
                        continue;
                    }
                    //对方账号 ==> 对方账号
                    if (i == 7) {
                        //receiptLinesDto.setOtherAccount(field);
                        continue;
                    }
                    //对方账号名称 ==>对方账号名称
                    if (i == 8) {
                        //receiptLinesDto.setOtherName(field);
                        continue;
                    }
                    //对方开户行
                    if (i == 9) {
                        //receiptLinesDto.setOtherBankName(field);
                        continue;
                    }
                    //交易时间 083533(时分秒)
                    if (i == 10) {
                        if (field.length() == 5) {
                            field = "0" + field;
                        }
                        if (field.length() == 4) {
                            field = field + "00";
                        }
                        if (field.length() == 4) {
                            field = field + "00";
                        }
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("第：" + j + "行数据有问题,解析数据项：" + line.get(i) + "出错");
                }
            }

        }
        return "";
    }

    private String formatField(String field) {
        if (!StringUtils.isAnyEmpty(field)) {
            return field.replace("\"", "").trim();
        }
        return "";
    }





}
