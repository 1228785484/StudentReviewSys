<template>
    <div class="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-2xl">
        <h1 class="text-2xl font-bold mb-4 text-center text-gray-800">代码审查</h1>
        <div class="space-y-4">
          <div>
            <label for="codeInput" class="block text-sm font-medium text-gray-700 mb-1">输入代码</label>
            <textarea
              id="codeInput"
              v-model="codeInput"
              placeholder="请输入代码"
              rows="10"
              class="w-full px-3 py-2 text-gray-700 border rounded-lg focus:outline-none focus:border-blue-500"
            ></textarea>
          </div>
          <div>
            <label for="feedbackInput" class="block text-sm font-medium text-gray-700 mb-1">系统评价内容</label>
            <textarea
              id="feedbackInput"
              v-model="feedbackInput"
              placeholder="系统评价内容"
              rows="3"
              disabled
              class="w-full px-3 py-2 text-gray-500 bg-gray-100 border rounded-lg"
            ></textarea>
          </div>
          <button
            @click="handleSend"
            :disabled="loading"
            class="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg transition duration-300 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 disabled:opacity-50"
          >
            {{ loading ? '发送中...' : '发送' }}
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { sendCode } from '@/api/review/code';
  
  export default {
    data() {
      return {
        codeInput: '',
        feedbackInput: '',
        loading: false
      };
    },
    methods: {
      async handleSend() {
        if (!this.codeInput.trim()) {
          alert('请输入代码！');
          return;
        }
  
        this.loading = true;
        try {
          const response = await sendCode({ code: this.codeInput });
          this.feedbackInput = response;
          alert('代码发送成功！');
        } catch (error) {
          alert('发送失败，请重试！');
          console.error('Error:', error);
        } finally {
          this.loading = false;
        }
      }
    }
  };
  </script>
  
  <style scoped>
  .min-h-screen {
    min-height: 100vh;
  }
  </style>
  