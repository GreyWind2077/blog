package com.greywind.blog.controller;

import com.greywind.blog.common.aop.LogAnnotation;
import com.greywind.blog.common.cache.Cache;
import com.greywind.blog.service.ArticleService;
import com.greywind.blog.vo.Result;
import com.greywind.blog.vo.params.ArticleParam;
import com.greywind.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


//json数据进行交互
@RestController
@RequestMapping("articles")
public class ArticleController {

    @PostMapping("search")
    public Result search(@RequestBody ArticleParam articleParam){
        String search=articleParam.getSearch();
        return articleService.searcherArticle(search);
    }

    @Autowired
    private ArticleService articleService;
    /**
     * 首页文章列表
     * @param pageParams
     * @return
     */


    @PostMapping
    @LogAnnotation(module = "文章",operator = "获取文章列表")
    @Cache(expire = 5*60*1000,name = "listArticle")
    public Result listArticle(@RequestBody PageParams pageParams)
    {


        return articleService.listArticle(pageParams);

    }

    /**
     * 首页最热文章
     * @return
     */

    @PostMapping("hot")
    @Cache(expire = 5*60*1000,name = "hot_article")
    public Result hotArticle()
    {
        //前5条
        int limit=5;
        return articleService.hotArticle(limit);
    }

    /**
     * 最新文章
     * @return
     */
    @PostMapping("new")
    @Cache(expire = 5*60*1000,name = "news_article")
    public Result newArticles()
    {
        int limit=5;
        return articleService.newArticles(limit);
    }


    @PostMapping("listArchives")
    public Result listArchives()
    {
        return articleService.listArchives();
    }

    @PostMapping("view/{id}")
    @Cache(expire = 5*60*1000,name = "view_article")
    public Result findArticleById(@PathVariable("id") Long id){
        return articleService.findArticleById(id);
    }

    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }

    @PostMapping("delete/{id}")
    public Result delete(@PathVariable("id") Long articleId)
    {
        return articleService.deleteArticleById(articleId);
    }

    @PostMapping("{id}")
    public Result articleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }




}
