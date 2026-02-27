import { cn } from '@/lib/utils'

interface PulsingDotProps extends React.HTMLAttributes<HTMLDivElement> {
  color?: string
}

export function PulsingDot({ color, className, ...props }: PulsingDotProps) {
  return (
    <div className={cn('inline-block', className)} style={{ color: color || '#4ade80' }} {...props}>
      <svg width="30" height="30" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
        <circle cx="20" cy="20" fill="none" r="10" stroke="currentColor" strokeWidth="4">
          <animate attributeName="r" from="8" to="20" dur="1.5s" begin="0s" repeatCount="indefinite" />
          <animate attributeName="opacity" from="1" to="0" dur="1.5s" begin="0s" repeatCount="indefinite" />
        </circle>
        <circle cx="20" cy="20" fill="currentColor" r="10" />
      </svg>
    </div>
  )
}
