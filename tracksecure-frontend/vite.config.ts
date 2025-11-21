import path from 'path';
import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, '.', '');
    return {
      server: {
        port: 3000,
        host: '0.0.0.0',
        // add proxy for API requests
        proxy: {
          '/api': {
            target: 'http://mqttrestapp:8081',
            changeOrigin: true,
            // Add runtime logging to help debugging proxy issues when running `npm run dev`
            // `configure` receives the http-proxy instance; use any to avoid TS complaints.
            // This only runs in dev mode with Vite server.
            configure: (proxy: any) => {
              proxy.on && proxy.on('proxyReq', (proxyReq: any, req: any, res: any) => {
                console.log(`[vite proxy] proxyReq -> ${req.method} ${req.url}`);
              });
              proxy.on && proxy.on('proxyRes', (proxyRes: any, req: any, res: any) => {
                console.log(`[vite proxy] proxyRes -> ${req.method} ${req.url} status=${proxyRes.statusCode}`);
              });
              proxy.on && proxy.on('error', (err: any, req: any, res: any) => {
                console.error('[vite proxy] error', err && err.message ? err.message : err);
              });
            }
          },
      },
      },
      plugins: [react()],
      
      resolve: {
        alias: {
          // Fix: `__dirname` is not available in ES modules. `path.resolve('.')` is used instead to correctly resolve the project root.
          '@': path.resolve('.'),
        }
      }
    };
    
});