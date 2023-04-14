<template>
  <div id="home">
	  <base-header :activeIndex="activeIndex"></base-header>
	  
    <div class="ui container">
    	
    	
		  
		  <router-view class="me-container"/>
		  
			
		  
		</div>
		
		<base-footer v-show="footerShow"></base-footer>
		
  </div>
  
</template>



<script>
import BaseFooter from '@/views/BaseFooter'
import BaseHeader from '@/views/BaseHeader'

export default {
  name: 'Home',
  data (){
  	return {
  			activeIndex: '/',
  			footerShow:true
  	}
  },
  components:{
  	'base-header':BaseHeader,
  	'base-footer':BaseFooter
  },
  beforeRouteEnter (to, from, next){
  	 next(vm => {
    	vm.activeIndex = to.path
  	})
  },
  beforeRouteUpdate (to, from, next) {
	  if(to.path == '/'){
	  	this.footerShow = true
	  }else{
	  	this.footerShow = false
	  }
	  this.activeIndex = to.path
	  next()
	}
}
</script>

<style>

.me-container{
  margin: 50px auto 140px;
}
</style>
