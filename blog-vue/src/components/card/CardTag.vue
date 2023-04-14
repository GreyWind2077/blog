<template>
	<div class="ui container">
		<div class="ui segments m-margin-top-large">
	                    <div class="ui secondary segment">
	                        <div class="ui two column grid">
	                            <div class="column">
	                                <i class="tags icon"></i>标签
	                            </div>
	                            <div class="right aligned column">
									<a @click="moreTags" class="me-pull-right me-tag-more">more<i class="angle double right icon"></i></a>
	                            </div>
	                        </div>
	                    </div>
	                    <div class="ui teal segment">
							
							  <li class="me-tag-item" v-for="t in tags" :key="t.id">
							   
							    <a class="ui teal basic left pointing label m-margin-tb-tiny" @click="tag(t.id)" size="mini" type="primary" round plain>{{t.tagName}}</a>
							  </li>
							
	                    </div>
	     </div>
	</div>
	
	
  <!-- <el-card :body-style="{ padding: '8px 18px' }">
    <div slot="header" class="me-tag-header">
      <span>最热标签</span>
      
    </div>

    <ul class="me-tag-list">
      <li class="me-tag-item" v-for="t in tags" :key="t.id">
        <!--type="primary"
        <el-button @click="tag(t.id)" size="mini" type="primary" round plain>{{t.tagName}}</el-button>
      </li>
    </ul>
  </el-card> -->

</template>

<script>
  export default {
    name: 'CardTag',
    props: {
      tags: Array
    },
    data() {
      return {}
    },
    methods: {
      moreTags() {
        this.$router.push('/tag/all')
      },
      tag(id) {
        this.$router.push({path: `/tag/${id}`})
      }
    }
  }
</script>

<style scoped>

  .me-tag-header {
    font-weight: 600;
  }

  .me-tag-more {
    font-size: 14px;
    color: #0000ff;
  }

  .me-tag-list {
    list-style-type: none;
  }

  .me-tag-item {
    display: inline-block;
    padding: 4px;
    font-size: 14px;
    color: #008080;
  }

  .me-tag-item a:hover {
    text-decoration: underline;
  }
</style>
