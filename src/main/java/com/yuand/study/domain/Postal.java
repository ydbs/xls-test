package com.yuand.study.domain;

/**
 * 数据源信息
 * Created by Administrator on 2017/6/20 0020.
 */
public class Postal {
    /**
     *"邮件号",
     */
    private String postalNo;
    /**
     *"收寄时间",
     */
    private String sendTime;
    /**
     *"机构名称",
     */
    private String orgName;
    /**
     *"产品名称",
     */
    private String productName;
    /**
     *"大客户",
     */
    private String customerName;
    /**
     *"寄达地",
     */
    private String address;
    /**
     *"揽收员",
     */
    private String courier;
    /**
     *"重量",
     */
    private int weight;
    /**
     *"长",
     */
    private int len;
    /**
     *"宽",
     */
    private int width;
    /**
     *"高",
     */
    private int height;
    /**
     *"体积重",
     */
    private int volumeWeight;
    /**
     *"计费重量",
     */
    private int chargedWeight;
    /**
     *"优惠率",
     */
    private double rate;
    /**
     *"实际优惠率",
     */
    private double effectiveRate;
    /**
     *"邮资",
     */
    private double postage;
    /**
     * 保价金额",
     */
    private double bjAmount;
    /**
     *"保险金额",
     */
    private double insuredAmount;
    /**
     *"总邮资",
     */
    private double totalPostage;
    /**
     *"标准邮资",
     */
    private double standPostage;
    /**
     *"件数",
     */
    private int num;
    /**
     *"内件数"
     */
    private int innerNum;

    public String getPostalNo() {
        return postalNo;
    }

    public void setPostalNo(String postalNo) {
        this.postalNo = postalNo;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getVolumeWeight() {
        return volumeWeight;
    }

    public void setVolumeWeight(int volumeWeight) {
        this.volumeWeight = volumeWeight;
    }

    public int getChargedWeight() {
        return chargedWeight;
    }

    public void setChargedWeight(int chargedWeight) {
        this.chargedWeight = chargedWeight;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getEffectiveRate() {
        return effectiveRate;
    }

    public void setEffectiveRate(double effectiveRate) {
        this.effectiveRate = effectiveRate;
    }

    public double getPostage() {
        return postage;
    }

    public void setPostage(double postage) {
        this.postage = postage;
    }

    public double getBjAmount() {
        return bjAmount;
    }

    public void setBjAmount(double bjAmount) {
        this.bjAmount = bjAmount;
    }

    public double getInsuredAmount() {
        return insuredAmount;
    }

    public void setInsuredAmount(double insuredAmount) {
        this.insuredAmount = insuredAmount;
    }

    public double getTotalPostage() {
        return totalPostage;
    }

    public void setTotalPostage(double totalPostage) {
        this.totalPostage = totalPostage;
    }

    public double getStandPostage() {
        return standPostage;
    }

    public void setStandPostage(double standPostage) {
        this.standPostage = standPostage;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getInnerNum() {
        return innerNum;
    }

    public void setInnerNum(int innerNum) {
        this.innerNum = innerNum;
    }
}
