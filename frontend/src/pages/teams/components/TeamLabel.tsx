import { Box, Tooltip } from '@chakra-ui/react'
import { ColorInstance } from 'color'
import { useAltColor, useColor } from '../../../util/color.utils.ts'
import { TeamLabelView } from '../../../util/views/team.view.ts'

const TeamLabel = ({ label }: { label: TeamLabelView }) => {
  const myColor: ColorInstance = useColor(label.color)

  const [colorHex, darkColor] = [myColor.hex(), useAltColor(myColor).hex()]

  return label.desc ? (
    <LabelWithTooltip name={label.name} color={colorHex} darkColor={darkColor} desc={label.desc} />
  ) : (
    <LabelComponent name={label.name} color={colorHex} darkColor={darkColor} />
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
    >
      {name}
    </Box>
  )
}

const LabelWithTooltip = ({ name, color, darkColor, desc }: { name: string; color: string; darkColor: string; desc: string }) => {
  return (
    <Tooltip label={desc} aria-label={desc}>
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
      >
        {name}
      </Box>
    </Tooltip>
  )
}

export default TeamLabel
