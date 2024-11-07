import request from "@/utils/request";

export const sendCode = async ({ code }) => {
  try {
    const response = await request({
      url: '/dify/workflow/run',
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      },
      data: { code }
    });

    // 直接从 response.data 获取 JSON 数据
    const data = response.data;

    if (data.event === 'workflow_finished' && data.data.outputs && data.data.outputs.result !== undefined) {
      const { result, reviewA, reviewB, reviewC, reviewD } = data.data.outputs;

      // 拼接内容，每个 review 中间隔一行
      const combinedOutput = [
        `# 总和成绩: ${result.toString()}`, // 将 result 转换为字符串
        `# A:复杂度评价`,
        reviewA,
        `# B:缺陷评价`,
        reviewB,
        `# C:性能评价:`,
        reviewC,
        `# D:技术评价:`,
        reviewD
      ].join('\n\n'); // 每段之间隔一行

      return combinedOutput;
    } else {
      console.warn('Unexpected response format:', data);
      return null;
    }

  } catch (error) {
    console.error('Error:', error);
    throw error;
  }
};
