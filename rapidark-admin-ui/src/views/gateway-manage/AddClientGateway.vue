<template>
  <div style="height: 100%;">
    <!--		<el-page-header @back="goBack" content="已注册服务端管理"></el-page-header>-->
    <el-dialog title="添加服务端" :visible.sync="dialogFormVisible" width="40%" :close-on-click-modal="false">
      <el-table size="mini" :data="routeTableData" style="width: 100%">
        <el-table-column label="服务ID" prop="id"></el-table-column>
        <el-table-column label="服务名称" prop="name"></el-table-column>
        <el-table-column label="服务地址" prop="uri"></el-table-column>
        <el-table-column label="操作" width="60">
          <template slot-scope="scope">
            <el-button size="mini" circle icon="el-icon-plus" type="success" title="添加" @click="handleAddRegServer(scope.row)"></el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="block" style="margin-top: 20px;">
        <el-pagination
          @size-change="handleRouteSizeChange"
          @current-change="handleRouteCurrentChange"
          :current-page="routeCurrentPage"
          :page-sizes="[10,  30, 50]"
          :page-size="routePageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="routeTotalNum">
        </el-pagination>
      </div>
    </el-dialog>

    <el-row :gutter="20" style="height: 100%;margin-top: 0px;">
      <el-col :span="8">
        <my-panel icon="el-icon-menu" fit>
          <div slot="title" style="float:right;">
            <span>网关服务端</span>
            <el-popover trigger="click" placement="bottom">
              <div style="font-size: 10pt;">
                <span>添加服务端说明：</span><br/>
                <span>1. 开启路由服务ID过滤后，只有请求参数或Header中带有clientId="当前客户端ID(注册KEY)"才能访问指定服务端。</span><br/>
                <span>2. 开启路由服务ID过滤后，此功能主要适用于对第三方开放服务，提供简单认证访问。</span><br/>
                <span>3. 新添加的路由服务端后，默认为禁止访问路由服务，请手动开启允许访问，才能生效。</span><br/>
              </div>
              <el-button slot="reference" style="padding: 3px 0; " icon="el-icon-question" type="text" title="说明"></el-button>
            </el-popover>

            <span style="margin-left: 50px;">
							<i class="el-icon-monitor"></i>
							<span style="font-size: 11pt;">
                <el-tag size="mini" style="font-weight: bold;">{{formModel.appName}}</el-tag>
								<el-tag size="mini" type="success" style="font-weight: bold;">{{formModel.ip}}</el-tag>
							</span>
						</span>
          </div>
          <div>
            <div style="float: right; margin-left: 10px;">
              <el-button icon="el-icon-circle-plus-outline" size="small" type="success" @click="search" title="查找服务端"> 添加服务 </el-button>
            </div>
            <div style="float: right; margin-left: 10px;">
              <el-button icon="el-icon-s-claim" size="small" type="primary" @click="startAll" title="启用所有客户端通行"> 全部允许 </el-button>
            </div>
            <div style="float: right; margin-left: 10px;">
              <el-button icon="el-icon-circle-close" size="small" type="danger" @click="stopAll" title="禁用所有客户端通行"> 全部禁止 </el-button>
            </div>
          </div>

          <el-table ref="table" :height="tableHeight" size="small" style="width: 100%" highlight-current-row
                    :data="tableData" @row-click="handleAppTableRowClick">
            <!--						<el-table-column label="服务ID" prop="id"></el-table-column>-->
            <el-table-column label="服务名称" prop="name"></el-table-column>
            <!--						<el-table-column label="服务地址" prop="uri"></el-table-column>-->
            <!--						<el-table-column label="断言路径" prop="path"></el-table-column>-->
            <el-table-column label="注册时间" prop="regServerTime" width="90">
              <template #default="{ row }">
                {{ row.regServerTime.substring(0, 10) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" prop="regServerStatus" width="100">
              <template slot-scope="scope">
                <div v-if="scope.row.regServerStatus===1"><i class="el-icon-success" style="color: #409EFF;"></i>&nbsp;<el-tag size="mini">{{'允许通行'}}</el-tag></div>
                <div v-if="scope.row.regServerStatus===0"><i class="el-icon-error" style="color: #f00000;"></i>&nbsp;<el-tag size="mini" type="danger">{{'禁止通行'}}</el-tag></div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="60">
              <template slot-scope="scope">
                <el-dropdown trigger="click" @command="handleCommandRegServer">
                  <el-button size="mini" circle icon="el-icon-setting" title="设置" style="border: 0px;"></el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :command="{command:'start', row: scope.row}"><i class="el-icon-success" style="color: #409EFF;"></i>允许通行</el-dropdown-item>
                    <el-dropdown-item :command="{command:'stop', row: scope.row}"><i class="el-icon-error" style="color: red;"></i>禁止通行</el-dropdown-item>
                    <el-dropdown-item :command="{command:'delete', row: scope.row}" divided><i class="el-icon-delete"></i>移除</el-dropdown-item>
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
        </my-panel>
      </el-col>
      <el-col :span="16">
        <my-panel icon="el-icon-menu" title="分配权限" >
          <template #handle>
            <el-button-group>
              <el-button icon="el-icon-s-claim" size="mini" type="success" @click="submitAppApis">保存</el-button>
            </el-button-group>
          </template>
          <el-form ref="form" :model="formModel"  label-width="100" :rules="formModelRules">
            <el-form-item prop="expireTime" label="过期时间">
              <el-badge v-if="formModel.isExpired" text="授权已过期">
                <el-date-picker v-model="formModel.expireTime" class="ivu-form-item-error" type="datetime"
                                placeholder="授权有效期"/>
              </el-badge>
              <el-date-picker v-else v-model="formModel.expireTime" type="datetime" placeholder="设置有效期"/>
            </el-form-item>
            <el-form-item prop="authorities" label="功能接口" >
              <el-transfer
                :height="tableHeight"
                :titles="['选择接口', '已选择接口']"
                :button-texts="['删除', '添加']"
                :data="selectApis"
                v-model="formModel.authorities"
                :render-content="transferRender"
                @change="handleTransferChange"
                filterable/>
            </el-form-item>
          </el-form>
        </my-panel>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import {addRegServer,deleteRegServer,stopRegServer,startRegServer,regServerPageList,startAllRegServer,stopAllRegServer,notRegServerPageList} from '@/api/gateway-manage/regserver_api.js'
import {getAuthorityApi, getAuthorityApp, grantAuthorityApp} from "@/api/authority";
import { MyPanel } from '$ui'

export default {
  components: { MyPanel },
  computed: {
    tableHeight: function () {
      const screenHeight = document.body.clientHeight;
      return screenHeight - 280
    }
  },
  data() {
    return {
      dialogFormVisible: false,
      formLabelWidth: '100px',
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      totalNum: 1,
      routeTableData: [],
      routeCurrentPage: 1,
      routePageSize: 10,
      routeTotalNum: 1,
      formModel: {
        apiKey: "",
        appDesc: "",
        appIcon: "",
        appId: "",
        appName: "",
        appNameEn: "",
        appOs: "",
        appType: "",
        authorities: [],
        createBy: "",
        createTime: "",
        developerId: 0,
        encryptType: "",
        expireTime: "",
        groupCode: "",
        ip: "",
        isEncrypt: 0,
        isExpired: false,
        isPersist: 0,
        isSign: 0,
        publicKey: "",
        secretKey: "",
        status: 0,
        updateBy: "",
        updateTime: "",
        website: ""
      },
      formModelRules: {},
      selectApis: [],
      apiModel: [],
      currentAppCode: ''
    };
  },
  created: function() {
    //在组件创建完毕后加载
    let query = this.$route.query;
    if (query){
      let client = query.client;
      console.log('client', client);
      this.init(client);
    }
  },
  mounted: function() {

  },
  beforeDestroy: function() {

  },
  methods:{
    init(client) {
      if (client){
        console.log('init client', client)
        delete client.authorities
        // this.formModel = client;
        Object.assign(this.formModel, client)
        // this.formModel.authorities.length = 0
        this.regServerList();
      }
    },
    goBack() {
      console.log('go back');
      this.$router.push({path:'/clientList',query:{}});
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.regServerList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.regServerList();
    },
    handleRouteSizeChange(val) {
      this.routePageSize = val;
      this.routeList();
    },
    handleRouteCurrentChange(val) {
      this.routeCurrentPage = val;
      this.routeList();
    },
    handleCommandRegServer(obj){
      console.log("command" , obj);
      let _this = this;
      if (obj.command === 'start'){
        this.$confirm('确认允许当前客户端访问"'+obj.row.name+'"该注册服务？').then(_ => {
          startRegServer({id:obj.row.regServerId}).then(function(result){
            _this.GLOBAL_FUN.successMsg();
            _this.regServerList();
          });
        }).catch(_ => {});
      } else if (obj.command === 'stop'){
        this.$confirm('确认禁止当前客户端访问"'+obj.row.name+'"该注册服务？').then(_ => {
          stopRegServer({id:obj.row.regServerId}).then(function(result){
            _this.GLOBAL_FUN.successMsg();
            _this.regServerList();
          });
        }).catch(_ => {});
      } else if (obj.command === 'delete'){
        this.$confirm('确认删除"'+obj.row.name+'"该注册服务？').then(_ => {
          deleteRegServer({id:obj.row.regServerId}).then(function(result){
            _this.GLOBAL_FUN.successMsg();
            _this.regServerList();
          })
        }).catch(_ => {});
      }
    },
    handleAppTableRowClick(row) {
      console.log(row)
      const appSystemCode = row.systemCode
      this.handleLoadAppGranted(this.formModel.appId, appSystemCode)
    },
    handleAddRegServer(row){
      let _this = this;
      addRegServer({ clientId: this.formModel.appId, routeId: row.id }).then(function(result){
        _this.GLOBAL_FUN.successMsg();
        _this.regServerList();
        _this.routeList();
      });
    },
    startAll(){
      let _this = this;
      this.$confirm('确认要允许当前客户端访问所有已注册服务？').then(_ => {
        startAllRegServer({clientId: _this.formModel.appId,}).then(function(result){
          _this.GLOBAL_FUN.successMsg();
          _this.regServerList();
        });
      }).catch(_ => {});
    },
    stopAll(){
      let _this = this;
      this.$confirm('确认要禁止当前客户端访问所有已注册服务？').then(_ => {
        stopAllRegServer({clientId: _this.formModel.appId,}).then(function(result){
          _this.GLOBAL_FUN.successMsg();
          _this.regServerList();
        });
      }).catch(_ => {});
    },
    regServerList(){
      let _this = this;
      regServerPageList({clientId: this.formModel.appId, currentPage: this.currentPage, pageSize: this.pageSize}).then(function(result){
        if (result.data){
          _this.tableData = result.data.content;
          _this.totalNum = result.data.totalElements - 0;

          if(_this.tableData && _this.tableData.length > 0) {
            _this.$refs['table'].setCurrentRow(_this.tableData[0])
            _this.currentAppCode = _this.tableData[0].systemCode
            _this.handleAppTableRowClick({ systemCode: _this.currentAppCode })
          }
        }
      });
    },
    routeList(){
      let _this = this;
      notRegServerPageList({
        clientId: this.formModel.appId,
        page: this.routeCurrentPage - 1,
        size: this.routePageSize
      }).then(function(result){
        console.log(result);
        if (result.data && result.data.content){
          _this.routeTableData = result.data.content;
          _this.routeTotalNum = result.data.totalElements - 0;
        }
      });
    },
    search(){
      this.dialogFormVisible = true;
      this.routeList();
    },
    transferRender (h, option) {
      return <span title={option.label}>{option.label}</span>
    },
    handleTransferChange (value, direction, moveKeys) {
      console.log('handleTransferChange', value)
      // if (newTargetKeys.indexOf('1') !== -1) {
      //   this.formModel.authorities = ['1']
      // } else {
      //   this.formModel.authorities = newTargetKeys
      // }
      // this.formModel.authorities = ['1524341061015105538']
      // this.formModel.authorities.push(newTargetKeys)
    },
    handleLoadAppGranted (openClientId, appSystemCode) {
      console.log('handleLoadAppGranted')
      if (!openClientId) {
        return
      }
      const that = this
      const p1 = getAuthorityApi(appSystemCode)
      const p2 = getAuthorityApp(openClientId, appSystemCode)
      Promise.all([p1, p2]).then(function (values) {
        let res1 = values[0]
        let res2 = values[1]
        if (res1.code === 0) {
          res1.data.map(item => {
            item.key = item.authorityId + ''
            item.label = `${item.apiName} - ${item.prefix.replace('/**', '')}${item.path}`
            item.disabled = item.path === '/**'
          })
          that.selectApis = res1.data
        }
        if (res2.code === 0) {
          // let authorities = ['1524341061015105538']
          // if(!that.formModel.authorities) {
          //   that.formModel.authorities = []
          // }
          that.formModel.authorities.length = 0;
          res2.data.map(item => {
            if (item.authority.indexOf('APP_') === -1 && !that.formModel.authorities.includes(item.authorityId)) {
              that.formModel.authorities.push(item.authorityId)
            }
          })
          // that.formModel.authorities = authorities
          // 时间
          if (res2.data.length > 0) {
            that.formModel.expireTime = res2.data[0].expireTime
            that.formModel.isExpired = res2.data[0].isExpired
          }
        }
        that.modalVisible = true
      })
    },
    submitAppApis() {
      // if (this.currentTab === this.forms[2]) {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.saving = true
          grantAuthorityApp({
            openClientId: this.formModel.appId,
            appSystemCode: this.currentAppCode,
            expireTime: this.formModel.expireTime ? this.formModel.expireTime.pattern('yyyy-MM-dd HH:mm:ss') : '',
            authorityIds: this.formModel.authorities
          }).then(res => {
            if (res.code === 0) {
              this.$Message.success('授权成功')
              // this.handleReset()
              // this.handleSearch()
            }
          }).finally(() => {
            this.saving = false
          })
        }
      })
      // }
    }
  }
}
</script>

<style scoped>
.el-icon-setting:before {
    font-size: 12pt;
    content: "\E6CA";
}
::v-deep .el-transfer-panel {
    width: 40%;
}
::v-deep .el-transfer__buttons {
    width: 96px;
    padding: 0 10px;
}
::v-deep .el-button + .el-button {
    margin-left: 0px;
}
::v-deep .my-panel__body {
    padding: 0;
}
::v-deep .el-transfer-panel__body {
    height: calc(100vh - 320px);
}
::v-deep .el-transfer-panel__list.is-filterable {
    height: calc(100vh - 370px);
}
</style>

