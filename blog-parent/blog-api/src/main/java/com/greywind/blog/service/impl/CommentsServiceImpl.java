package com.greywind.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greywind.blog.dao.mapper.ArticleMapper;
import com.greywind.blog.dao.mapper.CommentMapper;
import com.greywind.blog.dao.pojo.Article;
import com.greywind.blog.dao.pojo.Comment;
import com.greywind.blog.dao.pojo.SysUser;
import com.greywind.blog.service.CommentsService;
import com.greywind.blog.service.SysUserService;
import com.greywind.blog.utils.UserThreadLocal;
import com.greywind.blog.vo.CommentVo;
import com.greywind.blog.vo.Result;
import com.greywind.blog.vo.UserVo;
import com.greywind.blog.vo.params.CommentParam;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 根据文章id 查询所有评论列表
     *
     * @param id
     * @return
     */
    @Override
    public Result commentsByArticleId(Long id) {

        /**
         * 1.根据文章id 查询 评论列表 从comment表中查询
         * 2.根据作者的id查询作者的信息
         * 3.判断 如果 level=1 要去查询它有没有子评论
         * 4.如果有 根据评论id进行查询（parent_id)
         */
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,id);
        queryWrapper.eq(Comment::getLevel,1);
        //按时间排序。。。。,也是前端的问题。。
        queryWrapper.orderByDesc(Comment::getCreateDate);

        List<Comment> comments = commentMapper.selectList(queryWrapper);



        List<CommentVo> commentVoList=copyList(comments);

        return Result.success(commentVoList);
    }

    private List<CommentVo> copyList(List<Comment> comments) {
        List<CommentVo> commentVoList=new ArrayList<>();
        for(Comment comment:comments){
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo=new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        //作者信息

        //时间格式化
        commentVo.setCreateDate(new DateTime(comment.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        Long authorId = comment.getAuthorId();
        UserVo userVo = sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);

        //子评论
        Integer level=comment.getLevel();
        if(level==1){
            Long id=comment.getId();
            List<CommentVo> commentVoList=findCommentsByParentId(id);
            commentVo.setChildrens(commentVoList);
        }
        //to User 给谁评论
        if(level>1){
            Long toUid = comment.getToUid();
            UserVo toUserVo=sysUserService.findUserVoById(toUid);
            commentVo.setToUser(toUserVo);
        }

        return commentVo;
    }

    private List<CommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);

        return copyList(commentMapper.selectList(queryWrapper));

    }

    /**
     * 评论
     *
     * @param commentParam
     * @return
     */
    @Override
    public Result comment(CommentParam commentParam) {
        SysUser sysUser = UserThreadLocal.get();


        System.out.println(sysUser);
        Comment comment = new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setAuthorId(sysUser.getId());

        comment.setContent(commentParam.getContent());
        comment.setCreateDate(System.currentTimeMillis());

        Long parent = commentParam.getParent();
        if (parent == null || parent == 0) {
            comment.setLevel(1);
        }else{
            comment.setLevel(2);
        }
        comment.setParentId(parent == null ? 0 : parent);
        Long toUserId = commentParam.getToUserId();
        comment.setToUid(toUserId == null ? 0 : toUserId);
        this.commentMapper.insert(comment);

        UpdateWrapper<Article> updateWrapper= Wrappers.update();
        updateWrapper.eq("id",comment.getArticleId());
        updateWrapper.setSql(true,"comment_counts=comment_counts+1");
        this.articleMapper.update(null,updateWrapper);

        //评论之后直接返回
        CommentVo commentVo=copy(comment);
        return Result.success(commentVo);

        //return Result.success(null);

    }
}
