export default function (text: string | undefined, replacement: string | undefined) {
  if (text) return text.replaceAll('{}', replacement || '')
  else return ''
}
