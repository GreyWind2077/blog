package com.greywind.blog.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.greywind.blog.dao.mapper.ArticleMapper;
import com.greywind.blog.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {

    //期望在线程池中执行

    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {


        int viewCounts = article.getViewCounts();
        Article articleUpdate =new Article();

        articleUpdate.setViewCounts(viewCounts+1);
        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,article.getId());

        //设置一个 为了在多线程环境下 线程更安全
        updateWrapper.eq(Article::getViewCounts,viewCounts);
        //update article set view_count=100 where view=99 and id=11

        articleMapper.update(articleUpdate,updateWrapper);

        try {
            Thread.sleep(5000);
            System.out.println("更新完成了...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
