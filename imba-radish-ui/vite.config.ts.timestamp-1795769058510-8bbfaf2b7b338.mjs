// vite.config.ts
import { defineConfig } from "file:///E:/project/java/cyx/cyx-repository/imba-radish-ui/node_modules/vite/dist/node/index.js";
import vue from "file:///E:/project/java/cyx/cyx-repository/imba-radish-ui/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import path from "path";
var __vite_injected_original_dirname = "E:\\project\\java\\cyx\\cyx-repository\\imba-radish-ui";
var srcPath = path.resolve(__vite_injected_original_dirname, "./src");
var vite_config_default = defineConfig({
  plugins: [vue()],
  server: {
    host: "0.0.0.0",
    port: 5147,
    proxy: {
      "/api": {
        target: "http://127.0.0.1:8081",
        // 设置代理目标
        changeOrigin: true,
        // 是否改变请求源地址
        rewrite: (path2) => path2.replace(/^\/api/, "")
        // 将 /api 替换为空字符串
      },
      "/ws": {
        target: "ws://127.0.0.1:8081",
        // 设置代理目标
        changeOrigin: true
        // 是否改变请求源地址
      },
      "/download": {
        target: "http://192.168.226.130:9000",
        changeOrigin: true,
        // 是否改变请求源地址
        rewrite: (path2) => path2.replace(/^\/download/, "")
      }
    }
  },
  resolve: {
    alias: {
      "@": srcPath
    }
  }
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcudHMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJFOlxcXFxwcm9qZWN0XFxcXGphdmFcXFxcY3l4XFxcXGN5eC1yZXBvc2l0b3J5XFxcXGltYmEtcmFkaXNoLXVpXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ZpbGVuYW1lID0gXCJFOlxcXFxwcm9qZWN0XFxcXGphdmFcXFxcY3l4XFxcXGN5eC1yZXBvc2l0b3J5XFxcXGltYmEtcmFkaXNoLXVpXFxcXHZpdGUuY29uZmlnLnRzXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ltcG9ydF9tZXRhX3VybCA9IFwiZmlsZTovLy9FOi9wcm9qZWN0L2phdmEvY3l4L2N5eC1yZXBvc2l0b3J5L2ltYmEtcmFkaXNoLXVpL3ZpdGUuY29uZmlnLnRzXCI7aW1wb3J0IHsgZGVmaW5lQ29uZmlnIH0gZnJvbSAndml0ZSc7XHJcbmltcG9ydCB2dWUgZnJvbSAnQHZpdGVqcy9wbHVnaW4tdnVlJztcclxuaW1wb3J0IHBhdGggZnJvbSAncGF0aCc7XHJcblxyXG5jb25zdCBzcmNQYXRoID0gcGF0aC5yZXNvbHZlKF9fZGlybmFtZSwgJy4vc3JjJylcclxuXHJcbmV4cG9ydCBkZWZhdWx0IGRlZmluZUNvbmZpZyh7XHJcbiAgcGx1Z2luczogW3Z1ZSgpXSxcclxuICBzZXJ2ZXI6IHtcclxuICAgIGhvc3Q6ICcwLjAuMC4wJyxcclxuICAgIHBvcnQ6IDUxNDcsXHJcbiAgICBwcm94eToge1xyXG4gICAgICAnL2FwaSc6IHtcclxuICAgICAgICB0YXJnZXQ6ICdodHRwOi8vMTI3LjAuMC4xOjgwODEnLCAvLyBcdThCQkVcdTdGNkVcdTRFRTNcdTc0MDZcdTc2RUVcdTY4MDdcclxuICAgICAgICBjaGFuZ2VPcmlnaW46IHRydWUsIC8vIFx1NjYyRlx1NTQyNlx1NjUzOVx1NTNEOFx1OEJGN1x1NkM0Mlx1NkU5MFx1NTczMFx1NTc0MFxyXG4gICAgICAgIHJld3JpdGU6IChwYXRoKSA9PiBwYXRoLnJlcGxhY2UoL15cXC9hcGkvLCAnJykgLy8gXHU1QzA2IC9hcGkgXHU2NkZGXHU2MzYyXHU0RTNBXHU3QTdBXHU1QjU3XHU3QjI2XHU0RTMyXHJcbiAgICAgIH0sXHJcbiAgICAgICcvd3MnOiB7XHJcbiAgICAgICAgdGFyZ2V0OiAnd3M6Ly8xMjcuMC4wLjE6ODA4MScsIC8vIFx1OEJCRVx1N0Y2RVx1NEVFM1x1NzQwNlx1NzZFRVx1NjgwN1xyXG4gICAgICAgIGNoYW5nZU9yaWdpbjogdHJ1ZSwgLy8gXHU2NjJGXHU1NDI2XHU2NTM5XHU1M0Q4XHU4QkY3XHU2QzQyXHU2RTkwXHU1NzMwXHU1NzQwXHJcbiAgICAgIH0sXHJcbiAgICAgICcvZG93bmxvYWQnOiB7XHJcbiAgICAgICAgdGFyZ2V0OiAnaHR0cDovLzE5Mi4xNjguMjI2LjEzMDo5MDAwJyxcclxuICAgICAgICBjaGFuZ2VPcmlnaW46IHRydWUsICAvLyBcdTY2MkZcdTU0MjZcdTY1MzlcdTUzRDhcdThCRjdcdTZDNDJcdTZFOTBcdTU3MzBcdTU3NDBcclxuICAgICAgICByZXdyaXRlOiAocGF0aCkgPT4gcGF0aC5yZXBsYWNlKC9eXFwvZG93bmxvYWQvLCAnJylcclxuICAgICAgfVxyXG4gICAgfVxyXG4gIH0sXHJcbiAgcmVzb2x2ZToge1xyXG4gICAgYWxpYXM6IHtcclxuICAgICAgJ0AnOiBzcmNQYXRoXHJcbiAgICB9XHJcbiAgfVxyXG59KVxyXG5cclxuIl0sCiAgIm1hcHBpbmdzIjogIjtBQUFpVixTQUFTLG9CQUFvQjtBQUM5VyxPQUFPLFNBQVM7QUFDaEIsT0FBTyxVQUFVO0FBRmpCLElBQU0sbUNBQW1DO0FBSXpDLElBQU0sVUFBVSxLQUFLLFFBQVEsa0NBQVcsT0FBTztBQUUvQyxJQUFPLHNCQUFRLGFBQWE7QUFBQSxFQUMxQixTQUFTLENBQUMsSUFBSSxDQUFDO0FBQUEsRUFDZixRQUFRO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixPQUFPO0FBQUEsTUFDTCxRQUFRO0FBQUEsUUFDTixRQUFRO0FBQUE7QUFBQSxRQUNSLGNBQWM7QUFBQTtBQUFBLFFBQ2QsU0FBUyxDQUFDQSxVQUFTQSxNQUFLLFFBQVEsVUFBVSxFQUFFO0FBQUE7QUFBQSxNQUM5QztBQUFBLE1BQ0EsT0FBTztBQUFBLFFBQ0wsUUFBUTtBQUFBO0FBQUEsUUFDUixjQUFjO0FBQUE7QUFBQSxNQUNoQjtBQUFBLE1BQ0EsYUFBYTtBQUFBLFFBQ1gsUUFBUTtBQUFBLFFBQ1IsY0FBYztBQUFBO0FBQUEsUUFDZCxTQUFTLENBQUNBLFVBQVNBLE1BQUssUUFBUSxlQUFlLEVBQUU7QUFBQSxNQUNuRDtBQUFBLElBQ0Y7QUFBQSxFQUNGO0FBQUEsRUFDQSxTQUFTO0FBQUEsSUFDUCxPQUFPO0FBQUEsTUFDTCxLQUFLO0FBQUEsSUFDUDtBQUFBLEVBQ0Y7QUFDRixDQUFDOyIsCiAgIm5hbWVzIjogWyJwYXRoIl0KfQo=
