import { QrLevelDto } from '../../../util/views/qrFight.view'
import { Box } from '@chakra-ui/react'
import { ArcElement, Chart as ChartJS, ChartData, Legend, LegendItem, Tooltip } from 'chart.js'
import { Doughnut } from 'react-chartjs-2'
import { useMemo } from 'react'
import { randomColor } from '@chakra-ui/theme-tools'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { useColorModeValue } from '@chakra-ui/system'

interface LevelDataDisplayProps {
  level: QrLevelDto
}

ChartJS.register(ArcElement, Tooltip, Legend)

export function LevelDataDisplay({ level }: LevelDataDisplayProps) {
  const teams = useMemo(() => {
    return Object.keys(level.teams)
      .map((team) => ({ name: team, value: level.teams[team] }))
      .sort((a, b) => b.value - a.value)
  }, [level.teams])
  const colors = Object.keys(level.teams).map(() => randomColor())
  const config = useConfigContext()
  const color = useColorModeValue(config?.components.style.lightTextColor, config?.components.style.darkTextColor)
  const data = useMemo<ChartData<'doughnut', number[]>>(() => {
    return {
      labels: teams.map((t, index) => index + 1 + '. ' + t.name),
      datasets: [
        {
          label: 'Elfoglalt címkék',
          data: teams.map((t) => t.value),
          backgroundColor: colors.map((c) => c + '60'),
          borderColor: colors
        }
      ]
    }
  }, [])
  return (
    <Box w="100%" h={300}>
      <Doughnut
        height="100%"
        data={data}
        id={level.name}
        width="100%"
        options={{
          maintainAspectRatio: false,
          plugins: {
            legend: {
              title: {
                text: 'Top csapatok',
                display: true
              },
              position: 'top',
              labels: {
                color: color,
                filter(item: LegendItem, _: ChartData): boolean {
                  return item.index !== undefined && item.index < 5
                }
              }
            }
          }
        }}
      />
    </Box>
  )
}
