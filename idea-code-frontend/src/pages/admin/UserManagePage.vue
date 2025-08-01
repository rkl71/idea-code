<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      @change="doTableChange"
      row-key="id"
    >
      <template #bodyCell="{ column, record, index }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="64" />
        </template>

        <template v-else-if="column.dataIndex === 'userName'">
          <div v-if="editingKey === record.id">
            <a-input
              v-model:value="record.userName"
              @pressEnter="save(record)"
              @blur="save(record)"
            />
          </div>
          <div v-else>
            {{ record.userName }}
          </div>
        </template>

        <template v-else-if="column.dataIndex === 'userProfile'">
          <div v-if="editingKey === record.id">
            <a-input
              v-model:value="record.userProfile"
              @pressEnter="save(record)"
              @blur="save(record)"
            />
          </div>
          <div v-else>
            {{ record.userProfile }}
          </div>
        </template>

        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="editingKey === record.id">
            <!-- 如果是当前登录用户，不允许修改自己的角色 -->
            <a-select
              v-if="record.id !== loginUserStore.loginUser.id"
              v-model:value="record.userRole"
              style="width: 120px"
            >
              <a-select-option value="user">普通用户</a-select-option>
              <a-select-option value="admin">管理员</a-select-option>
            </a-select>
            <!-- 当前登录用户不能修改自己的角色 -->
            <div v-else>
              <a-tag v-if="record.userRole === 'admin'" color="green">管理员</a-tag>
              <a-tag v-else color="blue">普通用户</a-tag>
              <a-typography-text type="secondary" style="font-size: 12px; margin-left: 8px">
                (不能修改自己的角色)
              </a-typography-text>
            </div>
          </div>
          <div v-else>
            <a-tag v-if="record.userRole === 'admin'" color="green">管理员</a-tag>
            <a-tag v-else color="blue">普通用户</a-tag>
          </div>
        </template>

        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>

        <template v-else-if="column.key === 'action'">
          <div v-if="editingKey === record.id">
            <a-space>
              <a-button type="link" size="small" @click="save(record)">保存</a-button>
              <a-button type="link" size="small" @click="cancel(record)">取消</a-button>
            </a-space>
          </div>
          <div v-else>
            <a-space>
              <a-button type="link" size="small" @click="edit(record)">编辑</a-button>
              <a-popconfirm
                title="确定要删除这个用户吗？"
                @confirm="doDelete(record.id)"
                ok-text="确定"
                cancel-text="取消"
              >
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </div>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, listUserVoByPage, updateUser } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import { userLoginUserStore } from '@/stores/loginUser.ts'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    width: 120,
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    width: 120,
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    width: 80,
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    width: 200,
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    width: 160,
  },
]

// 展示的数据
const data = ref<API.UserVO[]>([])
const total = ref(0)
const editingKey = ref<string | null>(null)
const originalData = ref<Record<string, API.UserVO>>({})
const loginUserStore = userLoginUserStore()

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格分页变化时的操作
const doTableChange = (page: any) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

// 编辑行
const edit = (record: API.UserVO) => {
  // 深拷贝保存原始数据
  originalData.value[record.id!] = JSON.parse(JSON.stringify(record))
  editingKey.value = record.id!
}

// 保存编辑
const save = async (record: API.UserVO) => {
  try {
    const res = await updateUser({
      id: record.id,
      userName: record.userName,
      userProfile: record.userProfile,
      userRole: record.userRole,
    })

    if (res.data.code === 0) {
      message.success('保存成功')
      editingKey.value = null
      delete originalData.value[record.id!]
      // 刷新数据以确保数据一致性
      fetchData()
    } else {
      message.error('保存失败：' + res.data.message)
      // 保存失败时恢复原始数据
      const original = originalData.value[record.id!]
      if (original) {
        const index = data.value.findIndex((item) => item.id === record.id)
        if (index !== -1) {
          data.value.splice(index, 1, { ...original })
        }
      }
    }
  } catch (error) {
    message.error('保存失败')
    // 保存失败时恢复原始数据
    const original = originalData.value[record.id!]
    if (original) {
      const index = data.value.findIndex((item) => item.id === record.id)
      if (index !== -1) {
        data.value.splice(index, 1, { ...original })
      }
    }
  }
}

// 取消编辑
const cancel = (record: API.UserVO) => {
  const original = originalData.value[record.id!]
  if (original) {
    // 找到数据数组中对应的记录并完全恢复
    const index = data.value.findIndex((item) => item.id === record.id)
    if (index !== -1) {
      // 直接替换整个对象，确保所有属性都被恢复
      data.value.splice(index, 1, { ...original })
    }
  }
  editingKey.value = null
  delete originalData.value[record.id!]
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#userManagePage {
}

/* 确保所有角色标签大小一致 */
#userManagePage :deep(.ant-tag) {
  height: 22px;
  line-height: 20px;
  padding: 0 7px;
  font-size: 12px;
  border-radius: 2px;
}
</style>
