package com.greywind.blog.service;

import com.greywind.blog.vo.ArticleVo;
import com.greywind.blog.vo.Result;
import com.greywind.blog.vo.params.ArticleParam;
import com.greywind.blog.vo.params.PageParams;
import java.util.List;



public interface ArticleService {
    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */

    public Result listArticle(PageParams pageParams);

    /**
     * 最热文章
     * @param limit
     * @return
     */
    Result hotArticle(int limit);

    /**
     * 最新文章
     * @param limit
     * @return
     */
    Result newArticles(int limit);

    /**
     * 文章归档
     * @return
     */
    Result listArchives();

    /**
     * 查看文章详情
     * @param articleId
     * @return
     */
    Result findArticleById(Long articleId);

    /**
     * 文章发布
     * @param articleParam
     * @return
     */
    Result publish(ArticleParam articleParam);

    Result searcherArticle(String search);

    Result deleteArticleById(Long articleId);
}
