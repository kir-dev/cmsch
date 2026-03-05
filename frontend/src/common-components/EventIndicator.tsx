import { cn } from '@/lib/utils'
import { PulsingDot } from './PulsingDot'

interface EventIndicatorProps {
  isCurrent: boolean
  isUpcoming: boolean
  showLabel?: boolean
  className?: string
  color?: string
}

export function EventIndicator({ isCurrent, isUpcoming, color, showLabel, className }: EventIndicatorProps) {
  if (!isCurrent && !isUpcoming) return null
  if (showLabel)
    return (
      <div
        className={cn('flex items-center rounded-full bg-secondary text-secondary-foreground py-1 pl-4 pr-1 border shadow-sm', className)}
      >
        <span>{isUpcoming ? 'Hamarosan kezdődik' : 'Most zajlik'}</span>
        <PulsingDot color={color ?? (isUpcoming ? 'text-warning' : 'text-success')} />
      </div>
    )
  return <PulsingDot color={color ?? (isUpcoming ? 'text-warning' : 'text-success')} className={className} />
}
