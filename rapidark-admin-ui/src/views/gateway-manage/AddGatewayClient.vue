<template>
	<div>
<!--		<el-page-header @back="goBack" content="已注册客户端管理"></el-page-header>-->
		<el-dialog title="添加客户端" :visible.sync="dialogFormVisible" width="40%" :close-on-click-modal="false">
			<el-table size="mini" :data="clientTableData" style="width: 100%">
				<el-table-column label="客户端ID" width="280">
					<template slot-scope="scope">
						<el-tag size="small" type="warning" style="font-weight: bold;">{{scope.row.appId}}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="分组" width="120">
					<template slot-scope="scope">
						<el-tag v-for="group in groupOptions" :key="group.value" v-show="(group.value === scope.row.groupCode)" size="small" type="">{{group.label}}</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="名称" prop="appName"></el-table-column>
				<el-table-column label="IP地址" prop="ip"></el-table-column>
				<el-table-column label="操作" width="60">
					<template slot-scope="scope">
						<el-button size="mini" circle icon="el-icon-plus" type="success" title="添加" @click="handleAddRegClient(scope.row)"></el-button>
					</template>
				</el-table-column>
			</el-table>
			<div class="block" style="margin-top: 20px;">
				<el-pagination
					@size-change="handleSizeChange"
					@current-change="handleCurrentChange"
					:current-page="clientCurrentPage"
					:page-sizes="[10,  30, 50]"
					:page-size="clientPageSize"
					layout="total, sizes, prev, pager, next, jumper"
					:total="clientTotalNum">
				</el-pagination>
			</div>
			<div slot="footer" class="dialog-footer">
				<el-button @click="dialogFormVisible = false" size="small">关 闭</el-button>
			</div>
		</el-dialog>

		<el-row :gutter="20" style="margin-top: 20px;">
			<el-col>
				<el-card class="box-card">
					<div slot="header" class="clearfix">
						<span>网关客户端</span>
						<el-popover trigger="click" placement="bottom">
							<div style="font-size: 10pt;">
								<span>添加客户端说明：</span><br/>
								<span>1. 开启路由服务IP过滤后，默认只有已注册到当前服务的客户端才有权限访问。</span><br/>
								<span>2. 新添加的客户端，默认为禁止访问路由服务，请手动开启允许访问，才能生效。</span><br/>
								<span>3. 点击<i class="iconfont icon-xiaoxitongzhi"></i>查看与创建客户端JWT通行令牌。</span>
							</div>
							<el-button slot="reference" style="padding: 3px 0; " icon="el-icon-question" type="text" title="说明"></el-button>
						</el-popover>
						<span style="margin-left: 50px;">
							<i class="el-icon-connection"></i>
							<span style="font-size: 11pt;">
								<el-tag size="mini" style="font-weight: bold;">{{form.name}}</el-tag> -
								<el-tag size="mini" type="warning" style="font-weight: bold;">{{form.systemCode}}</el-tag> -
								<el-tag size="mini" type="success" style="font-weight: bold;">{{form.uri}}</el-tag>
							</span>
						</span>
						<div style="float: right; margin-left: 10px;">
						    <el-button icon="el-icon-circle-plus-outline" size="small" type="success" @click="search" title="查找客户端">添加客户端</el-button>
						</div>
						<div style="float: right; margin-left: 10px;">
						    <el-button icon="el-icon-s-claim" size="small" type="primary" @click="startAll" title="启用所有客户端通行">全部允许</el-button>
						</div>
						<div style="float: right; margin-left: 10px;">
						    <el-button icon="el-icon-circle-close" size="small" type="danger" @click="stopAll" title="禁用所有客户端通行">全部禁止</el-button>
						</div>
					</div>

					<el-table size="small" :data="tableData" style="width: 100%">
						<el-table-column label="客户端ID(系统生成)" width="180">
							<template slot-scope="scope">
								<el-tag size="small" type="warning" style="font-weight: bold;">{{scope.row.appId}}</el-tag>
							</template>
						</el-table-column>
						<el-table-column label="分组">
							<template slot-scope="scope">
								<el-tag v-for="group in groupOptions" :key="group.value" v-show="(group.value === scope.row.groupCode)" size="small" type="">{{group.label}}</el-tag>
							</template>
						</el-table-column>
						<el-table-column label="名称" prop="appName"></el-table-column>
						<el-table-column label="IP地址" prop="ip"></el-table-column>
						<el-table-column label="TOKEN" width="240">
							<template slot-scope="scope">
								<el-tag  v-if="scope.row.token != undefined && scope.row.token != ''" size="small" type="success" style="font-weight: bold;">JWT通行令牌</el-tag>
								<el-tag  v-if="scope.row.token == undefined || scope.row.token == ''" size="small" type="" style="font-weight: bold;">无通行令牌</el-tag>
								<el-tag  v-if="scope.row.isTimeout == '1'" size="small" type="danger" style="font-weight: bold;">已过期</el-tag>
								<el-tag  v-if="scope.row.token != undefined && scope.row.token != '' && scope.row.isTimeout != '1'" size="small" type="primary" style="font-weight: bold;">未过期</el-tag>

                <el-popover trigger="click" placement="bottom" :v-model="true">
									<div style="width:500px;font-size: 10pt; ">
										<div style="margin-bottom: 8px;">
                      <vab-icon icon="mail-line" style="font-size: 16pt; color: #90A0A5;" />
<!--											<i class="iconfont icon-xiaoxitongzhi" style="font-size: 16pt; color: #90A0A5;"></i>-->
											<span class="route-title">JWT通行令牌</span>
										</div>
										<div>
											过期时间：<el-date-picker :popperAppendToBody="false" v-model="tokenEffectiveTime" type="datetime" placeholder="选择截止过期时间" style="width:300px;" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker><br/><br/>
											加密密钥：<el-input placeholder="请输入加密密钥(最长200个字符，为空则默认取客户端ID)" v-model="secretKey" maxlength="200" style="width:430px;" clearable></el-input><br/><br/>
											<el-input size="small" type="textarea" :rows="8" placeholder="JWT通行令牌内容" v-model="token" :disabled="tokenIsTrue"></el-input><br/><br/>
											<div style="float: right; margin-left: 10px;">
												<el-button icon="el-icon-delete" size="small" type="danger" @click="handleRemoveToken(scope.row)">清空令牌</el-button>
												<el-button icon="el-icon-refresh" size="small" type="primary" @click="handleCreateToken(scope.row)">生成令牌</el-button>
												<el-button icon="el-icon-document-copy" size="small" type="success" @click="handleCopyToken()">复制令牌</el-button>
												<!-- <el-button icon="el-icon-circle-close" size="small" type="" @click="handleCloseToken(scope.$index)">关闭</el-button> -->
											</div>
										</div>
									</div>
									<el-button slot="reference" icon="iconfont ri-mail-line" type="text" @click="queryRegClientTokenInfo(scope.row, scope.$index)" title="点击查看JWT通行令牌"></el-button>
								</el-popover>
							</template>
						</el-table-column>
						<el-table-column label="TOKEN过期时间" prop="tokenEffectiveTime"></el-table-column>
						<el-table-column label="状态" prop="status" :formatter="formatterStatus">
							<template slot-scope="scope">
								<div v-if="scope.row.regServerStatus==='1'"><i class="el-icon-success" style="color: #409EFF;"></i>&nbsp;<el-tag size="mini">{{'允许通行'}}</el-tag></div>
								<div v-if="scope.row.regServerStatus==='0'"><i class="el-icon-error" style="color: #f00000;"></i>&nbsp;<el-tag size="mini" type="danger">{{'禁止通行'}}</el-tag></div>
							</template>
						</el-table-column>
						<el-table-column label="注册时间" prop="regServerTime"></el-table-column>
						<el-table-column label="操作" width="100">
							<template slot-scope="scope">
								 <el-dropdown trigger="click" @command="handleCommandRegClient">
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



				</el-card>
			</el-col>
		</el-row>

	</div>
</template>

<script>
	import { addRegClient,regClientPageList,startRegClient,stopRegClient,deleteRegClient,
    startAllRegClient,stopAllRegClient,
    notRegClientPageList,
    createRegClientToken,removeRegClientToken } from '@/api/gateway-manage/regserver_api.js'

	export default {
		data() {
			return {
				dialogFormVisible: false,
				secretKey:null,
				token:null,
				tokenEffectiveTime:null,
				tokenIsTrue: true,
				form:{},
				tableData: [{
				}],
				currentPage: 1,
				pageSize: 10,
				totalNum: 0,
				clientTableData: [{
				}],
				clientCurrentPage: 1,
				clientPageSize: 10,
				clientTotalNum:0,
				groupOptions: this.GLOBAL_VAR.groups
			};
		},
		created: function() {
			//在组件创建完毕后加载
			let query = this.$route.query;
			if (query){
				let route = query.route;
				console.log('route', route);
				this.init(route);
			}
		},
		mounted: function() {

		},
		beforeDestroy: function() {

		},
		methods:{
			init(route) {
				this.form = route.form;
				this.regClientList();
			},
			goBack() {
			    console.log('go back');
				this.$router.push({path:'/gatewayList',query:{}});
			},
			handleSizeChange(val) {
				this.pageSize = val;
			},
			handleCurrentChange(val) {
				this.currentPage = val;
			},
			handleShowInfo(row, index){
				this.infoForm = row;
			},
			formatterStatus(row, index){
				return "允许通行"
			},
			handleCommandRegClient(obj){
				console.log("command" , obj);
				let _this = this;
				if (obj.command === 'start'){
					this.$confirm('确认允许当前客户端"'+obj.row.name+'"访问该注册服务？').then(_ => {
						startRegClient({id:obj.row.regServerId}).then(function(result){
							_this.GLOBAL_FUN.successMsg();
							_this.regClientList();
						});
					}).catch(_ => {});
				} else if (obj.command === 'stop'){
					this.$confirm('确认禁止当前客户端"'+obj.row.name+'"访问该注册服务？').then(_ => {
						stopRegClient({id:obj.row.regServerId}).then(function(result){
							_this.GLOBAL_FUN.successMsg();
							_this.regClientList();
						});
					}).catch(_ => {});
				} else if (obj.command === 'delete'){
					this.$confirm('确认删除客户端"'+obj.row.name+'"注册该服务？').then(_ => {
						deleteRegClient({id:obj.row.regServerId}).then(function(result){
							_this.GLOBAL_FUN.successMsg();
							_this.regClientList();
						})
					}).catch(_ => {});
				}
			},
			handleAddRegClient(row){
				let _this = this;
				addRegClient({clientId: row.appId, routeId: this.form.id}).then(function(result){
					_this.GLOBAL_FUN.successMsg();
					_this.regClientList();
					_this.clientList();
				});
			},
			startAll(){
				let _this = this;
				this.$confirm('确认要允许当前服务下所有已注册的客户端访问？').then(_ => {
					startAllRegClient({routeId: _this.form.id,}).then(function(result){
						_this.GLOBAL_FUN.successMsg();
						_this.regClientList();
					});
				}).catch(_ => {});
			},
			stopAll(){
				let _this = this;
				this.$confirm('确认要禁止当前服务下所有已注册客户端访问？').then(_ => {
					stopAllRegClient({routeId: _this.form.id,}).then(function(result){
						_this.GLOBAL_FUN.successMsg();
						_this.regClientList();
					});
				}).catch(_ => {});
			},
			regClientList(){
				let _this = this;
        console.log('this.form', this.form)
				regClientPageList({routeId: this.form.id, currentPage: this.currentPage, pageSize: this.pageSize}).then(function(result){
					if (result.data){
						_this.tableData = result.data.content;
						_this.totalNum = result.data.totalElements - 0;
					}
				})
			},
			clientList(){
				let _this = this;
				notRegClientPageList({routeId: this.form.id, currentPage: this.clientCurrentPage, pageSize: this.clientPageSize}).then(function(result){
					console.log(result);
					if (result.data && result.data.content){
						_this.clientTableData = result.data.content;
						_this.clientTotalNum = result.data.totalElements - 0;
					}
				});
			},
			handleCreateToken(row){ //创建注册客户端Token
				let _this = this;
				console.log(this.tokenEffectiveTime);
				if (this.tokenEffectiveTime == null || this.tokenEffectiveTime == undefined || this.tokenEffectiveTime == ''){
					this.GLOBAL_FUN.errorMsg('JWT通行令牌过期时间不能为空');
					return false;
				}
				createRegClientToken({regServerId: row.regServerId, secretKey:this.secretKey, tokenEffectiveTime: this.tokenEffectiveTime}).then(function(result){
					if (result.data){
						_this.token = result.data;
						_this.GLOBAL_FUN.successMsg('创建成功');
						_this.tableData.forEach((data, index)=>{
							if (data.regServerId == row.regServerId){
								data.secretKey = _this.secretKey;
								data.tokenEffectiveTime = _this.tokenEffectiveTime;
								data.token = _this.token;
							}
						});
					}
				});
			},
			handleRemoveToken(row){ //清除注册客户端Token
				let _this = this;
				removeRegClientToken({regServerId: row.regServerId}).then(function(result){
					_this.token = null;
					_this.secretKey = null;
					_this.tokenEffectiveTime = null;
					_this.GLOBAL_FUN.successMsg('清除成功');
					_this.tableData.forEach((data, index)=>{
						if (data.regServerId == row.regServerId){
							data.secretKey = _this.secretKey;
							data.tokenEffectiveTime = _this.tokenEffectiveTime;
							data.token = _this.token;
						}
					});
				});
			},
			handleCopyToken(){//复制token
				if (this.token == null || this.token == undefined || this.token == ''){
					this.GLOBAL_FUN.errorMsg('JWT通行令牌内容不能为空');
					return false;
				}
				this.GLOBAL_FUN.copy(this.token);
			},
			queryRegClientTokenInfo(row,index){//查看Token
				this.token = row.token;
				this.secretKey = row.secretKey;
				this.tokenEffectiveTime = row.tokenEffectiveTime;
			},
			search(){
				this.dialogFormVisible = true;
				this.clientList();
			}
		}
	}
</script>

<style>
	.el-icon-setting:before {
	  font-size: 12pt;
    content: "\E6CA";
	}
</style>
