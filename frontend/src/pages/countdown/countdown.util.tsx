import { useBrandColor } from '@/util/core-functions.util.ts'

export const parseTopMessage = (topMessage: string) => {
  const splits = topMessage.split(/(\[\[.*?\]\])/g)
  const coloredComponents = splits.map((partial, idx) => {
    if (partial.match(/(\[\[.*?\]\])/)) {
      const coloredText = partial.substring(2, partial.length - 2)
      const color = useBrandColor()
      return (
        <span key={idx} id={coloredText} style={{ color }}>
          {coloredText}
        </span>
      )
    } else {
      return <span key={idx}>{partial}</span>
    }
  })
  return <>{coloredComponents}</>
}
