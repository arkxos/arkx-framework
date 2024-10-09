<template>
  <div>
    <Card>
      <Form ref="searchForm"
            :model="pageInfo"
            inline
            :label-width="80">
        <FormItem label="登录名" prop="userName">
          <Input type="text" v-model="pageInfo.userName" placeholder="请输入关键字"/>
        </FormItem>
        <FormItem label="手机号" prop="mobile">
          <Input type="text" v-model="pageInfo.mobile" placeholder="请输入关键字"/>
        </FormItem>
        <FormItem label="邮箱" prop="email">
          <Input type="text" v-model="pageInfo.email" placeholder="请输入关键字"/>
        </FormItem>
        <FormItem>
          <Button type="primary" @click="handleSearch(1)">查询</Button>&nbsp;
          <Button @click="handleResetForm('searchForm')">重置</Button>
        </FormItem>
      </Form>

      <div class="search-con search-con-top">
        <ButtonGroup>
          <Button :disabled="hasAuthority('developerEdit')?false:true" type="primary"
                  @click="handleModal()">
            <span>添加</span>
          </Button>
        </ButtonGroup>
      </div>

      <Table border :columns="columns" :data="data" :loading="loading">
        <template slot="status" slot-scope="{ row }">
          <Badge v-if="row.status===1" status="success" text="正常"/>
          <Badge v-else-if="row.status===2" status="warning" text="锁定"/>
          <Badge v-else status="error" text="禁用"/>
        </template>
        <template slot="type" slot-scope="{ row }">
          <Badge v-if="(row.type & 1) === 1" status="success" text="提供方"/>
          <Badge v-if="(row.type & 2) === 2" status="warning" text="调用方"/>
        </template>
        <template slot="action" slot-scope="{ row }">
          <a :disabled="hasAuthority('developerEdit')?false:true" @click="handleModal(row)">编辑</a>&nbsp;
          <Dropdown v-show="hasAuthority('developerEdit')" transfer ref="dropdown" @on-click="handleClick($event,row)">
            <a href="javascript:void(0)">
              <span>更多</span>
              <Icon type="ios-arrow-down"/>
            </a>
            <DropdownMenu slot="list">
              <DropdownItem name="sendToEmail">发送到密保邮箱</DropdownItem>
            </DropdownMenu>
          </Dropdown>&nbsp;
        </template>
      </Table>
      <Page transfer :total="pageInfo.total" :current="pageInfo.page" :page-size="pageInfo.limit" show-elevator
            show-sizer
            show-total
            @on-change="handlePage" @on-page-size-change='handlePageSize'/>
    </Card>

    <el-dialog
      :close-on-click-modal="false"
      :visible="modalVisible"
      :title="modalTitle"
      :before-close="handleReset">

      <el-tabs v-model='currentTabName' @tab-click="handleTabClick">
        <el-tab-pane label="开发商信息" name="form1">
          <el-form ref="form1" :model="formItem" :rules="formItemRules" label-width="120px">
            <el-form-item label="公司类型" prop="companyType">
              <el-checkbox-group v-model="formItem.companyType">
                <el-checkbox :label="1">服务提供方</el-checkbox>
                <el-checkbox :label="2">服务调用方</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="公司名称" prop="companyName">
              <el-input v-model="formItem.companyName" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="联系人" prop="personName">
              <el-input v-model="formItem.personName" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="手机号" prop="mobile">
              <el-input v-model="formItem.mobile" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="昵称" prop="nickName">
              <el-input v-model="formItem.nickName" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="登录名" prop="userName">
              <el-input :disabled="formItem.id?true:false" v-model="formItem.userName" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item v-if="formItem.id?false:true" label="登录密码" prop="password">
              <el-input type="password" v-model="formItem.password" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item v-if="formItem.id?false:true" label="再次确认密码" prop="passwordConfirm">
              <el-input type="password" v-model="formItem.passwordConfirm" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formItem.email" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="状态">
              <RadioGroup v-model="formItem.status" type="button">
                <Radio label="0">禁用</Radio>
                <Radio label="1">正常</Radio>
                <Radio label="2">锁定</Radio>
              </RadioGroup>
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="formItem.userDesc" type="textarea" placeholder="请输入内容"/>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane :disabled="!formItem.id" label="修改密码" name="form2">
          <el-form ref="form2" :model="formItem" :rules="formItemRules" label-width="120px">
            <el-form-item label="登录名" prop="userName">
              <el-input :disabled="formItem.id?true:false" v-model="formItem.userName" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="登录密码" prop="password">
              <el-input type="password" v-model="formItem.password" placeholder="请输入内容"/>
            </el-form-item>
            <el-form-item label="再次确认密码" prop="passwordConfirm">
              <el-input type="password" v-model="formItem.passwordConfirm" placeholder="请输入内容"/>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div slot="footer" class="dialog-footer">
        <el-button type="default" @click="handleReset">取消</el-button>&nbsp;
        <el-button type="primary" @click="handleSubmit" :loading="saving">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getDevelopers, updateDeveloper, addDeveloper, updatePassword } from '@/api/developer'

export default {
  name: 'SystemDeveloper',
  data () {
    const validateEn = (rule, value, callback) => {
      let reg = /^[_a-zA-Z0-9]+$/
      let reg2 = /^.{4,18}$/
      // 长度为6到18个字符
      if (value === '') {
        callback(new Error('登录名不能为空'))
      } else if (value !== '' && !reg.test(value)) {
        callback(new Error('只允许字母、数字、下划线'))
      } else if (value !== '' && !reg2.test(value)) {
        callback(new Error('长度6到18个字符'))
      } else {
        callback()
      }
    }
    const validatePass = (rule, value, callback) => {
      let reg2 = /^.{6,18}$/
      if (value === '') {
        callback(new Error('请输入密码'))
      } else if (value !== this.formItem.password) {
        callback(new Error('两次输入密码不一致'))
      } else if (value !== '' && !reg2.test(value)) {
        callback(new Error('长度6到18个字符'))
      } else {
        callback()
      }
    }

    const validatePassConfirm = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.formItem.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    const validateMobile = (rule, value, callback) => {
      let reg = /^1\d{10}$/
      if (value !== '' && !reg.test(value)) {
        callback(new Error('手机号码格式不正确'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      saving: false,
      modalVisible: false,
      modalTitle: '',
      currentTabName: 'form1',
      forms: [
        'form1',
        'form2'
      ],
      selectMenus: [],
      selectRoles: [],
      pageInfo: {
        page: 1,
        pageSize: 10,
        sort: 'createTime',
        order: 'desc'
      },
      formItemRules: {
        companyName: [
          { required: true, message: '公司名称不能为空', trigger: 'blur' }
        ],
        personName: [
          { required: true, message: '联系人不能为空', trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: '联系方式不能为空', trigger: 'blur' },
          { validator: validateMobile, trigger: 'blur' }
        ],
        companyType: [
          { required: true, message: '公司类型不能为空', trigger: 'blur' }
        ],
        userName: [
          { required: true, message: '开发商名不能为空', trigger: 'blur' },
          { required: true, validator: validateEn, trigger: 'blur' }
        ],
        password: [
          { required: true, validator: validatePass, trigger: 'blur' }
        ],
        passwordConfirm: [
          { required: true, validator: validatePassConfirm, trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: '昵称不能为空', trigger: 'blur' }
        ],
        email: [
          { required: false, type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ]
      },
      formItem: {
        companyName: '',
        personName: '',
        id: '',
        userName: '',
        nickName: '',
        password: '',
        passwordConfirm: '',
        status: 1,
        companyId: '',
        email: '',
        mobile: '',
        companyType: [1],
        userDesc: '',
        avatar: '',
        grantRoles: [],
        grantActions: [],
        grantMenus: [],
        expireTime: '',
        isExpired: false
      },
      columns: [
        {
          type: 'selection',
          width: 60
        },
        {
          title: '公司名称',
          key: 'companyName',
          width: 200
        },
        {
          title: '联系人',
          key: 'personName',
          width: 200
        },
        {
          title: '手机号',
          key: 'mobile',
          width: 200
        },
        {
          title: '公司类型',
          key: 'type',
          slot: 'type',
          width: 150
        },
        {
          title: '登录名',
          key: 'userName',
          width: 200
        },
        {
          title: '昵称',
          key: 'nickName',
          width: 150
        },
        {
          title: '邮箱',
          key: 'email',
          width: 200
        },
        {
          title: '状态',
          slot: 'status',
          key: 'status',
          width: 100
        },
        {
          title: '注册时间',
          key: 'createTime',
          width: 180
        },
        {
          title: '描述',
          key: 'userDesc'
        },
        {
          title: '操作',
          slot: 'action',
          fixed: 'right',
          width: 150
        }
      ],
      data: []
    }
  },
  methods: {
    handleModal (data) {
      if (data) {
        console.log('data', data)
        this.formItem = Object.assign({}, this.formItem, data)
        this.formItem.companyType = [];
        if((this.formItem.type & 1) === 1) {
          this.formItem.companyType.push(1)
        }
        if((this.formItem.type & 2) === 2) {
          this.formItem.companyType.push(2)
        }
      }
      if (this.currentTabName === this.forms[0]) {
        this.modalTitle = data ? '编辑开发商 - ' + data.userName : '添加开发商'
        this.modalVisible = true
      }
      if (this.currentTabName === this.forms[1]) {
        this.modalTitle = data ? '修改密码 - ' + data.userName : '修改密码'
        this.modalVisible = true
      }
      this.formItem.status = this.formItem.status + ''
    },
    handleResetForm (form) {
      this.$refs[form].resetFields()
    },
    handleReset () {
      const newData = {
        id: '',
        userName: '',
        nickName: '',
        password: '',
        passwordConfirm: '',
        status: 1,
        companyId: '',
        email: '',
        mobile: '',
        companyType: [1],
        userDesc: '',
        avatar: '',
        grantRoles: [],
        grantMenus: [],
        grantActions: [],
        expireTime: '',
        isExpired: false
      }
      this.formItem = newData
      // 重置验证
      this.forms.map(form => {
        this.handleResetForm(form)
      })
      this.currentTabName = this.forms[0]
      this.formItem.grantMenus = []
      this.formItem.grantActions = []
      this.modalVisible = false
      this.saving = false
    },
    handleSubmit () {
      console.log(this.currentTabName)
      if (this.currentTabName === this.forms[0]) {
        this.$refs[this.currentTabName].validate((valid) => {
          if (valid) {
            this.saving = true
            let me = this;
            this.formItem.type = 0
            this.formItem.companyType.forEach(item=>{
              me.formItem.type += item
            })
            if (this.formItem.id) {
              updateDeveloper(this.formItem).then(res => {
                if (res.code === 0) {
                  this.$Message.success('保存成功')
                  this.handleReset()
                  this.handleSearch()
                }
              }).finally(() => {
                this.saving = false
              })
            } else {
              addDeveloper(this.formItem).then(res => {
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

      if (this.currentTabName === this.forms[1] && this.formItem.id) {
        this.$refs[this.currentTabName].validate((valid) => {
          if (valid) {
            this.saving = true
            updatePassword({
              userId: this.formItem.id,
              password: this.formItem.password
            }).then(res => {
              if (res.code === 0) {
                this.$Message.success('修改成功')
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
      getDevelopers(this.pageInfo).then(res => {
        this.data = res.data.content
        this.pageInfo.total = res.data.totalElements - 0
      }).finally(() => {
        this.loading = false
      })
    },
    handlePage (current) {
      this.pageInfo.page = current
      this.handleSearch()
    },
    handlePageSize (size) {
      this.pageInfo.limit = size
      this.handleSearch()
    },
    handleClick (name, row) {
      switch (name) {
        case 'sendToEmail':
          this.$Message.warning('发送至密保邮箱,开发中...')
          break
      }
    },
    handleTabClick (target, action) {
      console.log('this.currentTabName: ', this.currentTabName)
      this.currentTabName = target.name
      console.log('this.currentTabName: ', this.currentTabName)
      this.handleModal()
    }
  },
  mounted: function () {
    this.handleSearch()
  }
}
</script>
<style scoped>
::v-deep .el-dialog__body {
  padding: 10px 20px 10px 20px;
}
</style>
