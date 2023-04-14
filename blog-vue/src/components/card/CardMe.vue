<template>
	
	<div class="ui container">
	            <div class="ui top attached segment">
	              <div class="ui header">
					  <h1 class="me-author-name">GreyWind</h1>
				  </div>
	            </div>
	            <div class="ui attached segment">
					
	              <p class="m-text">
	                摆烂摸鱼的学生,准备提桶跑路！
	              </p>
	              
	            </div>
	           
	            
	            <div class="ui bottom attached segment me-author-tool">
	              <a href="tencent://message/?Site=baidu.com&uin=2928560492&Menu=ye"><i class="qq icon"></i></a>
	              <a href=""><i class="wechat icon"></i></a>
	              <a href="https://steamcommunity.com/profiles/76561199046586037/"><i class="steam icon"></i></a>
	              <a href="https://github.com/GreyWind2077"><i class="github icon"></i></a>
	              <a href="https://mobile.twitter.com/GreyWind2077"><i class="twitter icon"></i></a>
	              <a href="https://space.bilibili.com/267320405"><i class="iconfont icon-bilibili-fill"></i></a>
	            </div>
	          </div>
	
	
 <!-- <el-card style="border-radius: 10px;">
    <h1 class="me-author-name">GreyWind</h1>
    <div class="me-author-description">
		<span><i class="me-icon-job"></i> &nbsp;摸鱼摆烂学生,准备提桶跑路</span>
    </div>
    <div class="me-author-tool">
      
	  <a href="tencent://message/?Site=baidu.com&uin=2928560492&Menu=ye"><i class="qq icon"></i></a>
	  <a href=""><i class="wechat icon"></i></a>
	  <a href="https://steamcommunity.com/profiles/76561199046586037/"><i class="steam icon"></i></a>
	  <a href="https://github.com/GreyWind2077"><i class="github icon"></i></a>
	  <a href="https://mobile.twitter.com/GreyWind2077"><i class="twitter icon"></i></a>
      <a href="https://space.bilibili.com/267320405"><i class="iconfont icon-bilibili-fill"></i></a>
	  
	  
	  
	  
	  
    </div>
  </el-card> -->

</template>

<script>
  export default {
    name: 'CardMe',
    data() {
      return {
        qq: {title: 'QQ', message: '2928560492'},
        bilibili: {
          title: '哔哩哔哩',
          message: '<a target="_blank" href="https://space.bilibili.com/473844125">https://space.bilibili.com/473844125</a>'
        }
      }
    },
    methods: {
      showTool(tool) {
        this.$message({
          duration: 0,
          showClose: true,
          dangerouslyUseHTMLString: true,
          message: '<strong>' + tool.message + '</strong>'
        });
      }
    }
  }
</script>

<style scoped>
  .me-author-name {
    text-align: center;
    font-size: 30px;
    
  }

  .me-author-description {
    padding: 8px 0;
  }

  .me-icon-job {
    padding-left: 16px;
  }

  .me-author-tool {
    text-align: center;
    padding-top: 10px;
  }

  .me-author-tool i {
    cursor: pointer;
    padding: 4px 10px;
    font-size: 30px;
  }
</style>
