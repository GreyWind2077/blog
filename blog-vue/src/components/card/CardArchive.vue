<template>
	
	<div class="ui container">
		<div class="ui segments m-margin-top-large">
		<div class="ui secondary segment">
		    <i class="clone icon"></i>{{cardHeader}}
		</div>
		<div class="ui teal segment">
			
			  <ul class="me-category-list">
			    <li v-for="a in archives" @click="view(a.year, a.month)" :key="a.year + a.month" class="me-category-item"><a>{{`${a.year}年${a.month}月(${a.count})`}}</a>
			    </li>
			  </ul>
			
		</div>
		</div>
	</div>

 <!-- <el-card :body-style="{ padding: '8px 18px' }">
    <div slot="header" class="me-category-header">
      <span>{{cardHeader}}</span>
    </div>

    <ul class="me-category-list">
      <li v-for="a in archives" @click="view(a.year, a.month)" :key="a.year + a.month" class="me-category-item"><a>{{`${a.year}年${a.month}月(${a.count})`}}</a>
      </li>
    </ul>
  </el-card> -->

</template>

<script>
  export default {
    name: "CardArchive",
    props: {
      cardHeader: {
        type: String,
        required: true
      },
      archives: {
        type: Array,
        required: true
      }
    },
    methods: {
      view(year, month) {
        this.$router.push({path: `/archives/${year}/${month}`})
      }
    }
  }
</script>

<style scoped>

  .me-category-header {
    font-weight: 600;
  }

  .me-category-list {
    list-style-type: none;
  }

  .me-category-item {
    display: inline-block;
    width: 40%;
    padding: 4px;
    font-size: 14px;
    color: #5FB878;
  }

  .me-category-item a:hover {
    text-decoration: underline;
  }

</style>
