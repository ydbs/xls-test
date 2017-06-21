package com.yuand.study.util;

import com.yuand.study.domain.BaseInfo;
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

    private List<BaseInfo> baseInfoList = new ArrayList<BaseInfo>();

    private List<Price> standPriceList = new ArrayList<Price>();
    private List<Price> fastPriceList = new ArrayList<Price>();
    private List<Price> innerPriceList = new ArrayList<Price>();

    public static PostalUtil getInstence() {
        return postalUtil;
    }

    private PostalUtil() {
        init();
    }

    private void init() {

        try {
            InputStream is = this.getClass().getResourceAsStream("/price.xls");
            Workbook wb = new POIExcelUtil().validateExcel("price.xls", is);
            POIExcelUtil poiExcelUtil = new POIExcelUtil();
            List<ArrayList<String>> datas = poiExcelUtil.read(wb);
            paserPrice(datas);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 加载单价表
     *
     * @param datas
     */
    private void paserPrice(List<ArrayList<String>> datas) {
        for (int j = 3; j < datas.size(); j++) {
            List<String> line = datas.get(j);
            //省际部分-标准
            Price standPirce = new Price(1);
            //省际部分-快包
            Price fastPirce = new Price(2);
            //省内部分
            Price innerPirce = new Price(3);

            for (int i = 0; i < line.size(); i++) {
                String field = formatField(line.get(i));
                if (StringUtils.isAllEmpty(field)) {
                    continue;
                }
                switch (i) {
                    case 0:
                        standPirce.setSrcAdress(field);
                        break;
                    case 1:
                        standPirce.setDestAdress(field);
                        break;
                    case 2:
                        standPirce.setAirline(field);
                        break;
                    case 3:
                        standPirce.setInnerFree(field == "" ? 0 : new Double(field));
                        break;
                    case 4:
                        standPirce.setAirFree(field == "" ? 0 : new Double(field));
                        break;
                    case 5:
                        standPirce.setOneFree(field == "" ? 0 : new Double(field));
                        break;
                    case 6:
                        standPirce.setTwoFree(field == "" ? 0 : new Double(field));
                        standPriceList.add(standPirce);
                        standPirce = new Price(1);
                        break;
                    case 7:
                        break;
                    case 8:
                        fastPirce.setSrcAdress(field);
                        break;
                    case 9:
                        fastPirce.setDestAdress(field);
                        break;
                    case 10:
                        fastPirce.setInnerFree(field == "" ? 0 : new Double(field));
                        break;
                    case 11:
                        fastPirce.setAirFree(field == "" ? 0 : new Double(field));
                        break;
                    case 12:
                        fastPirce.setOneFree(field == "" ? 0 : new Double(field));
                        break;
                    case 13:
                        fastPirce.setTwoFree(field == "" ? 0 : new Double(field));
                        fastPriceList.add(fastPirce);
                        fastPirce = new Price(2);
                        break;
                    case 14:
                        innerPirce.setDestAdress(field);
                        break;
                    case 15:
                        innerPirce.setDistance(field == "" ? 0 : new Double(field));
                        break;
                    case 16:
                        innerPirce.setInnerFree(field == "" ? 0 : new Double(field));
                        break;
                    case 17:
                        innerPirce.setAirFree(field == "" ? 0 : new Double(field));
                        break;
                    case 18:
                        innerPirce.setOneFree(field == "" ? 0 : new Double(field));
                        break;
                    case 19:
                        innerPirce.setTwoFree(field == "" ? 0 : new Double(field));
                        innerPriceList.add(innerPirce);
                        innerPirce = new Price(3);
                        break;
                }


            }
        }

    }

    private String formatField(String field) {
        if (!StringUtils.isAnyEmpty(field)) {
            return field.replace("\"", "").trim();
        }
        return "";
    }

    public Price searchPriceBySrcAddress(String srcAddress,int type){
        if(type ==1){
            return fetchPrice(standPriceList,srcAddress);

        }else if(type == 2){
            return fetchPrice(fastPriceList,srcAddress);
        }
        return null;

    }

    private Price fetchPrice(List<Price> priceList, String srcAddress) {
        if( priceList.size() == 0 || srcAddress == null || srcAddress == ""){
            return null;
        }else{
            for (Price price:priceList) {
                if(srcAddress.equals(price.getSrcAdress())){
                    return price;
                }
            }
            return null;
        }
    }


    public static void main(String[] args) {
        PostalUtil.getInstence();
    }

}
