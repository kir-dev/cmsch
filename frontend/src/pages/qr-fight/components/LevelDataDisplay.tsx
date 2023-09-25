import { Box, chakra } from '@chakra-ui/react'
import { useMemo } from 'react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { useColorModeValue } from '@chakra-ui/system'
import { ResponsiveBar } from '@nivo/bar'

interface LevelDataDisplayProps {
  teams: Record<string, number>
}

export function LevelDataDisplay({ teams }: LevelDataDisplayProps) {
  const data = useMemo(() => {
    return Object.keys(teams)
      .map((team) => ({ team, value: teams[team] }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 5)
  }, [teams])
  const config = useConfigContext()
  const color = useColorModeValue(config.components.style.lightTextColor, config.components.style.darkTextColor)

  return (
    <Box w="100%" h={300}>
      <ResponsiveBar
        data={data}
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
        axisBottom={{
          tickSize: 0,
          tickPadding: 25,
          tickRotation: 0,
          renderTick: ({ opacity, textAnchor, textX, textY, value, x, y }) => {
            return (
              <g transform={`translate(${x},${y})`} style={{ opacity }}>
                <text style={{ fill: color, fontSize: 14 }} textAnchor={textAnchor} transform={`translate(${textX},${textY})`}>
                  <chakra.tspan>{value.substring(0, 3)}</chakra.tspan>
                  <chakra.tspan display={{ base: 'none', md: 'block' }}>{value.substring(3)}</chakra.tspan>
                </text>
              </g>
            )
          }
        }}
      />
    </Box>
  )
}
