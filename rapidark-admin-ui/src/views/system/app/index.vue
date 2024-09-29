<template>
  <div>
    <el-card shadow>
      <el-form ref="searchForm"
               :model="pageInfo"
               inline
               label-width="80">
        <el-form-item label="AppId" prop="appId">
          <el-input type="text" v-model="pageInfo.appId" placeholder="请输入关键字"/>
        </el-form-item>
        <el-form-item label="中文名称" prop="appName">
          <el-input type="text" v-model="pageInfo.appName" placeholder="请输入关键字"/>
        </el-form-item>
        <el-form-item label="英文名称" prop="appName">
          <el-input type="text" v-model="pageInfo.appNameEn" placeholder="请输入关键字"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch(1)">查询</el-button>&nbsp;
          <el-button @click="handleResetForm('searchForm')">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="search-con search-con-top">
        <el-button-group>
          <el-button :disabled="hasAuthority('systemAppEdit')?false:true" class="search-btn" type="primary"
                     @click="handleModal()">
            <span>添加</span>
          </el-button>
        </el-button-group>
      </div>
      <el-alert type="info" show-icon>客户端模式,请授权相关接口资源。否则请求网关服务器将提示<code>"权限不足,拒绝访问!"</code></el-alert>
      <el-table border :data="data" :loading="loading">
        <el-table-column align="center" show-overflow-tooltip type="selection" width="55" />
        <el-table-column align="center" label="序号" show-overflow-tooltip width="55">
          <template #default="{ $index }">
            {{ $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column align="left" label="应用名称" prop="appName" width="200" show-overflow-tooltip />
        <el-table-column align="center" label="状态" prop="status" width="80" show-overflow-tooltip>
          <template #default="{ row }">
            <div v-if="row.status===1" class="el-timeline-item__dot" style="position: inherit">
              <span data-v-4607fb9e="" class="vab-dot vab-dot-success">
                <span data-v-4607fb9e=""></span>
              </span>
              <span>上线</span>
            </div>
            <div v-else class="el-timeline-item__dot" style="position: inherit">
              <span data-v-4607fb9e="" class="vab-dot vab-dot-error">
                <span data-v-4607fb9e=""></span>
              </span>
              <span>下线</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column align="left" label="开发者" prop="userName" width="100" show-overflow-tooltip />
        <el-table-column align="center" label="应用类型" prop="appType" width="100" show-overflow-tooltip
                         :filters="[{ text: '服务器应用', value: 0 }, { text: '手机应用', value: 1 },{ text: 'PC网页应用', value: 2 }, { text: '手机网页应用', value: 3 }]"
                         :filter-multiple="false"
                         :filter-method="filterAppType"
                         filter-placement="bottom-end">
          <template #default="{ row }">
            <el-tag type="success" v-if="row.appType==='server'">服务器应用</el-tag>
            <el-tag type="success" v-else-if="row.appType==='app'">手机应用</el-tag>
            <el-tag type="success" v-else-if="row.appType==='pc'">PC网页应用</el-tag>
            <el-tag type="success" v-else>手机网页应用</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="left" label="AppId" prop="appId" width="150" show-overflow-tooltip />
        <el-table-column align="left" label="ApiKey" prop="apiKey" width="200" show-overflow-tooltip />
        <el-table-column align="left" label="SecretKey" prop="secretKey" width="300" show-overflow-tooltip />

        <el-table-column align="center" label="操作" show-overflow-tooltip width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="text" @click="handleModal(row)" :disabled="row.appId != 'gateway' && hasAuthority('systemAppEdit') ?false:true">编辑</el-button>
            <el-button type="text" @click="handleRemove(row)" :disabled="row.appId != 'gateway' && hasAuthority('systemAppEdit') ?false:true">删除</el-button>
            <el-button type="text" @click="handleResetSecret(row)" :disabled="row.appId != 'gateway' && hasAuthority('systemAppEdit') ?false:true">重置密钥</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :total="pageInfo.total" :current-page="pageInfo.page" :page-size="pageInfo.size" align="left"
                     layout="total, sizes, prev, pager, next, jumper"
                     @current-change="handlePage" @size-change='handlePageSize'/>
    </el-card>

    <el-dialog :visible.sync="modalVisible"
               :title="modalTitle"
               width="50"
               @close="handleReset">
      <el-tabs :value="current" @tab-click="handleTabClick">
        <el-tab-pane label="应用信息" name="form1">
          <el-form ref="form1" v-show="current=='form1'" :model="formItem" :rules="formItemRules" label-width="135" label-position="right">
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
              <el-input readonly v-model="formItem.appId" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="开发者">
              <el-select v-model="formItem.developerId" filterable clearable>
                <el-option :title="item.userName" v-for="(item,index) in selectUsers" @click.native="handleOnSelectUser(item)"
                           :value="item.userId" :label="item.userName" :key="index">
                  <span>{{ item.userName }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="应用名称" prop="appName">
              <el-input v-model="formItem.appName" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="英文名称" prop="appNameEn">
              <el-input v-model="formItem.appNameEn" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="分组" prop="groupCode">
              <el-select filterable v-model="formItem.groupCode" placeholder="请选择分组" style="width: 300px;">
                <el-option v-for="item in groupOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="应用类型" prop="appType">
              <el-select v-model="formItem.appType" @on-change="handleOnAppTypeChange">
                <el-option value="server">服务器应用</el-option>
                <el-option value="app">手机应用</el-option>
                <el-option value="pc">PC网页应用</el-option>
                <el-option value="wap">手机网页应用</el-option>
              </el-select>
            </el-form-item>
            <el-form-item v-if="formItem.appType === 'app'" prop="appOs" label="操作系统">
              <el-radio-group v-model="formItem.appOs">
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
              <el-input v-model="formItem.ip" style="width: 300px;"></el-input>
            </el-form-item>
            <el-form-item label="应用官网" prop="website">
              <el-input v-model="formItem.website" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="formItem.status" type="button">
                <el-radio-button label="0">下线</el-radio-button>
                <el-radio-button label="1">上线</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="formItem.appDesc" type="textarea" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="是否验签">
              <el-radio-group v-model="formItem.isSign">
                <el-radio-button label="0">否</el-radio-button>
                <el-radio-button label="1">是</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="是否加密">
              <el-radio-group v-model="formItem.isEncrypt">
                <el-radio-button label="0">否</el-radio-button>
                <el-radio-button label="1">是</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="加密类型" prop="encryptType">
              <el-select v-model="formItem.encryptType" @on-change="handleOnEncryptTypeChange">
                <el-option value="RSA">RSA</el-option>
                <el-option value="AES">AES</el-option>
                <el-option value="DES">DES</el-option>
              </el-select>
            </el-form-item>
            <el-form-item v-if="formItem.encryptType === 'RSA'" prop="publicKey" label="RSA公钥">
              <el-input v-model="formItem.publicKey" type="textarea" placeholder="请输入RSA公钥"/>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane :disabled="!formItem.appId" label="开发信息" name="form2">
          <el-form ref="form2" v-show="current=='form2'" :model="formItem" :rules="formItemRules" label-width="135">
            <el-form-item label="ApiKey">
              <el-input disabled v-model="formItem.apiKey" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="SecretKey">
              <el-input disabled v-model="formItem.secretKey" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="授权类型" prop="grantTypes">
              <el-checkbox-group v-model="formItem.grantTypes">
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
              <el-checkbox-group v-model="formItem.scopes">
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
              <el-checkbox-group v-model="formItem.autoApproveScopes">
                <el-checkbox v-for="(item,index) in selectScopes" :label="item.label" :key="index"><span>{{ item.title }}</span>
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="令牌有效期" prop="accessTokenValidity">
              <el-radio-group v-model="formItem.tokenValidity" type="button">
                <el-radio-button label="1">设置有效期</el-radio-button>
                <el-radio-button label="0">不限制</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-show="formItem.tokenValidity === '1'" label="访问令牌有效期" prop="accessTokenValidity">
              <el-input-number :min="900" v-model="formItem.accessTokenValidity"/>
              <span>&nbsp;&nbsp;秒</span>
            </el-form-item>
            <el-form-item v-show="formItem.tokenValidity === '1'" label="刷新令牌有效期" prop="refreshTokenValidity">
              <el-input-number :min="900" v-model="formItem.refreshTokenValidity"/>
              <span>&nbsp;&nbsp;秒</span>
            </el-form-item>
            <el-form-item label="第三方登陆回调地址" prop="redirectUrls">
              <el-input v-model="formItem.redirectUrls" type="textarea" placeholder="请输入内容"/>
              <span>多个地址使用,逗号隔开</span>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane :disabled="!formItem.appId" label="分配权限" name="form3">
          <el-form ref="form3" v-show="current=='form3'" :model="formItem"  label-width="100" :rules="formItemRules">
            <el-form-item prop="expireTime" label="过期时间">
              <el-badge v-if="formItem.isExpired" text="授权已过期">
                <el-date-picker v-model="formItem.expireTime" class="ivu-form-item-error" type="datetime"
                                placeholder="授权有效期"/>
              </el-badge>
              <el-date-picker v-else v-model="formItem.expireTime" type="datetime" placeholder="设置有效期"/>
            </el-form-item>
            <el-form-item prop="authorities" label="功能接口" >
              <Transfer
                :data="selectApis"
                :list-style="{width: '45%',height: '480px'}"
                :titles="['选择接口', '已选择接口']"
                :render-format="transferRender"
                :target-keys="formItem.authorities"
                @on-change="handleTransferChange"
                filterable/>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button type="default" @click="handleReset">取消</el-button>&nbsp;
        <el-button type="primary" @click="handleSubmit" :loading="saving">保存</el-button>
      </template>

    </el-dialog>
  </div>
</template>

<script>
import { getApps, updateApp, addApp, removeApp, getAppClientInfo, updateAppClientInfo, restApp } from '@/api/app'
import { getAllDevelopers } from '@/api/developer'
import {
  getAuthorityApi,
  getAuthorityApp,
  grantAuthorityApp
} from '@/api/authority'

export default {
  name: 'SystemApp',
  data () {
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
      loading: false,
      saving: false,
      current: 'form1',
      forms: [
        'form1',
        'form2',
        'form3'
      ],
      selectApis: [],
      selectUsers: [{
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
      pageInfo: {
        total: 0,
        page: 1,
        size: 10,
        appId: '',
        appName: '',
        appNameEn: ''
      },
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
      formItemRules: {
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
      formItem: {
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
      groupOptions: this.GLOBAL_VAR.groups,
      data: []
    }
  },
  methods: {
    filterAppType (value, row) {
      if (value === 0) {
        return row.appType === 'server'
      } else if (value === 1) {
        return row.appType === 'app'
      } else if (value === 2) {
        return row.appType === 'pc'
      } else if (value === 3) {
        return row.appType === 'wap'
      }
    },
    handleModal (data) {
      if (data) {
        this.formItem = Object.assign({}, this.formItem, data)
      }
      if (this.current === this.forms[0]) {
        this.modalTitle = data ? '编辑应用 - ' + data.appName : '添加应用'
        this.handleLoadUsers()
      }
      if (this.current === this.forms[1]) {
        this.modalTitle = data ? '开发信息 - ' + data.appName : '开发信息'
        this.handleLoadAppClientInfo(this.formItem.apiKey)
      }
      if (this.current === this.forms[2]) {
        this.modalTitle = data ? '分配权限 - ' + data.appName : '分配权限'
        this.handleLoadAppGranted(this.formItem.appId)
      }
      this.formItem.status = this.formItem.status + ''
      this.formItem.isSign = this.formItem.isSign + ''
      this.formItem.isEncrypt = this.formItem.isEncrypt + ''
    },
    handleResetForm (form) {
      this.$refs[form].resetFields()
    },
    handleReset () {
      // 重置验证
      const newData = {
        appId: '',
        apiKey: '',
        secretKey: '',
        appName: '',
        appNameEn: '',
        appType: 'server',
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
      }
      this.formItem = newData
      this.forms.map(form => {
        this.handleResetForm(form)
      })
      this.current = this.forms[0]
      this.saving = false
      this.modalVisible = false
    },
    handleSubmit () {
      if (this.formItem.isEncrypt === 1 || this.formItem.isEncrypt === '1') {
        this.formItemRules.encryptType = { required: true, message: '请选择加密类型', trigger: 'blur' }
        if (this.formItem.encryptType === 'RSA') {
          this.formItemRules.publicKey = { required: true, message: 'RSA公钥不能为空', trigger: 'blur' }
        } else {
          this.formItemRules.publicKey = { required: false, message: 'RSA公钥不能为空', trigger: 'blur' }
        }
      } else {
        this.formItemRules.encryptType = { required: false, message: '请选择加密类型', trigger: 'blur' }
        this.formItemRules.publicKey = { required: false, message: 'RSA公钥不能为空', trigger: 'blur' }
      }
      if (this.current === this.forms[0]) {
        this.$refs[this.current].validate((valid) => {
          if (valid) {
            this.saving = true
            const data = Object.assign({}, this.formItem)
            if (data.appId) {
              updateApp(data).then(res => {
                if (res.code === 0) {
                  this.$Message.success('保存成功')
                  this.handleReset()
                  this.handleSearch()
                }
              }).finally(() => {
                this.saving = false
              })
            } else {
              addApp(data).then(res => {
                if (res.code === 0) {
                  this.$Message.success('保存成功')
                  this.handleReset()
                  this.handleSearch()
                }
              }).finally(() => {
                this.saving = false
              })
            }
          }
        })
      }

      if (this.current === this.forms[1]) {
        this.$refs[this.current].validate((valid) => {
          if (valid) {
            this.saving = true
            if (this.formItem.tokenValidity === '0') {
              this.formItem.accessTokenValidity = -1
              this.formItem.refreshTokenValidity = -1
            }
            updateAppClientInfo(this.formItem).then(res => {
              if (res.code === 0) {
                this.$Message.success('保存成功')
                this.handleReset()
                this.handleSearch()
              }
            }).finally(() => {
              this.saving = false
            })
          }
        })
      }

      if (this.current === this.forms[2]) {
        this.$refs[this.current].validate((valid) => {
          if (valid) {
            this.saving = true
            grantAuthorityApp({
              appId: this.formItem.appId,
              expireTime: this.formItem.expireTime ? this.formItem.expireTime.pattern('yyyy-MM-dd HH:mm:ss') : '',
              authorityIds: this.formItem.authorities
            }).then(res => {
              if (res.code === 0) {
                this.$Message.success('授权成功')
                this.handleReset()
                this.handleSearch()
              }
            }).finally(() => {
              this.saving = false
            })
          }
        })
      }
    },
    handleSearch (page) {
      if (page) {
        this.pageInfo.page = page
      }
      this.loading = true
      const queryParams = {
        ...this.pageInfo
      }
      queryParams.page = queryParams.page -1;
      getApps(queryParams).then(res => {
        this.data = res.data.content
        this.pageInfo.total = parseInt(res.data.totalElements)
      }).finally(() => {
        this.loading = false
      })
    },
    handleRemove (data) {
      this.$Modal.confirm({
        title: '删除后将无法恢复,确定删除吗？',
        onOk: () => {
          removeApp({ appId: data.appId }).then(res => {
            if (res.code === 0) {
              this.pageInfo.page = 1
              this.$Message.success('删除成功')
              this.handleSearch()
            }
          })
        }
      })
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
    handleTabClick (tab, event) {
      this.current = tab.name
      this.handleModal()
    },
    handleClick (name, row) {
      switch (name) {
        case 'remove':
          this.handleRemove(row)
          break
        case 'resetSecret':
          this.handleResetSecret(row)
          break
      }
    },
    handleOnAppTypeChange (data) {
      if (data !== 'app') {
        this.formItem.appOs = ''
      } else {
        if (!this.formItem.appOs) {
          this.formItem.appOs = 'ios'
        }
      }
    },
    handleOnEncryptTypeChange (data) {
      if (data !== 'RSA' || !this.formItem.publicKey) {
        this.formItem.publicKey = ''
      }
    },
    handleOnSelectUser (data) {
      this.formItem.developerId = data.userId
    },
    handlePage (current) {
      this.pageInfo.page = current
      this.handleSearch()
    },

    handlePageSize (size) {
      this.pageInfo.size = size
      this.handleSearch()
    },
    handleLoadAppGranted (appId) {
      if (!appId) {
        return
      }
      const that = this
      const p1 = getAuthorityApi('')
      const p2 = getAuthorityApp(appId)
      Promise.all([p1, p2]).then(function (values) {
        let res1 = values[0]
        let res2 = values[1]
        if (res1.code === 0) {
          res1.data.map(item => {
            item.key = item.authorityId
            item.label = `${item.prefix.replace('/**', '')}${item.path} - ${item.apiName}`
            item.disabled = item.path === '/**'
          })
          that.selectApis = res1.data
        }
        if (res2.code === 0) {
          let authorities = []
          res2.data.map(item => {
            if (item.authority.indexOf('APP_') === -1 && !authorities.includes(item.authorityId)) {
              authorities.push(item.authorityId)
            }
          })
          that.formItem.authorities = authorities
          // 时间
          if (res2.data.length > 0) {
            that.formItem.expireTime = res2.data[0].expireTime
            that.formItem.isExpired = res2.data[0].isExpired
          }
        }
        that.modalVisible = true
      })
    },
    handleLoadAppClientInfo (clientId) {
      if (!clientId) {
        return
      }
      getAppClientInfo({ clientId: clientId }).then(res => {
        if (res.code === 0) {
          this.formItem.scopes = res.data.scope ? res.data.scope : []
          this.formItem.redirectUrls = res.data.redirect_uri ? res.data.redirect_uri.join(',') : ''
          this.formItem.grantTypes = res.data.authorized_grant_types ? res.data.authorized_grant_types : []
          this.formItem.accessTokenValidity = res.data.access_token_validity
          this.formItem.refreshTokenValidity = res.data.refresh_token_validity
          this.formItem.autoApproveScopes = res.data.autoapprove ? res.data.autoapprove : []
          this.formItem.tokenValidity = this.formItem.accessTokenValidity === -1 ? '0' : '1'
        }
        this.modalVisible = true
      })
    },
    transferRender (item) {
      return `<span  title="${item.label}">${item.label}</span>`
    },
    handleTransferChange (newTargetKeys, direction, moveKeys) {
      if (newTargetKeys.indexOf('1') !== -1) {
        this.formItem.authorities = ['1']
      } else {
        this.formItem.authorities = newTargetKeys
      }
    },
    handleLoadUsers () {
      getAllDevelopers().then(res => {
        // if (res.code === 0) {
        //   this.selectUsers = res.data
        // }
        this.modalVisible = true
      })
    },
    handleView (name) {
      this.imgName = name
      this.visible = true
    },

    handleRemoveImg (file) {
      const fileList = this.$refs.upload.fileList
      this.$refs.upload.fileList.splice(fileList.indexOf(file), 1)
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
    }
  },
  mounted: function () {
    this.handleSearch()
  }
}
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
