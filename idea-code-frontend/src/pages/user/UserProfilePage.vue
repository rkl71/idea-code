<template>
  <div id="userProfilePage">
    <a-card title="个人设置" style="max-width: 600px; margin: 0 auto">
      <a-form
        :model="formState"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
        @finish="handleSubmit"
      >
        <a-form-item label="头像">
          <div class="avatar-uploader">
            <a-avatar :size="80" :src="formState.userAvatar" />
            <a-input
              v-model:value="formState.userAvatar"
              placeholder="请输入头像链接"
              style="margin-top: 12px"
            />
          </div>
        </a-form-item>

        <a-form-item
          label="用户名"
          name="userName"
          :rules="[{ required: true, message: '请输入用户名' }]"
        >
          <a-input v-model:value="formState.userName" placeholder="请输入用户名" />
        </a-form-item>

        <a-form-item label="个人简介">
          <a-textarea
            v-model:value="formState.userProfile"
            :rows="4"
            placeholder="请输入个人简介"
          />
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
          <a-space>
            <a-button type="primary" html-type="submit" :loading="loading"> 保存修改 </a-button>
            <a-button @click="resetForm">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { updateMyUser, getLoginUser } from '@/api/userController.ts'
import { userLoginUserStore } from '@/stores/loginUser.ts'

const formState = reactive<API.UserUpdateMyRequest>({
  userName: '',
  userAvatar: '',
  userProfile: '',
})

const loading = ref(false)
const loginUserStore = userLoginUserStore()

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await getLoginUser()
    if (res.data.code === 0 && res.data.data) {
      const userData = res.data.data
      formState.userName = userData.userName || ''
      formState.userAvatar = userData.userAvatar || ''
      formState.userProfile = userData.userProfile || ''
    }
  } catch (error) {
    message.error('获取用户信息失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  loading.value = true
  try {
    const res = await updateMyUser(formState)
    if (res.data.code === 0) {
      message.success('保存成功')
      // 更新本地用户状态
      await loginUserStore.fetchLoginUser()
    } else {
      message.error('保存失败：' + res.data.message)
    }
  } catch (error) {
    message.error('保存失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  fetchUserInfo()
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
#userProfilePage {
  padding: 24px;
}

.avatar-uploader {
  text-align: center;
}
</style>
