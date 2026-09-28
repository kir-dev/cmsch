import { type ChartConfig, ChartContainer } from '@/components/ui/chart'
import { useMemo } from 'react'
import { Bar, BarChart, LabelList, XAxis } from 'recharts'

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

  const chartConfig = {
    value: {
      label: 'Score',
      color: 'var(--primary)'
    }
  } satisfies ChartConfig

  return (
    <ChartContainer config={chartConfig} className="w-full h-75">
      <BarChart data={data} barCategoryGap="20%" margin={{ top: 24 }}>
        <XAxis dataKey="team" axisLine={false} tickLine={false} tick={{ style: { fill: 'var(--foreground)' } }} />
        <Bar dataKey="value" fill="var(--primary)" radius={8}>
          <LabelList dataKey="value" position="top" offset={8} fill="var(--foreground)" style={{ fontSize: 14 }} />
        </Bar>
      </BarChart>
    </ChartContainer>
  )
}
