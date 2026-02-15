import { Box, Tooltip } from '@chakra-ui/react'
import type { ColorInstance } from 'color'
import { useAltColor, useColor } from '../../../util/color.utils.ts'

const TeamLabel = ({ name, color, desc }: { name: string; color?: string; desc?: string }) => {
  const myColor: ColorInstance = useColor(color)

  const [colorHex, darkColor] = [myColor.hex(), useAltColor(myColor).hex()]

  return desc ? (
    <LabelWithTooltip name={name} color={colorHex} darkColor={darkColor} desc={desc} />
  ) : (
    <LabelComponent name={name} color={colorHex} darkColor={darkColor} />
  )
}

const LabelComponent = ({ name, color, darkColor }: { name: string; color: string; darkColor: string }) => {
  return (
    <Box
      border="2px solid"
      bg={darkColor}
      borderColor={color}
      color={color}
      px={3}
      py={0.5}
      borderRadius="xl"
      fontWeight="bold"
      fontSize={12}
      letterSpacing={1.2}
      width="fit-content"
      height="fit-content"
    >
      {name}
    </Box>
  )
}

const LabelWithTooltip = ({ name, color, darkColor, desc }: { name: string; color: string; darkColor: string; desc: string }) => {
  return (
    <Tooltip label={desc} aria-label={desc}>
      <Box>
        <LabelComponent name={name} color={color} darkColor={darkColor} />
      </Box>
    </Tooltip>
  )
}

export default TeamLabel
