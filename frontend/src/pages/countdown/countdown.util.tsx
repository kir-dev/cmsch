import { chakra } from '@chakra-ui/react'
import { useBrandColor } from '../../util/core-functions.util.ts'

export const parseTopMessage = (topMessage: string) => {
  const splits = topMessage.split(/(\[\[.*?\]\])/g)
  const coloredComponents = splits.map((partial) => {
    if (partial.match(/(\[\[.*?\]\])/)) {
      const coloredText = partial.substring(2, partial.length - 2)
      return <chakra.span color={useBrandColor(400, 400)}>{coloredText}</chakra.span>
    } else {
      return <span>{partial}</span>
    }
  })
  return <>{coloredComponents}</>
}
