package com.yuand.study.xls;

import com.yuand.study.domain.Postal;
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
        String[] header1 = new String[]{"邮件号","收寄时间","机构名称","产品名称","大客户","寄达地","揽收员","重量","长","宽",
                "高","体积重","计费重量","优惠率","实际优惠率","邮资","保价金额","保险金额","总邮资","标准邮资","件数","内件数"};
        //标间收支  快包收支
        String[] header2 = new String[]{"邮件号","收寄时间","机构名称","产品名称","大客户","寄达地","揽收员","重量","长","宽",
                "高","体积重","计费重量","优惠率","实际优惠率","邮资","保价金额","保险金额","总邮资","标准邮资","件数","内件数",
                "业务量（件）","总重量（千克）","费用合计","揽收","内部处理（经转+分拣+投递）","航空","一干陆运","一干陆运"
        };
        //基础表
        String[] header3 = new String[]{"省份名称","地名名称","分区","省会","省内运程(km)","到省运程(km)","省外运程(km)","总运程(km)"};
        //单价表
        String[] header4 = new String[]{"类型（省际部分-标准、省际部分-快包、省内部分）", "起","始","航空公司","距离","内部处理结算","航空结算","一干陆运结算","二干陆运结算"};
        fileHeader.put(1, header1);
        fileHeader.put(2, header2);
        fileHeader.put(3, header3);
        fileHeader.put(4, header4);
    }

    /**
     * 分析数据源
     * @param file
     * @param bankId
     * @return
     * @throws Exception
     */
    public Map<String, List<Postal>> analyseFile(File file, Integer bankId) throws Exception {
        List<Postal> result = checkFileName(file, bankId);
        HashMap<String, List<Postal>> map = new HashMap();
        map.put("result", result);
        return map;
    }

    private List<Postal> checkFileName(File file, Integer bankId) throws Exception {
        String fileName = file.getName();
        if (fileName.contains("test") && (bankId == 1 || bankId == 6)) {
            List<ArrayList<String>> datas = getFileDatas(file, fileName, bankId);
            return parsePostal(datas, bankId);
        }
        return null;
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
        if (bankId == 1 || bankId == 6) {
            headerList = datas.get(11);
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
    private List<Postal> parsePostal(List<ArrayList<String>> datas, Integer bankId) throws Exception {
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
                        Integer weight = field == ""?0:new Integer(field);
                        postal.setWeight(weight);
                        continue;
                    }
                    //长
                    if (i == 8) {
                        Integer len = field == ""?0:new Integer(field);
                        postal.setLen(len);
                        continue;
                    }
                    //宽
                    if (i == 9) {
                        Integer width = field == ""?0:new Integer(field);
                        postal.setWidth(width);
                        continue;
                    }
                    //高
                    if (i == 10) {
                        Integer height = field == ""?0:new Integer(field);
                        postal.setHeight(height);
                        continue;
                    }
                    //体积重
                    if (i == 11) {
                        Integer volumeWeight = field == ""?0:new Integer(field);
                        postal.setVolumeWeight(volumeWeight);
                        continue;
                    }
                    //计费重量
                    if (i == 12) {
                        Integer chargedWeight = field == ""?0:new Integer(field);
                        postal.setChargedWeight(chargedWeight);
                        continue;
                    }
                    //优惠率
                    if (i == 13) {
                        Double rate = field == ""?0:new Double(field);
                        postal.setRate(rate);
                        continue;
                    }
                    //实际优惠率
                    if (i == 14) {
                        Double effectiveRate = field == ""?0:new Double(field);
                        postal.setEffectiveRate(effectiveRate);
                        continue;
                    }
                    //邮资
                    if (i == 15) {
                        Double postage = field == ""?0:new Double(field);
                        postal.setPostage(postage);
                        continue;
                    }
                    //保价金额
                    if (i == 16) {
                        Double bjAmount = field == ""?0:new Double(field);
                        postal.setBjAmount(bjAmount);
                        continue;
                    }
                    //保险金额
                    if (i == 17) {
                        Double insuredAmount = field == ""?0:new Double(field);
                        postal.setInsuredAmount(insuredAmount);
                        continue;
                    }
                    //总邮资
                    if (i == 18) {
                        Double totalPostage = field == ""?0:new Double(field);
                        postal.setTotalPostage(totalPostage);
                        continue;
                    }
                    //标准邮资
                    if (i == 19) {
                        Double standPostage = field == ""?0:new Double(field);
                        postal.setStandPostage(standPostage);
                        continue;
                    }
                    //件数
                    if (i == 20) {
                        Integer num = field == ""?0:new Integer(field);
                        postal.setNum(num);
                        continue;
                    }
                    //内件数
                    if (i == 21) {
                        Integer innerNum = field == ""?0:new Integer(field);
                        postal.setInnerNum(innerNum);
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("第：" + j + "行数据有问题,解析数据项：" + line.get(i) + "出错");
                }
            }
            postalList.add(postal);
            if(postal.getProductName().contains("国内快递包裹物品经济时限")){

            }else{

            }

        }
        return postalList;
    }

    private String formatField(String field) {
        if (!StringUtils.isAnyEmpty(field)) {
            return field.replace("\"", "").trim();
        }
        return "";
    }


    public static void main(String[] args) {
        try {
            Map<String, List<Postal>> map = new PasePostal().analyseFile(new File("f:test.xls"), 1);
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}
