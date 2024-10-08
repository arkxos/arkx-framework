<template>
	<div>
<!--		<el-page-header @back="goBack" content="客户端管理"></el-page-header>-->
		<el-row :gutter="20" style="margin-top: 0px;">
			<el-col>
				<el-card class="box-card">
					<div slot="header" class="clearfix">
						<span>客户端配置</span>
						<div style="float: right; margin-left: 10px;"><el-button icon="el-icon-delete" size="mini" type="warning" @click="resetForm">清 空</el-button></div>
						<div style="float: right; margin-left: 10px;"><el-button icon="el-icon-s-claim" size="mini" type="success" @click="handleSubmit">发 布</el-button></div>
					</div>

          <el-tabs :value="currentTab" @tab-click="handleTabClick">
            <el-tab-pane label="应用信息" name="form1">
              <el-form ref="form1" v-show="currentTab=='form1'" :model="formModel" :rules="formModelRules" label-width="135" label-position="right">
                <el-form-item label="应用图标">
                  <div class="upload-list" v-for="(item,index) in uploadList" :key="index">
                    <template v-if="item.status === 'finished'">
                      <img :src="item.url">
                      <div class="upload-list-cover">
                        <Icon type="ios-eye-outline" @click.native="handleView(item.name)"/>
                        <Icon type="ios-trash-outline" @click.native="handleRemoveImg(item)"/>
                      </div>
                    </template>
                    <template v-else>
                      <Progress v-if="item.showProgress" :percent="item.percentage" hide-info/>
                    </template>
                  </div>
                  <Upload
                    ref="upload"
                    :show-upload-list="false"
                    :default-file-list="defaultList"
                    :format="['jpg','jpeg','png']"
                    :max-size="2048"
                    :on-success="handleSuccess"
                    :on-format-error="handleFormatError"
                    :on-exceeded-size="handleMaxSize"
                    :before-upload="handleBeforeUpload"
                    type="drag"
                    action="//jsonplaceholder.typicode.com/posts/"
                    style="display: inline-block;width:58px;">
                    <div style="width: 58px;height:58px;line-height: 58px;">
                      <Icon type="ios-camera" size="20"/>
                    </div>
                  </Upload>
                </el-form-item>
                <el-form-item label="AppId" prop="appId">
                  <el-input readonly v-model="formModel.appId" placeholder="自动生成"/>
                </el-form-item>
                <el-form-item label="开发商" prop="developerId">
                  <el-select v-model="formModel.developerId" filterable clearable>
                    <el-option :title="item.companyName" v-for="(item,index) in selectDevelopers" @click.native="handleOnSelectUser(item)"
                               :value="item.userId" :label="item.companyName" :key="index">
                      <span>{{ item.userName }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="应用代码" prop="appNameEn">
                  <el-input v-model="formModel.appNameEn" placeholder="请输入内容"/>
                </el-form-item>
                <el-form-item label="应用名称" prop="appName">
                  <el-input v-model="formModel.appName" placeholder="请输入内容"/>
                </el-form-item>

                <el-form-item label="分组" prop="groupCode">
                  <el-select filterable v-model="formModel.groupCode" placeholder="请选择分组" style="width: 300px;">
                    <el-option v-for="item in groupOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
                <el-form-item label="应用类型" prop="appType">
                  <el-select v-model="formModel.appType" @on-change="handleOnAppTypeChange">
                    <el-option value="server">服务器应用</el-option>
                    <el-option value="app">手机应用</el-option>
                    <el-option value="pc">PC网页应用</el-option>
                    <el-option value="wap">手机网页应用</el-option>
                  </el-select>
                </el-form-item>
                <el-form-item v-if="formModel.appType === 'app'" prop="appOs" label="操作系统">
                  <el-radio-group v-model="formModel.appOs">
                    <el-radio-button label="ios">
                      <Icon type="logo-apple"/>
                      <span>苹果IOS</span>
                    </el-radio-button>
                    <el-radio-button label="android">
                      <Icon type="logo-android"/>
                      <span>安卓Android</span>
                    </el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="IP" prop="ip">
                  <el-input v-model="formModel.ip" style="width: 300px;"></el-input>
                </el-form-item>
                <el-form-item label="应用官网" prop="website">
                  <el-input v-model="formModel.website" placeholder="请输入内容"/>
                </el-form-item>
                <el-form-item label="状态">
                  <el-radio-group v-model="formModel.status" type="button">
                    <el-radio-button label="0">下线</el-radio-button>
                    <el-radio-button label="1">上线</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="描述">
                  <el-input v-model="formModel.appDesc" type="textarea" placeholder="请输入内容"/>
                </el-form-item>
                <el-form-item label="是否验签">
                  <el-radio-group v-model="formModel.isSign">
                    <el-radio-button label="0">否</el-radio-button>
                    <el-radio-button label="1">是</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="是否加密">
                  <el-radio-group v-model="formModel.isEncrypt">
                    <el-radio-button label="0">否</el-radio-button>
                    <el-radio-button label="1">是</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="加密类型" prop="encryptType">
                  <el-select v-model="formModel.encryptType" @on-change="handleOnEncryptTypeChange">
                    <el-option value="RSA">RSA</el-option>
                    <el-option value="AES">AES</el-option>
                    <el-option value="DES">DES</el-option>
                  </el-select>
                </el-form-item>
                <el-form-item v-if="formModel.encryptType === 'RSA'" prop="publicKey" label="RSA公钥">
                  <el-input v-model="formModel.publicKey" type="textarea" placeholder="请输入RSA公钥"/>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane :disabled="!formModel.appId" label="开发信息" name="form2">
              <el-form ref="form2" v-show="currentTab=='form2'" :model="formModel" :rules="formModelRules" label-width="135">
                <el-form-item label="ApiKey">
                  <el-input disabled v-model="formModel.apiKey" placeholder="请输入内容"/>
                </el-form-item>
                <el-form-item label="SecretKey">
                  <el-input disabled v-model="formModel.secretKey" placeholder="请输入内容"/>
                </el-form-item>
                <el-form-item label="授权类型" prop="grantTypes">
                  <el-alert type="info" show-icon :closable="false">客户端模式,请授权相关接口资源。否则请求网关服务器将提示<code>"权限不足,拒绝访问!"</code></el-alert>
                  <el-checkbox-group v-model="formModel.grantTypes">
                    <el-tooltip :content="item.desc" v-for="(item,index) in selectGrantTypes" :key="index">
                      <el-checkbox :label="item.label"><span>{{ item.title }}</span></el-checkbox>
                    </el-tooltip>
                  </el-checkbox-group>
                </el-form-item>
                <el-form-item label="用户授权范围" prop="scopes">
              <span slot="label">用户授权范围
              <el-tooltip content="提醒用户确认授权可访问的资源">
                <Icon type="ios-alert" size="16"/>
              </el-tooltip>
              </span>
                  <el-checkbox-group v-model="formModel.scopes">
                    <el-checkbox v-for="(item,index) in selectScopes" :label="item.label" :key="index"><span>{{ item.title }}</span>
                    </el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
                <el-form-item label="自动授权范围">
              <span slot="label">自动授权范围
                <el-tooltip content="不再提醒用户确认授权可访问的资源">
                  <Icon type="ios-alert" size="16"/>
                </el-tooltip>
              </span>
                  <el-checkbox-group v-model="formModel.autoApproveScopes">
                    <el-checkbox v-for="(item,index) in selectScopes" :label="item.label" :key="index"><span>{{ item.title }}</span>
                    </el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
                <el-form-item label="令牌有效期" prop="accessTokenValidity">
                  <el-radio-group v-model="formModel.tokenValidity" type="button">
                    <el-radio-button label="1">设置有效期</el-radio-button>
                    <el-radio-button label="0">不限制</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item v-show="formModel.tokenValidity === '1'" label="访问令牌有效期" prop="accessTokenValidity">
                  <el-input-number :min="900" v-model="formModel.accessTokenValidity"/>
                  <span>&nbsp;&nbsp;秒</span>
                </el-form-item>
                <el-form-item v-show="formModel.tokenValidity === '1'" label="刷新令牌有效期" prop="refreshTokenValidity">
                  <el-input-number :min="900" v-model="formModel.refreshTokenValidity"/>
                  <span>&nbsp;&nbsp;秒</span>
                </el-form-item>
                <el-form-item label="第三方登陆回调地址" prop="redirectUrls">
                  <el-input v-model="formModel.redirectUrls" type="textarea" placeholder="请输入内容"/>
                  <span>多个地址使用,逗号隔开</span>
                </el-form-item>
              </el-form>
            </el-tab-pane>

          </el-tabs>

<!--					<el-form size="small" :rules="rules" ref="form" :model="form" label-width="100px">-->
<!--						<el-form-item label="名称" prop="name">-->
<!--							<el-input v-model="formModel.name" style="width: 300px;" :disabled="nameDisabled"></el-input>-->
<!--						</el-form-item>-->
<!--						<el-form-item label="系统代号" prop="systemCode">-->
<!--							<el-input v-model="formModel.systemCode" style="width: 300px;" :disabled="nameDisabled"></el-input>-->
<!--						</el-form-item>-->
<!--						<el-form-item label="分组" prop="groupCode">-->
<!--							<el-select filterable v-model="formModel.groupCode" placeholder="请选择分组" style="width: 300px;">-->
<!--								<el-option v-for="item in groupOptions" :key="item.value" :label="item.label" :value="item.value" />-->
<!--							</el-select>-->
<!--						</el-form-item>-->
<!--						<el-form-item label="IP" prop="ip">-->
<!--							<el-input v-model="formModel.ip" style="width: 300px;"></el-input>-->
<!--						</el-form-item>-->
<!--						<el-form-item label="状态" prop="status">-->
<!--							<el-radio-group v-model="formModel.status">-->
<!--								<el-radio label="0">启用</el-radio>-->
<!--								<el-radio label="1">禁用</el-radio>-->
<!--							</el-radio-group>-->
<!--						</el-form-item>-->
<!--						<el-form-item label="备注" prop="remarks">-->
<!--							<el-input type="textarea" v-model="formModel.remarks" style="width: 500px;"></el-input>-->
<!--						</el-form-item>-->
<!--					</el-form>-->
				</el-card>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import { addClient,updateClient } from '@/api/gateway-manage/client_api.js'
import {getAllDevelopers} from "@/api/developer";
import {addApp, getAppClientInfo, updateApp, updateAppClientInfo} from "@/api/gateway-manage/open_client";
import {getAuthorityApi, getAuthorityApp, grantAuthorityApp} from "@/api/authority";

export default {
	data() {
    const validateEn = (rule, value, callback) => {
      let reg = /^[-_a-zA-Z0-9]+$/
      if (value === '') {
        callback(new Error('英文不能为空'))
      } else if (value !== '' && !reg.test(value)) {
        callback(new Error('只允许字母、数字、下划线、横线'))
      } else {
        callback()
      }
    }
		return {
      currentTab: 'form1',
      forms: [
        'form1',
        'form2'
      ],
      statusOptions: [
        {value: null, label: '所有'},
        {value: '0',label: '启用'},
        {value: '1',label: '禁用'},
      ],

      selectDevelopers: [{
        userId: 0,
        userName: '未知'
      }],
      selectGrantTypes: [
        { label: 'authorization_code', title: '授权码模式', desc: 'Web服务端应用与第三方移动App应用' },
        { label: 'client_credentials', title: '客户端模式', desc: '没有用户参与的,内部服务端与第三方服务端' },
        { label: 'password', title: '密码模式', desc: '内部Web网页应用与内部移动App应用' },
        { label: 'implicit', title: '简化模式', desc: 'Web网页应用或第三方移动App应用' },
        { label: 'refresh_token', title: '刷新令牌', desc: '刷新并延迟访问令牌时长' }
      ],
      selectScopes: [
        { label: 'userProfile', title: '允许访问基本信息' },
        // 这是测试数据,自定义对应接口权限标识
        { label: 'api1', title: '允许访问....自定义信息' }
      ],
      formModel: {
        appId: '',
        apiKey: '',
        secretKey: '',
        appName: '',
        appNameEn: '',
        appType: 'server',
        groupCode: '',
        ip: '',
        appIcon: '',
        appOs: '',
        path: '',
        website: '',
        appDesc: '',
        status: 1,
        isSign: '',
        isEncrypt: '',
        encryptType: '',
        publicKey: '',
        redirectUrls: '',
        developerId: '',
        scopes: ['userProfile'],
        autoApproveScopes: [],
        authorities: [],
        grantTypes: [],
        accessTokenValidity: 43200,
        refreshTokenValidity: 2592000,
        expireTime: '',
        isExpired: false,
        tokenValidity: '1'
			},
      formModelRules: {
        developerId: [
          { required: true, message: '开发商不能为空', trigger: 'blur' }
        ],
        website: [
          { type: 'url', message: '请输入有效网址', trigger: 'blur' }
        ],
        appType: [
          { required: true, message: '应用类型不能为空', trigger: 'blur' }
        ],
        appOs: [
          { required: true, message: '操作系统不能为空', trigger: 'blur' }
        ],
        redirectUrls: [
          { required: true, message: '授权重定向地址不能为空', trigger: 'blur' }
        ],
        appName: [
          { required: true, message: '应用名称不能为空', trigger: 'blur' }
        ],
        appNameEn: [
          { required: true, validator: validateEn, trigger: 'blur' }
        ],
        groupCode: [{ required: true, message: '分组不能为空', trigger: 'blur' }],
        ip: [{ required: true, message: 'IP不能为空', trigger: 'blur' }],
        grantTypes: [
          { required: true, type: 'array', min: 1, message: '授权类型不能为空', trigger: 'blur' }
        ],
        scopes: [
          { required: true, type: 'array', min: 1, message: '用户授权范围不能为空', trigger: 'blur' }
        ],
        accessTokenValidity: [
          // { required: true, type: 'integer', min: 900, message: '访问令牌有效期不能少于900', trigger: 'blur' }
          { required: true, type: 'integer', message: '访问令牌有效期不能少于900', trigger: 'blur' }
        ],
        refreshTokenValidity: [
          { required: true, type: 'integer', message: '刷新令牌有效期不能少于900', trigger: 'blur' }
        ]
      },
			rules: {
				name: [
					{ required: true, message: '请输入客户端名称', trigger: 'blur' },
					{ min: 2, max: 40, message: '长度在 2 到 40 个字符', trigger: 'blur' },
				],
				systemCode: [
					{ required: true, message: '请输入客户端系统代号', trigger: 'blur' },
					{ min: 2, max: 40, message: '长度在 2 到 40 个字符', trigger: 'blur' },
				],
				groupCode: [
					{ required: true, message: '请选择分组', trigger: 'change' },
				],
				ip: [
					{ required: true, message: '请输入客户端IP', trigger: 'blur' },
					{ min: 8, max: 16, message: '长度在 8 到 16 个字符，如：0.0.0.0', trigger: 'blur' },
				],
				status: [
					{ required: true, message: '请选择状态', trigger: 'change' }
				],
				remarks: [
					{ min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
				]
			},
			handleType: 'add',
			nameDisabled: false,
			groupOptions: this.GLOBAL_VAR.groups,
      defaultList: [
        {
          'name': '',
          'url': ''
        }
      ],
      modalVisible: false,
      modalTitle: '',
      imgName: '',
      visible: false,
      uploadList: [],
		};
	},
	created: function() {
		//在组件创建完毕后加载
		let query = this.$route.query;
		if (query){
			this.handleType = query.handleType;
			if (this.handleType === 'edit'){
				// this.nameDisabled = true;
				let client = query.client;
				console.log('client', client);
				this.init(client);
			}
		}

    this.handleLoadDevelopers()
	},
	methods: {
		init(client) {
			if (client){
				this.formModel = client;
			}


		},
		goBack() {
			this.$router.push({ path: '/clientList', query: {} });
		},
    handleSubmit() {
			// let _this = this;
			// this.$refs['form'].validate((valid) => {
			// 	if (valid) {
			// 		console.log(_this.formModel);
			// 		if (this.handleType === 'edit'){
			// 			updateClient(_this.formModel).then(function(result){
			// 				_this.GLOBAL_FUN.successMsg();
			// 			});
			// 		} else {
			// 			addClient(_this.formModel).then(function(result){
			// 				_this.GLOBAL_FUN.successMsg();
			// 			});
			// 		}
			// 	} else {
			// 		console.log('error submit!!');
			// 		return false;
			// 	}
			// });
      if (this.formModel.isEncrypt === 1 || this.formModel.isEncrypt === '1') {
        this.formModelRules.encryptType = { required: true, message: '请选择加密类型', trigger: 'blur' }
        if (this.formModel.encryptType === 'RSA') {
          this.formModelRules.publicKey = { required: true, message: 'RSA公钥不能为空', trigger: 'blur' }
        } else {
          this.formModelRules.publicKey = { required: false, message: 'RSA公钥不能为空', trigger: 'blur' }
        }
      } else {
        this.formModelRules.encryptType = { required: false, message: '请选择加密类型', trigger: 'blur' }
        this.formModelRules.publicKey = { required: false, message: 'RSA公钥不能为空', trigger: 'blur' }
      }
      if (this.currentTab === this.forms[0]) {
        console.log('formModel', this.formModel)
        this.$refs[this.currentTab].validate((valid) => {
          if (valid) {
            this.saving = true
            const data = Object.assign({}, this.formModel)
            if (data.appId) {
              updateApp(data).then(res => {
                if (res.code == 0) {
                  this.$Message.success('保存成功')
                  // this.handleReset()
                  // this.handleSearch()
                }
              }).finally(() => {
                this.saving = false
              })
            } else {
              addApp(data).then(res => {
                if (res.code == 0) {
                  this.$Message.success('保存成功')
                  // this.handleReset()
                  // this.handleSearch()
                }
              }).finally(() => {
                this.saving = false
              })
            }
          }
        })
      }

      if (this.currentTab === this.forms[1]) {
        this.$refs[this.currentTab].validate((valid) => {
          if (valid) {
            this.saving = true
            if (this.formModel.tokenValidity === '0') {
              this.formModel.accessTokenValidity = -1
              this.formModel.refreshTokenValidity = -1
            }
            updateAppClientInfo(this.formModel).then(res => {
              if (res.code === 0) {
                this.$Message.success('保存成功')
                // this.handleReset()
                // this.handleSearch()
              }
            }).finally(() => {
              this.saving = false
            })
          }
        })
      }


		},
		resetForm() {
			this.formModel = {
				id: this.handleType === 'edit'?this.formModel.id:null,
				name: this.handleType === 'edit'?this.formModel.name:null,
				groupCode: null,
				ip: null,
				status: null,
				remarks: null
			};
		},
    handleView (name) {
      this.imgName = name
      this.visible = true
    },
    handleRemoveImg (file) {
      const fileList = this.$refs.upload.fileList
      this.$refs.upload.fileList.splice(fileList.indexOf(file), 1)
    },
    handleOnAppTypeChange (data) {
      if (data !== 'app') {
        this.formModel.appOs = ''
      } else {
        if (!this.formModel.appOs) {
          this.formModel.appOs = 'ios'
        }
      }
    },
    handleOnEncryptTypeChange (data) {
      if (data !== 'RSA' || !this.formModel.publicKey) {
        this.formModel.publicKey = ''
      }
    },
    handleSuccess (res, file) {
      file.url = ''
      file.name = ''
    },

    handleFormatError (file) {
      this.$Message.warning('图片支持png、jpg、jpeg')
    },

    handleMaxSize (file) {
      this.$Message.warning('图片大小不能超过2M.')
    },

    handleBeforeUpload () {
      const check = this.uploadList.length < 1
      if (!check) {
        this.$Message.warning('只能上传一张.')
      }
      return check
    },
    handleOnSelectUser (data) {
      this.formModel.developerId = data.userId
    },

    handleTabClick (tab, event) {
      this.currentTab = tab.name
      this.handleModal()
    },
    handleModal (data) {
      if (data) {
        this.formModel = Object.assign({}, this.formModel, data)
      }
      if (this.currentTab === this.forms[0]) {
        this.modalTitle = data ? '编辑应用 - ' + data.appName : '添加应用'
        this.handleLoadDevelopers()
      }
      if (this.currentTab === this.forms[1]) {
        this.modalTitle = data ? '开发信息 - ' + data.appName : '开发信息'
        this.handleLoadAppClientInfo(this.formModel.apiKey)
      }

      this.formModel.status = this.formModel.status + ''
      this.formModel.isSign = this.formModel.isSign + ''
      this.formModel.isEncrypt = this.formModel.isEncrypt + ''
    },
    handleLoadDevelopers () {
      getAllDevelopers().then(res => {
        console.log('getAllDevelopers', res)
        if (res.code === 0) {
          this.selectDevelopers = res.data
        }
        this.modalVisible = true
      })
    },
    handleLoadAppClientInfo (clientId) {
      if (!clientId) {
        return
      }
      getAppClientInfo({ clientId: clientId }).then(res => {
        if (res.code === 0) {
          this.formModel.scopes = res.data.scope ? res.data.scope : []
          this.formModel.redirectUrls = res.data.redirect_uri ? res.data.redirect_uri.join(',') : ''
          this.formModel.grantTypes = res.data.authorized_grant_types ? res.data.authorized_grant_types : []
          this.formModel.accessTokenValidity = res.data.access_token_validity
          this.formModel.refreshTokenValidity = res.data.refresh_token_validity
          this.formModel.autoApproveScopes = res.data.autoapprove ? res.data.autoapprove : []
          this.formModel.tokenValidity = this.formModel.accessTokenValidity === -1 ? '0' : '1'
        }
        this.modalVisible = true
      })
    },

	}
};
</script>

<style scoped>
.upload-list {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 60px;
  margin-right: 4px;
  overflow: hidden;
  line-height: 60px;
  text-align: center;
  background: #fff;
  border: 1px solid transparent;
  border-radius: 4px;
  box-shadow: 0 1px 1px rgba(0, 0, 0, .2);
}

.upload-list img {
  width: 100%;
  height: 100%;
}

.upload-list-cover {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  display: none;
  background: rgba(0, 0, 0, .6);
}

.upload-list:hover .upload-list-cover {
  display: block;
}

.upload-list-cover i {
  margin: 0 2px;
  font-size: 20px;
  color: #fff;
  cursor: pointer;
}

::v-deep .el-form-item {
  margin-right: 0 !important;
}
::v-deep .el-form-item__label {
  position: absolute;
  width: 135px;
}
::v-deep .el-form-item__content {
  width: 100%;
  padding-left: 135px;
}
::v-deep .el-select, .el-input_inner {
  width: 100%;
}
::v-deep .el-dialog__body {
  padding-top: 10px;
}

</style>
