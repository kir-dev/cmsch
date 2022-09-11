import { QrArea } from '../../../util/views/qrFight.view'
import { Box } from '@chakra-ui/react'
import { ArcElement, Chart as ChartJS, ChartData, Legend, Tooltip } from 'chart.js'
import { Doughnut } from 'react-chartjs-2'
import { useMemo } from 'react'
import { randomColor } from '@chakra-ui/theme-tools'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { useColorModeValue } from '@chakra-ui/system'

interface AreaDataDisplayProps {
  area: QrArea
}

ChartJS.register(ArcElement, Tooltip, Legend)

export function AreaDataDisplay({ area }: AreaDataDisplayProps) {
  const colors = area.teams.map(() => randomColor())
  const config = useConfigContext()
  const color = useColorModeValue(config?.components.style.lightTextColor, config?.components.style.darkTextColor)
  const data = useMemo<ChartData<'doughnut', number[]>>(() => {
    return {
      labels: area.teams.map((t) => t.name),
      datasets: [
        {
          label: 'Elfoglalt címkék',
          data: area.teams.map((t) => t.value),
          backgroundColor: colors.map((c) => c + '60'),
          borderColor: colors
        }
      ]
    }
  }, [])
  return (
    <Box>
      <Doughnut
        data={data}
        id={area.id}
        options={{
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false,
              position: 'right',
              labels: { color: color }
            }
          }
        }}
      />
    </Box>
  )
}
