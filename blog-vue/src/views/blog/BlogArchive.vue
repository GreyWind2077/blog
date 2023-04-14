<template>
  <div v-title :data-title="title">
    <div class="ui container">
		<div class="ui top attached segment">
			<h3 class="ui teal header">归档</h3>
		</div>
		
		
      <div class="ui attached segment">
        <div class="ui list">
          <li v-for="a in archives" :key="a.year + a.month" class="item">
			   <div class="ui middle aligned two column grid">
			                          <div class="column">
			                              <a class="ui header" @click="changeArchive(a.year, a.month)" size="small">{{a.year +'年' + a.month + '月'}}</a>
			                          </div>
			                          <div class="right aligned column">
			                              共 <h2 class="ui blue header m-inline-block m-text-thin"> {{a.count}}</h2> 篇
			                          </div>
				</div>

          </li>
        </div>

      </div>
	  
	  


      <div class="ui bottom attached segment">
        <div class="ui header">{{currentArchive}}</div>

        <article-scroll-page v-bind="article"></article-scroll-page>

      </div>
    </div>
  </div>

</template>

<script>
  import ArticleScrollPage from '@/views/common/ArticleScrollPage'
  import {listArchives} from '@/api/article'

  export default {
    name: "BlogArchive",
    components: {
      ArticleScrollPage
    },
    created() {
      this.listArchives()
    },
    watch: {
      '$route'() {
        if (this.$route.params.year && this.$route.params.month) {
          this.article.query.year = this.$route.params.year
          this.article.query.month = this.$route.params.month
        }
      }
    },
    data() {
      return {
        article: {
          query: {
            month: this.$route.params.month,
            year: this.$route.params.year
          }
        },
        archives: []
      }
    },
    computed: {
      title (){
        return this.currentArchive + ' - 文章归档'
      },
      currentArchive (){
        if(this.article.query.year && this.article.query.month){
          return `${this.article.query.year}年${this.article.query.month}月`
        }
        return '全部'
      }
    },
    methods: {

      changeArchive(year, month) {
        // this.currentArchive = `${year}年${month}月`
        // this.article.query.year = year
        // this.article.query.month = month
        this.$router.push({path: `/archives/${year}/${month}`})
      },
      listArchives() {
		  let that=this
        listArchives().then((data => {
          this.archives = data.data
        })).catch(error => {
          that.$message({type: 'error', message: '文章归档加载失败!', showClose: true})
        })
      }
    }
  }
</script>

<style scoped>

  .el-container {
    width: 640px;
  }

  .el-aside {
    position: fixed;
    left: 200px;
    margin-right: 50px;
    width: 150px !important;
  }

  .el-main {
    padding: 0px;
    line-height: 16px;
  }

  .me-month-list {
    margin-top: 10px;
    margin-bottom: 10px;
    text-align: center;
    list-style-type: none;
  }

  .me-month-item {
    margin-top: 18px;
    padding: 4px;
    font-size: 18px;
    color: #5FB878;
  }

  .me-order-list {
    float: right;
  }

  .me-month-title {
    margin-left: 4px;
    margin-bottom: 12px;
  }
</style>
