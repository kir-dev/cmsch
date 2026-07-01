import { useTime } from '@/hooks/useDate.ts'
import { intervalToDuration } from 'date-fns'
import { Minus } from 'lucide-react'

interface ClockProps {
  countTo: number
}

const Clock = ({ countTo }: ClockProps) => {
  const now = useTime(1000)
  const duration = intervalToDuration({ start: now, end: Math.max(countTo, now) })

  return (
    <div className="flex flex-col md:flex-row items-center justify-center">
      {(duration.years || 0) > 0 && (
        <>
          <ClockSegment value={duration.years?.toString()} label={'év'} />
          <Dash />
        </>
      )}
      {(duration.months || 0) > 0 && (
        <>
          <ClockSegment value={duration.months?.toString()} label={'hónap'} />
          <Dash />
        </>
      )}
      <ClockSegment value={duration.days?.toString()} label={'nap'} />
      <Dash />
      <ClockSegment value={duration.hours?.toString()} label={'óra'} />
      <Dash />
      <ClockSegment value={duration.minutes?.toString()} label={'perc'} />
      <Dash />
      <ClockSegment value={duration.seconds?.toString()} label={'másodperc'} />
    </div>
  )
}

interface ClockSegmentProps {
  value: string | undefined
  label: string
}

const ClockSegment = ({ value, label }: ClockSegmentProps) => {
  const valueText = (value || '00').padStart(2, '0')
  return (
    <div className="flex flex-col items-center w-full md:w-1/5 mx-10 my-5">
      <h2 className="text-6xl font-bold leading-10">{valueText}</h2>
      <p>{label}</p>
    </div>
  )
}

const Dash = () => <Minus className="hidden md:block h-8 w-8" />

export default Clock
