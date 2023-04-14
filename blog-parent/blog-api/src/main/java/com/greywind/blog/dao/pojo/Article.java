package com.greywind.blog.dao.pojo;



import lombok.Data;

//文章数据库表


@Data
public class Article {

    public static final int Article_TOP = 1;

    public static final int Article_Common = 0;
    //id
    //@TableId(type = IdType.AUTO)
    private Long id;
    //文章标题
    private String title;

    private String summary;
    //评论数
    private Integer commentCounts;
    //访问量
    private Integer viewCounts;

    /**
     * 作者id
     */
    private Long authorId;
    /**
     * 内容id
     */
    private Long bodyId;
    /**
     *类别id
     */
    private Long categoryId;

    /**
     * 置顶
     */
    private Integer weight;


    /**
     * 创建时间
     */
    private Long createDate;
}