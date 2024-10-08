<template>
  <div>
    <el-card class="box-card">
      <el-row style="margin-bottom: 10px;">
        <el-col :span="10">
          <div style="margin-bottom: 9px;">
            <span style="font-size: 18pt;font-weight: bold; "></span>
          </div>
        </el-col>
        <el-col :span="14">
          <div style="float: right; margin-left: 10px;">
            <el-button icon="el-icon-folder-add" type="primary" @click="handleCreateClient"></el-button>
          </div>
          <div style="float: right;">
            <el-input placeholder="请输入客户端名称" v-model="form.name" class="input-with-select" style="width: 620px;" clearable>
              <el-select v-model="form.groupCode" slot="prepend" placeholder="请选择分组" style="width: 140px; margin-right: 10px;">
                <el-option label="所有" value=""/>
                <el-option v-for="item in groupOptions" :key="item.value" :label="item.label" :value="item.value"/>
              </el-select>
              <el-popover placement="bottom" slot="prepend" trigger="click">
                <el-radio v-model="form.status" v-for="item in statusOptions" :key="item.value" :label="item.value">{{item.label}}</el-radio>
                <el-button slot="reference">
                  服务状态:{{form.status === '0' ? '启用': form.status === '1' ? '禁用' : '所有'}}<i class="el-icon-caret-bottom el-icon--right"></i>
                </el-button>
              </el-popover>
              <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
            </el-input>
          </div>
        </el-col>
      </el-row>
      <el-table size="small" :data="tableData" style="width: 100%">

        <el-table-column label="分组" align="center" width="120">
          <template #default="{ row }">
            <el-tag v-for="group in groupOptions" :key="group.value"
                    v-show="group.value === row.groupCode" size="small" type="">{{group.label}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="客户端代码" width="150">
          <template slot-scope="scope">
            <span style="font-weight: bold;" v-if="scope.row.appNameEn != undefined && scope.row.appNameEn != ''">{{scope.row.appNameEn}}</span>
          </template>
        </el-table-column>
        <el-table-column label="客户端名称" width="150">
          <template slot-scope="scope">
            {{scope.row.appName}}
          </template>
        </el-table-column>
        <el-table-column align="center" label="状态" prop="status" width="80" show-overflow-tooltip>
          <template #default="{ row }">
            <div v-if="row.status===1" class="el-timeline-item__dot" style="position: inherit">
              <span data-v-4607fb9e="" class="vab-dot vab-dot-success">
                <span data-v-4607fb9e=""></span>
              </span>
              <span>启用</span>
            </div>
            <div v-else class="el-timeline-item__dot" style="position: inherit">
              <span data-v-4607fb9e="" class="vab-dot vab-dot-error">
                <span data-v-4607fb9e=""></span>
              </span>
              <span>禁用</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="IP地址" width="100">
          <template slot-scope="scope">
            <el-tag size="small" type="success">{{scope.row.ip}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="140"></el-table-column>
        <!--				<el-table-column label="状态" prop="status" width="100">-->
        <!--					<template slot-scope="scope">-->
        <!--						<el-tag effect="dark" size="small" v-if="scope.row.status === 1" type="">启用</el-tag>-->
        <!--						<el-tag effect="dark" size="small" v-if="scope.row.status === 0" type="danger">禁用</el-tag>-->
        <!--					</template>-->
        <!--				</el-table-column>-->
        <el-table-column label="客户端ID(系统生成)" width="220">
          <template slot-scope="scope">
            <el-tag size="small" type="warning" style="font-weight: bold;">{{scope.row.appId}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="left" label="ApiKey" prop="apiKey" width="200" show-overflow-tooltip />
        <el-table-column align="left" label="SecretKey" prop="secretKey" width="300" show-overflow-tooltip />

        <el-table-column label="备注" prop="appDesc" width="350"></el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template slot-scope="scope">
            <el-dropdown trigger="click" @command="handleCommandClient">
              <el-button size="mini" type="warning">
                管理<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-connection" :command="{command:'addGateway', row: scope.row}">注册应用服务</el-dropdown-item>
                <el-dropdown-item icon="el-icon-tickets" :command="{command:'resetSecret', row: scope.row}">重置秘钥</el-dropdown-item>
                <el-dropdown-item icon="el-icon-tickets" :command="{command:'info', row: scope.row}">详情</el-dropdown-item>
                <el-dropdown-item icon="el-icon-edit" :command="{command:'edit', row: scope.row}">编辑</el-dropdown-item>
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
          :total="totalNum">
        </el-pagination>
      </div>
    </el-card>

    <el-drawer :visible.sync="drawer" :direction="direction" :before-close="handleClose" :with-header="false" size="26%" @opened="handleOpenedClientInfo">
      <!-- 父组件传参与子组件方法监听 -->
      <clientInfoComponent ref="clientInfo" :infoForm="infoForm"></clientInfoComponent>
    </el-drawer>
  </div>
</template>

<script>
import {clientPageList, deleteClient, startClient, stopClient} from '@/api/gateway-manage/client_api.js'
import clientInfoComponent from '@/component/ClientInfo.vue'
import {restApp} from "@/api/gateway-manage/open_client";

export default {
  data() {
    return {
      form:{
        name:null,
        groupCode: null,
        status: null
      },
      ip: null,
      drawer: false,
      direction: 'rtl',
      infoForm:{},
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      totalNum: 0,
      statusOptions: [
        {value: null, label: '所有'},
        {value: '0',label: '启用'},
        {value: '1',label: '禁用'},
      ],
      groupOptions: this.GLOBAL_VAR.groups
    };
  },
  created: function() {
    this.init();
  },
  components:{
    clientInfoComponent
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
    handleCreateClient(){
      this.$router.push({path:'/createClient',query:{handleType:'add'}});
    },
    handleOpenedClientInfo(){
      this.$refs.clientInfo.init();
    },
    handleCommandClient(obj){
      console.log("command" , obj);
      let _this = this;
      if (obj.command === 'addGateway'){
        this.$router.push({path:'/addClientGateway',query:{client:obj.row}});
      } else if (obj.command === 'info'){
        this.drawer = true;
        this.infoForm = obj.row;
      }  else if (obj.command === 'edit'){
        this.infoForm = obj.row;
        this.$router.push({path:'/createClient',query:{handleType:'edit',client:obj.row}});
      } else if (obj.command === 'start'){
        startClient({id:obj.row.appId}).then(function(result){
          _this.GLOBAL_FUN.successMsg();
          _this.search();
        });
      } else if (obj.command === 'stop'){
        stopClient({id:obj.row.appId}).then(function(result){
          _this.GLOBAL_FUN.successMsg();
          _this.search();
        });
      } else if (obj.command === 'delete'){
        this.$confirm('确认删除"'+obj.row.appName+'"客户端？').then(_ => {
          deleteClient({id:obj.row.appId}).then(function(result){
            _this.GLOBAL_FUN.successMsg();
            _this.search();
          })
        }).catch(_ => {});
      } else if (obj.command === 'resetSecret') {
        this.handleResetSecret({ appId: obj.row.appId })
      }
    },
    handleResetSecret (data) {
      this.$Modal.confirm({
        title: '重置后将影响应用正常使用,确定重置吗？',
        onOk: () => {
          restApp({ appId: data.appId }).then(res => {
            if (res.code === 0) {
              this.pageInfo.page = 1
              this.formItem.secretKey = res.data
              this.$Message.success('重置成功,请妥善保管.并及时更新到相关应用')
              this.handleSearch()
            }
          })
        }
      })
    },
    handleClose(done) {
      done();
    },
    search(){
      let _this = this;
      const params = {
        name:this.form.name,
        status: this.form.status,
        groupCode: this.form.groupCode,
        ip: this.ip,
        page: this.currentPage,
        pageSize: this.pageSize
      }
      clientPageList(params).then(function(result){
        if (result.data){
          _this.tableData = result.data.content;
          _this.totalNum = result.data.totalElements - 0;
        }
      });
    }
  }
};
</script>

<style>
.input-with-select .el-input-group__prepend {
    background-color: #fff;
}
</style>
