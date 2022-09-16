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
  const colors = Object.keys(level.teams).map(() => randomColor())
  const config = useConfigContext()
  const color = useColorModeValue(config?.components.style.lightTextColor, config?.components.style.darkTextColor)
  const data = useMemo<ChartData<'doughnut', number[]>>(() => {
    return {
      labels: Object.keys(level.teams),
      datasets: [
        {
          label: 'Elfoglalt címkék',
          data: Object.keys(level.teams).map((t) => level.teams[t]),
          backgroundColor: colors.map((c) => c + '60'),
          borderColor: colors
        }
      ]
    }
  }, [])
  return (
    <Box w="100%">
      <Doughnut
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
                  return item.index !== undefined && item.index < 6
                }
              }
            }
          }
        }}
      />
    </Box>
  )
}
