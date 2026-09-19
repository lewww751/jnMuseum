/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        ink: '#161310',
        panel: '#211C17',
        panel2: '#2B241D',
        line: '#3A322A',
        textMain: '#EDE6DA',
        textSub: '#A79A87',
        spring: '#6FA8A0',
        springHover: '#8FC2BA',
        clay: '#C0784F',
        gold: '#C9A96A',
      },
      fontFamily: {
        serif: ['"Noto Serif SC"', '"Songti SC"', '"STSong"', '"SimSun"', 'serif'],
        sans: ['"PingFang SC"', '"Microsoft YaHei"', 'system-ui', 'sans-serif'],
      }
    },
  },
  plugins: [],
}