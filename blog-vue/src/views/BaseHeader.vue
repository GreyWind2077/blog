<template>
	

<nav class="ui inverted attached segment m-padded-tb-mini">
    <div class="ui container">
        <div class="ui inverted secondary stackable menu" >
			
			<router-link to="/">
				<h2 class="ui blue header item">GreyWind</h2>
			</router-link>
            
			
			<a href="/#/" class="m-item item m-mobile-hide" v-if="!simple"><i class="home icon"></i>首页</a>
            <a href="/#/category/all" class="m-item item m-mobile-hide" v-if="!simple"><i class="idea icon"></i>分类</a>
            <a href="/#/tag/all" class="m-item item m-mobile-hide" v-if="!simple"><i class="tags icon"></i>标签</a>
            <a href="/#/archives" class="m-item item m-mobile-hide" v-if="!simple"><i class="clone icon"></i>归档</a>
			
			<a href="/#/write" class="m-item item m-mobile-hide" v-if="!simple"><i class="el-icon-edit"></i>写文章</a>
			<div class="m-item item m-mobile-hide" v-if="simple">写文章</div>
            
            <div class="m-item item m-mobile-hide" v-if="!simple">
				 <div class="ui icon input">
				                    <input type="text" placeholder="Search...">
				                    <i class="search link icon"></i>
				</div>
				
				
                
			</div>	
			
			
            
			
			
				
				
				
				
				  
				  
				    
				      
					  <div class="right m-item m-mobile-hide menu">
				        <template v-if="!user.login">
						
						<a href="/#/login" class="right  item">登录</a>
						<a href="/#/register"class="right  item">注册</a>
						
						
				      	  
				    </template>
				  
				    <template v-else>
						<div class="ui dropdown item" @click="dropdown">
						                    <div class="text">
						                        <img class="ui avatar image":src="user.avatar"/>
						                        {{user.nickname}}
						                    </div>
						                    <i class="dropdown icon"></i>
						                    <div class="menu">
						                        <a  @click="logout" class="item"><i class="el-icon-back"></i> 退出</a>
						                    </div>
						            </div>
						
						</template>
						</div>
				        
				      
				    
				  
				
			
				
				
            
        </div>
		
		
    </div>
<a href="#"index  @click="showitem" class="ui menu toggle black inverted icon button m-top-right m-mobile-show">
    <i class="sidebar icon"></i>
</a>

</nav>
	
<!-- 	
  <el-header class="me-area">
    <el-row class="me-header">

      <el-col :span="2" class="me-header-left">
        <router-link to="/" class="me-title">
          <h2 style="color: black; ">GreyWind</h2>
        </router-link>
      </el-col>

      <el-col v-if="!simple" :span="12">
        <el-menu :router=true menu-trigger="click" active-text-color="#5FB878" :default-active="activeIndex"
                 mode="horizontal">
								 
          <el-menu-item index="/"><i class="home icon" style="display: inline;"></i>首页</el-menu-item>
          <el-menu-item index="/category/all">文章分类</el-menu-item>
          <el-menu-item index="/tag/all">标签</el-menu-item>
          <el-menu-item index="/archives">文章归档</el-menu-item>

          <el-col :span="4" :offset="4">
            <el-menu-item index="/write"><i class="el-icon-edit"></i>写文章</el-menu-item>
          </el-col>

        </el-menu>
      </el-col>

      <template v-else>
        <slot></slot>
      </template>


      <el-col :span="6">
        <el-menu  mode="horizontal" active-text-color="#5FB878">
            <el-menu-item >
               <el-input placeholder="请输入搜索内容"  @keyup.enter.native="searchHandler" v-model="search"></el-input>

              <template>
                <el-autocomplete
                  v-model="search"
                  :fetch-suggestions="querySearchAsync"
                  placeholder="请输入内容"
                  @select="handleSelect"
                ></el-autocomplete>
              </template>

              </el-menu-item>
        </el-menu>
      </el-col>

      <el-col :span="4">
        <el-menu :router=true menu-trigger="click" mode="horizontal" active-text-color="#5FB878">

          <template v-if="!user.login">
            <el-menu-item index="/login">
              <el-button type="text">登录</el-button>
            </el-menu-item>
            <el-menu-item index="/register">
              <el-button type="text">注册</el-button>
            </el-menu-item>
          </template>

          <template v-else>
            <el-submenu index>
              <template slot="title">
                <img class="me-header-picture" :src="user.avatar"/>
              </template>
              <el-menu-item index @click="logout"><i class="el-icon-back"></i>退出</el-menu-item>
            </el-submenu>
          </template>
        </el-menu>
      </el-col>

    </el-row>
  </el-header> --> 
  
  
      
  
  
  
</template>



<script>
	
	
  import {searchArticle} from '@/api/article'
  export default {
    name: 'BaseHeader',
    props: {
      activeIndex: String,
      simple: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        search:'',
        articles:[]
      }
    },
    computed: {
      user() {
        let login = this.$store.state.account.length != 0
		let nickname = this.$store.state.name
        let avatar = this.$store.state.avatar
		
		
        return {
          login, avatar, nickname
        }
      }
    },
    methods: {
      logout() {
        let that = this
        this.$store.dispatch('logout').then(() => {
          this.$router.push({path: '/'})
        }).catch((error) => {
          if (error !== 'error') {
            that.$message({message: error, type: 'error', showClose: true});
          }
        })
      },
      querySearchAsync(queryString, cb){
        searchArticle(this.search).then((res)=>{
          if(res.success){
            var results = [];
            for(const item of res.data){
              results.push({
                id:item.id,
                value:item.title
              });
            }
            cb(results)
          }
        })

      },
      handleSelect(item){
        this.$router.push({path: '/view/'+item.id})
      },
	  showitem()
	  {
		  $('.m-item').toggleClass('m-mobile-hide')  
	  },
	  dropdown()
	  {
		  $('.ui.dropdown').dropdown({
		           on: 'hover'
		    })
	  }
    }
  }
  
</script>


<style>


</style>
