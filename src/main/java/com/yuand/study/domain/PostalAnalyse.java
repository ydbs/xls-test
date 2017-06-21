package com.yuand.study.domain;

import com.yuand.study.util.PostalUtil;

/**
 * 标件收支 和 快包收支 明细
 * Created by Administrator on 2017/6/20 0020.
 */
public class PostalAnalyse {

    private Postal postal;

    /**
     * 业务量（件） = 件数
     * 总重量（千克） = 计费重量/1000
     * 揽收 = 总邮资 * 0.15
     * 内部处理（经转+分拣+投递）(IF(总重量/业务量<=3,2.1,IF(总重量/业务量<=5,(ROUNDUP(总重量/业务量,0)-3)*0.6+2.1,(ROUNDUP(总重量/业务量,0)-5)*0.7+3.3))+总重量*0.09)*业务量
     * 航空 = SUMIF(单价表!A:A,寄达地,单价表!E:E)*总重量
     * 一干陆运 = SUMIF(单价表!A:A,寄达地,单价表!M:M)*总重量
     * 二干陆运 = SUMIF(单价表!A:A,寄达地,单价表!G:G)*总重量
     *
     * 费用合计 = 揽收 + 内部处理 + 航空 + 一干陆运 + 二干陆运
     */

    private int busNum;

    private double totalWeight;

    private double free;

    private double innerFree;

    private double airfreight;

    private double oneFree;

    private double twoFree;

    private double totalFree;

    public PostalAnalyse(Postal postal,int type) {
        this.postal = postal;
        this.busNum = postal.getNum();
        this.totalWeight = postal.getChargedWeight()/1000.0;
        this.free = postal.getTotalPostage() * 0.15;
        if(this.totalWeight/this.busNum<=3){
            this.innerFree = 2.1;
        }else {
            if(this.totalWeight/this.busNum<=5){
                this.innerFree = ((Math.ceil(this.totalWeight / (this.busNum * 0.1)) - 3) * 0.6 + 2.1 + this.totalWeight * 0.09) * this.busNum;
            }else{
                this.innerFree = (((Math.ceil(this.totalWeight/(this.busNum*0.1))-5)*0.7+3.3)+this.totalWeight*0.09) * this.busNum;
            }
        }
        Price price = PostalUtil.getInstence().searchPriceBySrcAddress(postal.getAddress(),type);
        this.airfreight = price == null?0:price.getAirFree() * this.totalWeight;
        this.oneFree = price == null?0:price.getOneFree() * this.totalWeight;
        this.twoFree =  price == null?0:price.getTwoFree() * this.totalWeight;

        this.totalFree = this.free+this.innerFree+this.airfreight+this.oneFree +this.twoFree;
    }

    public Postal getPostal() {
        return postal;
    }

    public void setPostal(Postal postal) {
        this.postal = postal;
    }

    public int getBusNum() {
        return busNum;
    }

    public void setBusNum(int busNum) {
        this.busNum = busNum;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public double getFree() {
        return free;
    }

    public void setFree(double free) {
        this.free = free;
    }

    public double getInnerFree() {
        return innerFree;
    }

    public void setInnerFree(double innerFree) {
        this.innerFree = innerFree;
    }

    public double getAirfreight() {
        return airfreight;
    }

    public void setAirfreight(double airfreight) {
        this.airfreight = airfreight;
    }

    public double getOneFree() {
        return oneFree;
    }

    public void setOneFree(double oneFree) {
        this.oneFree = oneFree;
    }

    public double getTwoFree() {
        return twoFree;
    }

    public void setTwoFree(double twoFree) {
        this.twoFree = twoFree;
    }

    public double getTotalFree() {
        return totalFree;
    }

    public void setTotalFree(double totalFree) {
        this.totalFree = totalFree;
    }
}
