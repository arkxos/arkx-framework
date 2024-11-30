<template>
  <my-layout style="padding: 0px 0px 0px 0px;">
    <template #west>
      <my-panel title="网关路由" fit class="my-panel--nopadding">
        <vue-magic-tree
          :setting="treeSetting"
          :nodes="serviceNodes"
          @onClick="onClick"
          @onCheck="onCheck"
          @onCreated="handleCreated"
        />
      </my-panel>
    </template>

    <my-panel title="API列表" class="my-panel--nopadding">
      <div class="search-con search-con-top" style="margin-bottom: 10px;">
        <el-button-group>
          <el-button icon="el-icon-plus" type="primary" :disabled="hasAuthority('systemApiEdit')?false:true"
                     @click="handleModal()">
            添加
          </el-button>
        </el-button-group>
        <el-dropdown v-if="tableSelection.length>0 && hasAuthority('systemApiEdit')" @click="handleBatchClick"
                     style="margin-left: 20px">
          <el-button>
            <span>批量操作</span>
            <i class="el-icon-arrow-down"></i>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item name="remove">删除</el-dropdown-item>
              <el-dropdown-item name="remove">
                <el-dropdown placement="right-start">
                  <el-dropdown-item>
                    <span>状态</span>
                    <Icon type="ios-arrow-forward"/>
                  </el-dropdown-item>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item name="status1">启用</el-dropdown-item>
                      <el-dropdown-item name="status2">禁用</el-dropdown-item>
                      <el-dropdown-item name="status3">维护中</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-dropdown-item>
              <el-dropdown-item name="remove">
                <el-dropdown placement="right-start">
                  <el-dropdown-item>
                    <span>公开访问</span>
                    <Icon type="ios-arrow-forward"/>
                  </el-dropdown-item>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item name="open1">允许公开访问</el-dropdown-item>
                      <el-dropdown-item name="open2">拒绝公开访问</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-dropdown-item>
              <el-dropdown-item name="remove">
                <el-dropdown placement="right-start">
                  <el-dropdown-item>
                    <span>身份认证</span>
                    <Icon type="ios-arrow-forward"/>
                  </el-dropdown-item>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item name="auth1">开启身份认证</el-dropdown-item>
                      <el-dropdown-item name="auth2">关闭身份认证</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
<!--      <el-alert show-icon :closable="false">-->
<!--        <span>自动扫描<code>@EnableResourceServer</code>资源服务器接口信息,注:自动添加的接口,都是未公开的. <code>只有公开的接口,才可以通过网关访问。否则将提示:"请求地址,拒绝访问!"</code></span>-->
<!--      </el-alert>-->

      <el-form ref="searchForm"
               :model="pageInfo"
               inline
               label-width="80">
        <el-form-item label="请求路径" prop="path">
          <el-input type="text" v-model="pageInfo.path" placeholder="请输入关键字"/>
        </el-form-item>
        <el-form-item label="接口名称" prop="apiName">
          <el-input type="text" v-model="pageInfo.apiName" placeholder="请输入关键字" style="width: 120px;"/>
        </el-form-item>
        <el-form-item label="接口编码" prop="apiCode">
          <el-input type="text" v-model="pageInfo.apiCode" placeholder="请输入关键字" style="width: 120px;"/>
        </el-form-item>
        <el-form-item label="服务名" prop="serviceId">
          <el-input type="text" v-model="pageInfo.serviceId" placeholder="请输入关键字" style="width: 120px;"/>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-search" type="primary" @click="handleSearch(1)">查询</el-button>&nbsp;
          <el-button @click="handleResetForm('searchForm')">重置</el-button>
        </el-form-item>
      </el-form>
      <Alert show-icon>
        <span>自动扫描<code>@EnableResourceServer</code>资源服务器接口信息,注:自动添加的接口,都是未公开的. <code>只有公开的接口,才可以通过网关访问。否则将提示:"请求地址,拒绝访问!"</code></span>
      </Alert>
      <el-table @on-selection-change="handleTableSelectChange" border :data="tableData" :loading="loading">
        <el-table-column align="center" show-overflow-tooltip type="selection" width="55" />
        <el-table-column align="center" label="序号" show-overflow-tooltip width="55">
          <template #default="{ $index }">
            {{ $index + 1 }}
          </template>
        </el-table-column>
<!--        <el-table-column align="center" label="md5编码" prop="apiCode" width="300" show-overflow-tooltip />-->
        <el-table-column align="left" label="名称" prop="apiName" width="200" show-overflow-tooltip
                         :filters="[{ text: '启用', value: 1 }, { text: '禁用', value: 0 }]"
                         :filter-multiple="false"
                         :filter-method="filterStatus"
                         filter-placement="bottom-end">
          <template slot-scope="{ row }">
            <span>{{row.apiName}}</span>
          </template>
        </el-table-column>
        <el-table-column align="left" label="地址" prop="path" width="200" show-overflow-tooltip />
        <el-table-column align="center" label="分类" prop="apiCategory" width="100" show-overflow-tooltip />
        <el-table-column align="center" label="接口安全" prop="isAuth" width="300" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <el-tag v-if="row.isOpen===1" type="success">允许公开访问</el-tag>
            <el-tag v-else-if="row.isOpen!==1" type="danger">拒绝公开访问</el-tag>
            <el-tag v-if="row.isAuth===1" type="success">开启身份认证</el-tag>
            <el-tag v-else-if="row.isAuth!==1" type="danger">关闭身份认证</el-tag>
            <el-tag v-if="row.status===1" type="success">启用</el-tag>
            <el-tag v-else-if="row.status===2" type="warning">维护中</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="服务名称" prop="serviceId" width="100" show-overflow-tooltip />
        <el-table-column align="center" label="描述" prop="apiDesc" width="200" show-overflow-tooltip />
        <el-table-column align="center" label="最后更新时间" prop="updateTime" width="180" show-overflow-tooltip />
        <el-table-column align="center" label="操作" show-overflow-tooltip width="120" fixed="right">
          <template #default="{ row }">
            <el-button :disabled="hasAuthority('systemApiEdit')?false:true" type="text" @click="handleModal(row)">编辑</el-button>
            <el-button :disabled="hasAuthority('systemApiEdit')?false:true" type="text" @click="handleRemove(row)">删除</el-button>

          </template>
        </el-table-column>
      </el-table>
      <el-pagination :total="pageInfo.total" :current-page="pageInfo.page" :page-size="pageInfo.limit"
                     layout="total, sizes, prev, pager, next, jumper"
                     @current-change="handlePage" @size-change='handlePageSize'/>

    </my-panel>

    <el-dialog :visible.sync="modalVisible"
           :title="modalTitle"
           width="50"
           @close="handleReset">
      <div>
        <el-alert show-icon v-if="formItem.apiId?true:false" :closable="false">
          <span>自动扫描接口swagger注解。</span>
          <el-popover placement="bottom" title="示例代码">
            <a slot="reference">示例代码</a>
            <div>
              <div v-highlight>
                <pre>
                      // 接口介绍
                      @Schema(title = "接口名称", name = "接口备注")
                      @PostMapping("/testApi")
                      // 忽略接口,将不再添加或修改次接口
                      @ApiIgnore
                      public ResultBody testApi() {
                          return ResultBody.success();
                      }
                </pre>
              </div>
            </div>
          </el-popover>
        </el-alert>
        <el-form ref="form1" :model="formItem" :rules="formItemRules" label-width="100px">
          <el-form-item label="服务名称" prop="serviceId">
            <el-select :disabled="formItem.apiId && formItem.isPersist === 1?true:false" v-model="formItem.serviceId"
                    filterable clearable>
              <el-option v-for="(item,index) in selectServiceList" :value="item.serviceId" :key="index">{{ item.serviceName }}</el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="接口分类" prop="apiCategory">
            <el-input v-model="formItem.apiCategory" placeholder="请输入内容"/>
          </el-form-item>
          <el-form-item label="接口编码" prop="apiCode">
            <el-input :disabled="formItem.apiId && formItem.isPersist === 1?true:false" v-model="formItem.apiCode"
                   placeholder="请输入内容"/>
          </el-form-item>
          <el-form-item label="接口名称" prop="apiName">
            <el-input :disabled="formItem.apiId && formItem.isPersist === 1?true:false" v-model="formItem.apiName"
                   placeholder="请输入内容"/>
          </el-form-item>
          <el-form-item label="请求地址" prop="path">
            <el-input :disabled="formItem.apiId && formItem.isPersist === 1?true:false" v-model="formItem.path"
                   placeholder="请输入内容"/>
          </el-form-item>
          <el-form-item label="优先级">
            <el-input-number v-model="formItem.priority"/>
          </el-form-item>
          <el-form-item label="身份认证">
            <el-radio-group v-model="formItem.isAuth" type="button">
              <el-radio :disabled="formItem.apiId && formItem.isPersist === 1?true:false" label="0">关闭</el-radio>
              <el-radio :disabled="formItem.apiId && formItem.isPersist === 1?true:false" label="1">开启</el-radio>
            </el-radio-group>
            <p><code>开启：未认证登录,提示"认证失败,请重新登录!";关闭: 不需要认证登录</code></p>
          </el-form-item>
          <el-form-item label="公开访问">
            <el-radio-group v-model="formItem.isOpen" type="button">
              <el-radio label="0">拒绝</el-radio>
              <el-radio label="1">允许</el-radio>
            </el-radio-group>
            <p><code>拒绝:提示"请求地址,拒绝访问!"</code></p>
          </el-form-item>
          <el-form-item label="状态">
            <el-radio-group v-model="formItem.status" type="button">
              <el-radio label="0">禁用</el-radio>
              <el-radio label="1">启用</el-radio>
              <el-radio label="2">维护中</el-radio>
            </el-radio-group>
            <p><code>禁用：提示"请求地址,禁止访问!";维护中：提示"正在升级维护中,请稍后再试!";</code></p>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="formItem.apiDesc" type="textarea" placeholder="请输入内容"/>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button type="default" @click="handleReset">取消</el-button>&nbsp;
        <el-button type="primary" @click="handleSubmit" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </my-layout>
</template>

<script>
import {
  getApis,
  updateApi,
  addApi,
  removeApi,
  batchRemoveApi,
  batchUpdateOpenApi,
  batchUpdateStatusApi,
  batchUpdateAuthApi
} from '@/api/api'
import { getServiceList } from '@/api/gateway'
import { MyPanel, MyLayout  } from '$ui'

import VueMagicTree from 'vue-magic-tree'

export default {
  name: 'SystemApi',
  components: { MyLayout, MyPanel, VueMagicTree },
  data () {
    const validateEn = (rule, value, callback) => {
      let reg = /^[_.a-zA-Z0-9]+$/
      if (value === '') {
        callback(new Error('接口标识不能为空'))
      } else if (value !== '' && !reg.test(value)) {
        callback(new Error('只允许字母、数字、点、下划线'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      modalVisible: false,
      modalTitle: '',
      saving: false,
      tableSelection: [],
      pageInfo: {
        total: 0,
        page: 1,
        limit: 10,
        path: '',
        apiName: '',
        apiCode: '',
        serviceId: ''
      },
      rootNode: { id: 'root', name:'全部', children: [] },
      selectServiceList: [],
      formItemRules: {
        serviceId: [
          { required: true, message: '所属服务不能为空', trigger: 'blur' }
        ],
        apiCategory: [
          { required: true, message: '接口分类不能为空', trigger: 'blur' }
        ],
        path: [
          { required: true, message: '接口地址不能为空', trigger: 'blur' }
        ],
        apiCode: [
          { required: true, validator: validateEn, trigger: 'blur' }
        ],
        apiName: [
          { required: true, message: '接口名称不能为空', trigger: 'blur' }
        ]
      },
      formItem: {
        apiId: '',
        apiCode: '',
        apiName: '',
        apiCategory: 'default',
        path: '',
        status: 1,
        isAuth: 1,
        openSwatch: false,
        authSwatch: true,
        serviceId: '',
        priority: 0,
        apiDesc: '',
        isOpen: 1
      },
      tableData: [],
      treeSetting: {
        check: {
          enable: false,
          chkboxType: {"Y": "", "N": ""},
          chkStyle: "radio",
          radioType: "all"
        },
        view: {
          // 开启图标显示功能
          showIcon: true,
          dblClickExpand: false
        },
        data: {
          // 设置图标库(采用iconfont class形式)
          iconMap: {
            'folder': 'ri-folder-4-fill',
            'pdf': 'ri-file-pdf-line',
            'doc': 'ri-file-word-line',
            'docx': 'ri-file-word-line',
            'txt': 'ri-file-3-line',
            'xls': 'ri-file-excel-line',
            'xlsx': 'ri-file-excel-line',
            'unknow': 'ri-file-list-2-line'
          },
          key: {
            children: "children",
            // 设置对应每个节点的节点类型，与数据中customType属性对应
            nodeType: 'fileType'
          },
          simpleData: {
            enable: false,
            pIdKey: "parentId"
          }
        },
        async: {
          enable: false,
          url: ""
        },
      },
      serviceNodes: [this.rootNode],
    }
  },
  methods: {
    onClick(evt, treeId, treeNode) {
      console.log('tree click', treeId, treeNode)
      if(treeNode.id === 'root') {
        this.pageInfo.serviceId = ''
      } else {
        this.pageInfo.serviceId = treeNode.id
      }

      this.handleSearch()
    },
    onCheck(evt, treeId, treeNode) {

    },
    handleCreated(ztreeObj) {
      ztreeObj.expandAll(true)
    },
    filterStatus(value, row) {
      if (value === 0) {
        return row.status === 0
      } else if (value === 1) {
        return row.status === 1
      }
    },
    handleModal (data) {
      console.log('handleModal')
      if (data) {
        this.modalTitle = '编辑接口 - ' + data.apiName
        this.formItem = Object.assign({}, this.formItem, data)
      } else {
        this.modalTitle = '添加接口'
      }
      this.formItem.status = this.formItem.status + ''
      this.formItem.isAuth = this.formItem.isAuth + ''
      this.formItem.isOpen = this.formItem.isOpen + ''
      this.modalVisible = true
    },
    handleResetForm (form) {
      this.$refs[form].resetFields()
    },
    handleReset () {
      const newData = {
        apiId: '',
        apiCode: '',
        apiName: '',
        apiCategory: 'default',
        path: '',
        status: 1,
        isAuth: 1,
        serviceId: '',
        priority: 0,
        apiDesc: '',
        isOpen: 1
      }
      this.formItem = newData
      // 重置验证
      this.handleResetForm('form1')
      this.modalVisible = false
      this.saving = false
    },
    handleSubmit () {
      this.$refs['form1'].validate((valid) => {
        if (valid) {
          this.saving = true
          if (this.formItem.apiId) {
            updateApi(this.formItem).then(res => {
              if (res.code === 0) {
                this.$Message.success('保存成功')
                this.handleReset()
                this.handleSearch()
              }
            }).finally(() => {
              this.saving = false
            })
          } else {
            addApi(this.formItem).then(res => {
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
    },
    handleRemove (data) {
      this.$Modal.confirm({
        title: '确定删除吗？',
        onOk: () => {
          removeApi(data.apiId).then(res => {
            if (res.code === 0) {
              this.pageInfo.page = 1
              this.$Message.success('删除成功')
              this.handleSearch()
            }
          })
        }
      })
    },
    handleSearch (page) {
      this.tableSelection = []
      if (page) {
        this.pageInfo.page = page
      }
      this.loading = true
      getApis(this.pageInfo).then(res => {
        this.tableData = res.data.content
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
    handleLoadServiceList () {
      getServiceList().then(res => {
        if (res.code === 0) {
          this.selectServiceList = res.data

          this.rootNode.children.length = 0
          this.selectServiceList.forEach(item=>{
            this.rootNode.children.push({
              id: item.serviceId,
              name: item.serviceName,
              pid: 'root'
            })
          })
          this.serviceNodes.length = 0;
          this.serviceNodes.push(this.rootNode)
          console.log('this.rootNode', this.rootNode)
        }
      })
    },
    handleTableSelectChange (selection) {
      this.tableSelection = selection
    },
    handleBatchClick (name) {
      if (name) {
        this.$Modal.confirm({
          title: `已勾选${this.tableSelection.length}项,是否继续执行操作？`,
          onOk: () => {
            let ids = []
            this.tableSelection.map(item => {
              if (!ids.includes(item.apiId)) {
                ids.push(item.apiId)
              }
            })
            switch (name) {
              case 'remove':
                batchRemoveApi(ids).then(res => {
                  if (res.code === 0) {
                    this.$Message.success('批量操作成功')
                    this.handleSearch()
                  }
                })
                break
              case 'open1':
                batchUpdateOpenApi({ ids: ids, open: 1 }).then(res => {
                  if (res.code === 0) {
                    this.$Message.success('批量操作成功')
                    this.handleSearch()
                  }
                })
                break
              case 'open2':
                batchUpdateOpenApi({ ids: ids, open: 2 }).then(res => {
                  if (res.code === 0) {
                    this.$Message.success('批量操作成功')
                    this.handleSearch()
                  }
                })
                break
              case 'status1':
                batchUpdateStatusApi({ ids: ids, status: 1 }).then(res => {
                  if (res.code === 0) {
                    this.$Message.success('批量操作成功')
                    this.handleSearch()
                  }
                })
                break
              case 'status2':
                batchUpdateStatusApi({ ids: ids, status: 0 }).then(res => {
                  if (res.code === 0) {
                    this.$Message.success('批量操作成功')
                    this.handleSearch()
                  }
                })
                break
              case 'status3':
                batchUpdateStatusApi({ ids: ids, status: 2 }).then(res => {
                  if (res.code === 0) {
                    this.$Message.success('批量操作成功')
                    this.handleSearch()
                  }
                })
                break
              case 'auth1':
                batchUpdateAuthApi({ ids: ids, auth: 1 }).then(res => {
                  if (res.code === 0) {
                    this.$Message.success('批量操作成功')
                    this.handleSearch()
                  }
                })
                break
              case 'auth2':
                batchUpdateAuthApi({ ids: ids, auth: 0 }).then(res => {
                  if (res.code === 0) {
                    this.$Message.success('批量操作成功')
                    this.handleSearch()
                  }
                })
                break
            }
          }
        })
      }
    }
  },
  mounted: function () {
    this.handleLoadServiceList()
    this.handleSearch()
  }
}
</script>
