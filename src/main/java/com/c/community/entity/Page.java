package com.c.community.entity;

/**
 * 封装分页的相关信息
 */
public class Page {

    //当前页码
    private int current = 1;
    //每页帖子数
    private int limit = 10;
    //帖子总数
    private int rows;
    //路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current > 0) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行，根据当前页计算
     * @return
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页码数
     * @return
     */
    public int getTotal() {
        return (rows + limit - 1) / limit;
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom() {
        return current >= 3 ? current - 2 : 1;
    }

    public int getTo() {
        int total = getTotal();
        return current <= total - 2 ? current + 2 : total;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }
}
