import ProgressBar from 'progress'

const SafeChars = [...'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789']

const generateCode = (length) =>
  Array.from({ length })
    .map(() => SafeChars[Math.floor(Math.random() * SafeChars.length)])
    .join('')

export const generateUniqueCodes = ({ count, prefix, length }) => {
  const codes = new Set()

  console.log("Tokenek generálása...")
  const bar = new ProgressBar(':bar :current/:total :percent', { total: count })

  let attempt = 0
  while (codes.size < count) {
    attempt++

    if (attempt > count * 100) {
      throw new Error('Nem sikerült elegendő egyedi kódot készíteni, növeld a lehetséges hosszt!')
    }

    const newCode = prefix + generateCode(length - prefix.length)
    const oldCount = codes.size
    codes.add(newCode)

    if (oldCount < codes.size) bar.tick()
  }

  return [...codes]
}
