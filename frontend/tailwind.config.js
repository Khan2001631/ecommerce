/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#ff6600",
        secondary: "#111",
        hover: "#ff8533",
      },
    },
  },
  plugins: [],
}

