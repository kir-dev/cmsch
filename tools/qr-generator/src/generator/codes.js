const SafeChars = [...'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789']

const generateCode = (length) =>
  Array.from({ length })
    .map(() => SafeChars[Math.floor(Math.random() * SafeChars.length)])
    .join('')

export const generateUniqueCodes = ({ count, prefix, length }) => {
  const codes = new Set()

  for (let i = 0; codes.size < count /* <- unusual predicate */; i++) {
    if (i > count * 100) {
      throw new Error('Nem sikerült elegendő egyedi kódot készíteni, növeld a lehetséges hosszt!')
    }

    codes.add(prefix + generateCode(length - prefix.length))
  }

  return [...codes]
}
