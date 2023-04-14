<template>
  <div class="" v-title :data-title="title">
    <div class="m-container-small m-padded-tb-big animated pulse">
      <div class="ui container">
		  
		  <div class="ui top attached segment">
			  <div class="ui horizontal link list">
				  <div class="item">
					  <img class="ui avatar image" :src="article.author.avatar"></img>
					  <div class="ui content">
						  <span>{{article.author.nickname}}</span>
					  </div>
				  </div>
				  <div class="item"><i class="calendar icon"></i> {{article.createDate | format}}</div>
				  <div class="item"><i class="eye icon"></i>{{article.viewCounts}} </div>
				  <div class="item">{{article.commentCounts}} </div>
				  <div class="item">
					  <button
					    v-if="this.article.author.id == this.$store.state.id"
					    @click="editArticle()"
					    class="ui teal button"
					    size="mini"
					    round
					    icon="el-icon-edit">编辑</button>
				  </div>
			  </div>
			  
		  </div>
		  
		  <div class="ui attached padded segment">
			  <h2 class="ui center aligned header">{{article.title}}</h2>
			  
			  <div id="content" class="typo typo-selection js-toc-content m-padded-lr m-padded-lr-large">
				  <markdown-editor :editor="article.editor"></markdown-editor>
				  
				  
			  </div>
		  </div>
		  <div class="ui attached padded segment">
		    <el-alert
		      title="文章End..."
		      type="success"
		      center
		      :closable="false">
		    </el-alert>
		  </div>
		<div class="ui attached positive message">
			<ui class="ui list padded">
				<li class="m-padded">
					标签:
					<button class="ui teal basic left pointing label"  @click="tagOrCategory('tag', t.id)" size="mini" type="primary" v-for="t in article.tags" :key="t.id" round plain>{{t.tagName}}</button>
				</li>
				<li class="m-padded">
					文章分类:
					<button class="ui orange basic label" @click="tagOrCategory('category', article.category.id)" size="mini" type="primary" round plain>{{article.category.categoryName}}</button>
				</li>
			</ui>
			
			
		</div>

        <div class="ui bottom attached segment">
         

          
            <div class="ui header">
              <span>{{article.commentCounts}} 条评论</span>
            </div>
			
			<div class="ui teal segment">
				<div class="ui comments">
					<commment-item
              v-for="(c,index) in comments"
              :comment="c"
              :articleId="article.id"
              :index="index"
              :rootCommentCounts="comments.length"
              @commentCountsIncrement="commentCountsIncrement"
              :key="c.id">
            </commment-item>
				</div>
				
				
			</div>

            
			
			
			<div class="ui form">
			  
			  
			<div class="field">
				<textarea
				  type="textarea"
				  :autosize="{ minRows: 2}"
				  placeholder="你的评论..."
				  class="me-view-comment-text"
				  v-model="comment.content"
				  resize="none">
				</textarea>
			</div>
			
			<div class="field m-mobile-wide m-margin-bottom-small">
			                <button class="ui teal button m-mobile-wide" type="text" @click="publishComment()">
			                  <i class="edit icon"></i>评论
			        </button>
			</div>
			  <!-- <el-row :gutter="20">
			    <el-col :span="2" :offset="22">
			      <el-button type="text" @click="publishComment()">评论</el-button>
			    </el-col>
			  </el-row> -->
			</div>
			
			

          </div>

        </div>
      </div>

    </div>
  
</template>

<script>
  import MarkdownEditor from '@/components/markdown/MarkdownEditor'
  import CommmentItem from '@/components/comment/CommentItem'
  import {viewArticle} from '@/api/article'
  import {getCommentsByArticle, publishComment} from '@/api/comment'

  import default_avatar from '@/assets/img/default_avatar.png'

  export default {
    name: 'BlogView',
    created() {
      this.getArticle()
    },
    watch: {
      '$route': 'getArticle'
    },
    data() {
      return {
        article: {
          id: '',
          title: '',
          commentCounts: 0,
          viewCounts: 0,
          summary: '',
          author: {},
          tags: [],
          category:{},
          createDate: '',
          editor: {
            value: '',
            toolbarsFlag: false,
            subfield: false,
            defaultOpen: 'preview'
          }
        },
        comments: [],
        comment: {
          article: {},
          content: ''
        }
      }
    },
    computed: {
      avatar() {
        let avatar = this.$store.state.avatar

        if (avatar.length > 0) {
          return avatar
        }
        return default_avatar
      },
      title() {
        return `${this.article.title} - 文章 `
      }
    },
    methods: {
      tagOrCategory(type, id) {
        this.$router.push({path: `/${type}/${id}`})
      },
      editArticle() {
        this.$router.push({path: `/write/${this.article.id}`})
      },
	  deleteArticle()
	  {
		  
	  },
      getArticle() {
        let that = this
        viewArticle(that.$route.params.id).then(data => {
          Object.assign(that.article, data.data)
          that.article.editor.value = data.data.body.content

          that.getCommentsByArticle()
        }).catch(error => {
          if (error !== 'error') {
            that.$message({type: 'error', message: '文章加载失败', showClose: true})
          }
        })
      },
      publishComment() {
        let that = this
        if (!that.comment.content) {
          return;
        }
        that.comment.article.id = that.article.id
        let parms = {articleId:that.article.id,content:that.comment.content}
        publishComment(parms,this.$store.state.token).then(data => {
          if(data.success){
            that.$message({type: 'success', message: '评论成功', showClose: true})
            that.comment.content = ''
            that.comments.unshift(data.data);
            that.commentCountsIncrement();
            

          }else{
               that.$message({type: 'error', message: data.msg, showClose: true})
          }

        }).catch(error => {
          if (error !== 'error') {
            that.$message({type: 'error', message: '评论失败', showClose: true})
          }
        })
      },

      getCommentsByArticle() {
        let that = this
        getCommentsByArticle(that.article.id).then(data => {
          if(data.success){
               that.comments = data.data
          }else{
             that.$message({type: 'error', message: '评论加载失败', showClose: true})
          }
        }).catch(error => {
          if (error !== 'error') {
            that.$message({type: 'error', message: '评论加载失败', showClose: true})
          }
        })
      },
      commentCountsIncrement() {
        this.article.commentCounts += 1
      }
    },
    components: {
      'markdown-editor': MarkdownEditor,
      CommmentItem
    },
    //组件内的守卫 调整body的背景色
    beforeRouteEnter(to, from, next) {
      window.document.body.style.backgroundColor = '#fff';
      next();
    },
    beforeRouteLeave(to, from, next) {
      window.document.body.style.backgroundColor = '#f5f5f5';
      next();
    }
  }
</script>

<style>
  .me-view-body {
    margin: 100px auto 140px;
  }

  .me-view-container {
    width: 800px;
  }

  .el-main {
    overflow: hidden;
  }

  .me-view-title {
    font-size: 34px;
    font-weight: 800;
    line-height: 1.3;
  }

  .me-view-author {
    /*margin: 30px 0;*/
    margin-top: 30px;
    vertical-align: middle;
  }

  .me-view-picture {
    width: 40px;
    height: 40px;
    border: 1px solid #ddd;
    border-radius: 50%;
    vertical-align: middle;
    background-color: #5fb878;
  }

  .me-view-info {
    display: inline-block;
    vertical-align: middle;
    margin-left: 8px;
  }

  .me-view-meta {
    font-size: 12px;
    color: #969696;
  }

  .me-view-end {
    margin-top: 20px;
  }

  .me-view-tag {
    margin-top: 20px;
    padding-left: 6px;
    border-left: 4px solid #c5cac3;
  }

  .me-view-tag-item {
    margin: 0 4px;
  }

  .me-view-comment {
    margin-top: 60px;
  }

  .me-view-comment-title {
    font-weight: 600;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 20px;
  }

  .me-view-comment-write {
    margin-top: 20px;
  }

  .me-view-comment-text {
    font-size: 16px;
  }

  .v-show-content {
    padding: 8px 25px 15px 30px !important;
  }

  .v-note-wrapper .v-note-panel {
    box-shadow: none !important;
  }

  .v-note-wrapper .v-note-panel .v-note-show .v-show-content, .v-note-wrapper .v-note-panel .v-note-show .v-show-content-html {
    background: #fff !important;
  }


</style>
