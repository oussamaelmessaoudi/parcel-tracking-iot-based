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
            target: 'http://mqttrestapp:8080',
            changeOrigin: true,
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