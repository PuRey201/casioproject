export default defineConfig({
  server: {
    host: '0.0.0.0',
    strictPort: true,
    port: 5173,
    watch: {
      usePolling: true
    },
    hmr: {
      host: 'casio-laravel.onrender.com'
    }
  },
  preview: {
    allowedHosts: ['casio-laravel.onrender.com']
  },
  // plugins ...
});
