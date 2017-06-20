package com.yuand.study.util;

import com.yuand.study.domain.BaseInfo;
import com.yuand.study.domain.Postal;
import com.yuand.study.domain.Price;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/20 0020.
 */
public class PostalUtil {

    private static PostalUtil postalUtil = new PostalUtil();

    private  List<BaseInfo> baseInfoList;

    private  List<Price> priceList;

    public static PostalUtil getInstence(){
        return postalUtil;
    }

    private PostalUtil(){
        init();
    }

    private void init(){

        try {
            InputStream is = this.getClass().getResourceAsStream("/price.xls");
            Workbook wb = new POIExcelUtil().validateExcel("price.xls", is);
            POIExcelUtil poiExcelUtil = new POIExcelUtil();
            List<ArrayList<String>> datas = poiExcelUtil.read(wb);
            System.out.println(datas);
            paserPrice(datas);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private void paserPrice(List<ArrayList<String>> datas) {
        for (int j = 3; j < datas.size(); j++) {
            List<String> line = datas.get(j);
            Price pirce = new Price();
            for (int i = 0; i < line.size(); i++) {
                String field = formatField(line.get(i));


            }
        }

    }

    private String formatField(String field) {
        if (!StringUtils.isAnyEmpty(field)) {
            return field.replace("\"", "").trim();
        }
        return "";
    }


    public static void main(String[] args) {
        PostalUtil.getInstence();
    }

}
