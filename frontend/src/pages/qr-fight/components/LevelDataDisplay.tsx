import { QrLevelDto } from '../../../util/views/qrFight.view'
import { Box } from '@chakra-ui/react'
import { ArcElement, Chart as ChartJS, ChartData, Legend, LegendItem, Tooltip } from 'chart.js'
import { Doughnut } from 'react-chartjs-2'
import { useMemo } from 'react'
import { randomColor } from '@chakra-ui/theme-tools'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { useColorModeValue } from '@chakra-ui/system'
import { TbBoxPadding } from 'react-icons/tb'

interface LevelDataDisplayProps {
  level: QrLevelDto
}

ChartJS.register(ArcElement, Tooltip, Legend)

const mock_teams = {
  Lábosch: 65,
  Chillámák: 73,
  Nightmaresch: 52,
  Schugár: 65,
  Geológusch: 63,
  Elvonásch: 72,
  Offo$ch: 2,
  SPQT: 25,
  Felejtősch: 20,
  Schírókák: 15,
  DETSCH: 3,
  Porszívósch: 20,
  Shrekkentésch: 21,
  Schííhcs: 48,
  NagyfeszültSchég: 32,
  CSÖCS: 5,
  Szakkoholischták: 12,
  Kínosh: 36,
  KultúrSchock: 18,
  Gyanúsch: 17,
  SzeretetSarok: 2
}

export function LevelDataDisplay({ level }: LevelDataDisplayProps) {
  level.teams = mock_teams
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
    <Box w="100%">
      <Doughnut
        height={300}
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
