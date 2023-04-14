package com.greywind.blog.service;

import com.greywind.blog.vo.Result;
import com.greywind.blog.vo.params.CommentParam;

public interface CommentsService {
    /**
     * 根据文章id 查询所有评论列表
     * @param id
     * @return
     */
    Result commentsByArticleId(Long id);

    /**
     * 评论
     * @param commentParam
     * @return
     */
    Result comment(CommentParam commentParam);
}
