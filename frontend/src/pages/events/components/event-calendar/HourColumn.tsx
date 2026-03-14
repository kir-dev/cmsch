import { addHours, endOfDay, format, startOfDay } from 'date-fns'
import { useMemo } from 'react'
import { calculatePosition } from './utils'

export function HourColumn({ h }: { h: number }) {
  const originDate = startOfDay(new Date())
  const dates = useMemo(() => {
    const datesTemp: Date[] = []
    for (let i = 0; i < 24; i++) {
      const date = addHours(originDate, i)
      datesTemp.push(date)
    }
    return datesTemp
  }, [originDate])

  const minDate = originDate
  const maxDate = endOfDay(originDate)

  return (
    <div className="w-12 relative" style={{ height: h }}>
      {dates.map((d) => (
        <span
          className="absolute -translate-y-1/2 m-0 p-0 whitespace-nowrap"
          style={{ top: calculatePosition(minDate.getTime(), maxDate.getTime(), d.getTime()) + '%' }}
          key={d.toISOString()}
        >
          {format(d, 'HH:mm')}
        </span>
      ))}
    </div>
  )
}
