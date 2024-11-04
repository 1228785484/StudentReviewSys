<template>
  <div class="review-container">
    <el-card class="review-card">
      <div slot="header" class="review-header">
        <h1 class="review-title">代码评审系统</h1>
        <p class="review-subtitle">请输入需要评审的代码，系统将为您提供详细的分析报告</p>
      </div>

      <div class="review-content">
        <!-- 代码输入区域 -->
        <div class="input-section">
          <div class="input-header">
            <el-row type="flex" align="middle">
              <el-col :span="12">
                <h3>
                  <i class="el-icon-document"></i>
                  代码输入
                </h3>
              </el-col>
              <el-col :span="12" class="text-right">
                <el-tag size="small">支持多种编程语言</el-tag>
              </el-col>
            </el-row>
          </div>
          <el-input
            type="textarea"
            v-model="codeInput"
            :rows="12"
            placeholder="请在此输入您需要评审的代码..."
            :autosize="{ minRows: 12, maxRows: 24 }"
          />
        </div>

        <!-- 评审结果区域 -->
        <div class="input-section">
          <div class="input-header">
            <el-row type="flex" align="middle">
              <el-col :span="12">
                <h3>
                  <i class="el-icon-document-checked"></i>
                  评审结果
                </h3>
              </el-col>
              <el-col :span="12" class="text-right">
                <el-tag size="small" type="success">支持 Markdown 格式</el-tag>
              </el-col>
            </el-row>
          </div>
          <div
            class="markdown-output"
            v-html="renderedFeedback"
          ></div>
        </div>

        <!-- 操作按钮 -->
        <div class="button-container">
          <el-row type="flex" justify="center" :gutter="20">
            <el-col :span="6">
              <el-button
                @click="handleClear"
                :disabled="loading || !codeInput"
                icon="el-icon-delete"
                plain
              >
                清空
              </el-button>
            </el-col>
            <el-col :span="6">
              <el-button
                type="primary"
                @click="handleSend"
                :loading="loading"
                :disabled="!codeInput.trim()"
                icon="el-icon-check"
              >
                开始评审
              </el-button>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { sendCode } from '@/api/review/code';
import MarkdownIt from 'markdown-it';

export default {
  name: 'CodeReview',
  data() {
    return {
      codeInput: '',
      feedbackInput: '',
      loading: false,
      md: null, // 在data中定义markdown-it实例
    };
  },
  computed: {
    renderedFeedback() {
      if (!this.feedbackInput) {
        return '<div class="empty-result">评审结果将在这里显示...</div>';
      }
      return this.md.render(this.feedbackInput);
    }
  },
  created() {
    // 初始化markdown-it
    this.md = new MarkdownIt({
      html: true,
      breaks: true,
      linkify: true,
      typographer: true,
      quotes: '""\'\'',
    });
  },
  methods: {
    async handleSend() {
      if (!this.codeInput.trim()) {
        this.$message.warning('请输入需要评审的代码');
        return;
      }

      this.loading = true;
      try {
        const response = await sendCode({ code: this.codeInput });
        this.feedbackInput = response;
        this.$message.success('代码评审完成！');
      } catch (error) {
        this.$message.error('评审失败，请重试！');
        console.error('Error:', error);
      } finally {
        this.loading = false;
      }
    },
    handleClear() {
      this.$confirm('确认清空所有内容?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.codeInput = '';
        this.feedbackInput = '';
        this.$message.success('已清空内容');
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.review-container {
  min-height: 100vh;
  padding: 2rem;
  background-color: #f0f2f5;
}

.review-card {
  max-width: 1200px;
  margin: 0 auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.review-header {
  text-align: center;
  margin-bottom: 2rem;
}

.review-title {
  font-size: 2rem;
  color: #303133;
  margin-bottom: 1rem;
}

.review-subtitle {
  color: #909399;
  font-size: 1.1rem;
}

.input-section {
  margin-bottom: 2rem;
}

.input-header {
  margin-bottom: 1rem;
}

.input-header h3 {
  margin: 0;
  color: #303133;
}

.text-right {
  text-align: right;
}

.button-container {
  margin-top: 2rem;
  padding: 1rem 0;
}

.empty-result {
  color: #909399;
  text-align: center;
  padding: 2rem;
  background-color: #fafafa;
  border-radius: 4px;
}

/* Markdown 输出样式 */
.markdown-output {
  padding: 1.5rem;
  background-color: #ffffff;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

.markdown-output >>> h1,
.markdown-output >>> h2,
.markdown-output >>> h3 {
  margin-top: 1.5rem;
  margin-bottom: 1rem;
  color: #303133;
}

.markdown-output >>> p {
  margin-bottom: 1rem;
  line-height: 1.6;
  color: #606266;
}

.markdown-output >>> ul,
.markdown-output >>> ol {
  padding-left: 2rem;
  margin-bottom: 1rem;
}

.markdown-output >>> li {
  margin-bottom: 0.5rem;
  color: #606266;
}

.markdown-output >>> code {
  background-color: #f5f7fa;
  padding: 0.2rem 0.4rem;
  border-radius: 3px;
  font-family: 'Fira Code', Consolas, monospace;
  font-size: 0.9em;
  color: #409eff;
}

.markdown-output >>> pre {
  background-color: #2d3748;
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
  margin-bottom: 1rem;
}

.markdown-output >>> pre code {
  background-color: transparent;
  padding: 0;
  color: #e2e8f0;
}

.markdown-output >>> blockquote {
  border-left: 4px solid #409eff;
  padding: 0.5rem 1rem;
  margin: 1rem 0;
  background-color: #ecf5ff;
  color: #606266;
}

.markdown-output >>> table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
  border: 1px solid #dcdfe6;
}

.markdown-output >>> th,
.markdown-output >>> td {
  border: 1px solid #dcdfe6;
  padding: 0.75rem;
  text-align: left;
}

.markdown-output >>> th {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #303133;
}
</style>
