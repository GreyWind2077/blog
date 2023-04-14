<template>
	
	
	<div class="ui container vertical m-padded-tb-large m-padded-lr-large">
		
		
		
	
	
  <div class="ui piled segment" >
    <div class="me-article-header">

      <a @click="view(id)" class="me-article-title">{{title}}</a>
      <el-button v-if="weight > 0" class="me-article-icon" type="text">置顶</el-button>
      <span class="me-pull-right me-article-count">
	    	<i class="me-icon-comment"></i>&nbsp;{{commentCounts}}
	    </span>
      <span class="me-pull-right me-article-count">
	    	<i class="eye icon"></i>&nbsp;{{viewCounts}}
	    </span>
    </div>

    <div class="me-artile-description">
      {{summary}}
    </div>
	
    <div class="me-article-header">
	  	<span class="me-article-author">
	    	<i class="me-icon-author"></i>&nbsp;{{author.nickname}}
	    </span>

      <div class="ui teal basic label m-padded-tiny m-text-thin" v-for="t in tags" :key="t.tagName" size="mini" type="success">{{t.tagName}}</div>
	
  </div>
  <div class="me-article-header">
		<span class="me-pull-right me-article-count">
	    	<i class="calendar icon"></i>&nbsp;{{createDate | format}}
	    </span>
  </div>
  
</div>
</div>
</template>

<script>
  import { formatTime } from "../../utils/time";

  export default {
    name: 'ArticleItem',
    props: {
      id: String,
      weight: Number,
      title: String,
      commentCounts: Number,
      viewCounts: Number,
      summary: String,
      author: String,
      tags: Array,
      createDate: String
    },
    data() {
      return {}
    },
    methods: {
      view(id) {
        this.$router.push({path: `/view/${id}`})
      }
    }
  }
</script>

<style scoped>

  .me-article-header {
    /*padding: 10px 18px;*/
    padding-bottom: 10px;
  }

  .me-article-title {
    font-weight: 600;
  }

  .me-article-icon {
    padding: 3px 8px;
  }

  .me-article-count {
    color: #a6a6a6;
    padding-left: 14px;
    font-size: 13px;
  }

  .me-pull-right {
    float: right;
  }

  .me-artile-description {
    font-size: 13px;
    line-height: 24px;
    margin-bottom: 10px;
  }

  .me-article-author {
    color: #a6a6a6;
    padding-right: 18px;
    font-size: 13px;
  }

  .el-tag {
    margin-left: 6px;
  }

</style>
