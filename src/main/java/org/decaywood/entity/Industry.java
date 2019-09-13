package org.decaywood.entity;

/**
 * @author: decaywood
 * @date: 2015/11/23 13:42
 */

/**
 * 行业板块
 */
public class Industry implements DeepCopy<Industry> {

    private final String industryName;//板块名字

    private final String industryInfo;//板块代码

    /**
     * MACD 金叉天数
     */
    private int macdCross; // MACD 金叉天数


    public Industry(final String industryName, final String industrySiteURL) {
        this.industryName = industryName;
        this.industryInfo = industrySiteURL;
    }

    public Industry(final String industryName, final String industrySiteURL, final int macdCross) {
        this.industryName = industryName;
        this.industryInfo = industrySiteURL;
        this.macdCross = macdCross;
    }


    public String getIndustryName() {
        return industryName;
    }

    public String getIndustryInfo() {
        return industryInfo;
    }

    public int getMacdCross() {
        return macdCross;
    }

    public void setMacdCross(int macdCross) {
        this.macdCross = macdCross;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Industry industry = (Industry) o;

        return industryName.equals(industry.industryName) && industryInfo.equals(industry.industryInfo);

    }

    @Override
    public int hashCode() {
        int result = industryName.hashCode();
        result = 31 * result + industryInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "industryName = " + industryName  + "  " + "industryInfo = " + industryInfo;
    }

    @Override
    public Industry copy() {
        return new Industry(industryName, industryInfo, macdCross);
    }
}
