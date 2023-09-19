import { QrLevelDto } from '../../../util/views/qrFight.view'
import { Box } from '@chakra-ui/react'
import { useMemo } from 'react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { useColorModeValue } from '@chakra-ui/system'
import { ResponsiveBar } from '@nivo/bar'

interface LevelDataDisplayProps {
  level: QrLevelDto
}

export function LevelDataDisplay({ level }: LevelDataDisplayProps) {
  const teams = useMemo(() => {
    return Object.keys(level.teams)
      .map((team) => ({ team, value: level.teams[team] }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 5)
  }, [level.teams])
  const config = useConfigContext()
  const color = useColorModeValue(config.components.style.lightTextColor, config.components.style.darkTextColor)

  if (!teams.length) return null
  return (
    <Box w="100%" h={300}>
      <ResponsiveBar
        data={teams}
        keys={['value']}
        indexBy="team"
        theme={{ axis: { ticks: { text: { fill: color, fontSize: 14 } } } }}
        labelTextColor={{ from: 'color', modifiers: [['darker', 5]] }}
        colors={[config.components.style.lightBrandingColor]}
        borderRadius={8}
        padding={0.3}
        margin={{ bottom: 45, left: 45, right: 15, top: 15 }}
        valueScale={{ type: 'linear' }}
        indexScale={{ type: 'band', round: true }}
        isInteractive={false}
        axisTop={null}
        axisRight={null}
        enableGridX={false}
        enableGridY={false}
      />
    </Box>
  )
}
