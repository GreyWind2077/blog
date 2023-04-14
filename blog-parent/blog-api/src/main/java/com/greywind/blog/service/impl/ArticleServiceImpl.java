package com.greywind.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.greywind.blog.dao.dos.Archives;
import com.greywind.blog.dao.mapper.ArticleBodyMapper;
import com.greywind.blog.dao.mapper.ArticleMapper;

import com.greywind.blog.dao.mapper.ArticleTagMapper;
import com.greywind.blog.dao.pojo.Article;

import com.greywind.blog.dao.pojo.ArticleBody;
import com.greywind.blog.dao.pojo.ArticleTag;
import com.greywind.blog.dao.pojo.SysUser;
import com.greywind.blog.service.*;

import com.greywind.blog.utils.UserThreadLocal;
import com.greywind.blog.vo.*;

import com.greywind.blog.vo.params.ArticleParam;
import com.greywind.blog.vo.params.PageParams;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public Result listArticle(PageParams pageParams){
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage=

                articleMapper.listArticle(page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());

        List<Article> records=articleIPage.getRecords();

        return Result.success(copyList(records,true,true));
    }

//    @Override
//    public Result listArticle(PageParams pageParams) {
//
//        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//
//        if(pageParams.getCategoryId()!=null){
//            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
//        }
//
//        List<Long> articleIdList=new ArrayList<>();
//        if(pageParams.getTagId()!=null){
//            //article中并没有tag字段 一个文章有多个标签
//            //article_tag article_id
//            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper=new LambdaQueryWrapper<>();
//            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
//            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
//            for (ArticleTag articleTag:articleTags){
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if(articleIdList.size()>0){
//                // and id in(1,2,3)
//                queryWrapper.in(Article::getId,articleIdList);
//            }
//
//        }
//
//
//        //order by create_date desc
//        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records=articlePage.getRecords();
//
//        List<ArticleVo> articleVoList = copyList(records,true,true);
//
//        return Result.success(articleVoList);
//    }



    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article : records) {
            articleVoList.add(copy(article,isTag,isAuthor,false,false));
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article : records) {
            articleVoList.add(copy(article,isTag,isAuthor,isBody,isCategory));
        }
        return articleVoList;
    }

    @Autowired
    private CategoryService categoryService;

    private ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory){
        ArticleVo articleVo = new ArticleVo();

        BeanUtils.copyProperties(article, articleVo);

        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //并不是所有接口都需要标签
        if(isTag)
        {
            Long articleId=article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor)
        {
            Long authorId=article.getAuthorId();
            SysUser sysUser=sysUserService.findUserById(authorId);
            UserVo userVo=new UserVo();
            userVo.setAvatar(sysUser.getAvatar());
            userVo.setId(sysUser.getId());
            userVo.setNickname(sysUser.getNickname());
            articleVo.setAuthor(userVo);
        }

        if (isBody)
        {
            Long bodyId=article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));

        }

        if(isCategory)
        {
            Long categoryId=article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }

        return articleVo;
    }


    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findArticleBodyById(Long bodyId) {

        ArticleBody articleBody=articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo=new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;

    }


    /**
     * 最热文章
     *
     * @param limit
     * @return
     */
    @Override
    public Result hotArticle(int limit) {

        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id,title from article order by view_counts desc limit 5
        List<Article> articles=articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false));
    }


    /**
     * 最新文章
     *
     * @param limit
     * @return
     */
    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id,title from article order by creat_date desc limit 5
        List<Article> articles=articleMapper.selectList(queryWrapper);


        return Result.success(copyList(articles,false,false));
    }

    /**
     * 文章归档
     *
     * @return
     */
    @Override
    public Result listArchives() {
        List<Archives> archivesList=articleMapper.listArchives();

        return Result.success(archivesList);
    }

    @Autowired
    private ThreadService threadService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    /**
     * 查看文章详情
     *
     * @param articleId
     * @return
     */
    @Override
    public Result findArticleById(Long articleId) {

        /**
         * 1. 根据id查询文章信息
         * 2. 根据bodyid 和categoryid 去做关联
         *
         */

        Article article=articleMapper.selectById(articleId);

        ArticleVo articleVo=copy(article,true,true,true,true);

        //查看完文章，新增阅读数

        //查看完成文章之后，本应该直接返回数据，这时候做了一个更新操作，更新加了写锁，阻塞其他的读操作，性能就会比较低

        //更新 增加了此次接口的耗时 如果一旦更新出问题，不能影响 查看文章的操作

        //线程池 可以把更新操作 扔到线程池中去执行，和主线程就不相关了


        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(articleVo);
    }

    /**
     * 文章发布
     *
     * @param articleParam
     * @return
     */
    @Override
    public Result publish(ArticleParam articleParam) {
        //此接口 要加入到拦截器中
        SysUser sysUser= UserThreadLocal.get();
        /**
         * 1. 发布文章 构建article对象
         * 2.作者id 当前登录用户
         * 3.标签 将标签加入到关联列表中
         * 4.内容存储 articlebody
         */
        Article article=new Article();
        boolean isEdit=false;
        if(articleParam.getId()!=null)
        {
            article=new Article();
            article.setId(articleParam.getId());
            article.setTitle(articleParam.getTitle());
            article.setSummary(articleParam.getSummary());
            article.setCategoryId(articleParam.getCategory().getId());
            articleMapper.updateById(article);
            isEdit=true;
        }
        else{
            article=new Article();
            article.setAuthorId(sysUser.getId());
            article.setWeight(Article.Article_Common);
            article.setViewCounts(0);
            article.setTitle(articleParam.getTitle());
            article.setSummary(articleParam.getSummary());
            article.setCommentCounts(0);
            article.setCreateDate(System.currentTimeMillis());
            article.setCategoryId(articleParam.getCategory().getId());
            //插入之后生成文章id
            articleMapper.insert(article);
        }


        //tag
        List<TagVo> tags=articleParam.getTags();
        if(tags!=null){
            for (TagVo tag:tags){
                Long articleId=article.getId();
                ArticleTag articleTag=new ArticleTag();
                articleTag.setTagId(tag.getId());
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }

        }

        //body
        if(isEdit){
            ArticleBody articleBody=new ArticleBody();
            articleBody.setArticleId(article.getId());
            articleBody.setContent(articleParam.getBody().getContent());
            articleBody.setContentHtml(articleParam.getBody().getContentHtml());
            LambdaUpdateWrapper<ArticleBody> updateWrapper= Wrappers.lambdaUpdate();
            updateWrapper.eq(ArticleBody::getArticleId,article.getId());
            articleBodyMapper.update(articleBody,updateWrapper);

        }
        else {
            ArticleBody articleBody = new ArticleBody();
            articleBody.setArticleId(article.getId());
            articleBody.setContent(articleParam.getBody().getContent());
            articleBody.setContentHtml(articleParam.getBody().getContentHtml());
            articleBodyMapper.insert(articleBody);

            article.setBodyId(articleBody.getId());
            articleMapper.updateById(article);
            //这里插入文章。也要用rocketmq来更新一下,否则发现新的文章刷不出来

        }


        Map<String,String> map=new HashMap<>();
        map.put("id",article.getId().toString());
        //所以不管是更新文章还是插入文章，都要发消息给rocketmq，更新缓存

        {
            //发送一条消息给rocketmq,当文章更新后，更新缓存
            ArticleMessage articleMessage=new ArticleMessage();
            articleMessage.setArticleId(article.getId());
            rocketMQTemplate.convertAndSend("blog-update-article",articleMessage);
        }
        return Result.success(map);
    }

    @Override
    public Result searcherArticle(String search) {

        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.like(Article::getTitle,search);
        List<Article> articles=articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false));
    }

    @Override
    public Result deleteArticleById(Long articleId) {

        articleMapper.deleteById(articleId);

        return Result.success(null);
    }
}

