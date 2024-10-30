export const sendCode = async ({ code }) => {
    try {
        const response = await fetch('https://dify.aipfuture.com/v1/workflows/run', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer app-ZJIcpi9DgtU5A1rCTF9O9B2K'
            },
            body: JSON.stringify({
                inputs: { code },
                response_mode: "streaming",
                user: "abc-123"
            }),
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        // 获取可读流
        const reader = response.body.getReader();
        let resultText = '';

        while (true) {
            const { done, value } = await reader.read();
            
            if (done) {
                break;
            }

            // 将 Uint8Array 转换为文本
            const chunk = new TextDecoder().decode(value);
            
            // 处理每个数据块
            const lines = chunk.split('\n');
            
            for (const line of lines) {
                if (line.trim() === '') continue;
                
                // 移除"data: "前缀
                const jsonStr = line.replace(/^data: /, '').trim();
                
                try {
                    const data = JSON.parse(jsonStr);
                    
                    // 只处理 workflow_finished 事件
                    if (data.event === 'workflow_finished') {
                        // 获取输出结果并解码 Unicode
                        const decodedResult = JSON.parse(JSON.stringify(data.data.outputs.result))
                            .replace(/\\u/g, '%u');
                        resultText = decodeURIComponent(decodedResult);
                        break;
                    }
                } catch (e) {
                    console.warn('Failed to parse JSON:', e);
                    continue;
                }
            }
        }

        return resultText;

    } catch (error) {
        console.error('Error:', error);
        throw error;
    }
};