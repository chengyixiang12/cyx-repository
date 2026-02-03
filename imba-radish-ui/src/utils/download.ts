export async function download(blob: Blob, filename: string) {
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
}

export async function downloadLargeFile(blob: Blob, filename: string) {
    try {
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a');
        link.href = url; // 直接指向后端下载接口的URL
        link.download = filename; // 指定下载文件名
        link.target = '_blank'; // 避免阻塞当前页面
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    } catch (error) {
        console.error('大文件下载失败：', error);
    }
}