package com.greywind.blog.service;

import com.greywind.blog.vo.CategoryVo;
import com.greywind.blog.vo.Result;

import java.util.List;

public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result categoryDetailById(Long id);
}
