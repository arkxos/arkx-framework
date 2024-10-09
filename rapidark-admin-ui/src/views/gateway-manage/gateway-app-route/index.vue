<template>
  <div>
    <el-card class="box-card">
      <el-row style="margin-bottom: 10px;">
        <el-col :span="10">
          <div style="margin-bottom: 9px;">
<!--            <span style="font-size: 18pt;font-weight: bold; ">服务管理</span>-->
          </div>
        </el-col>
        <el-col :span="14">
          <div style="float: right; margin-left: 10px;">
            <el-button icon="el-icon-folder-add" type="primary" @click="handleCreateGateway" title="创建网关服务"></el-button>
          </div>
          <div style="float: right;">
            <el-input placeholder="请输入网关服务名称" v-model="form.name" class="input-with-select" style="width: 620px;" clearable>
              <el-select v-model="form.groupCode" slot="prepend" placeholder="请选择分组" style="width: 140px; margin-right: 10px;">
                <el-option label="所有" value=""/>
                <el-option v-for="item in groupOptions" :key="item.value" :label="item.label" :value="item.value"/>
              </el-select>
              <el-popover placement="bottom" slot="prepend" trigger="click">
                <el-radio v-model="form.status" v-for="item in statusOptions" :key="item.value" :label="item.value">{{item.label}}</el-radio>
                <el-button slot="reference">
                  服务状态:{{form.status === '1' ? '启用': form.status === '0' ? '禁用' : '所有'}}<i class="el-icon-caret-bottom el-icon--right"></i>
                </el-button>
              </el-popover>
              <el-button slot="append" icon="el-icon-search" @click="search" title="查询网关服务"></el-button>
            </el-input>
          </div>
        </el-col>
      </el-row>
      <el-table size="small" :data="tableData" style="width: 100%">

        <el-table-column label="分组" width="100">
          <template slot-scope="scope">
            <el-tag v-for="group in groupOptions" :key="group.value" v-show="(group.value === scope.row.groupCode)" size="small" type="">{{group.label}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" prop="status" :formatter="formatterStatus">
          <template slot-scope="scope">
            <el-tag size="small" v-if="scope.row.status === '1'" type="">启用</el-tag>
            <el-tag size="small" v-if="scope.row.status === '0'" type="gray">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="应用名称" width="200">
          <template slot-scope="scope">
            {{scope.row.name}}
          </template>
        </el-table-column>
        <el-table-column label="应用代码" width="200">
          <template slot-scope="scope">
            {{scope.row.systemCode}}
          </template>
        </el-table-column>
<!--        <el-table-column label="服务ID" width="150">-->
<!--          <template slot-scope="scope">-->
<!--            <el-tag size="small" type="warning" style="font-weight: bold;">{{scope.row.id}}</el-tag>-->
<!--          </template>-->
<!--        </el-table-column>-->

        <el-table-column label="路由前缀" width="250">
          <template slot-scope="scope">
            {{scope.row.path}}
            <el-popover trigger="click" placement="bottom">
              <div style="font-size: 10pt;">
                <div style="margin-bottom: 8px;">
                  <i class="iconfont icon-zuzhiqunzu" style="font-size: 16pt; color: #90A0A5;"></i>
                  <span class="route-title">网关代理地址</span>
                </div>
                <span>
									<el-tag size="small" type="success" style="font-weight: bold;">{{scope.row.path}}</el-tag>
									<el-button slot="reference" icon="el-icon-document-copy" type="text" @click="handleCopy(scope.row.path)" title="复制"></el-button>
								</span>
                <br/>
              </div>
              <el-button slot="reference" icon="iconfont icon-IP" type="text" title="网关代理地址"></el-button>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="路由方式" width="90" prop="status" :formatter="formatterStatus">
          <template slot-scope="scope">
            <el-tag size="small" v-if="scope.row.type === 'service'" type="success">负载均衡</el-tag>
            <el-tag size="small" v-if="scope.row.type === 'url'">反向代理</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="路由目标" :show-overflow-tooltip="true" width="200">
          <template slot-scope="scope">
            <el-tag size="small" v-if="scope.row.type === 'service'" type="info" style="font-weight: bold;">{{scope.row.uri}}</el-tag>
            <el-tag size="small" v-if="scope.row.type === 'url'" type="info" style="font-weight: bold;">{{scope.row.uri}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请求模式" prop="method" width="70"></el-table-column>
        <el-table-column label="熔断器" prop="filterHystrixName" width="100">
          <template slot-scope="scope">
            <el-tag effect="plain" size="small" v-if="scope.row.filterHystrixName === 'default'" type="">全局方法</el-tag>
            <el-tag effect="plain" size="small" v-if="scope.row.filterHystrixName === 'custom'" type="success">自定义方法</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="限流器" prop="filterRateLimiterName" width="100"></el-table-column>
        <el-table-column label="创建时间" width="140" prop="createTime"></el-table-column>

        <el-table-column label="操作" width="100" fixed="right">
          <template :v-if="scope.row.id != null" slot-scope="scope">
            <el-dropdown trigger="click" @command="handleCommandGateway">
              <el-button size="mini" type="warning">
                管理<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-user" :command="{command:'addClient', row: scope.row}">注册客户端</el-dropdown-item>
                <el-dropdown-item icon="el-icon-tickets" :command="{command:'info', row: scope.row}">详情</el-dropdown-item>
                <el-dropdown-item icon="el-icon-orange" :command="{command:'topology', row: scope.row}">拓扑</el-dropdown-item>
                <el-dropdown-item icon="el-icon-edit" :command="{command:'edit', row: scope.row}">编辑</el-dropdown-item>
                <el-dropdown-item icon="el-icon-eleme" :command="{command:'rule', row: scope.row}">规则组件</el-dropdown-item>
                <el-dropdown-item :command="{command:'start', row: scope.row}" divided><i class="el-icon-success" style="color: #409EFF;"></i>启用</el-dropdown-item>
                <el-dropdown-item :command="{command:'stop', row: scope.row}"><i class="el-icon-error" style="color: red;"></i>禁用</el-dropdown-item>
                <el-dropdown-item icon="el-icon-delete" :command="{command:'delete', row: scope.row}" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      <div class="block" style="margin-top: 20px;">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10,  30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalNum"
        ></el-pagination>
      </div>
    </el-card>

    <el-drawer :visible.sync="drawer" :direction="direction" :before-close="handleClose" :with-header="false" size="24%">
      <!-- 父组件传参与子组件方法监听 -->
      <routeInfoComponent ref="routeInfo" :infoForm="infoForm"></routeInfoComponent>
    </el-drawer>
  </div>
</template>

<script>
import routeInfoComponent from '@/component/RouteInfo.vue'
import {routePageList,startRoute,stopRoute,deleteRoute} from '@/api/gateway-manage/gatewayAppRoute.js'

export default {
  data() {
    return {
      form:{
        name:null,
        groupCode: null,
        status: null
      },
      drawer: false,
      direction: 'rtl',
      formLabelWidth: '140px',
      infoForm:{},
      gatewayServer:'',
      alarmDate:'',
      alarmStatus:[],
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      totalNum: 1,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        }
      },
      statusOptions: [
        {value: null, label: '所有'},
        {value: '0',label: '启用'},
        {value: '1',label: '禁用'},
      ],
      groupOptions: [
        { value: 'public_api', label: '公共API' },
        { value: 'external_api', label: '第三方API' },
        { value: 'interior_api', label: '内网API' },
        { value: 'pay_api', label: '支付API' },
        { value: 'other_api', label: '其它API' }
      ]
    };
  },
  components:{
    routeInfoComponent
  },
  created: function() {
    this.init();
  },
  mounted: function() {

  },
  beforeDestroy: function() {

  },
  methods:{
    init() {
      this.search();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.search();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.search();
    },
    handleShowInfo(row, index){
      this.infoForm = row;
    },
    formatterStatus(row, index){
      return row.status === '0'?'启用':'禁用';
    },
    handleCreateGateway(){
      this.$router.push({path:'/gateway-manage/createGateway',query:{handleType:'add'}});
    },
    handleCommandGateway(obj){
      console.log("command" , obj);
      let _this = this;
      if (obj.command === 'addClient'){
        this.$router.push({path:'/gateway-manage/addGatewayClient',query:{route:this.newRoute(obj.row)}});
      } else if (obj.command === 'info'){
        this.drawer = true;
        this.infoForm = obj.row;
      } else if (obj.command === 'edit'){
        this.infoForm = obj.row;
        this.$router.push({path:'/gateway-manage/createGateway',query:{handleType:'edit',route:this.newRoute(obj.row)}});
      } else if (obj.command === 'rule'){
        this.$router.push({path:'/gateway-manage/addGroovyScript',query:{route:this.newRoute(obj.row)}});
      } else if (obj.command === 'start'){
        startRoute({id:obj.row.id}).then(function(result){
          _this.GLOBAL_FUN.successMsg();
          _this.search();
        });
      } else if (obj.command === 'stop'){
        stopRoute({id:obj.row.id}).then(function(result){
          _this.GLOBAL_FUN.successMsg();
          _this.search();
        });
      } else if (obj.command === 'delete'){
        this.$confirm('确认删除"'+obj.row.name+'"网关路由？').then(_ => {
          deleteRoute({id:obj.row.id}).then(function(result){
            _this.GLOBAL_FUN.successMsg();
            _this.search();
          })
        }).catch(_ => {});
      } else if (obj.command === 'topology'){
        this.$router.push({path:'/gateway-manage/gatewayTopology',query:{route:this.newRoute(obj.row)}});
      }
    },
    handleClose(done) {
      // this.$confirm('确认关闭？').then(_ => {
      done();
      // }).catch(_ => {});
    },
    handleCopy(val){
      let value = this.GLOBAL_VAR.gatewayRoutesURL + val;
      this.GLOBAL_FUN.copy(value);
    },
    search(){
      let _this = this;
      routePageList({form : this.form, currentPage: this.currentPage, pageSize: this.pageSize}).then(function(result){
        console.log('result:', result);
        if (result.data && result.data.content){
          _this.tableData = result.data.content;
          _this.totalNum = result.data.totalElements - 0;
        }
      });
    },
    newRoute(row){
      let route = {
        form: row,
        filter: {
          ipChecked: row.filterGatewaName!=null && row.filterGatewaName.indexOf('ip')!=-1,
          tokenChecked: row.filterGatewaName!=null && row.filterGatewaName.indexOf('token')!=-1,
          idChecked: row.filterGatewaName!=null && row.filterGatewaName.indexOf('id')!=-1
        },
        hystrix:{
          defaultChecked: row.filterHystrixName!=null && row.filterHystrixName.indexOf('default')!=-1,
          customChecked: row.filterHystrixName!=null && row.filterHystrixName.indexOf('custom')!=-1
        },
        limiter:{
          ipChecked: row.filterRateLimiterName!=null && row.filterRateLimiterName.indexOf('ip')!=-1,
          uriChecked: row.filterRateLimiterName!=null && row.filterRateLimiterName.indexOf('uri')!=-1,
          idChecked: row.filterRateLimiterName!=null && row.filterRateLimiterName.indexOf('id')!=-1
        },
        access:{
          headerChecked: row.filterAuthorizeName!=null && row.filterAuthorizeName.indexOf('header')!=-1,
          ipChecked:  row.filterAuthorizeName!=null && row.filterAuthorizeName.indexOf('ip')!=-1,
          parameterChecked: row.filterAuthorizeName!=null && row.filterAuthorizeName.indexOf('parameter')!=-1,
          timeChecked: row.filterAuthorizeName!=null && row.filterAuthorizeName.indexOf('time')!=-1,
          cookieChecked: row.filterAuthorizeName!=null && row.filterAuthorizeName.indexOf('cookie')!=-1
        },
        monitor:{
          checked: row.monitor != null && row.monitor.status != '1'
        }
      }
      return route;
    }

  }
};
</script>

<style>
.el-drawer__header {
  -ms-flex-align: center;
  display: flex;
  align-items: center;
  padding: 10px 20px 0;
  margin-bottom: 2px;
  color: #72767b;
}
.input-with-select .el-input-group__prepend {
  background-color: #fff;
}
.gateway-info-label{
  padding: 8px 0;
  font-size: 14px;
  color: #606266;
  text-align: right;
  vertical-align: middle;
}
.gateway-info-value{
  padding: 8px 0px ;
  font-size: 14px;
  line-height: 20px;
}

</style>
